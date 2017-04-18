package stacksmashers.smp30connectionlib.utils;

import java.net.MalformedURLException;
import java.net.URL;

import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 15/04/2017  *******
 ***************************/

public class InputValidator {

    public static void validateNotNull(Object o) throws SmpExceptionInvalidInput {
        if (o == null)
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
    }

    public static void validateUrl(String url) throws SmpExceptionInvalidInput {
        try {
            new URL(url);
        } catch (MalformedURLException ex) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_MALFORMED_SMP_SERVICE_URL);
        }
    }

    public static TypeHttpProtocol getURLProtocol(String url) throws SmpExceptionInvalidInput {

        TypeHttpProtocol result = null;

        try {
            URL u = new URL(url);
            if (u.getProtocol().equalsIgnoreCase(TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP.getValue())) {
                result = TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTP;
            }

            if (u.getProtocol().equalsIgnoreCase(TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS.getValue())) {
                result = TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS;
            }
        } catch (MalformedURLException ex) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_MALFORMED_SMP_SERVICE_URL);
        }

        return result;

    }

    public static void validateNotEmptyString(String input) throws SmpExceptionInvalidInput {
        if (input == null || input.trim().length() == 0) {
            throw new SmpExceptionInvalidInput(SmpExceptionInvalidInput.ERROR_TYPE_NULL_INPUT);
        }
    }
}
