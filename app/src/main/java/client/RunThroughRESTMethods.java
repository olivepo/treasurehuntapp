package client;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import model.RunThrough;
import model.RunThroughs;


public class RunThroughRESTMethods {
	private final static String baseUrl = Configuration.baseUrl+"runThroughService/";

	public static boolean put(RunThrough runThrough) throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(HttpAuthenticationFeature.basic(Configuration.tomcatUser, Configuration.tomcatUserPassword));

		WebTarget webTarget= client.target(baseUrl).path("putRunThrough");
		
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.put(Entity.entity(runThrough, MediaType.APPLICATION_JSON));

		switch (response.getStatus()) {

		case 200 : case 201 : case 204 :
			return true;

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static RunThrough get(String id) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget= client.target(baseUrl).path("getRunThrough").path(id);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		switch (response.getStatus()) {

		case 204 :
			return null;

		case 200 :
			return (RunThrough) response.readEntity(RunThrough.class);

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static boolean delete(String id) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget= client.target(baseUrl).path("deleteRunThrough").path(id);
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

	public static List<RunThrough> getRunThroughs(String accountEmail) throws Exception {
		
		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl).path("getRunThroughs").path(accountEmail);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		// Status 200 is successful.
		if (response.getStatus() != 200) {
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

		RunThroughs runThroughs = response.readEntity(RunThroughs.class);

		return runThroughs.list;
	}
}
