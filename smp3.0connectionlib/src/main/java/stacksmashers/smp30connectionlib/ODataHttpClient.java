package stacksmashers.smp30connectionlib;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import stacksmashers.smp30connectionlib.delegate.ODataHttpClientCallback;
import stacksmashers.smp30connectionlib.netutil.IonResponseManager;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 29/03/2017  *******
 ***************************/

public class ODataHttpClient {

    final String TAG = getClass().getCanonicalName();

    /**
     * Normally this parameter matches the value of {@link SmpConnection#appid}
     * In the case of multiple backend connections for your you should set the right
     * backend connection ID
     */
    private String odataBackendConnection;
    private SmpConnection connection;
    private Context context;
    private ODataHttpClientCallback delegate;

    private Class toPojoClass;
    private JsonDeserializer deserializer;


    public ODataHttpClient(SmpConnection connection, String odataBackendConnection) {
        this.connection = connection;
        this.odataBackendConnection = odataBackendConnection != null ? odataBackendConnection : this.connection.getAppId();
    }

    public ODataHttpClient with(Context context) {
        this.context = context;
        return this;
    }


    public ODataHttpClient setDelegate(ODataHttpClientCallback delegate) {
        this.delegate = delegate;
        return this;
    }

    public ODataHttpClient setDeserializer(Class clazz, JsonDeserializer deserializer){
        this.deserializer = deserializer;
        this.toPojoClass = clazz;
        return this;
    }


    /**
     * Fetches a collection of OData entities. The OData URL is built as
     * <p>
     * http[s]://<host>:<port>/<odataBackendConnection>/<entitySetName>
     *
     * @param entitySetName The name of the OData entity set.
     * @param resultSetType The GSON type used to deserialize JSON raw data. If you want to return a List of PojoClass
     *                      instances follow the next example
     *                      es. new TypeToken<ArrayList<PojoClass>>() {}.getType()
     **/
    public void fetchODataEntitySet(String entitySetName, final Type resultSetType) {

        Ion ion = connection.getIonInstance();
        String serviceUrl = connection.getSmpServiceRoot();
        serviceUrl += "/" + odataBackendConnection + "/" + entitySetName;

        ion.with(this.context)
                .load(serviceUrl)
                .addHeader("X-SMP-APPCID", connection.getRegisteredXSmpAppCid())
                .addHeader("Accept", "application/json")
                .basicAuthentication(connection.getAvailableCredentials().getUsername(),
                        connection.getAvailableCredentials().getPassword())
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
}
