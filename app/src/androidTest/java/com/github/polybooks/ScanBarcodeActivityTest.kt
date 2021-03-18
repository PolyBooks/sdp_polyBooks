package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class ScanBarcodeActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ScanBarcodeActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }


    @Test
    fun passISBNButtonRedirects() {
        Espresso.onView(ViewMatchers.withId(R.id.temporary_button)).perform(ViewActions.click())
        // TODO could check that the ISBN is correctly passed too, but most importantly for the UX, get rid of the button, automatically redirects and test that rather
        Intents.intended(IntentMatchers.hasComponent(FillSaleActivity::class.java.name))
    }
}