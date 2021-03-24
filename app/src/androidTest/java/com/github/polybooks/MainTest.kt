package com.github.polybooks

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.schibsted.spain.barista.assertion.BaristaClickableAssertions.assertClickable
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.concurrent.TimeUnit


class MainTest {
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    /*
    @Before
    fun before() {
        BaristaSleepInteractions.sleep(1, TimeUnit.SECONDS)
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }
*/
    //This one
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
/*
    @Test
    fun loginButton() {
        Intents.init()
        clickOn(R.id.log_button)
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun sellButton() {
        Intents.init()
        clickOn(R.id.sell_button)
        Intents.intended(IntentMatchers.hasComponent(AddSaleActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun databaseButton() {
        Intents.init()
        clickOn(R.id.button_open_db_tests)
        Intents.intended(IntentMatchers.hasComponent(ListSalesActivity::class.java.name))
        Intents.release()
    }

    //This one
    @Test
    fun signUpButton() {
        clickOn(R.id.signup_button)
        assertDisplayed(R.id.register_button)
    }
*/
}