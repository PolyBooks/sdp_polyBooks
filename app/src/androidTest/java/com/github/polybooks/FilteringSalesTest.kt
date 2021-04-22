package com.github.polybooks

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.*

import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class FilteringSalesTest {
    @get:Rule
    val activityRule: ActivityScenarioRule<FilteringSalesActivity>
        = ActivityScenarioRule(FilteringSalesActivity::class.java)

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

    @Ignore
    @Test
    fun intentIsFiredWhenClickingOnResults() {

        onView(withId(R.id.results_button)).perform(click())

        intended(allOf(
                hasComponent(ListSalesActivity::class.java.name),
                hasExtraWithKey(ListSalesActivity.EXTRA_SALE_QUERY_SETTINGS)))
    }

    @Test
    fun clickingOnButtonInViewDoesntCrash() {
        onView(withId(R.id.title_inc_sort)).perform(click())
    }

    @Test
    fun scrollAndClickingOnButtonOutsideTheViewDoesntCrash() {
        onView(withId(R.id.price_dec_sort)).perform(scrollTo(), click())
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
    fun reclickingOnAParamButtonClearsIt() {
        onView(withId(R.id.state_active)).perform(scrollTo(), click())
        onView(withId(R.id.state_active)).perform(click())
        onView(withId(R.id.state_active)).check(matches(isNotChecked()))
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
        onView(withId(R.id.book_name)).perform(scrollTo(),click())
        onView(withId(R.id.book_name)).check(matches(withText("")))
        onView(withId(R.id.book_isbn)).perform(scrollTo(),click())
        onView(withId(R.id.book_isbn)).check(matches(withText("")))
        onView(withId(R.id.price_min)).perform(scrollTo(),click())
        onView(withId(R.id.price_min)).check(matches(withText("")))
        onView(withId(R.id.price_max)).perform(scrollTo(),click())
        onView(withId(R.id.price_max)).check(matches(withText("")))
    }

    private fun clickOnAllParamButtons() {
        onView(withId(R.id.title_inc_sort)).perform(scrollTo(), click())
        onView(withId(R.id.title_dec_sort)).perform(scrollTo(), click())
        onView(withId(R.id.price_inc_sort)).perform(scrollTo(), click())
        onView(withId(R.id.price_dec_sort)).perform(scrollTo(), click())
        /*
        onView(withId(R.id.publish_date_inc_sort)).perform(scrollTo(), click())
        onView(withId(R.id.publish_date_dec_sort)).perform(scrollTo(), click())

        onView(withId(R.id.CS)).perform(scrollTo(), click())
        onView(withId(R.id.Biology)).perform(scrollTo(), click())
        onView(withId(R.id.Archi)).perform(scrollTo(), click())

        onView(withId(R.id.ic_ba1)).perform(scrollTo(), click())
        onView(withId(R.id.ma_ba2)).perform(scrollTo(), click())
        onView(withId(R.id.sv_ba3)).perform(scrollTo(), click())
        onView(withId(R.id.gc_ma1)).perform(scrollTo(), click())
        onView(withId(R.id.mt_ma2)).perform(scrollTo(), click())

        onView(withId(R.id.CS306)).perform(scrollTo(), click())
        onView(withId(R.id.COM480)).perform(scrollTo(), click())
        */
        onView(withId(R.id.state_active)).perform(scrollTo(), click())
        onView(withId(R.id.state_retracted)).perform(scrollTo(), click())
        onView(withId(R.id.state_concluded)).perform(scrollTo(), click())

        onView(withId(R.id.condition_new)).perform(scrollTo(), click())
        onView(withId(R.id.condition_good)).perform(scrollTo(), click())
        onView(withId(R.id.condition_worn)).perform(scrollTo(), click())
    }

    private fun checkAllParamButtons(isChecked : Boolean) {
        val checkFun = if(isChecked) isChecked() else isNotChecked()

        if(!isChecked) {
            onView(withId(R.id.title_inc_sort)).check(matches(checkFun))
            onView(withId(R.id.title_dec_sort)).check(matches(checkFun))
            onView(withId(R.id.price_inc_sort)).check(matches(checkFun))
            onView(withId(R.id.price_dec_sort)).check(matches(checkFun))
            /*
            onView(withId(R.id.publish_date_inc_sort)).check(matches(checkFun))
            onView(withId(R.id.publish_date_dec_sort)).check(matches(checkFun))
            */
        }
    /*
        onView(withId(R.id.CS)).check(matches(checkFun))
        onView(withId(R.id.Biology)).check(matches(checkFun))
        onView(withId(R.id.Archi)).check(matches(checkFun))

        onView(withId(R.id.ic_ba1)).check(matches(checkFun))
        onView(withId(R.id.ma_ba2)).check(matches(checkFun))
        onView(withId(R.id.sv_ba3)).check(matches(checkFun))
        onView(withId(R.id.gc_ma1)).check(matches(checkFun))
        onView(withId(R.id.mt_ma2)).check(matches(checkFun))

        onView(withId(R.id.CS306)).check(matches(checkFun))
        onView(withId(R.id.COM480)).check(matches(checkFun))
*/
        onView(withId(R.id.state_active)).check(matches(checkFun))
        onView(withId(R.id.state_retracted)).check(matches(checkFun))
        onView(withId(R.id.state_concluded)).check(matches(checkFun))

        onView(withId(R.id.condition_new)).check(matches(checkFun))
        onView(withId(R.id.condition_good)).check(matches(checkFun))
        onView(withId(R.id.condition_worn)).check(matches(checkFun))
    }
}
