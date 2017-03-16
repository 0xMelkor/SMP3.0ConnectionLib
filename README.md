[![](https://jitpack.io/v/insanediv/SMP3.0ConnectionLib.svg)](https://jitpack.io/#insanediv/SMP3.0ConnectionLib)

# SMP3.0 Connection Library
This project aims at providing a simple set of instruments to cope with SAP Mobile Platform (SMP3.0) login and user onboarding process.
This library is intended as a lightweight alternative to the heavy [SAP MAFLogon framework](https://github.com/SAP/sap_mobile_native_android).

You can include this library as a gradle dependency as follows.

**Add it in your root build.gradle at the end of repositories:**
```groovy
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
**Add the dependency to your module build.gradle file**
```groovy
	dependencies {
	        compile 'com.github.insanediv:SMP3.0ConnectionLib:1.0.1'
	}
```

## Basic usage
```java
	SmpConnection smpConnection = new SmpConnection();
	smpConnection.with(context);
	smpConnection.setUserCredentials("<username>","<password>");
        smpConnection.setSmpEndpoint("http(s)://<host>:<port>", "<your appid>");
        smpConnection.setDelegate(new  SmpConnectionEventsDelegate {
   		 void onCredentialsRequired(){
		 	//Your magic happens here..
		 }
    		 void onLoginError(Exception e, Response<String> result){
		 	//Your magic happens here..
		 }
    		 void onRegistrationError(Exception e, Response<String> result){
		 	//Your magic happens here..
		 }
 	         void onConnectionSuccess(){
		 	//Your magic happens here..
		 }
});
```
