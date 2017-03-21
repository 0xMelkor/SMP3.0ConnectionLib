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

class UserCredentials{

    private String username;
    private String password;

    UserCredentials(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

     String getPassword() {
        return password;
    }

     String getUsername() {
        return username;
    }

    boolean isValid(){
        return (username!=null && password!=null) &&
                (!username.equals("") && !password.equals(""));
    }
}

public class SmpConnection
{

    private Context _context;
    private String _smp_service_root;
    private String _appid;
    private UserCredentials _credentials;
    private SmpConnectionEventsDelegate _delegate;
    private boolean _ignoreCookies = false;

    private String REGISTRATION_XML_BODY =
            "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
            "<entry xmlns=\"http://www.w3.org/2005/Atom\" " +
            "xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" " +
            "xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\">" +
            "<content type=\"application/xml\">" +
            "<m:properties><d:DeviceType>Android</d:DeviceType></m:properties>" +
            "</content></entry>";


    public SmpConnection(){
        _credentials = new UserCredentials("","");
    }

    public SmpConnection with(@NonNull Context context) {
        this._context = context;
        return this;
    }

    /**
     * @param smproot Endpoint OData dell'applicazione (es. https://swfmmobilepp.aceaspa.it)
     * @param appid Id dell'app su SMP (es. swfm)
     * */
    public SmpConnection setSmpEndpoint(@NonNull String smproot, @NonNull String appid){
        this._smp_service_root = smproot;
        this._appid = appid;
        return this;
    }

    public SmpConnection setDelegate(SmpConnectionEventsDelegate delegate){
        this._delegate = delegate;
        return this;
    }

    public SmpConnection setUserCredentials(@NonNull String username, @NonNull String password){
        this._credentials = new UserCredentials(username, password);
        return this;
    }

    public boolean isIgnoreCookies(){
        return _ignoreCookies;
    }

    /**
     * Clear cookie cache before connecting
     * */
    public SmpConnection setIgnoreCookies(boolean value){
        this._ignoreCookies = value;
        return this;
    }

    /**
     * Si connette alla piattaforma secondo le seguenti regole:
     * 1. Se non ci sono credenziali specificate dall'utente o memorizzate localmente
     *    richiama la callback {@link SmpConnectionEventsDelegate#onCredentialsRequired()} <br/>
     * 2. Se ci sono credenziali allora effettua il login sulla piattaforma. Possono succedere 2 cose:
     *    <ul>
     *        <li>Esiste un Connection ID tra i cookie locali</li>
     *        <ul>
     *            <li>Viene inviato alla SMP nella chiamata di login</li>
     *            <li>Non deve esserci per forza un corrispettivo su SMP per l'app cid inviato.
     *            Infatti il cid può essere generato anche dall'app</li>
     *        <ul/>
     *        <li>Non esiste un Connection ID tra i cookie locali:</li>
     *        <ul>
     *            <li>Dopo il login la SMP non rimbalza nessun cookie</li>
     *            <li>Si effettua una chiamata di registrazione</li>
     *            <li>Il connection ID viene estratto e memorizzato localmente per le chiamate ai servizi e memorizzato nei cookies</li>
     *        <ul/>
     *     <ul/>
     *
     *     Note sulla documentazione ufficiale
     *     @see <a href="https://help.sap.com/saphelp_smp304sdk/helpdata/en/7c/0aa27070061014bf49dc643a537fd6/content.htm"></a>
     * */
    public void connect()
    {
        UserCredentials credentials = getAvailableCredentials();
        // Devono esserci delle credenziali, passate come parametro o memorizzate
        if(credentials == null){
            onCredentialsRequired();
            return;
        }
        doLogin();
    }

    /**
     * Clears stored credentials and X-SMP-APPCID token
     * */
    public void clearCache(){
        SharedPreferenceAdapter.clearAll(_context);
    }



    /**************************************/
    /********* Private methods ************/
    /**************************************/

    private void onCredentialsRequired(){
        if(_delegate!=null){
            _delegate.onCredentialsRequired();
        }
    }

    private void onConnectionSuccess(){
        if(_delegate!=null){
            _delegate.onConnectionSuccess();
        }
    }

    private void onLoginError(Exception e, Response<String> result){
        if(_delegate!=null){
            _delegate.onLoginError(e, result);
        }
    }

    private void onNetworkError(Exception e, Response<String> result){
        if(_delegate!=null){
            _delegate.onNetworkError(e, result);
        }
    }

    private void onRegistrationError(Exception e, Response<String> result){
        if(_delegate!=null){
            _delegate.onRegistrationError(e, result);
        }
    }

    private void doLogin(){
        final Ion ion = getIonInstance();
        if(_ignoreCookies){
            IonResponseManager.clearCookies(ion);
        }
        ion.build(_context)
                .load("GET", _smp_service_root+"/odata/applications/latest/"+_appid)
                .basicAuthentication(
                        _credentials.getUsername(),
                        _credentials.getPassword())
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            new IonResponseManager(e, result);
                            // Memorizzo le credenziali dell' utente
                            storeUserCredentials(_credentials.getUsername(),_credentials.getPassword());
                            // Il login è andato a buon fine (non ho eccezione da IonResponseManager)
                            // Se il server mi ha restituito il connectionID in un cookie allora sono già registrato
                            // Altrimenti effettuo la registrazione
                            String x_smp_appcid = IonResponseManager.getSMPCookie(ion, new URI(_smp_service_root));
                            if(!x_smp_appcid.equals("")){
                                storeXSMPAppCid(x_smp_appcid);
                                onConnectionSuccess();
                            }
                            else{
                                registerDevice();
                            }

                        } catch (Exception e1) {
                            if(result.getHeaders().code() == 401){
                                onLoginError(e, result);
                            }
                            else{
                                onNetworkError(e, result);
                            }
                        }
                    }
                });
    }


    private void registerDevice(){

        final Ion ion = getIonInstance();
        ion.build(_context)
                .load("POST", _smp_service_root +"/odata/applications/latest/"+_appid+"/Connections")
                .addHeader("Content-type", "application/xml")
                .basicAuthentication(
                    _credentials.getUsername(),
                    _credentials.getPassword())
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

                            if(result.getHeaders().code() == 403){
                                onRegistrationError(e, result);
                            }
                            else{
                                onNetworkError(e, result);
                            }
                        }
                    }
                });
    }


    private Ion getIonInstance(){

        Ion result;

        if(this._smp_service_root.startsWith("https") || this._smp_service_root.startsWith("HTTPS")){
            // Devo ottenere istanza di Ion che gestisce SSL
            try{
                result = IonSslUtil.getIonHttpsInstance(_context);
            }
            catch (Exception ex){
                result = null;
            }
        }
        else{
            result = Ion.getDefault(_context);
        }
        return result;
    }

    /**
     * @return Restituisce le credenziali impostate attraverso {@link this.setUserCredentials} se disponibili
     * altrimenti le credenziali memorizzate nelle sharedPreferences se disponibili oppure null
     * */
    private UserCredentials getAvailableCredentials()
    {
        UserCredentials result = null;

        if(_credentials.isValid()){
            result = _credentials;
        }
        else if(getStoredCredentials().isValid()){
            result = getStoredCredentials();
        }

        return  result;
    }

    private void storeXSMPAppCid(String x_smp_appcid){
        SharedPreferenceAdapter.setValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_CID,
                x_smp_appcid);
    }

    private void storeUserCredentials(String username, String password){
        SharedPreferenceAdapter.setValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_USERNAME, username);
        SharedPreferenceAdapter.setValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_PASSWORD, password);
    }

    private UserCredentials getStoredCredentials()
    {
        String username = SharedPreferenceAdapter.getValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_USERNAME);
        String password = SharedPreferenceAdapter.getValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_PASSWORD);

        return new UserCredentials(username, password);
    }

    private boolean isSmpAppCidRegistered(){
        return SharedPreferenceAdapter.getValueForKey(_context,
                SharedPreferenceAdapter.SHARED_PREFS_KEY_SMP_CID)!=null;
    }
}
