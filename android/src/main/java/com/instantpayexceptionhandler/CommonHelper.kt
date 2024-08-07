package com.instantpayexceptionhandler

import android.util.Log

object CommonHelper {

    const val LOG_TAG = "IpayExceptionLog*"

    /**
     * For Show Log
     */
    fun logPrint(value: String?) {
        if (value == null) {
            return
        }
        Log.i(LOG_TAG, value)
    }
}
