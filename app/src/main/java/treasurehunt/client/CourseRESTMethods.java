package treasurehunt.client;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import android.util.Base64;

import org.json.JSONObject;

import treasurehunt.model.Course;
import treasurehunt.model.Courses;
import treasurehunt.model.marshalling.JsonObjectMapperBuilder;

import com.android.volley.*;
import com.android.volley.toolbox.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CourseRESTMethods {

    private final static String serviceSuffix = "courseService/";

    public static boolean put(RequestQueue queue,Course course) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        JSONObject jsonCourse = new JSONObject(mapper.writeValueAsString(course));

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.PUT,baseUrl+"putCourse", jsonCourse, future, future) {

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

    public static Course get(RequestQueue queue,String id) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,baseUrl+"getCourse/"+id, new JSONObject(), future, future);
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
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        return mapper.readValue(response.toString(),Course.class);

    }

    public static boolean delete(RequestQueue queue,String id) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<String> future = RequestFuture.newFuture();
        StringRequest getRequest = new StringRequest(Request.Method.DELETE,baseUrl+"deleteCourse/"+id, future, future);
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

    public static List<Course> getNearestCourses(RequestQueue queue,double latitude,double longitude,int radiusInMetres) throws Exception {

        String baseUrl = Configuration.baseUrl+serviceSuffix;

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET,
                baseUrl+String.format("getNearestCourses/%s/%s/%s",Double.toString(latitude),Double.toString(longitude),Integer.toString(radiusInMetres)),
                new JSONObject(), future, future);
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
        ObjectMapper mapper = JsonObjectMapperBuilder.buildJacksonObjectMapper();
        Courses courses = mapper.readValue(response.toString(),Courses.class);
        return courses.list;

    }

}
