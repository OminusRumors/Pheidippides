package events.correlator.Pheidippides.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	/** Gets the date of the current event and returns a calendar array containing the min and max time according to window.
	 * @param current Current event time.
	 * @param window The time window in seconds.
	 * @return A calendar array.
	 */
	private Calendar[] dateToCalRange(Date current, int window) {
		Calendar[] retArray = new Calendar[3];

		// transforms the DATE of the event to CALENDAR instance
		Calendar curDate = Calendar.getInstance();
		curDate.setTime(current);
		retArray[1] = curDate;

		// gets the time of -2 seconds from the current time
		Calendar minDate = Calendar.getInstance();
		minDate.setTime(current);
		minDate.add(Calendar.SECOND, -window);
		retArray[0] = minDate;

		// gets the time of +2 seconds from the current time
		Calendar maxDate = Calendar.getInstance();
		maxDate.setTime(current);
		maxDate.add(Calendar.SECOND, +window);
		retArray[2] = maxDate;

		return retArray;
	}

	public void checkId4625(Date start, Date end) {
		List<MsEvent> eventList = dbc.getMsByEventId(4625, true, start, end); // get all events with id 4625
		List<MsEvent> eventUserList = new ArrayList<MsEvent>();
		List<MsEvent> finalList=new ArrayList<MsEvent>();
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
				if (e.getTargetUsername()==user){
					eventUserList.add(e);
					eventList.remove(e);
				}
			}
			for (MsEvent e:eventUserList){
				Calendar[] cal=dateToCalRange(e.getCreated(), 2);
				List<String> ipList=new ArrayList<String>();
				
				//loop for getting all the events of the specific user in a defined time range and save them to "finalList"
				for (MsEvent ev:eventUserList){
					if (ev.getCreated().compareTo(cal[0].getTime())>0 && ev.getCreated().compareTo(cal[2].getTime())<0){
						finalList.add(ev);
						if (!ipList.contains(ev.getIpAddress())){
							ipList.add(e.getIpAddress());
						}
					}
				}
				if (finalList.size()>=6){
					//alert, scan
					Map<String,String> reportData=new HashMap<String, String>();
					reportData.put("count", Integer.toString(finalList.size()));
					String msg=String.format("User %s failed %d times to logon from ip: %s", e.getTargetUsername(),
							Integer.toString(finalList.size()), ipList.toString());
					reportData.put("title", "Logon failure threshold reached");
					reportData.put("message", msg);
					reportData.put("eventId", "4625");
					reportData.put("eventDesc", "Logon failure");
					if (Helper.getStatus().containsKey(e.getStatus())){
						reportData.put("reason", Helper.getStatus().get(e.getStatus()));	
					}
					else{
						reportData.put("reason", "unknown");
					}
					String[] rec={"georgevassiliadis@hotmail.com", "georgios.vasileiadis@diagnostiekvooru.nl"};
					//alert.sendEmail({"georgevassiliadis@hotmail.com", "georgios.vasileiadis@diagnostiekvooru.nl"}, reportData);
					Alert.sendEmail(rec, reportData);
				}
			}
		}
	}

	public void checkId4768(Date start, Date end) {

		List<MsEvent> list4768 = dbc.getMsByEventId(4768, true, start, end);
		List<MsEvent> newList4768 = new ArrayList<MsEvent>();
		Map<String, String[]> username_ip = new HashMap<String, String[]>();

		// create new list with failed attempts of kerberos auth ticket events(4768)
		for (MsEvent e : list4768) {
			if (e.getKeywords() == "0x8010.*") {
				newList4768.add(e);
			}
		}
		for (MsEvent e : newList4768) {
			//if username exists in list and ipaddress does not exist for that username
			if (username_ip.containsKey(e.getTargetUsername()) && 
					!Arrays.asList(username_ip.get(e.getTargetUsername())).contains(e.getIpAddress())) {
				String[] addip = username_ip.get(e.getTargetUsername());
				addip[addip.length]=e.getIpAddress();
				username_ip.put(e.getTargetUsername(), addip);
			} else {
				String[] ip={e.getIpAddress()};
				username_ip.put(e.getTargetUsername(), ip);
			}
		}
		// TODO: implement action after Mapping the targetusernames is finished
		for (Map.Entry<String, String[]> e:username_ip.entrySet()){
			Map<String, String> reportData=new HashMap<String, String>();
			reportData.put("count", Integer.toString(e.getValue().length));
			reportData.put("message", "Kerberos authentication ticket request failed.");
			reportData.put("eventId", Integer.toString(4768));
			reportData.put("reason", "unknown");
		}
	}

	public void checkId4769(Date start, Date end) {
		List<MsEvent> orgEventList = dbc.getMsByEventId(4769, true, start, end);
		List<MsEvent> filtList = new ArrayList<MsEvent>();
		List<MsEvent> finalList = new ArrayList<MsEvent>();
		Calendar[] slwin;

		for (MsEvent e : orgEventList) {
			if (e.getKeywords() == "0x801.*") {
				filtList.add(e);
			}
		}

		for (MsEvent e : filtList) {
			slwin = this.dateToCalRange(e.getCreated(), 3);

			if (e.getCreated().compareTo(slwin[0].getTime()) > 0 && e.getCreated().compareTo(slwin[2].getTime()) < 0) {
				finalList.add(e);
			}
		}

		if (finalList.size() > 4) {
			// TODO: implement actions alarm
		}
	}

	public void checkId4776(Date start, Date end) {
		List<MsEvent> orgList = dbc.getMsByEventId(4776, true, start, end);
		List<MsEvent> filtList = new ArrayList<MsEvent>();

		for (MsEvent e : orgList) {
			if (e.getStatus() != "0x0") {
				filtList.add(e);
			}
		}

		// TODO: implement further actions
	}

	public void checkId5136(Date start, Date end){
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
	
	public void checkId5158(Date start, Date end){
		// TODO: implementation
	}
	
	public void checkId6272(Date start, Date end){
		// TODO: implementation
	}
	
	public void checkId6273(Date start, Date end){
		List<MsEvent> orgList=dbc.getMsByEventId(6273, false, start, end);
		
		if (!orgList.isEmpty()){
			// TODO: implement actions
		}
	}
}
