package com.samourai.wallet.cahoots;

import com.samourai.soroban.cahoots.CahootsContext;
import com.samourai.wallet.util.UtxoUtil;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

// shared payload for any Cahoots: Stonewallx2, Stowaway, MultiCahoots
public abstract class Cahoots<C extends CahootsContext> {
    private static final Logger log = LoggerFactory.getLogger(Cahoots.class);
    protected static final long SEQUENCE_RBF_ENABLED = 4294967293L;
    protected static final UtxoUtil utxoUtil = UtxoUtil.getInstance();

    private int version = 2;
    private int type = -1;
    private int step = -1;
    protected NetworkParameters params = null;

    public Cahoots()    {  }

    public Cahoots(int type, NetworkParameters params) {
        this.type = type;
        this.step = 0;
        this.params = params;
    }

    protected Cahoots(Cahoots c)    {
        this(c.getType(), c.getParams());
    }

    public int getVersion() {
        return version;
    }

    public int getType() {
        return type;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public NetworkParameters getParams() {
        return params;
    }

    public static boolean isCahoots(JSONObject obj)   {
        try {
            return obj.has("cahoots") && obj.getJSONObject("cahoots").has("type");
        }
        catch(JSONException je) {
            log.error("", je);
            return false;
        }
    }

    public static boolean isCahoots(String s)   {
        try {
            JSONObject obj = new JSONObject(s);
            return isCahoots(obj);
        }
        catch(JSONException je) {
            log.error("", je);
            return false;
        }
    }

    protected JSONObject toJSONObjectCahoots() throws Exception {
        JSONObject obj = new JSONObject();
        obj.put("version", version);
        obj.put("type", type);
        obj.put("step", step);
        if(params instanceof TestNet3Params)    {
            obj.put("params","testnet");
        }
        return obj;
    }

    public JSONObject toJSON() {

        JSONObject cObj = new JSONObject();

        try {
            JSONObject obj = toJSONObjectCahoots();
            cObj.put("cahoots", obj);
        }
        catch(JSONException je) {
            log.error("", je);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }

        return cObj;
    }

    protected void fromJSONObjectCahoots(JSONObject obj) throws Exception {
        if(obj.has("params") && obj.getString("params").equals("testnet"))    {
            this.params = TestNet3Params.get();
        }
        else    {
            this.params = MainNetParams.get();
        }
        if (obj.has("type") && obj.has("step") && obj.has("version") ) {
            this.version = obj.getInt("version");
            this.type = obj.getInt("type");
            this.step = obj.getInt("step");
        }
    }

    public void fromJSON(JSONObject cObj) {
        try {
            if(cObj.has("cahoots"))    {
                JSONObject obj = cObj.getJSONObject("cahoots");
                 fromJSONObjectCahoots(obj);
            }
        }
        catch(Exception e) {
            log.error("", e);
        }
    }

    public static Cahoots parse(String cahootsPayload) throws Exception {
        if (!isCahoots(cahootsPayload.trim())) {
            throw new Exception("Unrecognized #Cahoots");
        }
        JSONObject obj = new JSONObject(cahootsPayload);

        if (!isCahoots(obj)) {
            throw new Exception("Invalid #Cahoots");
        }
        int type = obj.getJSONObject("cahoots").getInt("type");
        CahootsType cahootsType = CahootsType.find(type).get();

        // instanciate
        Class<?> clazz = Class.forName(cahootsType.getCahootsClassName());
        Constructor<?> constructor = clazz.getConstructor(JSONObject.class);
        Cahoots cahoots = (Cahoots)constructor.newInstance(obj);
        return cahoots;
    }

    public String toJSONString() {
        return toJSON().toString();
    }
}
