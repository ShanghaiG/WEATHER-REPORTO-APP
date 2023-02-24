package com.example.weatherapp1.helpers

import android.app.Dialog
import android.content.Context
import com.example.weatherapp1.R

class ProgressDialog(private val context: Context) {

    private var mProgressDialog: Dialog? = null

    public fun showCustomProgressDialog() {
        mProgressDialog = Dialog(context)

        /**
         * Set the screen content from a layout resource.
         * The resource will be inflated, adding all top-level views to the screen
         */
        mProgressDialog!!.setContentView(R.layout.dialog_custom_progress)

        /** Start the dialog and display it */
        mProgressDialog!!.show()
    }

    public fun hideCustomProgressDialog() {
        if(mProgressDialog != null) {
            /** dismiss() function here closes dialog */
            mProgressDialog!!.dismiss()
        }
    }
}