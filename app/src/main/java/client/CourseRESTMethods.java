package client;

import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import model.Course;
import model.Courses;


public class CourseRESTMethods {
	private final static String baseUrl = Configuration.baseUrl+"courseService/";

	public static boolean put(Course course) throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(HttpAuthenticationFeature.basic(Configuration.tomcatUser, Configuration.tomcatUserPassword));

		WebTarget webTarget= client.target(baseUrl).path("putCourse");
		
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.put(Entity.entity(course, MediaType.APPLICATION_JSON));

		switch (response.getStatus()) {

		case 200 : case 201 : case 204 :
			return true;

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static Course get(String id) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget= client.target(baseUrl).path("getCourse").path(id);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		switch (response.getStatus()) {

		case 204 :
			return null;

		case 200 :
			return (Course) response.readEntity(Course.class);

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static boolean delete(String id) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget= client.target(baseUrl).path("deleteCourse").path(id);
		Invocation.Builder invocationBuilder = webTarget.request();
		Response response = invocationBuilder.delete();

		switch (response.getStatus())
		{
		case 200 :
			return true;

		case 400 :
			return false;

		default :
			throw new Exception("Failed : HTTP error code : "
					+ response.getStatus());
		}

	}

	public static List<Course> getNearestCourses(float latitude,float longitude) throws Exception {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl).path("getNearestCourses").path("0.0").path("0.0");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		// Status 200 is successful.
		if (response.getStatus() != 200) {
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

		Courses courses = response.readEntity(Courses.class);

		return courses.list;
	}
}
