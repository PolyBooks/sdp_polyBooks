package com.github.polybooks

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.polybooks.camera.BarcodeAnalyzer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)

class ScanBarcodeActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ScanBarcodeActivity::class.java)
    
    @Before
    fun before() {
        Intents.init()
    }

    @After
    fun after() {
        Intents.release()
    }


    @Test
    fun passISBNFunctionRedirects() {
        /*
        val barcodeAnalyzer: BarcodeAnalyzer = BarcodeAnalyzer(Executors.newSingleThreadExecutor(), ScanBarcodeActivity)
        barcodeAnalyzer.scanBarcodes() // how to pass a trial imageProxy


        // TODO could check that the ISBN is correctly passed too
        intended(hasComponent(FillSaleActivity::class.java.name))

         */
    }
}