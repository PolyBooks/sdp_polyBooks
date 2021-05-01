package com.github.polybooks

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.hamcrest.Matchers
import org.junit.*
import java.util.concurrent.TimeUnit


class ListSalesTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListSalesActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }
    //This one
    /*
    @Test
    fun countIsCorrect() {
        assertRecyclerViewItemCount(R.id.recyclerView, 1)
        sleep(4, TimeUnit.SECONDS)
        assertRecyclerViewItemCount(R.id.recyclerView, 7)
    }
    */

    // FIXME waiting for PR #101 to be merged. Adapting the tests using Dummy seem to take way to much time for a temporary thing

    @Ignore
    @Test
    fun itemsAreCorrect() {
        assertDisplayed("Book1")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Book1")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_price, "23.00")
        sleep(4, TimeUnit.SECONDS)
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Book1")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_price, "23.00")
        assertDisplayedAtPosition(R.id.recyclerView, 4, R.id.text_view_title, "Book6")
        assertDisplayedAtPosition(R.id.recyclerView, 6, R.id.text_view_price, "23.66")
    }

//    @Test
//    fun ItemsAreCorrect() {
//        assertDisplayed("Book1")
//        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.BookName, "Book1");
//        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.SalePrice, "23.00");
//        sleep(4, TimeUnit.SECONDS)
//        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.BookName, "Book1");
//        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.SalePrice, "23.00");
//        assertDisplayedAtPosition(R.id.recyclerView, 4, R.id.BookName, "Book6");
//        assertDisplayedAtPosition(R.id.recyclerView, 6, R.id.SalePrice, "23.66");
//    }

    @Test
    fun navBarSales() {
        Espresso.onView(ViewMatchers.withId(R.id.sales)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarProfile() {
        Espresso.onView(ViewMatchers.withId(R.id.user_profile)).perform(ViewActions.click())
        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
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