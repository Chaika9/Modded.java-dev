package fr.modded.api.requests;

import fr.modded.api.ModdedImpl;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

public class Requester {
    private final ModdedImpl modded;
    private final OkHttpClient client;

    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf8");

    public Requester(ModdedImpl modded) {
        this.modded = modded;
        this.client = new OkHttpClient();
    }

    public <T> void request(Request<T> request) {
        execute(request);
    }

    public void execute(Request<?> apiRequest) {
        final okhttp3.Request.Builder builder = new okhttp3.Request.Builder();

        final String url = modded.getUrl() + apiRequest.getRoute().getRoute();
        builder.url(url);

        JSONObject body = apiRequest.getRequestBody();
        if (body == null) {
            body = new JSONObject();
        }

        final RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, body.toString());
        builder.method("POST", requestBody);

        builder.header("Content-Type", "application/json");

        final okhttp3.Request request = builder.build();
        final okhttp3.Response[] responses = new okhttp3.Response[4];
        okhttp3.Response lastResponse = null;

        try {
            int attempt = 0;
            do {
                final Call call = client.newCall(request);
                lastResponse = call.execute();
                responses[attempt] = lastResponse;

                if (lastResponse.code() < 500) {
                    break;
                }

                attempt++;
                try {
                    Thread.sleep(50L * attempt);
                } catch (InterruptedException ignored) {
                }
            } while (attempt < 3 && lastResponse.code() >= 500);

            if (lastResponse.code() >= 500) {
                final Response response = new Response(lastResponse);
                apiRequest.handleResponse(response);
                return;
            }
            apiRequest.handleResponse(new Response(lastResponse));
        } catch (SocketTimeoutException exception) {
            apiRequest.handleResponse(new Response(lastResponse, exception));
        } catch (Exception exception) {
            apiRequest.handleResponse(new Response(lastResponse, exception));
        } finally {
            for (okhttp3.Response r : responses) {
                if (r == null) {
                    break;
                }
                r.close();
            }
        }
    }
}
