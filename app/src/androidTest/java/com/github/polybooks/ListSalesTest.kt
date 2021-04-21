package com.github.polybooks

import android.content.Intent
import android.os.Bundle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.github.polybooks.core.Sale
import com.github.polybooks.core.database.implementation.SaleDatabase
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit


class ListSalesTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListSalesActivity::class.java)
    

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
    fun ItemsAreCorrect() {
        assertDisplayed("Book1")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Book1");
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_price, "23.00");
        sleep(4, TimeUnit.SECONDS)
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_title, "Book1");
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.text_view_price, "23.00");
        assertDisplayedAtPosition(R.id.recyclerView, 4, R.id.text_view_title, "Book6");
        assertDisplayedAtPosition(R.id.recyclerView, 6, R.id.text_view_price, "23.66");
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

}