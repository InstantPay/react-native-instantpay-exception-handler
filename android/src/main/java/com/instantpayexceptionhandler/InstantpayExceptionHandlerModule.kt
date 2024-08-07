package com.instantpayexceptionhandler

import android.content.Intent
import android.util.Log
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.WritableMap
import kotlin.reflect.KClass

class InstantpayExceptionHandlerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    val SUCCESS: String = "SUCCESS"
    val FAILED: String = "FAILED"
    lateinit var DATA: String
    private var responsePromise: Promise? = null
    private var callBackHolder: Callback? = null
    private var originalHandler: Thread.UncaughtExceptionHandler? = null


    companion object {
        const val NAME = "InstantpayExceptionHandler"

        private lateinit var nativeExceptionHandler: NativeExceptionHandlerIfc

        private var errorIntentTargetClass = DefaultErrorScreen.javaClass::class

        fun replaceErrorScreenActivityClass(errorScreenActivityClass: KClass<*>) {
            //errorIntentTargetClass = errorScreenActivityClass
        }

        fun setNativeExceptionHandler(nativeExceptionHandler: NativeExceptionHandlerIfc){
            InstantpayExceptionHandlerModule.nativeExceptionHandler = nativeExceptionHandler
        }
    }

    override fun getName(): String {
        return NAME
    }


    @ReactMethod
    fun multiply(a: Double, b: Double, promise: Promise) {
        promise.resolve(a * b)
    }

    @ReactMethod
    fun setHandlerforNativeException(executeOriginalUncaughtExceptionHandler: Boolean, forceToQuit: Boolean, customHandler: Callback){

        callBackHolder = customHandler

        originalHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler(){ thread: Thread, throwable: Throwable ->

            val stackTraceString = Log.getStackTraceString(throwable)
            callBackHolder?.invoke(stackTraceString)

            if(nativeExceptionHandler!=null){
                nativeExceptionHandler.handleNativeException(thread, throwable, originalHandler!!)
            }
            else{
                val activity = currentActivity

                val intent = Intent();

                intent.setClass(activity!!, errorIntentTargetClass.java)

                intent.putExtra("stack_trace_string", stackTraceString)

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                activity.startActivity(intent)

                activity.finish()

                if(executeOriginalUncaughtExceptionHandler && originalHandler != null){
                    originalHandler?.uncaughtException(thread, throwable)
                }

                if(forceToQuit){
                    System.exit(0)
                }
            }
        }
    }

    /**
     * Help to return data to React native
     */
    private fun resolve(message: String, status: String = FAILED, data: WritableMap? = null, actCode: String = "") {

        if (responsePromise == null) {
            return;
        }

        val map: WritableMap = Arguments.createMap()
        map.putString("status", status)
        map.putString("message", message)

        if(data != null){
            map.putMap("data", data)
        }
        else{
            map.putString("data", "")
        }

        responsePromise!!.resolve(map)
        responsePromise = null
    }
}
