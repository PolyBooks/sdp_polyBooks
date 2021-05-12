package com.github.polybooks

import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaCheckedAssertions.assertChecked
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertNotClickable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn

import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.hamcrest.Matchers
import org.hamcrest.Matchers.not
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
        Intents.intended(IntentMatchers.hasComponent(RegisterActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        Espresso.onView(ViewMatchers.withId(R.id.sales)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.add_picture)).perform(scrollTo()).check(
            ViewAssertions.matches(
                ViewMatchers.isDisplayed()))
    }

    @Test
    fun navBarBooks() {
        Espresso.onView(ViewMatchers.withId(R.id.books)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(FilteringBooksActivity::class.java.name))
    }

    @Test
    fun navBarDefault() {
        Espresso.onView(ViewMatchers.withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                ViewMatchers.withEffectiveVisibility(
                    ViewMatchers.Visibility.GONE
                )
            )
        )
        Espresso.onView(ViewMatchers.withId(R.id.default_selected))
            .check(ViewAssertions.matches(Matchers.not(ViewMatchers.isEnabled())))
    }

    @Test
    fun navBarSelected() {
        Espresso.onView(ViewMatchers.withId(R.id.default_selected))
            .check(ViewAssertions.matches(ViewMatchers.isSelected()))
    }

    @Test
    fun navBarHome() {
        Espresso.onView(ViewMatchers.withId(R.id.home)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

}