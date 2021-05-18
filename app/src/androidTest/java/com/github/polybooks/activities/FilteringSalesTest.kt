package com.github.polybooks.activities

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.R
import com.github.polybooks.adapter.InterestsParameterAdapter
import com.github.polybooks.core.*
import com.github.polybooks.database.SaleOrdering
import com.github.polybooks.utils.FilteringTestUtils
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilteringSalesTest {

    companion object {
        val COURSE = InterestsParameterAdapter.Interest.COURSE
        val SEMESTER = InterestsParameterAdapter.Interest.SEMESTER
        val FIELD = InterestsParameterAdapter.Interest.FIELD

        const val RANDOM_STRING = "BL1Abl6-a"
        const val RANDOM_NUMBER = "42"
    }

    @get:Rule
    val activityRule: ActivityScenarioRule<FilteringSalesActivity> =
        ActivityScenarioRule(FilteringSalesActivity::class.java)

    private val utils = FilteringTestUtils(ApplicationProvider.getApplicationContext())

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    //======================================================================
    //                            Tests Filtering
    //======================================================================
    @Test
    fun intentIsFiredWhenClickingOnResults() {
        onView(withId(R.id.results_button)).perform(click())
        intended(
            allOf(
                hasComponent(ListSalesActivity::class.java.name),
                hasExtraWithKey(ListActivity.EXTRA_SALE_QUERY)
            )
        )
    }

    @Test
    fun allParameterItemsAreDisplayed() {
        utils.swdo()
        utils.performOnEnumParameter(SaleOrdering.DEFAULT, R.id.sale_sort_parameter)
        utils.performOnEnumParameter(SaleState.ACTIVE, R.id.sale_state_parameter)
        utils.performOnEnumParameter(BookCondition.NEW, R.id.sale_condition_parameter)

        utils.swup()
        utils.performOnInterestParameter<Field>(FIELD, R.id.field_parameter)
        utils.performOnInterestParameter<Semester>(SEMESTER, R.id.semester_parameter)
        utils.performOnInterestParameter<Course>(COURSE, R.id.course_parameter)
    }

    @Test
    fun scrollAndClickingOnAllParameterButtonDoesntCrash() {
        clickOnAllParamButtons()
        // cannot test because nested recyclerview
//         checkAllParamButtons(true)
    }

    @Test
    fun canWriteInTexts() {
        writeInTextEdits()

        /* this part fails when using the small screen of cirrus because the keyboard hides
           the text field, must uncomment to test locally */
//        onView(withId(R.id.book_name)).perform(scrollTo(),click())
//        onView(withId(R.id.book_name)).check(matches(withText(RANDOM_STRING)))
    }

    @Test
    fun orderingItemsAreMutuallyExclusive() {
        utils.perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click())
        utils.perform(R.id.sale_sort_parameter, SaleOrdering.PUBLISH_DATE_DEC, click())

        utils.check(R.id.sale_sort_parameter, SaleOrdering.PUBLISH_DATE_DEC, matches(isChecked()))
        utils.check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isNotChecked()))
    }

    @Test
    fun clickingThreeTimesOnOrderingItemWorks() {
        utils.perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click()) // is now checked
        utils.perform(
            R.id.sale_sort_parameter,
            SaleOrdering.TITLE_INC,
            click()
        ) // is now not checked
        utils.check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isNotChecked()))
        utils.perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click()) // is now checked
        utils.check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isChecked()))
    }

    @Test
    fun clickingOnResetClearsEverything() {
        writeInTextEdits()
        clickOnAllParamButtons()
        onView(withId(R.id.reset_button)).perform(click())

        /* this instruction fails when using the small screen of cirrus because the
           keyboard hides the text field, must uncomment to test locally */
//        checkTextEditsAreEmpty()

        checkAllParamButtons(false)
    }


    private fun writeInTextEdits() {
        onView(withId(R.id.book_name)).perform(scrollTo(), clearText(), typeText(RANDOM_STRING))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.book_isbn)).perform(scrollTo(), clearText(), typeText(RANDOM_STRING))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.price_min)).perform(scrollTo(), clearText(), typeText(RANDOM_NUMBER))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.price_max)).perform(scrollTo(), clearText(), typeText(RANDOM_NUMBER))
        Espresso.closeSoftKeyboard()
    }

    private fun checkTextEditsAreEmpty() {
        onView(withId(R.id.book_name)).perform(scrollTo(), click())
        onView(withId(R.id.book_name)).check(matches(withText("")))
        onView(withId(R.id.book_isbn)).perform(scrollTo(), click())
        onView(withId(R.id.book_isbn)).check(matches(withText("")))
        onView(withId(R.id.price_min)).perform(scrollTo(), click())
        onView(withId(R.id.price_min)).check(matches(withText("")))
        onView(withId(R.id.price_max)).perform(scrollTo(), click())
        onView(withId(R.id.price_max)).check(matches(withText("")))
    }

    private fun clickOnAllParamButtons() {
        utils.swdo()
        utils.performOnEnumParameter(SaleOrdering.DEFAULT, R.id.sale_sort_parameter, click())
        utils.performOnEnumParameter(SaleState.ACTIVE, R.id.sale_state_parameter, click())
        utils.performOnEnumParameter(BookCondition.NEW, R.id.sale_condition_parameter, click())

        utils.swup()
        utils.performOnInterestParameter<Field>(FIELD, R.id.field_parameter, click())
        utils.performOnInterestParameter<Semester>(SEMESTER, R.id.semester_parameter, click())
        utils.performOnInterestParameter<Course>(COURSE, R.id.course_parameter, click())
    }

    private fun checkAllParamButtons(isChecked: Boolean) {
        val checkFun = if (isChecked) isChecked() else isNotChecked()

        utils.swdo()
        if (!isChecked) {
            utils.performOnEnumParameter(
                SaleOrdering.DEFAULT, R.id.sale_sort_parameter, null, matches(checkFun)
            )
        }
        utils.performOnEnumParameter(
            SaleState.ACTIVE, R.id.sale_state_parameter, null, matches(checkFun)
        )
        utils.performOnEnumParameter(
            BookCondition.NEW, R.id.sale_condition_parameter, null, matches(checkFun)
        )

        // Cannot test because recyclerView
//        swup()
//        performOnInterestParameter<Field>(
//            FIELD, R.id.parameter, null, matches(checkFun)
//        )
//        performOnInterestParameter<Semester>(
//            SEMESTER, R.id.semester_parameter, null, matches(checkFun)
//        )
//        performOnInterestParameter<Course>(
//            COURSE, R.id.course_parameter, null, matches(checkFun)
//        )
    }

    //======================================================================
    //                            Tests NavBar
    //======================================================================

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        intended(hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
    }

    @Test
    fun navBarDefault() {
        onView(withId(R.id.default_selected)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.GONE
                )
            )
        )
        onView(withId(R.id.default_selected)).check(matches(Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.default_selected)).check(matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}
