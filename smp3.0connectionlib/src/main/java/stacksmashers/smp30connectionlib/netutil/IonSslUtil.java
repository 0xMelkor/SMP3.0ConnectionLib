package stacksmashers.smp30connectionlib.netutil;

import android.annotation.SuppressLint;
import android.content.Context;

import com.koushikdutta.ion.Ion;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by a.simeoni on 10/03/2017.
 */

public class IonSslUtil {


    private static void trustAllHttpsClient(Ion ion) throws NoSuchAlgorithmException, KeyManagementException {

        SSLContext _context = SSLContext.getInstance("TLS");

        HostnameVerifier hostnameVerifier = new HostnameVerifier() {
            @SuppressLint("BadHostnameVerifier")
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        TrustManager[] truster=new TrustManager[]{new X509TrustManager() {
            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
            }

            @SuppressLint("TrustAllX509TrustManager")
            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] x509Certificates,
                    String s) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }
        }};

        _context.init(null, truster, new SecureRandom());

        ion.getHttpClient().getSSLSocketMiddleware().setSpdyEnabled(false);
        ion.getHttpClient().getSSLSocketMiddleware().setHostnameVerifier(hostnameVerifier);
        ion.getHttpClient().getSSLSocketMiddleware().setSSLContext(_context);
        ion.getHttpClient().getSSLSocketMiddleware().setTrustManagers(truster);

    }

    public static Ion getIonHttpsInstance(Context context) throws NoSuchAlgorithmException, KeyManagementException, CertificateException, KeyStoreException, IOException {
        Ion ion = Ion.getDefault(context);
        trustAllHttpsClient(ion);
        return ion;
    }
}
