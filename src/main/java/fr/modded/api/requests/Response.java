package fr.modded.api.requests;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class Response {
    public static final int ERROR_CODE = -1;

    private final int code;
    private final InputStream body;
    private final okhttp3.Response rawResponse;
    private Exception exception;

    public Response(okhttp3.Response response, Exception exception) {
        this(response, response != null ? response.code() : ERROR_CODE);
        this.exception = exception;
    }

    public Response(okhttp3.Response response, int code) {
        this.code = code;
        this.rawResponse = response;

        if (response == null) {
            this.body = null;
        } else {
            body = response.body().byteStream();
        }
    }

    public Response(okhttp3.Response response) {
        this(response, response.code());
    }

    public boolean isEmpty() {
        if (body == null) {
            return true;
        }

        try {
            return body.read(new byte[0]) == -1;
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public String getRawObject() {
        final ByteArrayOutputStream result = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try {
            for (int length; (length = body.read(buffer)) != -1; ) {
                result.write(buffer, 0, length);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return result.toString();
    }

    public boolean isOk() {
        return code > 199 && code < 300;
    }

    public int getCode() {
        return code;
    }

    public JSONObject getObject() {
        return new JSONObject(getRawObject());
    }

    public JSONArray getArray() {
        return new JSONArray(getRawObject());
    }
}
