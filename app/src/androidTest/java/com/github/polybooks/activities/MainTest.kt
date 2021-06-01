package com.github.polybooks.activities

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import org.hamcrest.Matchers.not
import org.junit.*


class MainTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)


    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Firebase.auth.currentUser?.delete() // Par securité
        Intents.release()
    }

    //This one
    @Test
    fun allButtonsClickable() {
        onView(withId(R.id.log_button)).check(matches(isDisplayed()))
        onView(withId(R.id.log_button)).check(matches(isClickable()))

        onView(withId(R.id.signup_button)).check(matches(isDisplayed()))
        onView(withId(R.id.signup_button)).check(matches(isClickable()))

        onView(withId(R.id.sell_button)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        
        onView(withId(R.id.view_books_button)).check(matches(isDisplayed()))
        onView(withId(R.id.view_books_button)).check(matches(isClickable()))

    }

    @Test
    fun loginButton() {
        onView(withId(R.id.log_button)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun sellButton() {

        onView(withId(R.id.signup_button)).perform(click())
        onView(withId(R.id.username_field)).perform(
            scrollTo(),
            ViewActions.typeText("TestTestTest"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.email_field)).perform(
            scrollTo(),
            ViewActions.typeText("test@test.test"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password1_field)).perform(
            scrollTo(),
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.password2_field)).perform(
            scrollTo(),
            ViewActions.typeText("123456"),
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.button_reg)).perform(scrollTo(), click())
        Thread.sleep(1500)
        onView(withId(R.id.home)).perform(click())

        onView(withId(R.id.signup_button)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.log_button)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(R.id.sell_button)).perform(click())
        Firebase.auth.currentUser.delete()

    }

    @Test

    fun allBooksButton() {
        onView(withId(R.id.view_books_button)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
    }

    @Test
    fun listBookButton() {

        onView(withId(R.id.view_books_button)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
    }

    //This one
    @Test
    fun signUpButton() {
        onView(withId(R.id.signup_button)).perform(click())
        intended(hasComponent(RegisterActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        intended(hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        intended(hasComponent(ListBooksActivity::class.java.name))
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
        onView(withId(R.id.view_books_button)).check(matches(isDisplayed()))
    }

}