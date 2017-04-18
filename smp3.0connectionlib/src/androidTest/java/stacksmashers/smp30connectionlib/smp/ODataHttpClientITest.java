package stacksmashers.smp30connectionlib.smp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Base64;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.ion.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import stacksmashers.smp30connectionlib.delegate.ODataHttpClientCallback;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.testmodel.TypeTeamMember;
import stacksmashers.smp30connectionlib.utils.AssetsUtils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 18/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class ODataHttpClientITest {

    private int SERVER_PORT = 5555;
    private AsyncHttpServer httpServer;
    private String baseUrl = "http://localhost:" + SERVER_PORT;

    private String xsmpappcid = "xxxx-xxxx-xxxx-xxxx";
    private String username = "username";
    private String password = "password";

    private Type type;


    @Before
    public void setUp() {
        // Setup server
        httpServer = new AsyncHttpServer();
        httpServer.listen(SERVER_PORT);
    }

    private ODataHttpClient getTestClient() throws SmpExceptionInvalidInput {
        ODataHttpClient oDataHttpClient = new ODataHttpClient(getContext(), xsmpappcid, username, password);
        type = new TypeToken<ArrayList<TypeTeamMember>>() {
        }.getType();
        oDataHttpClient.setDeserializer(TypeTeamMember.class,
                new TypeTeamMember.TypeTeamMemberDeserializer());
        return oDataHttpClient;
    }

    @After
    public void tearDown() throws Exception {
        httpServer.stop();
        AsyncServer.getDefault().stop();
    }


    @Test(expected = SmpExceptionInvalidInput.class)
    public void fetchODataEntitySetNullType() throws Exception {
        getTestClient().fetchODataEntitySet(null, new TypeToken<ArrayList<Object>>() {
        }.getType());
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void fetchODataEntitySetNullUrl() throws Exception {
        getTestClient().fetchODataEntitySet("http://www.google.com", null);
    }

    @Test
    public void fetchODataEntitySetError() throws Exception {
        // Setup mock server
        httpServer.get("/appid/CollectionEndpoint", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                response.code(500);
                response.send("error");
            }
        });

        // Setup test client
        final Semaphore semaphore = new Semaphore(0);
        ODataHttpClient oDataHttpClient = getTestClient();
        oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
            @Override
            public void onErrorCallback(Exception ex, Response response) {
                assertEquals(response.getHeaders().code(), 500);
            }

            @Override
            public void onFetchEntitySuccessCallback(Object result) {

            }

            @Override
            public void onFetchEntitySetSuccessCallback(List result) {

            }
        });

        oDataHttpClient.fetchODataEntitySet("http://localhost:" + SERVER_PORT + "/appid/CollectionEndpoint",
                type);
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void fetchODataEntitySet() throws Exception {
        // Setup mock server
        httpServer.get("/appid/CollectionEndpoint", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                String result = "";
                try {
                    InputStream fis =
                            InstrumentationRegistry.getContext().getResources().getAssets().open("TeamSet.json");
                    JsonElement el = AssetsUtils.loadJSONFromAsset(fis);
                    response.code(200);
                    result = el.toString();

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });

        // Setup test client
        final Semaphore semaphore = new Semaphore(0);
        ODataHttpClient oDataHttpClient = getTestClient();
        oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
            @Override
            public void onErrorCallback(Exception ex, Response response) {

            }

            @Override
            public void onFetchEntitySuccessCallback(Object result) {

            }

            @Override
            public void onFetchEntitySetSuccessCallback(List result) {
                assertTrue(result.size() == 55);
            }
        });

        oDataHttpClient.fetchODataEntitySet("http://localhost:" + SERVER_PORT + "/appid/CollectionEndpoint",
                type);
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void fetchODataEntitySetDeserializer() throws Exception {

        // Setup mock server
        httpServer.get("/appid/CollectionEndpoint", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {

                // GET basic authentication header
                String authorization = request.getHeaders().get("Authorization").replace("Basic ", "");
                authorization = new String(Base64.decode(authorization, Base64.DEFAULT));
                String[] parts = authorization.split(":");
                assertTrue(parts.length == 2);

                String pUsername = parts[0];
                String pPassword = parts[1];

                // Assert correct basic authentication parameters are provided
                assertEquals(pUsername, username);
                assertEquals(pPassword, password);

                // GET X-SMP-APPCID header
                String appcidheader = request.getHeaders().get("X-SMP-APPCID");
                assertEquals(appcidheader, xsmpappcid);

                String result = "";

                try {
                    InputStream fis =
                            InstrumentationRegistry.getContext().getResources().getAssets().open("TeamSet.json");
                    JsonElement el = AssetsUtils.loadJSONFromAsset(fis);
                    response.code(200);
                    result = el.toString();

                } catch (Exception e) {
                    response.code(500);
                } finally {
                    response.send(result);
                }
            }
        });

        // Setup test client
        final Semaphore semaphore = new Semaphore(0);
        ODataHttpClient oDataHttpClient = getTestClient();
        oDataHttpClient.setDelegate(new ODataHttpClientCallback() {
            @Override
            public void onErrorCallback(Exception ex, Response response) {

            }

            @Override
            public void onFetchEntitySuccessCallback(Object result) {

            }

            @Override
            public void onFetchEntitySetSuccessCallback(List result) {
                assertTrue(result.size() == 55);
                try {
                    TypeTeamMember item = ((TypeTeamMember) result.get(0));
                    assertTrue(item != null);
                } catch (ClassCastException ex) {
                    assertTrue(false);
                }
            }
        });

        oDataHttpClient.fetchODataEntitySet("http://localhost:" + SERVER_PORT + "/appid/CollectionEndpoint",
                type);
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);

    }


    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }

}