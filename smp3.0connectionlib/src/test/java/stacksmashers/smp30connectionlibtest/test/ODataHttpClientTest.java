package stacksmashers.smp30connectionlibtest.test;

import android.content.Context;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.lang.reflect.Type;

import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.smp.ODataHttpClient;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 15/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class ODataHttpClientTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    @Mock
    Context mockContext;

    private ODataHttpClient oDataHttpClient;
    private String xsmpappcid = "xxxx-xxxx-xxxx-xxxx";
    private String username = "unsername";
    private String password = "password";

    @Before
    public void init() throws SmpExceptionInvalidInput {
        oDataHttpClient = new ODataHttpClient(mockContext, xsmpappcid, username, password);
    }

    @Test
    public void testCorrectInstance() {
        try {
            ODataHttpClient client = new ODataHttpClient(mockContext, xsmpappcid, username, password);
            assertNotNull(client);
        } catch (SmpExceptionInvalidInput ex) {
            assertTrue(false);
        }
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullContext() throws SmpExceptionInvalidInput {
        new ODataHttpClient(null, xsmpappcid, username, password);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullXSmpAppcid() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, null, username, password);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testEmptyXSmpAppcid() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, "  ", username, password);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullUsername() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, xsmpappcid, null, password);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testEmptyUsername() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, xsmpappcid, "    ", password);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullPassword() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, xsmpappcid, username, null);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testEmptyPassword() throws SmpExceptionInvalidInput {
        new ODataHttpClient(mockContext, xsmpappcid, username, "");
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void setNullDelegate() throws SmpExceptionInvalidInput {
        oDataHttpClient.setDelegate(null);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void setNullDeserializer() throws SmpExceptionInvalidInput {
        oDataHttpClient.setDeserializer(null, new JsonDeserializer() {
            @Override
            public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return null;
            }
        });
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void setNullPojoClass() throws SmpExceptionInvalidInput {
        oDataHttpClient.setDeserializer(Object.class, null);
    }

}