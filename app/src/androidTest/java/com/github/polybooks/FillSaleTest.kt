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
        onView(withId(R.id.add_picture)).perform(scrollTo(), click())
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
        onView(withId(R.id.confirm_sale_button)).perform(scrollTo(), click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }


    @Test
    fun navBarSales() {
        Espresso.onView(ViewMatchers.withId(R.id.sales)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarBooks() {
        Espresso.onView(ViewMatchers.withId(R.id.books)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(ListBooksActivity::class.java.name))
    }

    @Test
    fun navBarDefault() {
        onView(withId(R.id.default_selected)).check(
            matches(
                withEffectiveVisibility(
                    Visibility.GONE
                )
            )
        )
        onView(withId(R.id.default_selected))
            .check(matches(Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.default_selected))
            .check(matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(MainActivity::class.java.name))
    }
}
