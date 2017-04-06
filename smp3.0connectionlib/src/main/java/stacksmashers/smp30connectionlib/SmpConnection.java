package stacksmashers.smp30connectionlib;

import android.content.Context;
import android.support.annotation.NonNull;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.net.URI;

import stacksmashers.smp30connectionlib.delegate.SmpConnectionEventsDelegate;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;
import stacksmashers.smp30connectionlib.netutil.IonSslUtil;
import stacksmashers.smp30connectionlib.sharedprefs.SharedPreferenceAdapter;


/**************************
 ** {__-StAcK_SmAsHeRs-__} **
 *** @author: a.simeoni *******
 *** @date: 10/03/2017  *******
 ***************************/

class UserCredentials {

    private String username;
    private String password;

    UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    String getPassword() {
        return password;
    }

    String getUsername() {
        return username;
    }

    boolean isValid() {
        return (username != null && password != null) &&
                (!username.equals("") && !password.equals(""));
    }
}

public class SmpConnection {

    private Context context;
    private String smpServiceRoot;
    private String appid;
    private SmpConnectionEventsDelegate delegate;
    private boolean ignoreCookies = false;

    public SmpConnection with(@NonNull Context context) {
        this.context = context;
        return this;
    }

    /**
     * @param smproot Endpoint OData dell'applicazione (es. https://swfmmobilepp.aceaspa.it)
     * @param appid   Id dell'app su SMP (es. swfm)
     */
    public SmpConnection setSmpEndpoint(@NonNull String smproot, @NonNull String appid) {
        this.smpServiceRoot = smproot;
        this.appid = appid;
        return this;
    }

    public SmpConnection setDelegate(SmpConnectionEventsDelegate delegate) {
        this.delegate = delegate;
        return this;
    }

    public SmpConnection setUserCredentials(@NonNull String username, @NonNull String password) {
        storeUserCredentials(username, password);
        return this;
    }

    public boolean isIgnoreCookies() {
        return ignoreCookies;
    }

    /**
     * Clear cookie cache before connecting
     */
    public SmpConnection setIgnoreCookies(boolean value) {
        this.ignoreCookies = value;
        return this;
    }

    /**
     * Si connette alla piattaforma secondo le seguenti regole:
     * 1. Se non ci sono credenziali specificate dall'utente o memorizzate localmente
     * richiama la callback {@link SmpConnectionEventsDelegate#onCredentialsRequired()} <br/>
     * 2. Se ci sono credenziali allora effettua il login sulla piattaforma. Possono succedere 2 cose:
     * <ul>
     * <li>Esiste un Connection ID tra i cookie locali</li>
     * <ul>
     * <li>Viene inviato alla SMP nella chiamata di login</li>
     * <li>Non deve esserci per forza un corrispettivo su SMP per l'app cid inviato.
     * Infatti il cid può essere generato anche dall'app</li>
     * <ul/>
     * <li>Non esiste un Connection ID tra i cookie locali:</li>
     * <ul>
     * <li>Dopo il login la SMP non rimbalza nessun cookie</li>
     * <li>Si effettua una chiamata di registrazione</li>
     * <li>Il connection ID viene estratto e memorizzato localmente per le chiamate ai servizi e memorizzato nei cookies</li>
     * <ul/>
     * <ul/>
     * <p>
     * Note sulla documentazione ufficiale
     *
     * @see <a href="https://help.sap.com/saphelp_smp304sdk/helpdata/en/7c/0aa27070061014bf49dc643a537fd6/content.htm"></a>
     */
    public void connect() {
        // Devono esserci delle credenziali, passate come parametro o memorizzate
        if (!getStoredCredentials().isValid()) {
            onCredentialsRequired();
            return;
        }
        doLogin();
    }

    /**
     * Clears stored credentials and X-SMP-APPCID token
     */
    public void clearCache() {
        SharedPreferenceAdapter.clearAll(context);
    }


    /**************************************/
    /********* Private methods ************/
    /**************************************/


    private void onCredentialsRequired() {
        if (delegate != null) {
            delegate.onCredentialsRequired();
        }
    }

    private void onConnectionSuccess() {
        if (delegate != null) {
            delegate.onConnectionSuccess();
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

    private void doLogin() {
        final Ion ion = getIonInstance();
        if (ignoreCookies) {
            IonResponseManager.clearCookies(ion);
        }
        ion.build(context)
                .load("GET", smpServiceRoot + "/odata/applications/latest/" + appid)
                .basicAuthentication(
                        getStoredCredentials().getUsername(),
                        getStoredCredentials().getPassword())
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
                                storeXSMPAppCid(x_smp_appcid);
                                onConnectionSuccess();
                            } else {
                                registerDevice();
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


    private void registerDevice() {

        final Ion ion = getIonInstance();

        String REGISTRATION_XML_BODY = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
                "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" " +
                "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\">" +
                "<content type=\"application/xml\">" +
                "<m:properties><d:DeviceType>Android</d:DeviceType></m:properties>" +
                "</content></entry>";

        ion.build(context)
                .load("POST", smpServiceRoot + "/odata/applications/latest/" + appid + "/Connections")
                .addHeader("Content-type", "application/xml")
                .basicAuthentication(
                        getStoredCredentials().getUsername(),
                        getStoredCredentials().getPassword())
                .setStringBody(REGISTRATION_XML_BODY)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            new IonResponseManager(e, result);
                            // Non ho errori, allora salvo il cookie X-SMP-APPCID nelle shared preferences
                            storeXSMPAppCid(IonResponseManager.getConnectionIdFromResponse(result));
                            onConnectionSuccess();
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

    public String getSmpServiceRoot() {
        return this.smpServiceRoot;
    }

    public String getAppId() {
        return this.appid;
    }

    public Ion getIonInstance() {

        Ion result;

        if (this.smpServiceRoot.startsWith("https") || this.smpServiceRoot.startsWith("HTTPS")) {
            // Devo ottenere istanza di Ion che gestisce SSL
            try {
                result = IonSslUtil.getIonHttpsInstance(context);
            } catch (Exception ex) {
                result = null;
            }
        } else {
            result = Ion.getDefault(context);
        }
        return result;
    }


    private void storeXSMPAppCid(String x_smp_appcid) {
        SharedPreferenceAdapter.setValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_CID,
                x_smp_appcid);
    }

    private void storeUserCredentials(String username, String password) {
        SharedPreferenceAdapter.setValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_USERNAME, username);
        SharedPreferenceAdapter.setValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_PASSWORD, password);
    }

    public UserCredentials getStoredCredentials() {
        String username = SharedPreferenceAdapter.getValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_USERNAME);
        String password = SharedPreferenceAdapter.getValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_PASSWORD);

        return new UserCredentials(username, password);
    }

    public String getRegisteredXSmpAppCid() {
        return SharedPreferenceAdapter.getValueForKey(context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_CID);
    }
}
