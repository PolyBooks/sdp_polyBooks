package com.github.polybooks

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import org.junit.After
import org.junit.Before
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