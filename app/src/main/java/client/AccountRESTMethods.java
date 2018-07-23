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

import treasurehunt.model.Account;
import treasurehunt.model.Accounts;


public class AccountRESTMethods {

	private final static String baseUrl = Configuration.baseUrl+"accountService/";

	public static boolean put(Account account) throws Exception {

		Client client = ClientBuilder.newClient();
		client.register(HttpAuthenticationFeature.basic(Configuration.tomcatUser, Configuration.tomcatUserPassword));

		WebTarget webTarget = client.target(baseUrl).path("putAccount");
		
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.put(Entity.entity(account, MediaType.APPLICATION_JSON));

		switch (response.getStatus()) {

		case 200 : case 201 : case 204 :
			return true;

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static Account get(String email) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl).path("getAccount").path(email);
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		switch (response.getStatus()) {

		case 204 :
			return null;

		case 200 :
			return (Account) response.readEntity(Account.class);

		default :
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

	}

	public static boolean delete(String email) throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl).path("deleteAccount").path(email);
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

	public static List<Account> getAll() throws Exception {

		Client client = ClientBuilder.newClient();
		WebTarget webTarget = client.target(baseUrl).path("getAccounts");
		Invocation.Builder invocationBuilder = webTarget.request(MediaType.APPLICATION_JSON);
		Response response = invocationBuilder.get();

		// Status 200 is successful.
		if (response.getStatus() != 200) {
			throw new Exception("Failed : HTTP error code : "+response.getStatus());
		}

		Accounts accounts = response.readEntity(Accounts.class);

		return accounts.list;
	}

}
