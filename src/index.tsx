import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-instantpay-exception-handler' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const InstantpayExceptionHandler = NativeModules.InstantpayExceptionHandler
    ? NativeModules.InstantpayExceptionHandler
    : new Proxy(
        {},
        {
            get() {
            throw new Error(LINKING_ERROR);
            },
        }
    );

const noop = () => { };

var global:any = global;

const RNException = {
    setJSExceptionHandler : (customHandler = noop, allowedInDevMode = false) => {

        if (typeof allowedInDevMode !== "boolean" || typeof customHandler !== "function") {
            console.log("setJSExceptionHandler is called with wrong argument types.. first argument should be callback function and second argument is optional should be a boolean");
            console.log("Not setting the JS handler .. please fix setJSExceptionHandler call");
            return;
        }

        const allowed = allowedInDevMode ? true : !__DEV__;
        if (allowed) {
            global.ErrorUtils.setGlobalHandler(customHandler);
            const consoleError = console.error;
            console.error = (...args) => {
                global.ErrorUtils.reportError(...args);
                consoleError(...args);
            };
        } else {
            console.log("Skipping setJSExceptionHandler: Reason: In DEV mode and allowedInDevMode = false");
        }
    },
    getJSExceptionHandler : () => {
        return global.ErrorUtils.getGlobalHandler();
    },
    setNativeExceptionHandler : (customErrorHandler = noop, forceApplicationToQuit = true, executeDefaultHandler = false) => {

        if (typeof customErrorHandler !== "function" || typeof forceApplicationToQuit !== "boolean") {
            console.log("setNativeExceptionHandler is called with wrong argument types.. first argument should be callback function and second argument is optional should be a boolean");
            console.log("Not setting the native handler .. please fix setNativeExceptionHandler call");
            return;
        }

        if (Platform.OS === "ios") {
            InstantpayExceptionHandler.setHandlerforNativeException(executeDefaultHandler, customErrorHandler);
        } else {
            InstantpayExceptionHandler.setHandlerforNativeException(executeDefaultHandler, forceApplicationToQuit, customErrorHandler);
        }
    }
}

export default RNException;
