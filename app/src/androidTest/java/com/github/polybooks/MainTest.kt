package com.github.polybooks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit


class MainTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun before() {
        BaristaSleepInteractions.sleep(2, TimeUnit.SECONDS)
        Intents.init()
    }
/*
    @After
    fun after() {
        Intents.release()
    }
*/
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
        Intents.init()
        onView(withId(R.id.log_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun sellButton() {
        Intents.init()
        onView(withId(R.id.sell_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(AddSaleActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun databaseButton() {
        Intents.init()
        onView(withId(R.id.button_open_db_tests)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
        Intents.release()
    }

    //This one
    @Test
    fun signUpButton() {
        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.register_button)).check(matches(isDisplayed()))
    }

}