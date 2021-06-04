package com.github.polybooks.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R
import com.github.polybooks.core.Course
import com.github.polybooks.core.Topic
import com.github.polybooks.core.Semester
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import org.junit.Rule
import org.junit.Test

class EditUserInterestsTest {
    val mockTopics: List<Topic> = listOf(
        Topic("Biology"),
        Topic("Computer Science"),
        Topic("Architecture")
    )

    val mockCourses: List<Course> = listOf(
        Course("COM-101"),
        Course("CS-306"),
        Course("CS-323"),
        Course("EE-280"),
        Course("MSE-210"),
        Course("HUM-201"),
        Course("DH-405"),
        Course("ENV-444"),
        Course("MICRO-511")
    )

    val mockSemesters: List<Semester> = listOf(

        Semester("IN", "BA1"),
        Semester("SV", "BA1"),
        Semester("GC", "MA2"),
        Semester("SC", "BA6"),
        Semester("MT", "BA2"),
        Semester("MX", "BA3"),
        Semester("AR", "MA1"),
        Semester("CD", "BA4"),
        Semester("ENV", "BA5")
    )

    @get:Rule
    val activityRule = ActivityScenarioRule(EditUserInterestsActivity::class.java)

    @Test
    fun filterButtonTest(){
        onView(withId(R.id.filter_button))
            .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.GONE)))
    }
    @Test
    fun editUserTest(){
        BaristaListAssertions.assertListItemCount(R.id.recyclerView,
            mockCourses.size + mockTopics.size + mockSemesters.size)
        BaristaListAssertions.assertDisplayedAtPosition(
            R.id.recyclerView,
            0,
            R.id.parameter_value_button,
            mockTopics[0].name
        )
        // TODO better tests
        //onView(withId(R.id.recyclerView)). .onChildView(withId(R.id.parameter_value_button)).check(matches(isChecked()))

    }
}