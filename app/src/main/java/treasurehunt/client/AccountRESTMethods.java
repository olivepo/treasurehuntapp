package treasurehunt.client;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.util.Base64;

import org.json.JSONObject;

import treasurehunt.model.Account;
import treasurehunt.model.Accounts;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AccountRESTMethods {

    private final static String serviceSuffix = "accountService/";

    public static boolean put(RequestQueue queue,Account account) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        ObjectMapper mapper = new ObjectMapper();
        JSONObject jsonAccount = new JSONObject(mapper.writeValueAsString(account));

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT,baseUrl+"putAccount", jsonAccount, future, future) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                String username = Configuration.tomcatUser;
                String password = Configuration.tomcatUserPassword;
                String auth = new String(username + ":" + password);
                byte[] data = auth.getBytes();
                String base64 = Base64.encodeToString(data, Base64.CRLF);
                final String basicAuth = "Basic " + base64;
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", basicAuth);
                // headers.put("Content-type","application/json");
                return headers;

            }

        };;
        queue.add(getRequest);
        try {
            future.get(10000, TimeUnit.MILLISECONDS); // this will block
        } catch (TimeoutException  e) {
            return false;
        } catch (InterruptedException | ExecutionException  e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError error = (VolleyError) e.getCause();
                throw new Exception("Failed : HTTP error code : " + error.networkResponse.statusCode);
            }
        }

        return true;

    }

    public static Account get(RequestQueue queue,String email) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,baseUrl+"getAccount/"+email, new JSONObject(), future, future);
        queue.add(getRequest);
        JSONObject response = null;
        try {
            response = future.get(10000, TimeUnit.MILLISECONDS); // this will block
        } catch (TimeoutException  e) {
            return null;
        } catch (InterruptedException | ExecutionException  e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError error = (VolleyError) e.getCause();
                switch (error.networkResponse.statusCode) {

                    case 204:
                        return null;

                    default:
                        throw new Exception("Failed : HTTP error code : " + error.networkResponse.statusCode);
                }
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response.toString(),Account.class);

    }

    public static boolean delete(RequestQueue queue,String email) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest getRequest = new StringRequest(Request.Method.DELETE,baseUrl+"deleteAccount/"+email, future, future);
        queue.add(getRequest);
        try {
            future.get(10000, TimeUnit.MILLISECONDS); // this will block
        } catch (TimeoutException  e) {
            return false;
        } catch (InterruptedException | ExecutionException  e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError error = (VolleyError) e.getCause();
                throw new Exception("Failed : HTTP error code : " + error.networkResponse.statusCode);
            }
        }

        return true;
    }

    public static List<Account> getAll(RequestQueue queue) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,baseUrl+"getAccounts", new JSONObject(), future, future);
        queue.add(getRequest);
        JSONObject response = null;
        try {
            response = future.get(10000, TimeUnit.MILLISECONDS); // this will block
        } catch (TimeoutException  e) {
            return null;
        } catch (InterruptedException | ExecutionException  e) {
            if (VolleyError.class.isAssignableFrom(e.getCause().getClass())) {
                VolleyError error = (VolleyError) e.getCause();
                throw new Exception("Failed : HTTP error code : " + error.networkResponse.statusCode);
            }
        }
        ObjectMapper mapper = new ObjectMapper();
        Accounts accounts = mapper.readValue(response.toString(),Accounts.class);
        return accounts.list;

    }

}
