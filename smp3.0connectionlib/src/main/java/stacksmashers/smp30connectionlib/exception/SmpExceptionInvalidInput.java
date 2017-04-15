package stacksmashers.smp30connectionlib.exception;

/**************************
 ** {__-StAcK_SmAsHeRs-__} ** 
 *** @author: a.simeoni ******* 
 *** @date: 13/04/2017  *******
 ***************************/

public class SmpExceptionInvalidInput extends Exception{

    public final static String ERROR_TYPE_MALFORMED_SMP_SERVICE_URL = "ERROR_TYPE_MALFORMED_SMP_SERVICE_URL";
    public final static String ERROR_TYPE_INVALID_APPID = "ERROR_TYPE_INVALID_APPID";
    public final static String ERROR_TYPE_NULL_INPUT = "ERROR_TYPE_NULL_INPUT";

    public SmpExceptionInvalidInput(String msg){
        super(msg);
    }

}
