package stacksmashers.smp30connectionlib.smp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.builder.Builders;
import com.koushikdutta.ion.builder.LoadBuilder;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import stacksmashers.smp30connectionlib.delegate.SmpConnectionEventsDelegate;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 13/04/2017  *******
 ***************************/

public class SmpIntegration {

    private Context context;
    private Ion ion;
    private SmpConnectionEventsDelegate delegate;

    private String smpServiceRoot;
    private String appid;
    private boolean ignoreCookies = false;
    private boolean testModeEnabled = false;

    public SmpIntegration(@NonNull Context context, @NonNull Ion ion,
                          @NonNull String smpServiceRoot, @NonNull String appid,
                          boolean ignoreCookies) throws SmpExceptionInvalidInput {

        validateInput(context, ion, smpServiceRoot, appid);

        this.context = context;
        this.ion = ion;
        this.smpServiceRoot = smpServiceRoot;
        this.appid = appid;
        this.ignoreCookies = ignoreCookies;
    }

    public void setDelegate(@NonNull SmpConnectionEventsDelegate delegate)
            throws SmpExceptionInvalidInput {
        if (delegate == null)
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        else
            this.delegate = delegate;
    }

    public void setTestModeEnabled(boolean testModeEnabled) {
        this.testModeEnabled = testModeEnabled;
    }

    private void validateInput(@NonNull Context context, @NonNull Ion ion,
                               @NonNull String smpServiceRoot, @NonNull String appid)
            throws SmpExceptionInvalidInput {

        // URL Validation
        try {
            new URL(smpServiceRoot);
        } catch (MalformedURLException e) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_MALFORMED_SMP_SERVICE_URL);
        }

        // Appid validation
        if (appid.trim().equals("")) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_INVALID_APPID);
        }

        // Dependencies validation
        if (smpServiceRoot == null || appid == null || context == null || ion == null) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        }

    }

    /**
     * Clear cookie cache before connecting
     */
    public void setIgnoreCookies(boolean value) {
        this.ignoreCookies = value;
    }

    /**
     * Attempts connection to the smp endpoint
     *
     * @param username username to build basic authentication header
     * @param password password to build basic authentication header
     */
    public void connect(String username, String password) throws SmpExceptionInvalidInput {
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password))
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);

        doLogin(username, password);
    }


    private void doLogin(final String username, final String password) {

        if (this.ignoreCookies) {
            IonResponseManager.clearCookies(this.ion);
        }

        String url = this.smpServiceRoot + "/odata/applications/latest/" + this.appid;

        // Get Ion Builder reference
        Builders.Any.B builder = this.ion.build(this.context).load("GET", url);

        if (testModeEnabled) {
            // need to null out the handler since the semaphore blocks the main thread,
            // and ion's default behavior is to post back onto the main thread or calling Handler.
            builder.setHandler(null);
        }

        builder.basicAuthentication(username, password)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            new IonResponseManager(e, result);

                            // Il login è andato a buon fine (non ho eccezione da IonResponseManager)
                            // Se il server mi ha restituito il connectionID in un cookie allora sono già registrato
                            // Altrimenti effettuo la registrazione
                            String x_smp_appcid = IonResponseManager.getSMPCookie(ion, new URI(smpServiceRoot));
                            if (!x_smp_appcid.equals("")) {
                                onConnectionSuccess(x_smp_appcid);
                            } else {
                                registerDevice(username, password);
                            }

                        } catch (Exception e1) {
                            if (result != null && result.getHeaders() != null && result.getHeaders().code() == 401) {
                                onLoginError(e, result);
                            } else {
                                onNetworkError(e, result);
                            }
                        }
                    }
                });
    }


    private void registerDevice(String username, String password) {

        String REGISTRATION_XML_BODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
                "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" " +
                "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\">" +
                "<content type=\"application/xml\">" +
                "<m:properties><d:DeviceType>Android</d:DeviceType></m:properties>" +
                "</content></entry>";

        if (this.ignoreCookies) {
            IonResponseManager.clearCookies(ion);
        }

        ion.build(context)
                .load("POST", smpServiceRoot + "/odata/applications/latest/" + appid + "/Connections")
                .addHeader("Content-type", "application/xml")
                .basicAuthentication(username, password)
                .setStringBody(REGISTRATION_XML_BODY)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            new IonResponseManager(e, result);
                            String xsmpappcid = IonResponseManager.getConnectionIdFromResponse(result);
                            onConnectionSuccess(xsmpappcid);
                        } catch (Exception e1) {

                            if (result != null && result.getHeaders() != null && result.getHeaders().code() == 403) {
                                onRegistrationError(e, result);
                            } else {
                                onNetworkError(e, result);
                            }
                        }
                    }
                });
    }


    ///////////////////////////////////
    /////// Delegate callbacks ///////
    /////////////////////////////////

    private void onConnectionSuccess(String xsmpappcid) {
        if (delegate != null) {
            delegate.onConnectionSuccess(xsmpappcid);
        }
    }

    private void onLoginError(Exception e, Response<String> result) {
        if (delegate != null) {
            delegate.onLoginError(e, result);
        }
    }

    private void onNetworkError(Exception e, Response<String> result) {
        if (delegate != null) {
            delegate.onNetworkError(e, result);
        }
    }

    private void onRegistrationError(Exception e, Response<String> result) {
        if (delegate != null) {
            delegate.onRegistrationError(e, result);
        }
    }

}
