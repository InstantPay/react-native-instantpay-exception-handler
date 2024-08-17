package com.instantpayexceptionhandler

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class DefaultErrorScreen : AppCompatActivity() {

    private var threadId: Long = 0
    private lateinit var throwable: Throwable

    companion object {

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        threadId = intent.getLongExtra("thread_id", 0)
        throwable = intent.getSerializableExtra("throwable") as Throwable

        val errorTitle = getText(R.string.error_title)
        val errorReason = throwable.localizedMessage
        val causeTitle = getText(R.string.cause_title)
        val causeReason = throwable.cause ?: "-"

        AlertDialog.Builder(this)
            .setIcon(R.drawable.baseline_error_outline_24)
            .setTitle("Unexpected Error")
            .setMessage("${errorTitle} ${errorReason}\n${causeTitle} ${causeReason}")
            .setPositiveButton("Report") { dialog, _ ->
                dialog.dismiss()
                closeApp()
            }
            .setNegativeButton("Close") { dialog, _ ->
                dialog.dismiss()
                closeApp()
            }
            .setCancelable(false)
            .show()

    }

    private fun restartApp() {
        //passExceptionToDefaultHandler()
        /*val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        android.os.Process.killProcess(android.os.Process.myPid()) */// Close the current process
    }

    private fun closeApp() {
        passExceptionToDefaultHandler()
        android.os.Process.killProcess(android.os.Process.myPid()) // Close the current process
    }

    private fun passExceptionToDefaultHandler() {
        InstantpayExceptionHandlerModule.defaultUncaughtExceptionHandler?.uncaughtException(InstantpayExceptionHandlerModule.systemThread, throwable)
    }

}
