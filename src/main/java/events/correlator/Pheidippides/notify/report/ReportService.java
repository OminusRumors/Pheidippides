package events.correlator.Pheidippides.notify.report;

import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import events.correlator.Pheidippides.models.GenericReportModel;


@Path("/reports")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ReportService {
	
	private Report report;
	
	public ReportService() throws ClassNotFoundException {
		this.report=new Report();
	}
	
	@GET
	@Path("/topdestinations")
	public Response topDestinations(@QueryParam("top") int top, @QueryParam("start") String start,
			@QueryParam("end") String end){
		try{
			GenericEntity<Map<String, Integer>> results=new GenericEntity<Map<String,Integer>>(report.topDestinations(top, start, end)) {};
			return Response.ok(results).build();
		}
		catch(Exception e){
			return Response.status(500).entity(e.getLocalizedMessage()).build();
		}
	}

	@GET
	@Path("/dropdestinations")
	public Response dropDestinations(@QueryParam("top") int top, @QueryParam("start") String start,
			@QueryParam("end") String end){
		try{
			GenericEntity<Map<String, Integer>> results=new GenericEntity<Map<String,Integer>>(report.droppedDestinations(top, start, end)) {};
			return Response.ok(results).build();
		}
		catch(Exception e){
			return Response.status(500).entity(e.getLocalizedMessage()).build();
		}
	}
	
	@GET
	@Path("/attacks")
	public Response attacksPerAddress(@QueryParam("start") String start, @QueryParam("end") String end){
		try{
			GenericEntity<List<GenericReportModel>> results=new GenericEntity<List<GenericReportModel>>(report.attacksPerAddress(start, end)) {};
			return Response.ok(results).build();
		}
		catch(Exception e){
			return Response.status(500).entity(e.getLocalizedMessage()).build();
		}
	}
	
	@GET
	@Path("/unknowndestinations")
	public Response unknownDestinations(@QueryParam("top") int top, @QueryParam("start") String start, @QueryParam("end") String end){
		try{
			GenericEntity<GenericReportModel> results=new GenericEntity<GenericReportModel>(report.unknownDestinations(top, start, end)) {};
			return Response.ok(results).build();
		}
		catch (Exception e){
			return Response.status(500).entity(e.getLocalizedMessage()).build();
		}
	}
}
