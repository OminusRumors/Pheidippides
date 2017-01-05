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

			msRuler.checkId4625(start, end);
			return Response.ok("Check your hotmail and DvU mail!").build();

	}
	
	@GET
	@Path("/authrequest")
	public Response check4768(@QueryParam("start") String start, @QueryParam("end") String end){
		msRuler.checkId4768(start, end);
		return Response.ok("Check your emails for 4768 event.").build();
	}
	
	@GET
	@Path("/servicerequest")
	public Response check4769(@QueryParam("start") String start, @QueryParam("end") String end){
		msRuler.checkId4769(start, end);
		return Response.ok("Check your emails for 4769 event.").build();
	}
	
	@GET
	@Path("/validation")
	public Response check4776(@QueryParam("start") String start, @QueryParam("end") String end){
		msRuler.checkId4776(start, end);
		return Response.ok("Check your emails for 4769 event.").build();
	}
	
	@GET
	@Path("/modification")
	public Response check5136(@QueryParam("start") String start, @QueryParam("end") String end){
		msRuler.checkId5136_2(start, end);
		return Response.ok("Check your emails for 5136 and 4662 events.").build();
	}
}
