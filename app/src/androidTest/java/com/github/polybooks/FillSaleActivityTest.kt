package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

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
    fun confirmSaleRedirects() {
        // TODO the destination in the intent will probably be changed in the future
        Espresso.onView(ViewMatchers.withId(R.id.confirm_sale)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

}