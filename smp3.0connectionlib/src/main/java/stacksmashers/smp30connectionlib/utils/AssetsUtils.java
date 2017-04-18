package stacksmashers.smp30connectionlib.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;

/**************************
 ** {__-StAcK_SmAsHeRs-__} **
 *** @author: a.simeoni *******
 *** @date: 24/03/2017  *******
 ***************************/

public class AssetsUtils {

    public static JsonObject loadJSONFromAsset(InputStream is) {
        String json;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        JsonParser jsonParser = new JsonParser();
        return (JsonObject) jsonParser.parse(json);
    }
}
