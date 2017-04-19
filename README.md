[![](https://jitpack.io/v/insanediv/SMP3.0ConnectionLib.svg)](https://jitpack.io/#insanediv/SMP3.0ConnectionLib)
[![Jenkins](https://img.shields.io/jenkins/s/https/jenkins.qa.ubuntu.com/view/Precise/view/All%20Precise/job/precise-desktop-amd64_default.svg)]()
[![Github Releases](https://img.shields.io/badge/awesome-project-ff69b4.svg)]()

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
	        compile 'com.github.insanediv:SMP3.0ConnectionLib:1.0.9'
	}
```

## Get a SMP registration token
```java
	  // Reference to context
        Context context = this;
        // Referencce to Ion for networking
        Ion ion = Ion.getDefault(context);

        // http(s)://<smp_host>:<port>
        String smpServiceRoot = "https://smp.host.org";
        // Application connectionid as defined in the SMP Cockpit
        String appid = "appid";
        // Ignore cookies during connection id retrieval
        boolean ignoreCookies = true;
        
        // Credentials
        String username = "username";
        String password = "password";


        try {
            SmpIntegration smpIntegration = new SmpIntegration(this, Ion.getDefault(this),
                    smpServiceRoot, appid, ignoreCookies);
	
	    // Handle connection events
            smpIntegration.setDelegate(new SmpConnectionEventsDelegate() {
                @Override
                public void onLoginError(Exception e, Response<String> result) {
                    // The server raised authentication error HTTP-401 Unauthorized
                }

                @Override
                public void onRegistrationError(Exception e, Response<String> result) {
                     // The server raised error during the registration phase
                }

                @Override
                public void onConnectionSuccess(String xsmpappcid) {
                    // You got a registration token!!
		    // You are better to store this for further API calls
                }

                @Override
                public void onNetworkError(Exception e, Response<String> result) {
                    // A network error happened. Use parameters "e" and "result" to get more details
                }
            });

            // Starts connection attempt and  x-smp-app-cid retrieval. Events are back reported to the delegate
            smpIntegration.connect(username, password);
        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
        }
```
## Invoke an SMP OData endpoint and parse the result with GSON
```java
	          // Reference to context
        Context context = this;

        // http(s)://<smp_host>:<port>
        String smpOdataEndpoint = "https://smp.host.org/appcid/ODataCollectionEndpoint";

        // Credentials
        String username = "username";
        String password = "password";

        // Previously stored during connection phase
        String xsmpappcid = "xxxx-xxxx-xxxx-xxxx";

        // Ignore cookies during connection id retrieval
        try {
            ODataHttpClient oDataHttpClient = new ODataHttpClient(context, xsmpappcid,
                    username, password);
            oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
                @Override
                public void onErrorCallback(Exception ex, Response response) {
                    // A network error happened. Use parameters "ex" and "response" to get more details
                }

                @Override
                public void onFetchEntitySuccessCallback(Object result) {
                    // Callback invoked for single object retrieval
                    // Currently unused
                }

                @Override
                public void onFetchEntitySetSuccessCallback(List result) {
                    // Callback invoked for object collection retrieval
                }
            });
            
            // GSON will inject json to a collection of PojoClass instances
            Type type = new TypeToken<ArrayList<PojoClass>>() {}.getType() {};
	    // If everything is ok you would get a List<PojoClass> in the delegate method
	    // onFetchEntitySetSuccessCallback
            oDataHttpClient.fetchODataEntitySet(smpOdataEndpoint, type);

        } catch (SmpExceptionInvalidInput ex) {
            // INVALID PARAMETERS ARE PROVIDED
            ex.printStackTrace();
        }
```
