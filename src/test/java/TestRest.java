import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.starkcore.Settings;
import com.starkcore.utils.Page;
import com.starkcore.utils.Rest;
import com.starkcore.utils.StarkHost;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import java.util.*;

import com.google.gson.*;


public class TestRest {

    @Test
    public void testRestGet() throws Exception {

        Settings.user = utils.User.defaultProject();

        Map<String, Object> params = new HashMap<>();
        params.put("before", "2022-02-01");
        params.put("limit", 1);

        Page transactions = Rest.getPage(
            "0.0.0",
            StarkHost.bank.toString(),
            "v2",
            null,
            Transaction.data,
            "pt-BR",
            15,
            params
        );
        Transaction transaction = (Transaction) transactions.entities.get(0);
        System.out.println(transaction);
    }

    @Test
    public void testRestInvalidTimeout() throws Exception {

        Settings.user = utils.User.defaultProject();
        Settings.timeout = -1; 

        try {
            Page transactions = Rest.getPage(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                null,
                Transaction.data,
                "pt-BR",
                15,
                null
            );
            Assert.fail("Expected an Exception to be thrown");
        } catch (Exception e) {
            Assert.assertEquals(e.getMessage(), "Timeout must be a positive integer");
        } finally {
            Settings.timeout = null;
        }
    }

    @Test
    public void testRestGetRaw() throws Exception {

        Settings.user = utils.User.defaultProject();

        String path = "/invoice";
        Map<String, Object> query = new HashMap<>();
        query.put("limit", 10);
        String request = Rest.getRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                query,
                null,
                null,
                true
        ).content();

        System.out.println(request);

        Gson gson = new Gson();
        JsonObject contentJson = gson.fromJson(request, JsonObject.class);

        JsonArray requests = contentJson.get("invoices").getAsJsonArray();
        JsonObject requestElement = requests.get(0).getAsJsonObject();

        Assert.assertNotNull(requestElement.get("id"));
    }

    @Test
    public void testRestPostRaw() throws Exception {

        Settings.user = utils.User.defaultProject();

        String path = "/invoice";
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Jaime Lannister" + UUID.randomUUID().toString());
        payload.put("amount", 100);
        payload.put("taxId", "20.018.183/0001-80");

        List<Object> holderList = new ArrayList<Object>();
        holderList.add(payload);

        Map<String, Object> data = new HashMap<>();
        data.put("invoices", holderList);

        String request = Rest.getRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                null,
                data,
                null,
                true
        ).content();

        System.out.println(request);

        Gson gson = new Gson();
        JsonObject contentJson = gson.fromJson(request, JsonObject.class);

        JsonArray requests = contentJson.get("invoices").getAsJsonArray();
        JsonObject requestElement = requests.get(0).getAsJsonObject();

        Assert.assertNotNull(requestElement.get("id"));
    }

    @Test
    public void testRestPatchRaw() throws Exception {

        String path = "/invoice";

        Map<String, Object> query = new HashMap<>();
        query.put("limit", 1);
        query.put("status", "created");

        String request = Rest.getRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                query,
                null,
                null,
                true
        ).content();

        Gson gson = new Gson();
        JsonObject contentJson = gson.fromJson(request, JsonObject.class);

        JsonArray requests = contentJson.get("invoices").getAsJsonArray();
        JsonObject requestElement = requests.get(0).getAsJsonObject();

        String requestId = requestElement.get("id").getAsString();

        path += "/" + requestId;

        HashMap<String, Object> data = new HashMap<>();;
        data.put("amount", 0);

        request = Rest.patchRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                null,
                data,
                null,
                true
        ).content();

        System.out.println(request);

        contentJson = gson.fromJson(request, JsonObject.class);

        JsonObject requestObject = contentJson.get("invoice").getAsJsonObject();

        Assert.assertNotNull(requestObject.get("id"));
    }

    @Test
    public void testRequestPut() throws Exception {

        Settings.user = utils.User.defaultProject();

        String path = "/split-profile";
        Map<String, Object> payload = new HashMap<>();
        payload.put("interval", "day");
        payload.put("delay", 0);

        List<Object> holderList = new ArrayList<Object>();
        holderList.add(payload);

        Map<String, Object> data = new HashMap<>();
        data.put("profiles", holderList);

        String request = Rest.putRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                null,
                data,
                null,
                true
        ).content();

        System.out.println(request);

        Gson gson = new Gson();
        JsonObject contentJson = gson.fromJson(request, JsonObject.class);

        JsonArray requests = contentJson.get("profiles").getAsJsonArray();
        JsonObject requestElement = requests.get(0).getAsJsonObject();

        Assert.assertNotNull(requestElement.get("delay"));
        Assert.assertNotNull(requestElement.get("interval"));
    }

    @Test
    public void testRequestDelete() throws Exception {

        Settings.user = utils.User.defaultProject();

        String path = "/transfer";
        Map<String, Object> payload = new HashMap<>();
        payload.put("name", "Jaime Lannister" + UUID.randomUUID().toString());
        payload.put("amount", 100);
        payload.put("taxId", "20.018.183/0001-80");
        payload.put("bankCode", "001");
        payload.put("branchCode", "1234");
        payload.put("accountNumber", "123456-0");
        payload.put("accountType", "checking");
        payload.put("externalId", UUID.randomUUID().toString());

        List<Object> holderList = new ArrayList<Object>();
        holderList.add(payload);

        Map<String, Object> data = new HashMap<>();
        data.put("transfers", holderList);

        String request = Rest.postRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                null,
                data,
                null,
                true
        ).content();

        System.out.println(request);

        Gson gson = new Gson();
        JsonObject contentJson = gson.fromJson(request, JsonObject.class);

        JsonArray requests = contentJson.get("transfers").getAsJsonArray();
        JsonObject requestElement = requests.get(0).getAsJsonObject();

        String requestElementId = requestElement.get("id").getAsString();

        path += "/" + requestElementId;

        request = Rest.deleteRaw(
                "0.0.0",
                StarkHost.bank.toString(),
                "v2",
                path,
                null,
                "pt-BR",
                15,
                null,
                null,
                null,
                true
        ).content();

        contentJson = gson.fromJson(request, JsonObject.class);

        requestElement = contentJson.get("transfer").getAsJsonObject();

        Assert.assertNotNull(requestElement.get("id"));
    }
}
