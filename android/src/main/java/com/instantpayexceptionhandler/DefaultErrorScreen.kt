package com.instantpayexceptionhandler

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DefaultErrorScreen : AppCompatActivity() {

    private lateinit var quitButton: Button
    private lateinit var relaunchButton: Button
    private lateinit var showDetailsButton: Button
    private lateinit var stackTraceView: TextView

    companion object {

        fun doRestart(context: Context){

            try {

                if (context != null) {

                    val packageManage = context.packageManager

                    if(packageManage != null){

                        val startActivityIntent = packageManage.getLaunchIntentForPackage(context.packageName)

                        if(startActivityIntent !=null){

                            startActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                            val mPendingIntentId = 654311

                            val pendingIntent = PendingIntent.getActivity(context, mPendingIntentId, startActivityIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE)

                            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent)

                            System.exit(0)
                        }
                        else{
                            throw Exception("Was not able to restart application, startActivityIntent null")
                        }
                    }
                    else{
                        throw Exception("Was not able to restart application, packageManage null")
                    }
                }
                else{

                    throw Exception("Was not able to restart application, Context null")
                }
            }
            catch (e:Exception){

                CommonHelper.logPrint("Was not able to restart application")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var stackTraceString = "StackTrace unavailable"

        try {
            stackTraceString = intent.extras?.getString("stack_trace_string").toString()
        }
        catch (e:Exception){
            CommonHelper.logPrint("Was not able to get StackTrace: ${e.message}")
        }

        setContentView(R.layout.activity_default_error_screen)

        quitButton = findViewById(R.id.eh_quit_button)

        relaunchButton = findViewById(R.id.eh_restart_button)

        showDetailsButton = findViewById(R.id.eh_show_details_button)

        stackTraceView = findViewById(R.id.eh_stack_trace_text_view)

        stackTraceView.text = stackTraceString

        showDetailsButton.setOnClickListener {

            val stackTraceViewVisibility = stackTraceView.visibility

            if(stackTraceViewVisibility == View.VISIBLE){
                stackTraceView.visibility = View.GONE

                showDetailsButton.text = "SHOW DETAILS"
            }
            else{
                stackTraceView.visibility = View.VISIBLE

                showDetailsButton.text = "HIDE DETAILS"
            }
        }

        relaunchButton.setOnClickListener {
            doRestart(applicationContext)
        }

        quitButton.setOnClickListener {
            System.exit(0)
        }

    }

}
