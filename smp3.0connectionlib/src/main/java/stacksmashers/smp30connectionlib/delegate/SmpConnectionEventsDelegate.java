package stacksmashers.smp30connectionlib.delegate;

import com.koushikdutta.ion.Response;

public interface SmpConnectionEventsDelegate {

    /**
     * Si è verificato un errore nella fase di autenticazione
     *
     * @param e      Eccezione generata
     * @param result Response http ricevuta dal servizio in fase di autenticazione
     */
    void onLoginError(Exception e, Response<String> result);

    /**
     * Si è verificato un errore nella fase di recupero del connection ID
     *
     * @param e      Eccezione generata
     * @param result Response http ricevuta dal servizio in fase di autenticazione
     */
    void onRegistrationError(Exception e, Response<String> result);

    /**
     * Il processo di connessione alla SMP è andato a buon fine. Il connection id è memorizzato localmente
     * nelle shared preferences
     * @param xsmpappcid Retrieved application connection ID
     * */
    void onConnectionSuccess(String xsmpappcid);

    /**
     * Si è verificato un errore di rete
     *
     * @param e      Eccezione generata
     * @param result Response http ricevuta dal servizio in fase di autenticazione
     */
    void onNetworkError(Exception e, Response<String> result);
}
