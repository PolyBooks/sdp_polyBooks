package com.github.polybooks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


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

        onView(withId(R.id.sell_button)).check(matches(isDisplayed()))
        onView(withId(R.id.sell_button)).check(matches(isClickable()))

        onView(withId(R.id.button_open_db_tests)).check(matches(isDisplayed()))
        onView(withId(R.id.button_open_db_tests)).check(matches(isClickable()))

    }

    @Test
    fun loginButton() {

        onView(withId(R.id.log_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))

    }

    @Test
    fun sellButton() {

        onView(withId(R.id.sell_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(AddSaleActivity::class.java.name))

    }

//    @Test
//    fun databaseButton() {
//
//        onView(withId(R.id.button_open_db_tests)).perform(click())
//        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
//
//    }

    @Test
    fun salesFilterButton() {

        onView(withId(R.id.button_open_db_tests)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
        /*
          onView(withId(R.id.button_open_db_tests)).perform(click())
          Intents.intended(IntentMatchers.hasComponent(FilteringBooksActivity::class.java.name))
        */
    }

    //This one
    @Test
    fun signUpButton() {
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.register_button)).check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        onView(withId(R.id.button_open_db_tests)).check(matches(isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(FilteringBooksActivity::class.java.name))
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
        onView(withId(R.id.button_open_db_tests)).check(matches(isDisplayed()))
    }

}