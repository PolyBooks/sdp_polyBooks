package com.github.polybooks.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R

import org.hamcrest.Matchers.not
import org.junit.*


class MainTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun before() {
        //BaristaSleepInteractions.sleep(2, TimeUnit.SECONDS)
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    //This one
    @Test
    fun allButtonsClickable() {
        onView(withId(R.id.log_button)).check(matches(isDisplayed()))
        onView(withId(R.id.log_button)).check(matches(isClickable()))

        onView(withId(R.id.signup_button)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_button)).check(matches(isClickable()))

        //onView(withId(R.id.sell_button)).check(matches(isDisplayed()))
        //onView(withId(R.id.sell_button)).check(matches(isClickable()))

        onView(withId(R.id.view_books_button)).check(matches(isDisplayed()))
        onView(withId(R.id.view_books_button)).check(matches(isClickable()))

    }

    @Test
    fun loginButton() {
        onView(withId(R.id.log_button)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Ignore("Need user to be logged in")
    @Test
    fun sellButton() {
        onView(withId(R.id.sell_button)).perform(click())
        intended(hasComponent(AddSaleActivity::class.java.name))

    }

    @Test

    fun allBooksButton() {
        onView(withId(R.id.view_books_button)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
    }

    @Test
    fun listBookButton() {

        onView(withId(R.id.view_books_button)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
    }

    //This one
    @Test
    fun signUpButton() {
        onView(withId(R.id.signup_button)).perform(click())
        intended(hasComponent(RegisterActivity::class.java.name))
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
        onView(withId(R.id.default_selected)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.default_selected)).check(matches(not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.home)).check(matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        onView(withId(R.id.view_books_button)).check(matches(isDisplayed()))
    }

}