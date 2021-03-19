package com.github.polybooks

import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaListAssertions.assertDisplayedAtPosition
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Rule
import org.junit.Test

class ListSalesTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(ListSalesActivity::class.java)

    @Test
    fun countIsCorrect() {
        assertRecyclerViewItemCount(R.id.recyclerView, 1)
        Thread.sleep(1500)
        assertRecyclerViewItemCount(R.id.recyclerView, 7)
    }

    @Test
    fun ItemsAreCorrect() {
        assertDisplayed("Book1")
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.BookName,"Book1");
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.SalePrice,"23.00");
        Thread.sleep(1500)
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.BookName,"Book1");
        assertDisplayedAtPosition(R.id.recyclerView, 0, R.id.SalePrice,"23.00");
        assertDisplayedAtPosition(R.id.recyclerView, 4, R.id.BookName,"Book6");
        assertDisplayedAtPosition(R.id.recyclerView, 6, R.id.SalePrice,"23.66");
    }

}