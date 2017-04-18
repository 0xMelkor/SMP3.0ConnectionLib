package stacksmashers.smp30connectionlib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.koushikdutta.ion.Ion;

import org.junit.Test;
import org.junit.runner.RunWith;

import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.smp.SmpIntegration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 14/04/2017  *******
 ***************************/

@RunWith(AndroidJUnit4.class)
public class SmpCreationITest {

    private Context context = InstrumentationRegistry.getTargetContext();
    private Ion ion = Ion.getDefault(context);
    private String smpServiceRoot = "https://www.gmail.com";
    private String appid = "appid";


    @Test
    public void testCorrectSmpIntegrationCreation(){
        try{
            new SmpIntegration(context, ion, smpServiceRoot, appid, true);
            assertTrue(true);
        }
        catch (SmpExceptionInvalidInput ex){
            assertTrue(false);
        }
    }

    @Test
    public void testWrongDelegateSetup(){
        try{
            SmpIntegration instance =
                    new SmpIntegration(context, ion, smpServiceRoot, appid, true);
            instance.setDelegate(null);
        }
        catch (SmpExceptionInvalidInput ex){
            String msg = ex.getMessage();
            assertEquals(msg, SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        }
    }

}
