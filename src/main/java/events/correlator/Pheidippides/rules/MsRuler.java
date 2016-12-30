package events.correlator.Pheidippides.rules;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import events.correlator.Pheidippides.database.DbConnector;
import events.correlator.Pheidippides.notify.alert.Alert;
//import events.correlator.Pheidippides.models.Event;
import events.correlator.Pheidippides.models.MsEvent;
import events.correlator.Pheidippides.utilities.Helper;

public class MsRuler {
	private DbConnector dbc;
//	private Alert alert;

	public MsRuler(DbConnector dbc) {
		this.dbc = dbc;
//		this.alert=new Alert();
	}

	public MsRuler() {
		
	}

	private Calendar getPrevious(Calendar current, int window) {
		
		Calendar ret=Calendar.getInstance();
		ret=current;
		ret.add(Calendar.SECOND, -window);
		return ret;
		
	}
	
	private Calendar getNext(Calendar current, int window) {
		
		Calendar ret=Calendar.getInstance();
		ret=current;
		ret.add(Calendar.SECOND, window);
		return ret;
		
	}

	//4625: failed logon
	public void checkId4625(String start, String end) {

		List<MsEvent> eventList = dbc.getMsByEventId(4625, true, start, end); // get all events with id 4625
		
		for (MsEvent e:eventList){
			System.out.println(e.getKeyId());
		}
		
		List<MsEvent> eventUserList = new ArrayList<MsEvent>();
		List<MsEvent> slidWinList=new ArrayList<MsEvent>();
		List<String> targetUserList=new ArrayList<String>();
		
		
		//loop for creating a distinct username list
		for (MsEvent e : eventList) {
			if (!targetUserList.contains(e.getTargetUsername())){
				targetUserList.add(e.getTargetUsername());
			}
		}
		
		for (String user:targetUserList){
			eventUserList.clear();
			
			//loop for creating a list with all the events from the specific user
			for (MsEvent e:eventList){
				if (Objects.equals(e.getTargetUsername(),user)){
					eventUserList.add(e);
				}
			}
//			eventList.removeAll(eventUserList);
			
			for (MsEvent ev:eventUserList){
				List<String> ipList=new ArrayList<String>();

				//hit the db to get all with id=4625 between the slid window algorithm
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				slidWinList=dbc.getMsByEventId(4625, true, sdf.format(getPrevious(ev.getCreated(), 3).getTime()),
						sdf.format(getNext(ev.getCreated(), 3).getTime()));
				
				//loop for getting all the events of the specified user in a defined time range and save them to "finalList"
				for (MsEvent e : slidWinList){
					if (!ipList.contains(e.getIpAddress())){
						ipList.add(e.getIpAddress());
					}
				}

				System.out.println("Sliding window list: " + slidWinList.size());

				if (slidWinList.size()>=4){
					//alert, scan
					System.out.println("Inside if in check 4625.");
					Map<String,String> reportData=new HashMap<String, String>();
					reportData.put("count", Integer.toString(slidWinList.size()));
					String msg=String.format("User %s failed %d times to logon from ip: %s", ev.getTargetUsername(),
							slidWinList.size(), ipList.toString());
					reportData.put("title", "Logon failure threshold reached");
					reportData.put("message", msg);
					reportData.put("eventId", "4625");
					reportData.put("eventDesc", "Logon failure");
					if (Helper.getStatus().containsKey(ev.getStatus())){
						reportData.put("reason", Helper.getStatus().get(ev.getStatus()));	
					}
					else{
						reportData.put("reason", "unknown");
					}
					String[] rec={"georgevassiliadis@hotmail.com", "georgios.vasileiadis@diagnostiekvooru.nl"};
					Alert.sendEmail(rec, reportData);
				}
			}
		}
	}

	//kerberos auth ticket was requested
	public void checkId4768(String start, String end) {

		List<MsEvent> list4768 = dbc.getMsByEventId(4768, true, start, end);
		List<MsEvent> newList4768 = new ArrayList<MsEvent>();
		Map<String, List<String>> username_ip = new HashMap<String, List<String>>();

		// create new list with failed attempts of kerberos auth ticket events(4768)
		for (MsEvent e : list4768) {
			if (e.getKeywords().matches("0x8010(.*)")) {
				newList4768.add(e);
			}
		}
		for (MsEvent e : newList4768) {
			//if username exists in list and ipaddress does not exist for that username
			if (username_ip.containsKey(e.getTargetUsername()) && 
					!username_ip.get(e.getTargetUsername()).contains(e.getIpAddress())) {
				List<String> addip = new ArrayList<String>(username_ip.get(e.getTargetUsername()));
				addip.add(e.getIpAddress());
				username_ip.put(e.getTargetUsername(), addip);
			} else {
				List<String> ip=new ArrayList<String>();
				ip.add(e.getIpAddress());
				username_ip.put(e.getTargetUsername(), ip);
			}
		}
		Map<String, String> reportData = null;
		
		for (Map.Entry<String, List<String>> e:username_ip.entrySet()){
			reportData=new HashMap<String, String>();
			reportData.put("title", "Failed auth");
			reportData.put("count", Integer.toString(e.getValue().size()));
			String msg=String.format("Kerberos authentication failed %d times to verify the user %s from ip: %s", 
					e.getValue().size(), e.getKey(), e.getValue());
			reportData.put("message", msg);
			reportData.put("eventId", Integer.toString(4768));
			reportData.put("reason", "unknown");
		}
		String[] rec={"georgevassiliadis@hotmail.com", "georgios.vasileiadis@diagnostiekvooru.nl"};
		Alert.sendEmail(rec, reportData);
	}

	//kerberos service ticket was requested
	public void checkId4769(String start, String end) {
		List<MsEvent> orgEventList = dbc.getMsByEventId(4769, true, start, end);
		List<MsEvent> filtList = new ArrayList<MsEvent>();
		List<MsEvent> finalList = new ArrayList<MsEvent>();

		for (MsEvent e : orgEventList) {
			if (e.getKeywords().matches("0x801(.*)")) {
				filtList.add(e);
			}
		}

		for (MsEvent e1 : filtList) {
			for (MsEvent e2 : filtList){
				if ((e1.getCreated().getTimeInMillis() - e2.getCreated().getTimeInMillis() >= 3000 && 
						e1.getCreated().getTimeInMillis() - e2.getCreated().getTimeInMillis() <= -3000) || (e1==e2)){
					finalList.add(e1);
				}
			}
		}

		if (finalList.size() >= 4) {
			Map<String, String> report=new HashMap<>();
			List<String> ipList=new ArrayList<>();
			for (MsEvent e:finalList){
				if (!ipList.contains(e.getIpAddress())){
					ipList.add(e.getIpAddress());
				}
			}
			report.put("title", "Kerberos service ticket failure.");
			String msg=String.format("Kerberos service ticket request was denied %d times for user: %s from IP: %s",
					finalList.size(), finalList.get(0).getTargetUsername(), ipList);
			report.put("message", msg);
			String[] rec={"georgevassiliadis@hotmail.com", "georgios.vasileiadis@diagnostiekvooru.nl"};
			Alert.sendEmail(rec, report);
		}
	}

	//credentials validation attempt
	public void checkId4776(String start, String end) {
		List<MsEvent> orgList = dbc.getMsByEventId(4776, true, start, end);
		List<MsEvent> filtList = new ArrayList<MsEvent>();

		for (MsEvent e : orgList) {
			if (e.getStatus() != "0x0") {
				filtList.add(e);
			}
		}
		
		//make a new list and insert all TargetUserNames
		List<String> usersList=new ArrayList<String>();
		for (MsEvent e : filtList){
			if (!usersList.contains(e.getTargetUsername())){
				usersList.add(e.getTargetUsername());
			}
		}
		
//		for (String user : usersList){
//			for (MsEvent e : filtList){
//				if 
//			}
//		}
	}

	//directory service object modified
	public void checkId5136(String start, String end){
		List<MsEvent> orgList =dbc.getMsByEventId(5136, true, start, end);
		List<MsEvent> list4662=dbc.getMsByEventId(4662, false, start, end);
		
		for (MsEvent e5136 : orgList){
			for (MsEvent e4662 :list4662){
				if (e5136.getSubjectLogonId()==e4662.getSubjectLogonId()){
					if (e5136.getCreated().compareTo(e4662.getCreated())<0){
						// TODO: this is bad, implement actions
					}
				}
			}
		}
	}
	
	//WFP has permitted a bind to a local port
	public void checkId5158(Date start, Date end){
		// TODO: implementation
	}
	
	//network policy server granted access to a user
	public void checkId6272(Date start, Date end){
		// TODO: implementation
	}
	
	//network policy server denied access to a user
	public void checkId6273(Calendar start, Calendar end){
		List<MsEvent> orgList=dbc.getMsByEventId(6273, false, start, end);
		
		if (!orgList.isEmpty()){
			// TODO: implement actions
		}
	}
}
