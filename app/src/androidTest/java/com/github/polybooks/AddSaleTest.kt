package com.github.polybooks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class AddSaleTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddSale::class.java)

    @Test
    fun scanButtonRedirects() {
        Intents.init()
        onView(withId(R.id.scan_button)).perform(click())
        intended(hasComponent(ScanBarcode::class.java.name))
        Intents.release()
    }

    @Test
    fun passISBNButtonRedirects() {
        Intents.init()
        onView(withId(R.id.pass_isbn_button)).perform(click())
        // TODO could check that the ISBN is correctly passed too
        intended(hasComponent(FillSale::class.java.name))
        Intents.release()
    }
}