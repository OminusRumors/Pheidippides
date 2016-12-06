package events.correlator.Pheidippides.notify.report;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import events.correlator.Pheidippides.database.DbConnector;

public class Report {

	private DbConnector dbc;

	public Report() throws ClassNotFoundException {
		try {
			dbc = new DbConnector();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
			System.out.println("Inside Report constructor");
			System.out.println(e.getClass().getName());
		}
	}

	public Report(DbConnector dbc) {
		this.dbc = dbc;
	}

	protected Map<String, Integer> topDestinations(int top, String start, String end) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		try {
			if (top <= 0) {
				top = 10;
			}
			
			String sql = "SELECT dstip, count(dstip) FROM firewall_traffic_log WHERE type='traffic' AND created BETWEEN '"
					+ start + "' AND '" + end + "' GROUP BY dstip ORDER BY count(dstip) DESC LIMIT "
					+ Integer.toString(top) + ";";

			ResultSet result = dbc.customQuery(sql);

			while (result.next()) {
				if (result.getString(1) != "255.255.255.255") {
					resultMap.put(result.getString(1), result.getInt(2));
				}
			}
			return resultMap;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			return null;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}

	public Map<String, Integer> droppedDestinations(int top, String start, String end) {
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		
		if (top <= 0) {
			top = 10;
		}
		String sql = "SELECT dstip, count(dstip) FROM firewall_traffic_log WHERE type='traffic' AND action='deny' AND created BETWEEN "
				+ start + " AND " + end + " AND " + " GROUP BY dstip ORDER BY count(dstip) DESC LIMIT " 
				+ Integer.toString(top) + ";";
		ResultSet result = dbc.customQuery(sql);

		try {
			while (result.next()) {
				if (result.getString(0) != "255.255.255.255") {
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
