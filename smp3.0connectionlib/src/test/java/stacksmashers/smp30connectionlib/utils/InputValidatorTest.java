package stacksmashers.smp30connectionlib.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 18/04/2017  *******
 ***************************/

@RunWith(JUnit4.class)
public class InputValidatorTest {

    @Test
    public void validateNotNull() throws Exception {
        InputValidator.validateNotNull(new Object());
        assertTrue(true);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void validateNotNullFail() throws Exception {
        InputValidator.validateNotNull(null);
    }

    @Test
    public void validateNotEmptyString() throws Exception {
        InputValidator.validateNotEmptyString("teststring");
        assertTrue(true);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void validateNotEmptyStringFail() throws Exception {
        InputValidator.validateNotEmptyString("   ");
    }

    @Test
    public void validateUrl() throws Exception {
        InputValidator.validateUrl("https://www.google.com");
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void validateUrlFail() throws Exception {
        InputValidator.validateUrl("aaa:sss");
    }

    @Test
    public void getURLProtocol() throws Exception {
        TypeHttpProtocol type = InputValidator.getURLProtocol("https://www.google.com");
        assertEquals(type.getValue(), "HTTPS");

        type = InputValidator.getURLProtocol("http://www.google.com");
        assertEquals(type.getValue(), "HTTP");
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void getURLProtocolFailNull() throws Exception {
        InputValidator.getURLProtocol(null);
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void getURLProtocolFailMalformed() throws Exception {
        InputValidator.getURLProtocol("asdasvv qw");
    }

    @Test(expected = SmpExceptionInvalidInput.class)
    public void getURLProtocolFailEmpty() throws Exception {
        InputValidator.getURLProtocol("");
    }

}