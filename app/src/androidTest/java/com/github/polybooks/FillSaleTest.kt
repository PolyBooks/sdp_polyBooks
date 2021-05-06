package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FillSaleTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(FillSaleActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }


    @Test
    fun addPictureRedirects() {
        onView(withId(R.id.add_picture)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(TakeBookPictureActivity::class.java.name))
    }

    @Test
    fun confirmSaleInitiallyDisabled() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    private fun inputPrice(price: String) {
        onView(withId(R.id.filled_price)).perform(scrollTo(), clearText(), typeText(price))
        Espresso.closeSoftKeyboard()
    }

    private fun selectCondition(condition: String) {
        onView(withId(R.id.filled_condition)).perform(scrollTo(), click())
        onView(withText(condition)).perform(click())
    }

    @Test
    fun confirmSaleEnabling1() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("5")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).check(matches(isEnabled()))
        inputPrice("")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Select")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    @Test
    fun confirmSaleEnabling2() {
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("5")
        onView(withId(R.id.confirm_sale_button)).check(matches(isEnabled()))
        selectCondition("Select")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
        inputPrice("")
        onView(withId(R.id.confirm_sale_button)).check(matches(not(isEnabled())))
    }

    @Ignore("Would cause sends to Firebase at each PR push")
    @Test
    fun confirmSaleRedirects() {
        // TODO the destination in the intent will probably be changed in the future
        inputPrice("5")
        selectCondition("Worn")
        onView(withId(R.id.confirm_sale_button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
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