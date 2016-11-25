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
	
	@GET
	@Path("/topDestinations")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, Integer> topDestinations(@QueryParam("top") int top, @QueryParam("start") Date start, @QueryParam("end") Date end){
		Map<String, Integer> resultMap=new HashMap<String, Integer>();
		String startDate=new SimpleDateFormat("yyyy-MM-dd").format(start);
		String startTime=new SimpleDateFormat("HH:mm:ss").format(start.getTime());
		String endDate=new SimpleDateFormat("yyyy-MM-dd").format(end);
		String endTime=new SimpleDateFormat("HH:mm:ss").format(end.getTime());
		String sql="SELECT dstip, count(dstip) FROM firewall_traffic_log WHERE type='traffic' AND date BETWEEN " + startDate + " AND " + endDate + " AND time BETWEEN " + startTime + " AND " + endTime + " GROUP BY dstip ORDER BY count(dstip) DESC LIMIT " + Integer.toString(top) + ";";
		ResultSet result=dbc.customQuery(sql);
		try {
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
