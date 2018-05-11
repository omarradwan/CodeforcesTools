import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Scanner {

    private JSONArray array;
    private int idx;

    public void readArray(String path) throws JSONException {
        array = new JSONArray(path);
        idx = array.length() - 1;
    }

    public JSONObject readObject(String path) throws JSONException {
        return new JSONObject(path);
    }

    public JSONObject nextObject() throws JSONException {
        return idx == -1? null : array.getJSONObject(idx--);
    }
}
