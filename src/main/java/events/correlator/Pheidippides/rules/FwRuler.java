package events.correlator.resources.Rules;

import java.util.Date;
import java.util.List;

import events.correlator.database.DbConnector;
import events.correlator.resources.Event.FwEvent;
import events.correlator.utilities.Helper;

public class FwRuler {

	private DbConnector dbc;

	public FwRuler(DbConnector dbc) {
		this.dbc = dbc;
	}

	public FwRuler() {
	}

	public void checkTraffic(Date start, Date end) {
		List<FwEvent> orgList = dbc.getFwByType("traffic", start, end);

		for (FwEvent e : orgList) {
			if (e.getAction() == "den.*" && Helper.getFwLevels().get(e.getLevel()) <= 3) {
				// TODO: implement actions for scan and stats
			} else if (e.getAction() == "den.*" && Helper.getFwLevels().get(e.getLevel()) > 3) {
				// TODO: implement stats action
			}
		}
	}

	public void checkAppCtrl(Date start, Date end) {
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

	public void checkDlp(Date start, Date end) {
		List<FwEvent> orgList = dbc.getFwByType("dlp", start, end);

		if (!orgList.isEmpty()) {
			// TODO: implement blacklist, scan, alert
		}
	}

	public void checkAnomaly(Date start, Date end) {
		List<FwEvent> orgList = dbc.getFwByType("anomaly", start, end);

		if (!orgList.isEmpty()) {
			// TODO: implement blacklist, scan
		}
	}

	public void checkWebFilter(Date start, Date end) {
		List<FwEvent> orgLsit = dbc.getFwByType("webfilter", start, end);

		for (FwEvent e : orgLsit) {
			if (e.getAction() != "allo.*" || e.getAction() != "exempt.*" || e.getAction() != "pas.*") {
				// TODO: implement blacklist, scan
			}
		}
	}

	public void checkSystem(Date start, Date end){
		List<FwEvent> orgList=dbc.getFwByType("system", start, end);

		for (FwEvent e:orgList){
			if (e.getAction()=="interface-stat-change"){
				
			}
		}
	}
}
