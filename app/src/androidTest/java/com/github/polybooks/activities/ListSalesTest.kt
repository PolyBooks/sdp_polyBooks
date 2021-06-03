package com.github.polybooks.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.*

import androidx.test.espresso.matcher.ViewMatchers.*
import com.github.polybooks.R
import com.github.polybooks.activities.SaleInformationActivity.Companion.EXTRA_SALE_INFORMATION
import com.github.polybooks.core.*
import com.github.polybooks.database.Database
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertCustomAssertionAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertListItemCount
import com.schibsted.spain.barista.interaction.BaristaListInteractions.clickListItem
import org.hamcrest.CoreMatchers
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*


class ListSalesTest {
    private val saleDB = Database.saleDatabase


    class SaleActivityRule: TestWatcher() {

        private val testUser = LoggedUser("301966", "Le givre")
        private val testBook =
            Book("9780156881807", listOf("Molière"), "Tartuffe, by Moliere", null, null, null, null, null)

        private val dummySale: Sale = Sale(
            testBook,
            testUser,
            500f,
            BookCondition.WORN,
            Date(),
            SaleState.ACTIVE,
            null
        )

        private lateinit var a :ActivityScenario<ListSalesActivity>
        override fun starting(description: Description?) {
            Database.saleDatabase.addSale(dummySale).get()
            a = ActivityScenario.launch(ListSalesActivity::class.java)
        }

        override fun finished(description: Description?) {
            a.close()
        }
    }


    @Rule @JvmField
    var salerule = SaleActivityRule()

    @Before
    fun before() {
        saleDB.listAllSales().thenApply { list -> list.forEach{sale -> saleDB.deleteSale(sale)} }.get()
        Intents.init()
    }

    @After
    fun after() {
        saleDB.listAllSales().thenApply { list -> list.forEach{sale -> saleDB.deleteSale(sale)} }.get()
        Intents.release()
    }

    @Test
    fun salesAreCorrect(){
        Thread.sleep(2000)
        assertListItemCount(R.id.recyclerView, 1)
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Tartuffe, by Moliere")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_condition, "WORN")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_price, "500.00")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_author, "Molière")
        assertCustomAssertionAtPosition(R.id.recyclerView, 0, R.id.text_view_edition, matches(not(isDisplayed())))
    }

    @Test
    fun clickOnSaleIsCorrect(){
        Thread.sleep(2000)
        clickListItem(R.id.recyclerView, 0)
        intended(
            allOf(
                hasComponent(SaleInformationActivity::class.java.name),
                hasExtraWithKey(EXTRA_SALE_INFORMATION)
            ) )

    }

    @Test
    fun filterButton() {
        onView(withId(R.id.filter_button)).perform(click())
        Intents.intended(hasComponent(FilteringSalesActivity::class.java.name))
    }

    @Test
    fun navBarSales() {
        onView(withId(R.id.sales)).perform(click())
        onView(withId(R.id.sales))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarProfile() {
        onView(withId(R.id.user_profile)).perform(click())
        Intents.intended(hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun navBarBooks() {
        onView(withId(R.id.books)).perform(click())
        Intents.intended(hasComponent(ListBooksActivity::class.java.name))
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
        onView(withId(R.id.sales))
            .check(ViewAssertions.matches(isSelected()))
    }

    @Test
    fun navBarHome() {
        onView(withId(R.id.home)).perform(click())
        Intents.intended(hasComponent(MainActivity::class.java.name))
    }
}