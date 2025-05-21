package dataAccess;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class DataAccessException extends Exception {
    private int statusCode;

    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(String message, Throwable ex) {
        super(message, ex);
    }

    public DataAccessException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", statusCode));
    }

    public static dataAccess.DataAccessException fromJson(InputStream stream) {
        var map = new Gson().fromJson(new InputStreamReader(stream), HashMap.class);
        var status = ((Double)map.get("status")).intValue();
        String message = map.get("message").toString();
        return new dataAccess.DataAccessException(status, message);
    }

    public int StatusCode() {
        return statusCode;
    }
}