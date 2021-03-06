package com.github.polybooks.activities

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
import com.github.polybooks.R
import com.github.polybooks.database.Database
import com.github.polybooks.utils.GlobalVariables.EXTRA_ISBN
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith
import java.util.concurrent.CompletableFuture

@RunWith(AndroidJUnit4::class)
class AddSaleTest {

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
        onView(withId(R.id.fill_in_ISBN)).perform(clearText(), typeText(isbn))
        closeSoftKeyboard()
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
        val extraKey = EXTRA_ISBN
        val stringISBN = "9780156881807"
        inputISBN(stringISBN)
        onView(withId(R.id.pass_isbn_button)).perform(click())
        intended(
            allOf(
                hasComponent(FillSaleActivity::class.java.name),
                IntentMatchers.hasExtra(extraKey, stringISBN)
            )
        )
    }

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