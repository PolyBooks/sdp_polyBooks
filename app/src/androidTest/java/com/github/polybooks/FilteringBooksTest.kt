package com.github.polybooks

import androidx.test.espresso.Espresso.onView
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
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FilteringBooksTest {
    @get:Rule
    val activityRule : ActivityScenarioRule<FilteringBooksActivity>
            = ActivityScenarioRule(FilteringBooksActivity::class.java)

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
        intended(hasComponent(ListSalesActivity::class.java.name))

        intended(allOf(
                hasComponent(ListSalesActivity::class.java.name),
                hasExtraWithKey(ListSalesActivity.EXTRA_BOOKS_QUERY_SETTINGS)))
    }

    @Test
    fun clickingOnButtonInViewDoesntCrash() {
        onView(withId(R.id.sv_ba3)).perform(click())
    }

    @Test
    fun scrollAndClickingOnButtonOutsideTheViewDoesntCrash() {
        onView(withId(R.id.CS306)).perform(scrollTo(), click())
    }

    @Test
    fun scrollAndClickingOnAllParameterButtonDoesntCrash() {
        clickOnAllParamButtons()
        checkAllParamButtons(true)
    }

    @Test
    fun reclickingOnAParamButtonClearsIt() {
        onView(withId(R.id.ic_ba1)).perform(scrollTo(), click())
        onView(withId(R.id.ic_ba1)).perform(click())
        onView(withId(R.id.ic_ba1)).check(ViewAssertions.matches(ViewMatchers.isNotChecked()))
    }

    @Test
    fun clickingOnResetClearsEverything() {
        // set everything
        clickOnAllParamButtons()

        // click on reset to clear everything
        onView(withId(R.id.reset_button)).perform(click())

        // check everything is cleared
        checkAllParamButtons(false)
    }

    private fun clickOnAllParamButtons() {
        onView(withId(R.id.popularity_sort)).perform(scrollTo(), click())
        onView(withId(R.id.title_inc_sort)).perform(scrollTo(), click())
        onView(withId(R.id.title_dec_sort)).perform(scrollTo(), click())

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
    }

    private fun checkAllParamButtons(isChecked : Boolean) {
        val checkFun = if(isChecked) ViewMatchers.isChecked() else ViewMatchers.isNotChecked()

        if(!isChecked) {
            onView(withId(R.id.popularity_sort)).check(ViewAssertions.matches(checkFun))
            onView(withId(R.id.title_inc_sort)).check(ViewAssertions.matches(checkFun))
            onView(withId(R.id.title_dec_sort)).check(ViewAssertions.matches(checkFun))
        }

        onView(withId(R.id.CS)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.Biology)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.Archi)).check(ViewAssertions.matches(checkFun))

        onView(withId(R.id.ic_ba1)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.ma_ba2)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.sv_ba3)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.gc_ma1)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.mt_ma2)).check(ViewAssertions.matches(checkFun))

        onView(withId(R.id.CS306)).check(ViewAssertions.matches(checkFun))
        onView(withId(R.id.COM480)).check(ViewAssertions.matches(checkFun))
    }
}