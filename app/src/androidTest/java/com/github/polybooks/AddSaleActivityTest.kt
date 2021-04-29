package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.CoreMatchers.not
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)

class AddSaleActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AddSaleActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun scanButtonRedirects() {
        onView(withId(R.id.scan_button)).perform(click())
        intended(hasComponent(ScanBarcodeActivity::class.java.name))
    }

    @Test
    fun passISBNInitiallyDisabled() {
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
    }

    private fun inputISBN(isbn: String) {
        onView(withId(R.id.fill_in_ISBN)).perform(ViewActions.clearText(), ViewActions.typeText(isbn))
        Espresso.closeSoftKeyboard()
    }

    @Test
    fun passISBNEnabling() {
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
        inputISBN("0")
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
        inputISBN("abc")
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
        inputISBN("9780345432360")
        onView(withId(R.id.pass_isbn_button)).check(matches((isEnabled())))
        inputISBN("1")
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
        inputISBN("")
        onView(withId(R.id.pass_isbn_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun passValidISBN() {
        val extraKey = "com.github.polybooks.ISBN"
        val stringISBN = "9780345432360"
        inputISBN(stringISBN)
        onView(withId(R.id.pass_isbn_button)).perform(click())
        intended(
            Matchers.allOf(
                hasComponent(FillSaleActivity::class.java.name),
                IntentMatchers.hasExtra(extraKey, stringISBN)
            )
        )
    }
}