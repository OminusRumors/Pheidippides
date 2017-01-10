package events.correlator.Pheidippides.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import events.correlator.Pheidippides.database.DbConnector;

@Path("/servicerequests")
@Consumes(MediaType.APPLICATION_JSON)

public class ServiceRequest {
	
	private DbConnector dbc;

	public ServiceRequest() throws ClassNotFoundException {
		try {
			dbc = new DbConnector();
		} catch (ClassNotFoundException e) {

			System.out.println("Inside ServiceRequest constructor");
			System.out.println(e.getClass().getName());
		}
	}

	public ServiceRequest(DbConnector dbc) {
		this.dbc = dbc;
	}
	
	@GET
	@Path("/ip")
	public boolean checkIP(@QueryParam("ip") String ip){
		String address="http://ip-api.com/json/"+ip;
		try {
			//get the country and city from "ip-api" and save them to DB
			JSONObject data=new JSONObject(makeRequest(address));
			String city=data.getString("city");
			String country=data.getString("country");
			return dbc.insertBlackList(ip, country, city);
			
		} catch (JSONException e) {
			System.out.println("Error while parsing JSON. ServiceREquest.checkIp()");
		}
		return false;
		
	}
	
	public String makeRequest(String address) {

		  try {
			URL url = new URL(address);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/json");
			conn.connect();

			if (conn.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode() +": "+conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

			String result = "";
			String output;
//			System.out.println("Output from Server .... \n");
			while ((output = br.readLine()) != null) {
//				System.out.println(output);
				result += output + " \n ";
			}

			conn.disconnect();
			return result;

		  } catch (MalformedURLException e) {

			e.printStackTrace();

		  } catch (IOException e) {

			e.printStackTrace();

		  }
		return null;

		}
}
