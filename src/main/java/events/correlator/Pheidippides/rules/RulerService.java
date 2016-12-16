package events.correlator.Pheidippides.rules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import events.correlator.Pheidippides.database.DbConnector;

@Path("/rules")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RulerService {
	
	private MsRuler msRuler;
	private DbConnector dbc;
	
	public RulerService() throws ClassNotFoundException{
		this.dbc=new DbConnector();
		this.msRuler=new MsRuler(dbc);
	}

	@GET
	@Path("/logonfails")
	public Response check4625(@QueryParam("start") String start, @QueryParam("end") String end){
//		try{
//			SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			Calendar st=Calendar.getInstance();
//			Calendar en=Calendar.getInstance();
//			st.setTime(sdf.parse(start));
//			en.setTime(sdf.parse(end));
			msRuler.checkId4625(start, end);
			return Response.ok("Check your hotmail and DvU mail!").build();
//		}
//		catch(IllegalArgumentException | NullPointerException ex){
//			return Response.status(500).entity(ex.getLocalizedMessage()).build();
//		}
//		catch(Exception ex){
//			return Response.status(500).entity(ex.getLocalizedMessage()).build();
//		}
	}
	
}