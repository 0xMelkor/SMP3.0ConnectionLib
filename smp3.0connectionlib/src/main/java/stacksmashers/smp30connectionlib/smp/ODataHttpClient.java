package stacksmashers.smp30connectionlib.smp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.HeadersResponse;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.List;

import stacksmashers.smp30connectionlib.delegate.ODataHttpClientCallback;
import stacksmashers.smp30connectionlib.delegate.ODataHttpTokenClientCallback;
import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.netutil.IonFactory;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;
import stacksmashers.smp30connectionlib.utils.InputValidator;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 15/04/2017  *******
 ***************************/

public class ODataHttpClient {

    private final String TAG = getClass().getCanonicalName();

    private Context context;

    /**
     * X-SMP-APPCID cookie retrieved during the registration phase
     * {@link SmpIntegration#connect(String, String)}
     **/
    private String xsmpappcid;

    /**
     * Username bound to {@link ODataHttpClient#xsmpappcid}
     **/
    private String username;

    /**
     * Password related to {@link ODataHttpClient#username}
     **/
    private String password;

    /**
     * Handle events from the OData service
     **/
    private ODataHttpClientCallback delegate;
    private ODataHttpTokenClientCallback tokenDelegate;

    /**
     * Class used to inject json raw data
     **/
    private Class toPojoClass;

    /**
     * Custom json deserializer
     **/
    private JsonDeserializer deserializer;


    public ODataHttpClient(@NonNull Context context, @NonNull String xsmpappcid,
                           @NonNull String username, @NonNull String password) throws SmpExceptionInvalidInput {
        InputValidator.validateNotNull(context);
        InputValidator.validateNotEmptyString(username);
        InputValidator.validateNotEmptyString(password);
        InputValidator.validateNotEmptyString(xsmpappcid);
        this.context = context;
        this.username = username;
        this.password = password;
        this.xsmpappcid = xsmpappcid;
    }

    public void setDelegate(ODataHttpClientCallback delegate) throws SmpExceptionInvalidInput {
        InputValidator.validateNotNull(delegate);
        this.delegate = delegate;
    }

    public void setTokenDelegate(ODataHttpTokenClientCallback tokenDelegate) throws SmpExceptionInvalidInput {
        InputValidator.validateNotNull(tokenDelegate);
        this.tokenDelegate = tokenDelegate;
    }

    public void setDeserializer(Class clazz, JsonDeserializer deserializer) throws SmpExceptionInvalidInput {
        InputValidator.validateNotNull(clazz);
        InputValidator.validateNotNull(deserializer);
        this.deserializer = deserializer;
        this.toPojoClass = clazz;
    }


    /**
     * Fetches a collection of OData entities.
     *
     * @param resultSetType The GSON type used to deserialize JSON raw data. If you want to return a List of PojoClass
     *                      instances follow the next example
     *                      es. new TypeToken<ArrayList<PojoClass>>() {}.getType()
     **/
    public void fetchODataEntitySet(String url, final Type resultSetType) throws SmpExceptionInvalidInput {

        InputValidator.validateNotNull(resultSetType);
        TypeHttpProtocol protocol = InputValidator.getURLProtocol(url);

        try {
            IonFactory ionFactory = new IonFactory(this.context, protocol);
            Ion ion = ionFactory.build();
            ion.with(ionFactory.getContext())
                    .load(url)
                    .addHeader("X-SMP-APPCID", xsmpappcid)
                    .addHeader("Accept", "application/json")
                    .basicAuthentication(username, password)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                List dataList = buildResult(result.getResult(), resultSetType);
                                if (delegate != null) {
                                    delegate.onFetchEntitySetSuccessCallback(dataList);
                                }
                            } catch (Exception e1) {
                                if (delegate != null) {
                                    delegate.onErrorCallback(e, result);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            throw new SmpExceptionInvalidInput(e.getMessage());
        }
    }

    private List buildResult(JsonObject raw, Type type) {
        JsonArray jsonArray = raw.get("d").getAsJsonObject().get("results").getAsJsonArray();
        List result;
        if(toPojoClass != null && deserializer != null){
            // Use deserialization if available
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(toPojoClass, deserializer);
            result = gsonBuilder.create().fromJson(jsonArray, type);
        }
        else{
            result = new Gson().fromJson(jsonArray,type);
        }
        return result;
    }

    public void getXCSRFToken(String url) throws SmpExceptionInvalidInput {
        TypeHttpProtocol protocol = InputValidator.getURLProtocol(url);

        try {
            IonFactory ionFactory = new IonFactory(this.context, protocol);
            Ion ion = ionFactory.build();
            ion.with(ionFactory.getContext())
                    .load(url)
                    .addHeader("X-SMP-APPCID", xsmpappcid)
                    .addHeader("Accept", "application/json")
                    .addHeader("X-CSRF-Token","Fetch")
                    .basicAuthentication(username, password)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            try {
                                new IonResponseManager(e, result);
                                Headers response = result.getHeaders().getHeaders();
                                String token = response.get("x-csrf-token");

                                if (tokenDelegate != null) {
                                    tokenDelegate.onFetchXCSRFTokenSetSuccessCallback(token);
                                }
                            } catch (Exception e1) {
                                if (tokenDelegate != null) {
                                    tokenDelegate.onErrorCallback(e, result);
                                }
                            }
                        }
                    });
        } catch (Exception e) {
            throw new SmpExceptionInvalidInput(e.getMessage());
        }
    }

}
