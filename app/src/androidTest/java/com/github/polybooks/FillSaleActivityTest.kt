package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FillSaleActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FillSaleActivity::class.java)



    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun addPictureRedirects() {
        // TODO this test is currently not needed and useless
        onView(withId(R.id.add_picture)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    @Test
    fun confirmSaleInitiallyDisabled() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    private fun inputPrice(price: String) {
        onView(withId(R.id.filled_price)).perform(ViewActions.clearText(), ViewActions.typeText(price))
        Espresso.closeSoftKeyboard()
    }

    private fun selectCondition(condition: String) {
        onView(withId(R.id.filled_condition)).perform(click())
        onView(withText(condition)).perform(click())
    }

    @Test
    fun confirmSaleEnabling1() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("5")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).check(matches(isEnabled()))
        inputPrice("")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Select")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun confirmSaleEnabling2() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("5")
        onView(withId(R.id.confirm_sale_button)).check(matches(isEnabled()))
        selectCondition("Select")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun confirmSaleRedirects() {
        // TODO the destination in the intent will probably be changed in the future
        inputPrice("5")
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

}