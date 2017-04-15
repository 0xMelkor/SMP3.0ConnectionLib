package stacksmashers.smp30connectionlib;

import android.content.Context;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.smp.SmpIntegration;

import static org.junit.Assert.assertEquals;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 14/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class SmpIntegrationUnitTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    Context mockContext;

    @Mock
    SmpIntegration mockSmpIntegration;


    @Test
    public void TestSmpIntegrationNullInputContext(){

        String smpServiceRoot = "http://www.google.com";
        String appid = "appid";

        try {
            new SmpIntegration(null, null, smpServiceRoot , appid,true);
        } catch (SmpExceptionInvalidInput ex) {
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        }
    }

    @Test
    public void TestSmpIntegrationNullInputIon(){

        String smpServiceRoot = "http://www.google.com";
        String appid = "appid";

        try {
            new SmpIntegration(mockContext, null, smpServiceRoot , appid, true);
        } catch (SmpExceptionInvalidInput ex) {
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        }
    }

    @Test
    public void TestSmpIntegrationMalformedSmpUrl(){

        String smpServiceRoot = "www.google.com";
        String appid = "appid";

        try {
            new SmpIntegration(mockContext, null, smpServiceRoot , appid,true);
        } catch (SmpExceptionInvalidInput ex) {
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_MALFORMED_SMP_SERVICE_URL);
        }
    }

    @Test
    public void TestSmpIntegrationEmptyAppid(){

        String smpServiceRoot = "http://www.google.com";
        String appid = "";

        try {
            new SmpIntegration(mockContext, null, smpServiceRoot , appid,true);
        } catch (SmpExceptionInvalidInput ex) {
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_INVALID_APPID);
        }
    }

    @Test
    public void TestSmpIntegrationBlankAppid(){

        String smpServiceRoot = "http://www.google.com";
        String appid = "";

        try {
            new SmpIntegration(mockContext, null, smpServiceRoot , appid,true);
        } catch (SmpExceptionInvalidInput ex) {
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_INVALID_APPID);
        }
    }

}
