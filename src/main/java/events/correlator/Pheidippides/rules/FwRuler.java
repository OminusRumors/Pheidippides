package events.correlator.Pheidippides.rules;

import java.util.Calendar;
import java.util.List;

import events.correlator.Pheidippides.database.DbConnector;
import events.correlator.Pheidippides.models.FwEvent;
import events.correlator.Pheidippides.utilities.Helper;

public class FwRuler {

	private DbConnector dbc;

	public FwRuler(DbConnector dbc) {
		this.dbc = dbc;
	}

	public FwRuler() {
	}

	public void checkTraffic(Calendar start, Calendar end) {
		List<FwEvent> orgList = dbc.getFwByType("traffic", start, end);

		for (FwEvent e : orgList) {
			if (e.getAction().matches("den.*") && Helper.getFwLevels().get(e.getLevel()) <= 3) {
				// TODO: implement actions for scan and stats
				HttpGet httpGet=new HttpGet("sth.org");
			} else if (e.getAction() == "den.*" && Helper.getFwLevels().get(e.getLevel()) > 3) {
				// TODO: implement stats action
			}
		}
	}

	public void checkAppCtrl(Calendar start, Calendar end) {
		List<FwEvent> orgList = dbc.getFwByType("app-ctrl", start, end);

		for (FwEvent e : orgList) {
			if (e.getAction() != "pas.*") {
				if (Helper.getFwLevels().get(e.getLevel()) >= 3) {
					// TODO: implement blacklist, scan actions
					
				} else {
					// TODO: implement blacklist, scan, alert
				}
			}
		}
	}

	public void checkDlp(Calendar start, Calendar end) {
		List<FwEvent> orgList = dbc.getFwByType("dlp", start, end);

		if (!orgList.isEmpty()) {
			// TODO: implement blacklist, scan, alert
		}
	}

	public void checkAnomaly(Calendar start, Calendar end) {
		List<FwEvent> orgList = dbc.getFwByType("anomaly", start, end);

		if (!orgList.isEmpty()) {
			// TODO: implement blacklist, scan
		}
	}

	public void checkWebFilter(Calendar start, Calendar end) {
		List<FwEvent> orgLsit = dbc.getFwByType("webfilter", start, end);

		for (FwEvent e : orgLsit) {
			if (e.getAction() != "allo.*" || e.getAction() != "exempt.*" || e.getAction() != "pas.*") {
				// TODO: implement blacklist, scan
			}
		}
	}

	public void checkSystem(Calendar start, Calendar end){
		List<FwEvent> orgList=dbc.getFwByType("system", start, end);

		for (FwEvent e:orgList){
			if (e.getAction()=="interface-stat-change"){
				
			}
		}
	}
}
