package com.github.polybooks.utils

import android.content.Context
import android.os.SystemClock
import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import com.github.polybooks.activities.FilteringSalesTest
import com.github.polybooks.R
import com.github.polybooks.adapter.InterestsParameterAdapter
import com.github.polybooks.adapter.ParameterViewHolder
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Interest
import com.github.polybooks.core.Semester
import com.github.polybooks.database.Database
import com.github.polybooks.database.DummyInterestDatabase

class FilteringTestUtils(private val context: Context?) {

    fun pause() {
        SystemClock.sleep(3000)
    }

    fun pauselong() {
        SystemClock.sleep(10000)
    }

    fun initInterestDB() {
        val interestDB = Database.interestDatabase
        interestDB.addField(Field("Biology"))
        interestDB.addField(Field("Computer Science"))
        interestDB.addField(Field("Architecture"))
        interestDB.addSemester(Semester("IN", "BA1"))
        interestDB.addSemester(Semester("ENV", "BA5"))
        interestDB.addSemester(Semester("SC", "BA6"))
        interestDB.addCourse(Course("COM-101"))
        interestDB.addCourse(Course("CS-306"))
        interestDB.addCourse(Course("CS-323"))
    }

    fun <T: FieldWithName> performOnEnumParameter(
        instance: T,
        parameterId: Int,
        action: ViewAction? = null,
        assertion: ViewAssertion? = null
    ) {
        val values = instance.javaClass.enumConstants
            .drop(
                if (parameterId == R.id.sale_sort_parameter ||
                    parameterId == R.id.book_sort_parameter
                ) 1 else 0
            )

        performOnParameterList(parameterId, values, action, assertion)
    }

    fun <T: Interest> performOnInterestParameter(
        interestType: InterestsParameterAdapter.Interest,
        parameterId: Int,
        action: ViewAction? = null,
        assertion: ViewAssertion? = null
    ) {
        val values = when (interestType) {
            FilteringSalesTest.FIELD -> DummyInterestDatabase.mockFields
            FilteringSalesTest.COURSE -> DummyInterestDatabase.mockCourses
            FilteringSalesTest.SEMESTER -> {
                if (assertion == null)
                    DummyInterestDatabase.mockSemesters
                else //TODO otherwise test don't pass
                    listOf(DummyInterestDatabase.mockSemesters[0])
            }
            else -> error("Interest type does not exist")
        }

        performOnParameterList(parameterId, values, action, assertion)
    }


    private fun <T> performOnParameterList(
        parameterId: Int,
        values: List<T>,
        action: ViewAction? = null,
        assertion: ViewAssertion? = null
    ) {
        for (value in values) {
            scrollToValue(parameterId, value)

            if (assertion != null) {
                check(parameterId, value, assertion)
            }

            if (action != null) {
                perform(parameterId, value, action)
            }
        }
    }

    private fun <T> scrollToValue(parameterId: Int, value: T) {
        Espresso.onView(ViewMatchers.withId(parameterId)).perform(
            scrollTo(),
            RecyclerViewActions.scrollTo<ParameterViewHolder<T>>(
                ViewMatchers.hasDescendant(ViewMatchers.withText(getName(value)))
            )
        )
    }

    fun <T> perform(parameterId: Int, value: T, action: ViewAction) {
        scrollToValue(parameterId, value)
        Espresso.onView(ViewMatchers.withText(getName(value))).perform(scrollTo(), action)
    }

    fun <T> check(parameterId: Int, value: T, assertion: ViewAssertion) {
        scrollToValue(parameterId, value)
        Espresso.onView(ViewMatchers.withText(getName(value))).check(assertion)
    }

    private fun <T> getName(value: T): String {
        return when (value) {
            is FieldWithName -> (value as FieldWithName).fieldName(context)
            is Field -> (value as Field).name
            is Course -> (value as Course).name
            is Semester -> {
                val v = value as Semester
                v.section + "-" + v.semester
            }
            else -> error("unsupported type")
        }
    }

    fun swup() {
        Espresso.onView(ViewMatchers.withId(R.id.main_scroll)).perform(ViewActions.swipeUp())
    }

    fun swdo() {
        Espresso.onView(ViewMatchers.withId(R.id.main_scroll)).perform(ViewActions.swipeDown())
    }
}