package stacksmashers.smp30connectionlib.smp;

import com.koushikdutta.async.http.NameValuePair;

/**
 * Created by Y2J on 03/05/17.
 */

public class Header implements NameValuePair {

    private String name;
    private String value;

    public Header(String name, String value){
        this.name = name;
        this.value =value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }
}
