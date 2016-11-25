package events.correlator.resources.filter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import events.correlator.database.DbConnector;
import events.correlator.resources.Event.Event;
import events.correlator.resources.Event.MsEvent;
import events.correlator.resources.Event.FwEvent;

public class Filter {
	
	private DbConnector dbc;
	private List<MsEvent> msUnfilteredList;
	private List<FwEvent> fwUnfilteredList;
	private List<MsEvent> msFilteredList;
	private List<FwEvent> fwFilteredList;
	
	public Filter() throws ClassNotFoundException{
		this.dbc=new DbConnector();
		fwFilteredList=new ArrayList<FwEvent>();
		msFilteredList=new ArrayList<MsEvent>();
	}
	
	public Filter(DbConnector dbc){
		this.dbc=dbc;
		fwFilteredList=new ArrayList<FwEvent>();
		msFilteredList=new ArrayList<MsEvent>();
	}
	
	public void getAllByType(String sourceLog, Date start, Date end){
		
		switch (sourceLog){
		case "security":
			msUnfilteredList=dbc.getSecurityLog(false, start, end);
			break;
		case "mssql":
			msUnfilteredList=dbc.getMssqlLog(false, start, end);
			break;
		case "fwEvents":
			fwUnfilteredList=dbc.getFwEventLog(false, start, end);
			break;
		case "fwTraffic":
			fwUnfilteredList=dbc.getFwTrafficLog(false, start, start);
			break;
		}
	}
	
	public void filterMsEvents(List<MsEvent> eventList){
		
		for (MsEvent e : eventList){
			switch (e.getEventId()){
			case 4624:
				dbc.setMsFiltered(e);
				break;
			case 4625:
				dbc.setMsFiltered(e);
				break;
			case 4634:
				dbc.setMsFiltered(e);
				break;
			case 4658:
				dbc.setMsFiltered(e);
				break;
			case 4661:
				dbc.setMsFiltered(e);
				break;
			case 4768:
				dbc.setMsFiltered(e);
				break;
			case 4769:
				dbc.setMsFiltered(e);
				break;
			case 4776:
				dbc.setMsFiltered(e);
				break;
			case 5136:
				dbc.setMsFiltered(e);
				break;
			case 5158:
				dbc.setMsFiltered(e);
				break;
			case 6272:
				dbc.setMsFiltered(e);
				break;
			case 6273:
				dbc.setMsFiltered(e);
				break;
			case 6278:
				dbc.setMsFiltered(e);
				break;
			default:
				break;
			}
		}
	}
	
	public void filterFwEvents(List<FwEvent> eventList){
		for (FwEvent e : eventList){
			if (e.getType()=="traffic" && e.getAction().matches("den.*")){
				dbc.setFwFiltered(e);
			}
			if (e.getSubtype().matches("app.*") && !e.getAction().matches("pas.*")){
				dbc.setFwFiltered(e);
			}
			if (e.getSubtype().matches("dlp")){
				dbc.setFwFiltered(e);
			}
			if (e.getSubtype().matches("anom.*")){
				dbc.setFwFiltered(e);
			}
			if (e.getSubtype().matches("web.*") && (!e.getAction().matches("allo.*")) || !e.getAction().matches("exem.*") ||
					!e.getAction().matches("pas.*")){
				dbc.setFwFiltered(e);
			}
			if (e.getSubtype().matches("system") && e.getAction().matches("interface-stat-change")){
				dbc.setFwFiltered(e);
			}
		}
	}

}
