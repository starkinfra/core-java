package com.starkcore.utils;

import com.google.gson.JsonObject;
import com.starkbank.ellipticcurve.Ecdsa;
import com.starkcore.Environment;
import com.starkcore.error.InputErrors;
import com.starkcore.error.InternalServerError;
import com.starkcore.error.UnknownError;
import com.starkcore.user.PublicUser;
import com.starkcore.user.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;


public class Response {

    public int status;
    public InputStream stream;

    public Response(int status, InputStream stream) {
        this.status = status;
        this.stream = stream;
    }

    public String content() throws java.io.IOException {
        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (stream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
        return textBuilder.toString();
    }

    public static Response fetch(String host, String sdkVersion, User user, String method, String path, JsonObject payload, Map<String, Object> query,
          String apiVersion, String language, int timeout, String prefix, Boolean raiseException) throws Exception {
        User checkedUser = Check.user(user);
        String checkedLanguage = Check.language(language);
        Integer checkedTimeout = Check.timeout(timeout);

        HashMap <String, String> serviceMap = new HashMap<String, String>() {
            {
                put(StarkHost.infra.toString(), "starkinfra");
                put(StarkHost.bank.toString(), "starkbank");
                put(StarkHost.sign.toString(), "starksign");
            }
        };

        String service = serviceMap.get(host);

        String agent = prefix != null ? prefix + "-Java-" + System.getProperty("java.version") + "-SDK-" + host + "-" + sdkVersion : "Java-" + System.getProperty("java.version") + "-SDK-" + host + "-" + sdkVersion;

        HashMap <String, String> urlMap = new HashMap<String, String>() {
            {
                put(Environment.production.toString(), "https://api." + service + ".com/");
                put(Environment.sandbox.toString(), "https://sandbox.api." + service + ".com/");
            }
        };

        if (query != null) {
            path += Url.encode(query);
        }
        if (path.startsWith("/")) {
            path = path.substring(1);
        }

        String url = urlMap.get(checkedUser.environment) + apiVersion + "/";

        String body = "";
        if (payload != null) {
            body = payload.toString();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", agent);
        headers.put("Accept-Language", checkedLanguage);
        headers.put("Content-Type", "application/json");

        headers.putAll(authenticationHeaders(checkedUser, body));

        Response response = executeMethod(url, path, body, method, headers, checkedTimeout);

        if (!raiseException) {
            return response;
        }

        if (response.status == 400) {
            throw new InputErrors(response.content());
        }
        if (response.status == 500) {
            throw new InternalServerError(response.content());
        }
        if (response.status != 200) {
            throw new UnknownError(response.content());
        }
        return response;
    }


    private static Response executeMethod(String baseUrl, String path, String body, String method, Map<String, String> headers, int timeout) throws Exception {
        ClientService service = makeInstance(baseUrl, timeout);
        retrofit2.Response<ResponseBody> response;
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), body);
        switch (method) {
            case "GET":
                response = service.get(path, headers).execute();
                break;
            case "POST":
                response = service.post(path, requestBody, headers).execute();
                break;
            case "PATCH":
                response = service.patch(path, requestBody, headers).execute();
                break;
            case "PUT":
                response = service.put(path, requestBody, headers).execute();
                break;
            case "DELETE":
                response = service.delete(path, headers).execute();
                break;
            default:
                throw new Exception("unknown HTTP method");
        }

        int status = response.code();

        InputStream contentStream;
        if (status == 200) {
            try (ResponseBody responseBody = response.body()) {
                assert responseBody != null;
                contentStream = responseBody.byteStream();
            }
        } else {
            try (ResponseBody responseBody = response.errorBody()) {
                assert responseBody != null;
                contentStream = responseBody.byteStream();
            }
        }
        return new Response(status, contentStream);
    }

    private static Map<String, String> authenticationHeaders(User user, String body) {
        if(user instanceof PublicUser) return new HashMap<>();

        String accessTime = String.valueOf(currentTimeMillis() / 1000L);
        String message = user.accessId() + ':' + accessTime + ':';
        if (body != null) {
            message += body;
        }
        String signature = Ecdsa.sign(message, user.privateKey()).toBase64();

        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Access-Id", user.accessId());
        headers.put("Access-Time", accessTime);
        headers.put("Access-Signature", signature);

        return headers;
    }

    private static ClientService makeInstance(String baseUrl, int timeout)
    {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(timeout, TimeUnit.SECONDS)
                .readTimeout(timeout, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client).build();
        return retrofit.create(ClientService.class);
    }
}
