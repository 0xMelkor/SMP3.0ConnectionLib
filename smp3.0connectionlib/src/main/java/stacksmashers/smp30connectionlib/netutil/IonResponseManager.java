package stacksmashers.smp30connectionlib.netutil;

import com.koushikdutta.async.http.Headers;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.koushikdutta.ion.cookie.CookieMiddleware;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by a.simeoni on 10/03/2017.
 */

public class IonResponseManager {

    public IonResponseManager(Exception e, Response result) throws Exception{

        if (e != null) throw e;
        if (result == null) throw new Exception("No result error");

        int resultCode = result.getHeaders().code();

        if (resultCode > 299 || resultCode < 200) {
            throw new Exception("Error");
        }
    }

    public static String getSMPCookie(Ion ion, URI uri) throws IOException {
        CookieMiddleware middleware = ion.getCookieMiddleware();
        CookieManager manager = middleware.getCookieManager();
        Headers cookieHeaders = new Headers();
        Map<String, List<String>> cookies = manager.get(uri, cookieHeaders.getMultiMap());
        @SuppressWarnings("unchecked")
        String cookieString = "";
        try{
            cookieString = ((List<String>)cookies.values().toArray()[0]).get(0);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return getSmpAppCidCookieMatch(cookieString);
    }

    public static String getConnectionIdFromResponse(Response<String> result){
        List<String> cookieHeaders = result.getHeaders().getHeaders().getAll("Set-Cookie");
        String smp_app_cid = "";
        if(cookieHeaders!=null && cookieHeaders.get(0)!=null){
            smp_app_cid = getSmpAppCidCookieMatch(cookieHeaders.get(0));
        }

        return smp_app_cid;
    }

    public static void clearCookies(Ion ion){
        CookieMiddleware middleware = ion.getCookieMiddleware();
        middleware.clear();
    }

    private static String getSmpAppCidCookieMatch(String cookie){
        Pattern pattern = Pattern.compile("X-SMP-APPCID=(.*?)(;|$)");
        Matcher matcher = pattern.matcher(cookie);
        if (matcher.find())
            return matcher.group(1);
        return "";
    }

}
