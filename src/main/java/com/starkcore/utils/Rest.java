package com.starkcore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.starkcore.user.User;


public final class Rest {
    public static Page getPage(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String language, int timeout, Map<String, Object> params) throws Exception {
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            Api.endpoint(resource),
            null,
            params,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonElement cursorJson = contentJson.get("cursor");
        String cursor = cursorJson.isJsonNull() ? null : cursorJson.getAsString();

        List<SubResource> entities = new ArrayList<>();
        JsonArray jsonArray = contentJson.get(Api.getLastNamePlural(resource)).getAsJsonArray();
        for (JsonElement resourceElement : jsonArray) {
            JsonObject jsonObject = resourceElement.getAsJsonObject();
            entities.add(gson.fromJson(jsonObject, (Type) resource.cls));
        }

        return new Page(entities, cursor);
    }

    public static <T extends SubResource> Generator<T> getStream(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String language, int timeout, Map<String, Object> params) {
        return new Generator<T>() {
            public void run() throws Exception {
                Map<String, Object> paramsCopy = new HashMap<>();
                for (Map.Entry<String, Object> entry: params.entrySet()) {
                    paramsCopy.put(entry.getKey(), entry.getValue());
                }
                Integer limit = (Integer) paramsCopy.get("limit");
                String cursor = null;
                do {
                    paramsCopy.put("cursor", cursor);
                    if (limit != null) {
                        paramsCopy.put("limit", limit > 100 ? "100" : limit.toString());
                        limit -= 100;
                    }
                    String content = Response.fetch(
                        host,
                        sdkVersion,
                        user,
                        "GET",
                        Api.endpoint(resource),
                        null,
                        paramsCopy,
                        apiVersion,
                        language,
                        timeout,
                        null,
                        true
                    ).content();
                    Gson gson = GsonEvent.getInstance();
                    JsonObject contentJson = gson.fromJson(content, JsonObject.class);
                    JsonElement cursorJson = contentJson.get("cursor");
                    cursor = cursorJson != null ? (cursorJson.isJsonNull() ? "" : cursorJson.getAsString()) : null;
                    JsonArray jsonArray = contentJson.get(Api.getLastNamePlural(resource)).getAsJsonArray();
                    for (JsonElement resourceElement : jsonArray) {
                        JsonObject jsonObject = resourceElement.getAsJsonObject();
                        T element = gson.fromJson(jsonObject, (Type) resource.cls);
                        if(element == null)
                            break;
                        this.yield(element);
                    }
                } while (cursor != null && !cursor.isEmpty() && (limit == null || limit > 0));
            }
        };
    }

    public static <T extends SubResource> Generator<T> getSimpleList(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String language, int timeout, Map<String, Object> params) {
        return new Generator<T>() {
            public void run() throws Exception {
                Map<String, Object> paramsCopy = new HashMap<>();
                for (Map.Entry<String, Object> entry: params.entrySet()) {
                    paramsCopy.put(entry.getKey(), entry.getValue());
                }
                String content = Response.fetch(
                    host,
                    sdkVersion,
                    user,
                    "GET",
                    Api.endpoint(resource),
                    null,
                    paramsCopy,
                    apiVersion,
                    language,
                    timeout,
                    null,
                    true
                ).content();
                Gson gson = GsonEvent.getInstance();
                JsonObject contentJson = gson.fromJson(content, JsonObject.class);
                JsonArray jsonArray = contentJson.get(Api.getLastNamePlural(resource)).getAsJsonArray();
                for (JsonElement resourceElement : jsonArray) {
                    JsonObject jsonObject = resourceElement.getAsJsonObject();
                    T element = gson.fromJson(jsonObject, (Type) resource.cls);
                    if(element == null)
                        break;
                    this.yield(element);
                }
            }
        };
    }

    public static <T extends SubResource> T getId(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, String language, int timeout) throws Exception {
        return getId(
            sdkVersion,
            host,
            apiVersion,
            user,
            resource,
            id,
            language,
            timeout,
            null
        );
    }

    public static <T extends SubResource> T getId(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, String language, int timeout, Map<String, Object> query) throws Exception {
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            Api.endpoint(resource, id),
            null,
            query,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(resource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) resource.cls);
    }

    public static <T extends SubResource> List<T> post(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, List<T> entities, String language, int timeout) throws Exception {
        return post(
            sdkVersion,
            host,
            apiVersion,
            user,
            resource,
            entities,
            null,
            language,
            timeout
        );
    }

    public static <T extends SubResource> List<T> post(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, List<T> entities, Map<String, Object> data, String language, int timeout) throws Exception {
        JsonObject payload = new JsonObject();
        payload.add(Api.getLastNamePlural(resource), new Gson().toJsonTree(entities).getAsJsonArray());
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "POST",
            Api.endpoint(resource),
            payload,
            data,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        JsonObject contentJson = new Gson().fromJson(content, JsonObject.class);
        List<T> postEntities = new ArrayList<>();
        JsonArray jsonArray = contentJson.get(Api.getLastNamePlural(resource)).getAsJsonArray();
        for (JsonElement resourceElement : jsonArray) {
            JsonObject jsonObject = resourceElement.getAsJsonObject();
            postEntities.add(GsonEvent.getInstance().fromJson(jsonObject, (Type) resource.cls));
        }
        return postEntities;
    }

    public static <T extends SubResource> T patch(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, String language, int timeout, Map<String, Object> data) throws Exception {
        JsonObject payload = new Gson().fromJson(new Gson().toJson(data), JsonObject.class);
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "PATCH",
            Api.endpoint(resource, id),
            payload,
            null,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(resource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) resource.cls);
    }

    public static InputStream getContent(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String subResourceName, String id, String language, int timeout, Map<String, Object> options) throws Exception {
        return Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            Api.endpoint(resource, id) + "/" + subResourceName,
            null,
            options,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).stream;
    }

    public static <T extends SubResource> T getSubResource(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, SubResource.ClassData subResource, String language, int timeout, Map<String, Object> options) throws Exception {
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            Api.endpoint(resource, id) + "/" + Api.endpoint(subResource),
            null,
            options,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = new Gson().fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(subResource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) subResource.cls);
    }

    public static <T extends SubResource> List<T> getSubResources(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, SubResource.ClassData subResource, String language, int timeout, Map<String, Object> options) throws Exception {
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            Api.endpoint(resource, id) + "/" + Api.endpoint(subResource),
            null,
            options,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        JsonObject contentJson = new Gson().fromJson(content, JsonObject.class);
        JsonArray jsonArray = contentJson.get(Api.getLastNamePlural(subResource)).getAsJsonArray();
        List<T> entities = new ArrayList<>();
        for (JsonElement resourceElement : jsonArray) {
            JsonObject jsonObject = resourceElement.getAsJsonObject();
            entities.add(GsonEvent.getInstance().fromJson(jsonObject, (Type) subResource.cls));
        }
        return entities;
    }

    public static <T extends SubResource> T postSubResource(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, SubResource.ClassData subResource, String language, int timeout, SubResource entity) throws Exception {
        JsonObject payload = (JsonObject) new Gson().toJsonTree((entity));
        String content = Response.fetch(
                host,
                sdkVersion,
                user,
                "POST",
                Api.endpoint(resource, id) + "/" + Api.endpoint(subResource),
                payload,
                null,
                apiVersion,
                language,
                timeout,
                null,
                true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(subResource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) subResource.cls);
    }

    public static <T extends SubResource> T delete(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, String language, int timeout, Map<String, Object> query) throws Exception {
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "DELETE",
            Api.endpoint(resource, id),
            null,
            query,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(resource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) resource.cls);
    }

    public static <T extends SubResource> T delete(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, String id, String language, int timeout) throws Exception {
        return delete(
            sdkVersion,
            host,
            apiVersion,
            user,
            resource,
            id,
            language,
            timeout,
            null
        );
    }

    public static <T extends SubResource> T postSingle(String sdkVersion, String host, String apiVersion, User user, Resource.ClassData resource, SubResource entity, String language, int timeout) throws Exception {
        JsonObject payload = (JsonObject) new Gson().toJsonTree((entity));
        String content = Response.fetch(
            host,
            sdkVersion,
            user,
            "POST",
            Api.endpoint(resource),
            payload,
            null,
            apiVersion,
            language,
            timeout,
            null,
            true
        ).content();
        Gson gson = GsonEvent.getInstance();
        JsonObject contentJson = gson.fromJson(content, JsonObject.class);
        JsonObject jsonObject = contentJson.get(Api.getLastName(resource)).getAsJsonObject();
        return gson.fromJson(jsonObject, (Type) resource.cls);
    }

    public static Response getRaw(String sdkVersion, String host, String apiVersion, String path, User user, String language, int timeout, Map<String, Object> query, Map<String, Object> data, String prefix, Boolean raiseException) throws IOException, Exception {
        return Response.fetch(
            host,
            sdkVersion,
            user,
            "GET",
            path,
            null,
            query,
            apiVersion,
            language,
            timeout,
            prefix,
            raiseException
        );
    }

    public static Response postRaw(String sdkVersion, String host, String apiVersion, String path, User user, String language, int timeout, Map<String, Object> query, Map<String, Object> data, String prefix, Boolean raiseException) throws IOException, Exception {
        JsonObject payload = new Gson().fromJson(new Gson().toJson(data), JsonObject.class);
        return Response.fetch(
                host,
                sdkVersion,
                user,
                "POST",
                path,
                payload,
                query,
                apiVersion,
                language,
                timeout,
                prefix,
                raiseException
        );
    }

    public static Response patchRaw(String sdkVersion, String host, String apiVersion, String path, User user, String language, int timeout, Map<String, Object> query, Map<String, Object> data, String prefix, Boolean raiseException) throws IOException, Exception {
        JsonObject payload = new Gson().fromJson(new Gson().toJson(data), JsonObject.class);
        return Response.fetch(
                host,
                sdkVersion,
                user,
                "PATCH",
                path,
                payload,
                query,
                apiVersion,
                language,
                timeout,
                prefix,
                raiseException
        );
    }

    public static Response putRaw(String sdkVersion, String host, String apiVersion, String path, User user, String language, int timeout, Map<String, Object> query, Map<String, Object> data, String prefix, Boolean raiseException) throws IOException, Exception {
        JsonObject payload = new Gson().fromJson(new Gson().toJson(data), JsonObject.class);
        return Response.fetch(
                host,
                sdkVersion,
                user,
                "PUT",
                path,
                payload,
                query,
                apiVersion,
                language,
                timeout,
                prefix,
                raiseException
        );
    }

    public static Response deleteRaw(String sdkVersion, String host, String apiVersion, String path, User user, String language, int timeout, Map<String, Object> query, Map<String, Object> data, String prefix, Boolean raiseException) throws IOException, Exception {
        return Response.fetch(
                host,
                sdkVersion,
                user,
                "DELETE",
                path,
                null,
                query,
                apiVersion,
                language,
                timeout,
                prefix,
                raiseException
        );
    }
}
