import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class Scanner {

    private JSONParser parser;
    private JSONArray array;
    private int idx;

    public Scanner() {
        parser = new JSONParser();
    }

    public void readArray(String path) throws IOException, ParseException {
        array = (JSONArray) parser.parse(new FileReader(path));
        idx = array.size() - 1;
    }

    public JSONObject readObject(String path) throws IOException, ParseException {
        return (JSONObject) parser.parse(new FileReader(path));
    }

    public JSONObject nextObject() {
        return idx == -1? null : (JSONObject) array.get(idx--);
    }
}
