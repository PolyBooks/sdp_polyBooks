package com.github.polybooks

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class MainTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }

    @Test
    fun allButtonsClickable() {
        assertDisplayed(R.id.log_button)
        assertClickable(R.id.log_button)
        assertDisplayed(R.id.signup_button)
        assertClickable(R.id.signup_button)
        assertDisplayed(R.id.sell_button)
        assertClickable(R.id.sell_button)
        assertDisplayed(R.id.button_open_db_tests)
        assertClickable(R.id.button_open_db_tests)
    }
 
    @Test
    fun loginButton() {
        clickOn(R.id.log_button)
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }

    @Test
    fun sellButton() {
        clickOn(R.id.sell_button)
        Intents.intended(IntentMatchers.hasComponent(AddSaleActivity::class.java.name))
    }

    @Test
    fun databaseButton() {
        clickOn(R.id.button_open_db_tests)
        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
    }

    @Test
    fun signUpButton() {
        clickOn(R.id.signup_button)
        assertDisplayed(R.id.register_button)
    }

}