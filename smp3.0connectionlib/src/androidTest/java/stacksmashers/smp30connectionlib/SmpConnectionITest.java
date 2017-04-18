package stacksmashers.smp30connectionlib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.http.Headers;
import com.koushikdutta.async.http.server.AsyncHttpServer;
import com.koushikdutta.async.http.server.AsyncHttpServerRequest;
import com.koushikdutta.async.http.server.AsyncHttpServerResponse;
import com.koushikdutta.async.http.server.HttpServerRequestCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import stacksmashers.smp30connectionlib.delegate.SmpConnectionEventsDelegate;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.smp.SmpIntegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 14/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class SmpConnectionITest {

    private int SERVER_PORT = 5555;
    private AsyncHttpServer httpServer;
    private String baseUrl = "http://localhost:" + SERVER_PORT;

    @Before
    public void setUp() throws Exception {
        httpServer = new AsyncHttpServer();
        httpServer.listen(SERVER_PORT);
    }

    @After
    public void tearDown() throws Exception {
        httpServer.stop();
        AsyncServer.getDefault().stop();
    }

    @Test
    public void testUnauthorizedLogin() throws Exception {
        httpServer.get("/odata/applications/latest/appid", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.code(401);
                response.send("unauthorized");
            }
        });

        SmpIntegration smpIntegration = getTestSmpIntegration();
        smpIntegration.setDelegate(new SmpConnectionEventsDelegate() {
            @Override
            public void onLoginError(Exception e, Response<String> result) {
                assertEquals(result.getHeaders().code(), 401);
            }

            @Override
            public void onRegistrationError(Exception e, Response<String> result) {
                assertTrue(false);
            }

            @Override
            public void onConnectionSuccess(String xsmpappcid) {
                assertTrue(false);
            }

            @Override
            public void onNetworkError(Exception e, Response<String> result) {
                assertTrue(false);
            }
        });

        final Semaphore semaphore = new Semaphore(0);
        smpIntegration.connect("username", "password");
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);
    }

    @Test
    public void testSuccessfulRegisterDevice() throws Exception {

        final String expectedRegistrationCookie = "aa12b-ffba2-ccab1-eeff1-aa00";

        // First call if for login
        httpServer.get("/odata/applications/latest/appid", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                response.code(200);
                response.getHeaders().set("Set-Cookie", "SSO=TESTSSO;");
                response.send("unauthorized");
            }
        });

        // Second call if for registration
        httpServer.post("/odata/applications/latest/appid/Connections", new HttpServerRequestCallback() {
            @Override
            public void onRequest(AsyncHttpServerRequest request, AsyncHttpServerResponse response) {
                Headers headers = response.getHeaders();
                headers.set("Set-Cookie", "X-SMP-APPCID=" + expectedRegistrationCookie + ";");
                response.code(200);
                response.send("ok");
            }
        });
        SmpIntegration smpIntegration = getTestSmpIntegration();
        smpIntegration.setDelegate(new SmpConnectionEventsDelegate() {
            @Override
            public void onLoginError(Exception e, Response<String> result) {
                assertTrue(false);
            }

            @Override
            public void onRegistrationError(Exception e, Response<String> result) {
                assertTrue(false);
            }

            @Override
            public void onConnectionSuccess(String xsmpappcid) {
                assertEquals(xsmpappcid, expectedRegistrationCookie);
            }

            @Override
            public void onNetworkError(Exception e, Response<String> result) {
                assertTrue(false);
            }
        });

        final Semaphore semaphore = new Semaphore(0);
        smpIntegration.connect("username", "password");
        semaphore.tryAcquire(10000, TimeUnit.MILLISECONDS);
    }

    // Get SmpIntegration test instance
    private SmpIntegration getTestSmpIntegration() throws SmpExceptionInvalidInput {
        SmpIntegration result = new SmpIntegration(getContext(), Ion.getDefault(getContext()), baseUrl, "appid", true);
        result.setTestModeEnabled(true);
        return result;
    }

    private Context getContext() {
        return InstrumentationRegistry.getTargetContext();
    }
}
