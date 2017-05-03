package stacksmashers.smp30connectionlib.delegate;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Response;

/**
 * Created by Y2J on 03/05/17.
 */

public interface ODataHttpTokenClientCallback<T> {
    /**
     * Invoked on network or HTTP service error
     **/
    void onErrorCallback(Exception ex, Response<JsonObject> response);

    /**
     * Invoked on success of token fetch
     */
    void onFetchXCSRFTokenSuccessCallback(String token);
}
