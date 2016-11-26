package events.correlator.Pheidippides.notify.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import events.correlator.Pheidippides.database.DbConnector;

@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Report {
	
	private DbConnector dbc;
	
	public Report(){
		
	}
	
	public Report(DbConnector dbc){
		this.dbc=dbc;
	}
	
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String getThis(){
		return "It WORKS!!";
	}
	
	private String checkDateTime(String date){
		try{
			String startDate=new SimpleDateFormat("yyyy-MM-dd").format(date);
			return startDate;
		}
		catch(IllegalArgumentException e){
			String startDate=new SimpleDateFormat("HH:mm:ss").format(date);
			return startDate;
		}
		catch (NullPointerException e) {
			return null;
		}
		catch (Exception e){
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	@GET
	@Path("/topDestinations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Integer> topDestinations(@QueryParam("top") int top, @QueryParam("start") String start, 
			@QueryParam("end") String end){
		
		String startDate=checkDateTime(start);
		String startTime=checkDateTime(start);
		String endDate=checkDateTime(end);
		String endTime=checkDateTime(end);
		String sql="SELECT dstip, count(dstip) FROM firewall_traffic_log WHERE type='traffic' AND date BETWEEN " + startDate + 
				" AND " + endDate + " AND time BETWEEN " + startTime + " AND " + endTime + 
				" GROUP BY dstip ORDER BY count(dstip) DESC LIMIT " + Integer.toString(top) + ";";
		try {
			ResultSet result=dbc.customQuery(sql);
			Map<String, Integer> resultMap = new HashMap<String, Integer>();
			while(result.next()){
				if (result.getString(0)!="255.255.255.255"){
					resultMap.put(result.getString(0), result.getInt(1));
				}
			}
			return resultMap;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}
	
	
}
