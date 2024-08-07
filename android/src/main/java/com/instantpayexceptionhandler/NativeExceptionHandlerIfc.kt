package com.instantpayexceptionhandler

interface NativeExceptionHandlerIfc {
    fun handleNativeException(
        thread: Thread,
        throwable: Throwable,
        originalHandler: Thread.UncaughtExceptionHandler
    )
}
