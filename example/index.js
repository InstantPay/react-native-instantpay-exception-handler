import { AppRegistry } from 'react-native';
import App from './src/App';
import { name as appName } from './app.json';
import RNException from 'react-native-instantpay-exception-handler';
import { Alert, BackHandler } from 'react-native'

const reporter = (error) => {
    // Logic for reporting to devs
    // Example : Log issues to github issues using github apis.
    console.log('reporter :',error); // sample
};

const customErrorHandler = (error, isFatal) => {
    // Logic for reporting to devs
    // Example : Log issues to github issues using github apis.
    console.log(error, isFatal); // example
};

const previousErrorHandler = RNException.getJSExceptionHandler(); // by default u will get the red screen error handler here

const errorHandler = (e, isFatal) => {
    if (isFatal) {
        reporter(e);
        Alert.alert(
            'Unexpected error occurred',
            `
            Error: ${(isFatal) ? 'Fatal:' : ''} ${e.name} ${e.message}
    
            We have reported this to our team ! Please close the app and start again!
            `,
            [{
                text: 'Close',
                onPress: () => {
                    BackHandler.exitApp()
                }
            }]
        );
        
    } else {
        console.log("errorHandler else :",e); // So that we can see it in the ADB logs in case of Android if needed
    }
};

RNException.setJSExceptionHandler(errorHandler, true);

const exceptionhandler = (exceptionString) => {
    // your exception handler code here

    console.log('exceptionhandler :',exceptionString); // sample
};

RNException.setNativeExceptionHandler(
    exceptionhandler,
    true,
    false
);

AppRegistry.registerComponent(appName, () => App);
