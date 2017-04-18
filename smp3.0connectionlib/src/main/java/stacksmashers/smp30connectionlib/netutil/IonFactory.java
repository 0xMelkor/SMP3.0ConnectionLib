package stacksmashers.smp30connectionlib.netutil;

import android.content.Context;
import android.support.annotation.NonNull;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import stacksmashers.smp30connectionlib.enums.TypeHttpProtocol;
import stacksmashers.smp30connectionlib.exception.SmpExceptionInvalidInput;
import stacksmashers.smp30connectionlib.utils.InputValidator;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 18/04/2017  *******
 ***************************/

public class IonFactory {

    private Context context;
    private TypeHttpProtocol httpProtocol;

    private Ion ion;

    public IonFactory(@NonNull Context context, @NonNull TypeHttpProtocol httpProtocol) throws SmpExceptionInvalidInput {
        InputValidator.validateNotNull(context);
        InputValidator.validateNotNull(httpProtocol);
        this.context = context;
        this.httpProtocol = httpProtocol;
    }

    public Ion build() throws CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {

        ion = Ion.getDefault(context);

        if (this.httpProtocol == TypeHttpProtocol.TYPE_HTTP_PROTOCOL_HTTPS) {
            IonSslUtil.getIonHttpsInstance(context, ion);
        }

        return ion;
    }

    public Ion getIon() {
        return ion;
    }

    public Context getContext() {
        return context;
    }
}
