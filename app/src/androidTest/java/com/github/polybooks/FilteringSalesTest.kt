package com.github.polybooks

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.adapter.ParameterViewHolder
import com.github.polybooks.core.BookCondition
import com.github.polybooks.core.SaleState
import com.github.polybooks.core.database.interfaces.SaleOrdering
import com.github.polybooks.utils.FieldWithName
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FilteringSalesTest {

    @get:Rule
    val activityRule: ActivityScenarioRule<FilteringSalesActivity> =
        ActivityScenarioRule(FilteringSalesActivity::class.java)

    private val targetContext: Context = ApplicationProvider.getApplicationContext();

    private val RANDOM_STRING = "BL1Abl6-a"
    private val RANDOM_NUMBER = "42"

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun intentIsFiredWhenClickingOnResults() {
        onView(withId(R.id.results_button)).perform(click())

        intended(
            allOf(
                hasComponent(ListSalesActivity::class.java.name),
                hasExtraWithKey(ListSalesActivity.EXTRA_SALE_QUERY_SETTINGS)
            )
        )
    }

    @Test
    fun sortingItemsAreDisplayed() {
        performOnParameter(SaleOrdering.DEFAULT, R.id.sale_sort_parameter)
    }

    @Test
    fun stateItemsAreDisplayed() {
        performOnParameter(SaleState.ACTIVE, R.id.sale_state_parameter)
    }

    @Test
    fun bookConditionItemsAreDisplayed() {
        performOnParameter(BookCondition.NEW, R.id.sale_book_condition_parameter)
    }

    @Test
    fun scrollAndClickingOnAllParameterButtonDoesntCrash() {
        clickOnAllParamButtons()
        checkAllParamButtons(true)
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
        perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click())
        perform(R.id.sale_sort_parameter, SaleOrdering.PUBLISH_DATE_DEC, click())

        check(R.id.sale_sort_parameter, SaleOrdering.PUBLISH_DATE_DEC, matches(isChecked()))
        check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isNotChecked()))
    }

    @Test
    fun clickingThreeTimesOnOrderingItemWorks() {
        perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click()) // is now checked
        perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click()) // is now not checked
        check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isNotChecked()))
        perform(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, click()) // is now checked
        check(R.id.sale_sort_parameter, SaleOrdering.TITLE_INC, matches(isChecked()))

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
        performOnParameter(SaleOrdering.DEFAULT, R.id.sale_sort_parameter, click())
        performOnParameter(SaleState.ACTIVE, R.id.sale_state_parameter, click())
        performOnParameter(BookCondition.NEW, R.id.sale_book_condition_parameter, click())
    }

    private fun checkAllParamButtons(isChecked: Boolean) {
        val checkFun = if (isChecked) isChecked() else isNotChecked()

        if (!isChecked) {
            performOnParameter(
                SaleOrdering.DEFAULT,
                R.id.sale_sort_parameter,
                null,
                matches(checkFun)
            )
        }

        performOnParameter(
            SaleState.ACTIVE,
            R.id.sale_state_parameter,
            null,
            matches(checkFun)
        )
        performOnParameter(
            BookCondition.NEW,
            R.id.sale_book_condition_parameter,
            null,
            matches(checkFun)
        )
    }

    private fun <T: FieldWithName> performOnParameter(
        instance: T,
        parameterId: Int,
        action: ViewAction? = null,
        assertion: ViewAssertion? = null
    ) {
        val values = instance.javaClass.enumConstants
            .drop(if (parameterId == R.id.sale_sort_parameter) 1 else 0)

        for (value in values) {
            if (action != null) {
                perform(parameterId, value, action)
            }

            if (assertion != null) {
                check(parameterId, value, assertion)
            }
        }
    }

    private fun <T: FieldWithName> scrollToValue(parameterId: Int, value: T) {
        onView(withId(parameterId)).perform(
            RecyclerViewActions.scrollTo<ParameterViewHolder<T>>(
                hasDescendant(withText(value.fieldName(targetContext)))
            )
        )
    }

    private fun <T: FieldWithName> perform(parameterId: Int, value: T, action: ViewAction) {
        scrollToValue(parameterId, value)
        onView(withText(value.fieldName(targetContext))).perform(action)
    }

    private fun <T: FieldWithName> check(parameterId: Int, value: T, assertion: ViewAssertion) {
        scrollToValue(parameterId, value)
        onView(withText(value.fieldName(targetContext))).check(assertion)
    }
}
