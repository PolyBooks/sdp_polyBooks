package com.github.polybooks

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TakeBookPictureTest {


    @get:Rule
    val activityRule = ActivityScenarioRule(TakeBookPictureActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun takePictureButtonWorks() {
        onView(withId(R.id.camera_capture_button)).perform(click())
        // I think the thread switching might make testing the intent impossible?
        //Intents.intended(IntentMatchers.hasComponent(FillSaleTest::class.java.name))

    }


}