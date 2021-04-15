package com.github.polybooks

import android.R
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class UserProfileTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(UserProfileActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun buttonsViews() {
        onView(withId(com.github.polybooks.R.id.button_disco)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(com.github.polybooks.R.id.button_disco)).check(ViewAssertions.matches(isClickable()))

        onView(withId(com.github.polybooks.R.id.sell_book_button)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(com.github.polybooks.R.id.sell_book_button)).check(ViewAssertions.matches(isClickable()))

        onView(withId(com.github.polybooks.R.id.my_sales_button)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(com.github.polybooks.R.id.my_sales_button)).check(ViewAssertions.matches(isClickable()))

        onView(withId(com.github.polybooks.R.id.logo)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(com.github.polybooks.R.id.logo)).check(ViewAssertions.matches(isClickable()))
    }

    fun backHome(){
        onView(withId(com.github.polybooks.R.id.logo)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }

    fun disconnectButton(){
        onView(withId(com.github.polybooks.R.id.button_disco)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }


}