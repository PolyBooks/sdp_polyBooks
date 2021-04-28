package com.github.polybooks

import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TakeBookPictureFragmentTest {


    @Test
    fun takePictureButtonWorks() {
        val scenario = launchFragmentInContainer<TakeBookPictureFragment>()
        onView(withId(R.id.camera_capture_button)).perform(click())
        // Assert expected behavior
    }

    @Test
    fun testStartingState() {
        val scenario = launchFragmentInContainer<TakeBookPictureFragment>(
            initialState = Lifecycle.State.STARTED,
        )
        scenario.moveToState(Lifecycle.State.RESUMED)
    }
}