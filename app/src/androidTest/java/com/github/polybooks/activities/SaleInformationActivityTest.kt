package com.github.polybooks.activities

import android.content.Intent
import android.widget.RatingBar
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.polybooks.R
import com.github.polybooks.core.*
import com.github.polybooks.utils.StringsManip
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import junit.framework.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SaleInformationActivityTest {
    private val dummySaleTartuffe: Sale = Sale(
        Book(
            "isbn102",
            listOf("Molière"),
            "Le Tartuffe",
            "Edition pré-censurée",
            "French",
            "Editions De l'Aire",
            null,
            "pocket format"
        ),
        LocalUser,
        33.5f,
        BookCondition.GOOD,
        null,
        SaleState.ACTIVE,
        null
    )

    private val dummySaleRandom: Sale = Sale(
        Book(
            "isbnRandom",
            listOf("Molière", "Beethoven", "Alice de la Compta"),
            "DCAP",
            "Edition v.4.2.1",
            "Frenglish",
            "Editions De l'Aire",
            null,
            "pocket format",
            9.0,
            2
        ),
        LocalUser,
        37.57f,
        BookCondition.NEW,
        null,
        SaleState.RETRACTED,
        null
    )

    val intent: Intent = Intent(ApplicationProvider.getApplicationContext(), SaleInformationActivity::class.java)
        .putExtra(SaleInformationActivity.EXTRA_SALE_INFORMATION, dummySaleTartuffe)

    @get:Rule
    val activityRule = ActivityScenarioRule<SaleInformationActivity>(intent)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    private fun assertEverythingDisplayed(expected: Sale) {
        fun expectedDisplayed(expectedNullable: String?): String {
            return expectedNullable ?: "null"
        }

        // Static
        assertDisplayed(R.id.sale_information_value_by_1, R.string.by)
        assertDisplayed(R.id.sale_information_value_in, R.string.value_in)
        assertDisplayed(R.id.sale_information_value_condition, R.string.sale_book_condition)
        assertDisplayed(R.id.sale_information_value_price, R.string.sale_price)
        assertDisplayed(R.id.sale_information_value_currency, R.string.currency)

        // Dynamic
        assertDisplayed(R.id.sale_information_title, expected.book.title)
        assertDisplayed(R.id.sale_information_edition, expectedDisplayed(expected.book.edition))
        assertDisplayed(R.id.sale_information_authors, StringsManip.listAuthorsToString(expected.book.authors))
        assertDisplayed(R.id.sale_information_book_format, expectedDisplayed(expected.book.format))
        assertDisplayed(R.id.sale_information_condition, expected.condition.name)
        assertDisplayed(R.id.sale_information_price, expected.price.toString())
    }

    @Test
    fun t_assertAllInformationDisplayed() {
        assertEverythingDisplayed(dummySaleTartuffe)
    }
}