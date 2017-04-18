package stacksmashers.smp30connectionlib;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.koushikdutta.ion.Ion;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.netutil.IonFactory;

import static org.junit.Assert.assertNotNull;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 18/04/2017  *******
 ***************************/
@RunWith(JUnit4.class)
public class IonFactoryCreationITest {
    private Context context = InstrumentationRegistry.getTargetContext();
    private IonFactory ionHttpFactory, ionHttpsFactory;

    @Before
    public void before() throws Exception {
        this.ionHttpFactory = new IonFactory(context, TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP);
        this.ionHttpsFactory = new IonFactory(context, TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS);
    }

    @Test
    public void build() throws Exception {
        Ion httpIon, httpsIon;
        httpIon = ionHttpFactory.build();
        httpsIon = ionHttpsFactory.build();
        assertNotNull(httpIon);
        assertNotNull(httpsIon);
    }

}
