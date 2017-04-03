package stacksmashers.smp30connectionlib.delegate;

import com.google.gson.JsonObject;
import com.koushikdutta.ion.Response;

import java.util.List;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 29/03/2017  *******
 ***************************/

public interface ODataHttpClientCallback<T> {
    /**
     * Invoked on network or HTTP service error
     **/
    void onErrorCallback(Exception ex, Response<JsonObject> response);

    /**
     * Invoked on success of single entity fetch
     */
    void onFetchEntitySuccessCallback(T result);

    /**
     * Invoked on success of entity-set fetch
     */
    void onFetchEntitySetSuccessCallback(List<T> result);
}
