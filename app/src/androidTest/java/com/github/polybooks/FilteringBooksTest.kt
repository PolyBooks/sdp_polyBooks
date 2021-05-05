package com.github.polybooks

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.core.Course
import com.github.polybooks.core.Field
import com.github.polybooks.core.Semester
import com.github.polybooks.core.database.interfaces.BookOrdering
import com.github.polybooks.utils.FilteringTestUtils
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilteringBooksTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<FilteringBooksActivity> =
        ActivityScenarioRule(FilteringBooksActivity::class.java)

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
                hasExtraWithKey(ListSalesActivity.EXTRA_BOOKS_QUERY_SETTINGS)
            )
        )
    }

    @Test
    fun allParameterItemsAreDisplayed() {
        utils.swdo()
        utils.performOnEnumParameter(BookOrdering.DEFAULT, R.id.book_sort_parameter)

        utils.swup()
        utils.performOnInterestParameter<Field>(FilteringSalesTest.FIELD, R.id.field_parameter)
        utils.performOnInterestParameter<Semester>(
            FilteringSalesTest.SEMESTER,
            R.id.semester_parameter
        )
        utils.performOnInterestParameter<Course>(FilteringSalesTest.COURSE, R.id.course_parameter)
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
        utils.perform(R.id.book_sort_parameter, BookOrdering.TITLE_INC, click())
        utils.perform(R.id.book_sort_parameter, BookOrdering.TITLE_DEC, click())

        utils.check(
            R.id.book_sort_parameter, BookOrdering.TITLE_DEC,
            ViewAssertions.matches(ViewMatchers.isChecked())
        )
        utils.check(
            R.id.book_sort_parameter, BookOrdering.TITLE_INC,
            ViewAssertions.matches(ViewMatchers.isNotChecked())
        )
    }

    @Test
    fun clickingThreeTimesOnOrderingItemWorks() {
        utils.perform(R.id.book_sort_parameter, BookOrdering.TITLE_INC, click()) // is now checked
        utils.perform(
            R.id.book_sort_parameter,
            BookOrdering.TITLE_INC,
            click()
        ) // is now not checked
        utils.check(
            R.id.book_sort_parameter, BookOrdering.TITLE_INC,
            ViewAssertions.matches(ViewMatchers.isNotChecked())
        )
        utils.perform(R.id.book_sort_parameter, BookOrdering.TITLE_INC, click()) // is now checked
        utils.check(
            R.id.book_sort_parameter, BookOrdering.TITLE_INC,
            ViewAssertions.matches(ViewMatchers.isChecked())
        )
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
        onView(withId(R.id.book_name)).perform(
            scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(FilteringSalesTest.RANDOM_STRING)
        )
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.book_isbn)).perform(
            scrollTo(),
            ViewActions.clearText(),
            ViewActions.typeText(FilteringSalesTest.RANDOM_STRING)
        )
        Espresso.closeSoftKeyboard()
    }

    private fun checkTextEditsAreEmpty() {
        onView(withId(R.id.book_name)).perform(scrollTo(), click())
        onView(withId(R.id.book_name)).check(ViewAssertions.matches(ViewMatchers.withText("")))
        onView(withId(R.id.book_isbn)).perform(scrollTo(), click())
        onView(withId(R.id.book_isbn)).check(ViewAssertions.matches(ViewMatchers.withText("")))
    }

    private fun clickOnAllParamButtons() {
        utils.swdo()
        utils.performOnEnumParameter(BookOrdering.DEFAULT, R.id.book_sort_parameter, click())

        utils.swup()
        utils.performOnInterestParameter<Field>(
            FilteringSalesTest.FIELD,
            R.id.field_parameter,
            click()
        )
        utils.performOnInterestParameter<Semester>(
            FilteringSalesTest.SEMESTER,
            R.id.semester_parameter,
            click()
        )
        utils.performOnInterestParameter<Course>(
            FilteringSalesTest.COURSE,
            R.id.course_parameter,
            click()
        )
    }

    private fun checkAllParamButtons(isChecked: Boolean) {
        val checkFun = if (isChecked) ViewMatchers.isChecked() else ViewMatchers.isNotChecked()

        utils.swdo()
        if (!isChecked) {
            utils.performOnEnumParameter(
                BookOrdering.DEFAULT, R.id.book_sort_parameter, null,
                ViewAssertions.matches(checkFun)
            )
        }

        // Cannot test because recyclerView
//        swup()
//        performOnInterestParameter<Field>(
//            FIELD, R.id.field_parameter, null, matches(checkFun)
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
        intended(hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        onView(withId(R.id.results_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        onView(withId(R.id.results_button)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    @Test
    fun navBarDefault() {
        onView(withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.GONE
                )
            )
        )
        onView(withId(R.id.default_selected))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.books))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}