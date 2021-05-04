package com.github.polybooks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class LoginPasswordTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun FillAndLoginWithPassword() {
        onView(withId(R.id.email_field)).perform(typeText("login@bypassword.test"), closeSoftKeyboard())
        onView(withId(R.id.password_field)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.log_button)).perform(click())
        Thread.sleep(1500);
        Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
        Intents.intended(toPackage("com.github.polybooks"));
        Intents.intended(hasExtra(EXTRA_MESSAGE, "TestLogin"));
        Thread.sleep(1500);
        onView(withId(R.id.button_disco)).perform(click())
        Thread.sleep(1500);
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }
}