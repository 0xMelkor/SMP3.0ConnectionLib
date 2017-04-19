package stacksmashers.smp30connectionlib.netutil;

import android.content.Context;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 18/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class IonFactoryTest {

    @Rule
    public MockitoRule rule = MockitoJUnit.rule();
    @Mock
    Context mockContext;

    IonFactory ionHttpFactory, ionHttpsFactory;

    @Before
    public void before() throws Exception {
        Mockito.when(mockContext.getApplicationContext()).thenReturn(mockContext);
        this.ionHttpFactory = new IonFactory(mockContext, TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP);
        this.ionHttpsFactory = new IonFactory(mockContext, TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullContextCreation() throws Exception {
        new IonFactory(null, TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void testNullHttpTypeCreation() throws Exception {
        new IonFactory(mockContext, null);
    }

}