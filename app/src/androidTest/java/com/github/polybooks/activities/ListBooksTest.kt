package com.github.polybooks.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.database.Database
import com.github.polybooks.database.FirebaseProvider
import com.github.polybooks.database.TestBookProvider
import com.google.firebase.Timestamp
import com.schibsted.spain.barista.assertion.BaristaListAssertions
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.concurrent.CompletableFuture


class ListBooksTest {

    class BookActivityRule: TestWatcher() {
        private val bookDB = Database.bookDatabase(ApplicationProvider.getApplicationContext())
        private lateinit var a : ActivityScenario<ListBooksActivity>
        override fun starting(description: Description?) {
            TestBookProvider.books.values.forEach { book -> bookDB.addBook(book).get() }
            a = ActivityScenario.launch(ListBooksActivity::class.java)
        }

        override fun finished(description: Description?) {
            a.close()
        }
    }


    @Rule @JvmField
    var bookrule = BookActivityRule()

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun BooksAreCorrect(){
        Thread.sleep(2000)
        BaristaListAssertions.assertListItemCount(R.id.recyclerView, 3)
        BaristaListAssertions.assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Tartuffe, by Moliere")
        BaristaListAssertions.assertDisplayedAtPosition(R.id.recyclerView,0,R.id.text_view_author,"Molière")
        BaristaListAssertions.assertCustomAssertionAtPosition(R.id.recyclerView,0,R.id.text_view_edition,ViewAssertions.matches(Matchers.not(isDisplayed())))
    }

    @Test
    fun filterButton() {
        onView(withId(R.id.filter_button)).perform(click())
        intended(hasComponent(FilteringBooksActivity::class.java.name))
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
        onView(withId(R.id.books))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarDefault() {
        onView(withId(R.id.default_selected)).check(
            ViewAssertions.matches(
                withEffectiveVisibility(
                    Visibility.GONE
                )
            )
        )
        onView(withId(R.id.default_selected))
            .check(ViewAssertions.matches(Matchers.not(isEnabled())))
    }

    @Test
    fun navBarSelected() {
        onView(withId(R.id.books))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        intended(hasComponent(MainActivity::class.java.name))
    }
}