package com.github.polybooks

import android.R.layout
import android.content.Intent
import android.provider.Settings.Global.getString
import androidx.core.content.ContextCompat.startActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.ComponentNameMatchers.hasShortClassName
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)

class RegisterTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun FillAndRegister() {
        onView(withId(R.id.username_field)).perform(typeText("TestTestTest"), closeSoftKeyboard())
        onView(withId(R.id.email_field)).perform(typeText("test@test.test"), closeSoftKeyboard())
        onView(withId(R.id.password1_field)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.password2_field)).perform(typeText("123456"), closeSoftKeyboard())
        onView(withId(R.id.button_reg)).perform(click())
        Thread.sleep(1500);
        Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
        Intents.intended(toPackage("com.github.polybooks"));
        Intents.intended(hasExtra(EXTRA_MESSAGE, "TestTestTest"));
        Thread.sleep(1500);
        Firebase.auth.currentUser.delete()
    }
}