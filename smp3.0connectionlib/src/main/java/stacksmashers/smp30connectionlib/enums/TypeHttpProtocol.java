package stacksmashers.smp30connectionlib.enums;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 13/04/2017  *******
 ***************************/

public enum TypeHttpProtocol {
    TYPE_HTTP_PROTOCOL_HTTP("HTTP"), TYPE_HTTP_PROTOCOL_HTTPS("HTTPS");
    String value;

    TypeHttpProtocol(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
