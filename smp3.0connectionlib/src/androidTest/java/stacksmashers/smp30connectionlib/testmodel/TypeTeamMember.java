package stacksmashers.smp30connectionlib.testmodel;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;


public class TypeTeamMember {

    /**
     * Nome e cognome
     */
    private String sName;
    /**
     * Indirizzo email
     */
    private String email;
    /**
     * Unità organizzativa
     */
    private String orgTx;
    /**
     * Mansione
     */
    private String pLstx;
    /**
     * Richieste pendenti
     */
    private String numRichieste;
    /**
     * Se è presente a lavoro
     */
    private String isPresent;


    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrgTx() {
        return orgTx;
    }

    public void setOrgTx(String orgTx) {
        this.orgTx = orgTx;
    }

    public String getpLstx() {
        return pLstx;
    }

    public void setpLstx(String pLstx) {
        this.pLstx = pLstx;
    }

    public String getNumRichieste() {
        return numRichieste;
    }

    public void setNumRichieste(String numRichieste) {
        this.numRichieste = numRichieste;
    }

    public String isPresent() {
        return isPresent;
    }

    public void setPresent(String present) {
        isPresent = present;
    }

    public static class TypeTeamMemberDeserializer implements JsonDeserializer<TypeTeamMember> {

        @Override
        public TypeTeamMember deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            TypeTeamMember member = new TypeTeamMember();
            member.setsName(getStringValueForKey(json, "Sname"));
            member.setEmail(getStringValueForKey(json, "Email"));
            member.setNumRichieste(getStringValueForKey(json, "NumRichieste"));
            member.setOrgTx(getStringValueForKey(json, "Orgtx"));
            member.setpLstx(getStringValueForKey(json, "Plstx"));
            member.setPresent(getStringValueForKey(json, "IsPresent"));

            return member;
        }

        private String getStringValueForKey(JsonElement json, String key) {
            JsonElement element = json.getAsJsonObject().get(key);
            return element != null ? element.getAsString() : "";
        }
    }


}
