package com.github.polybooks.utils

import android.content.Context
import android.widget.Button
import androidx.core.content.ContextCompat
import com.github.polybooks.R

object UIManip {
    /**
     * disableButton allows a button to not be clickable and change its appearance to grey
     * To be called whenever fields are missing
     */
    fun disableButton(button: Button, applicationContext: Context) {
        button.isEnabled = false
        button.isClickable = false
        button.setTextColor(ContextCompat.getColor(applicationContext, R.color.white))
        button.setBackgroundColor(ContextCompat.getColor(applicationContext, R.color.grey))
    }

    /**
     * enableButton allows a button to be clickable and change its appearance to active
     * To be called once all the fields have been set
     */
    fun enableButton(button: Button, applicationContext: Context) {
        button.isEnabled = true
        button.isClickable = true
        button.setTextColor(ContextCompat.getColor(applicationContext, R.color.black))
        button.setBackgroundColor(
            ContextCompat.getColor(
                applicationContext,
                R.color.blue_green_400
            )
        )
    }
}