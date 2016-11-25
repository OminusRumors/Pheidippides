package events.correlator.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import events.correlator.resources.Event.FwEvent;
import events.correlator.resources.Event.MsEvent;

public final class DbConnector {
	static Connection con;

	final static String queryFsecurity = "SELECT * FROM filtered_ms WHERE sourceLog = 'security' AND";
	final static String queryFmssql = "SELECT * FROM filtered_ms WHERE sourceLog = 'mssql' AND";
	final static String queryFfwEvents = "SELECT * FROM filtered_fw WHERE sourceLog = 'fwEvents' AND";
	final static String querFfwTraffic = "SELECT * FROM filtered_fw WHERE sourceLog = 'fwTraffic' AND";

	final static String querySecurity = "SELECT * FROM Security_table WHERE";
	final static String queryMssql = "SELECT * FROM Mssql_table WHERE";
	final static String queryFwEvents = "SELECT * FROM firewall_event_log WHERE";
	final static String queryFwTraffic = "SELECT * FROM firewall_traffic_log WHERE";

	public DbConnector() throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");

		con = null;

		try {
			con = DriverManager.getConnection("jdbc:sqlite:C:/Users/George/Desktop/software tools/test.db");
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public ResultSet customQuery(String sql){
		Statement stm;
		try{
			stm=con.createStatement();
			ResultSet results=stm.executeQuery(sql);
			return results;
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public List<FwEvent> getFwByType(String type, Date start, Date end) {
		Statement stm;
		try {
			stm = con.createStatement();
			ResultSet raw_log = null;
			List<FwEvent> eventList = new ArrayList<FwEvent>();
			String sql;
			if (type.toLowerCase() == "Traffic".toLowerCase()) {
				sql = "SELECT * FROM filtered_fw WHERE lower(type)='traffic' AND" + datesToSting(start, end);
			} else {
				sql = "SELECT * FROM filtered_fw WHERE lower(subtype)=lower(" + type + ") AND" + datesToSting(start, end);
			}
			raw_log=stm.executeQuery(sql);
			while (raw_log.next()) {
				FwEvent fw_event = new FwEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						raw_log.getDate("created"), raw_log.getString("type"), raw_log.getString("subtype"),
						raw_log.getString("level"), raw_log.getString("action"), raw_log.getString("dstip"),
						raw_log.getString("dstcountry"), raw_log.getString("dstintf"), raw_log.getString("srcip"),
						raw_log.getString("srccountry"), raw_log.getString("srcintf"), raw_log.getString("app"),
						raw_log.getString("msg"), raw_log.getString("recepient"), raw_log.getString("sender"),
						raw_log.getString("service"), raw_log.getString("ref"));
				eventList.add(fw_event);
			}
			return eventList;
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public List<MsEvent> getMsByEventId(int eventId, boolean filtered, Date start, Date end) {
		Statement stm;
		try {
			stm = con.createStatement();
			ResultSet raw = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();
			String sql;

			if (filtered) {
				sql = "SELECT * FROM filtered_ms WHERE eventId=" + eventId + this.datesToSting(start, end)
						+ " ORDER BY created;";
			} else {
				sql = "SELECT * FROM security_table WHERE eventId=" + eventId + this.datesToSting(start, end)
						+ " ORDER BY created;";
			}

			raw = stm.executeQuery(sql);
			while (raw.next()) {
				MsEvent ms_event = new MsEvent(raw.getInt("keyId"), raw.getString("sourceLog"), raw.getDate("created"),
						raw.getInt("eventId"), raw.getString("keywords"), raw.getString("subjectLogonId"),
						raw.getString("handleId"), raw.getString("logonId"), raw.getString("status"),
						raw.getString("substatus"), raw.getInt("logonType"), raw.getString("targetUsername"),
						raw.getString("targetDomainName"), raw.getString("ipAddress"));
				eventList.add(ms_event);
			}
			return eventList;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean setMsFiltered(MsEvent event) {
		try {
			con.setAutoCommit(false);
			String sql = "INSERT INTO filtered_ms VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prstm = con.prepareStatement(sql);
			Date utilDate = event.getCreated();
			java.sql.Date created = new java.sql.Date(utilDate.getTime());
			prstm.setString(1, event.getSourceLog());
			prstm.setInt(2, event.getEventId());
			prstm.setString(3, event.getKeywords());
			prstm.setDate(4, created);
			prstm.setString(5, event.getSubjectLogonId());
			prstm.setString(6, event.getHandleId());
			prstm.setString(7, event.getLogonId());
			prstm.setString(8, event.getStatus());
			prstm.setString(9, event.getSubstatus());
			prstm.setInt(10, event.getLogonType());
			prstm.setString(11, event.getTargetDomainName());
			prstm.setString(12, event.getTargetUsername());
			prstm.setString(13, event.getIpAddress());
			prstm.setString(14, event.getApp());
			prstm.executeUpdate();
			con.commit();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}

	}

	public boolean setFwFiltered(FwEvent event) {
		try {
			con.setAutoCommit(false);
			String sql = "INSERT INTO filtered_fw VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prstm = con.prepareStatement(sql);
			Date utilDate = event.getCreated();
			java.sql.Date created = new java.sql.Date(utilDate.getTime());
			prstm.setInt(0, event.getKeyId());
			prstm.setString(1, event.getSourceLog());
			prstm.setDate(2, created);
			prstm.setString(3, event.getType());
			prstm.setString(4, event.getSubtype());
			prstm.setString(5, event.getLevel());
			prstm.setString(6, event.getAction());
			prstm.setString(7, event.getDstIp());
			prstm.setString(8, event.getDstCountry());
			prstm.setString(9, event.getDstIntf());
			prstm.setString(10, event.getSrcIp());
			prstm.setString(11, event.getSrcCountry());
			prstm.setString(12, event.getSrcIntf());
			prstm.setString(13, event.getApp());
			prstm.setString(14, event.getMsg());
			prstm.setString(15, event.getRecepient());
			prstm.setString(16, event.getSender());
			prstm.setString(17, event.getService());
			prstm.executeUpdate();
			con.commit();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
			return false;
		}

	}

	public List<MsEvent> getSecurityLog(boolean filtered, Date startDate, Date endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();
			// List<Event> eventList = new ArrayList<Event>();
			if (filtered) {
				raw_log = stm.executeQuery(queryFsecurity + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(querySecurity + datesToSting(startDate, endDate));
			}
			while (raw_log.next()) {
				MsEvent ms_event = new MsEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						raw_log.getDate("created"), raw_log.getInt("eventId"), raw_log.getString("keywords"),
						raw_log.getString("subjectLogonId"), raw_log.getString("handleId"),
						raw_log.getString("logonId"), raw_log.getString("status"), raw_log.getString("substatus"),
						raw_log.getInt("logonType"), raw_log.getString("targetUsername"),
						raw_log.getString("targetDomainName"), raw_log.getString("ipAddress"));
				eventList.add(ms_event);
			}
			return eventList;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public List<MsEvent> getMssqlLog(boolean filtered, Date startDate, Date endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFmssql + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryMssql + datesToSting(startDate, endDate));
			}
			while (raw_log.next()) {
				MsEvent ms_event = new MsEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						raw_log.getDate("created"), raw_log.getInt("eventId"), raw_log.getString("keywords"),
						raw_log.getString("subjectLogonId"), raw_log.getString("handleId"),
						raw_log.getString("logonId"), raw_log.getString("status"), raw_log.getString("substatus"),
						raw_log.getInt("logonType"), raw_log.getString("targetUsername"),
						raw_log.getString("targetDomainName"), raw_log.getString("ipAddress"));
				eventList.add(ms_event);
			}
			return eventList;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public List<FwEvent> getFwEventLog(boolean filtered, Date startDate, Date endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<FwEvent> eventList = new ArrayList<FwEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + datesToSting(startDate, endDate));
			}
			while (raw_log.next()) {
				FwEvent fw_event = new FwEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						raw_log.getDate("created"), raw_log.getString("type"), raw_log.getString("subtype"),
						raw_log.getString("level"), raw_log.getString("action"), raw_log.getString("dstip"),
						raw_log.getString("dstcountry"), raw_log.getString("dstintf"), raw_log.getString("srcip"),
						raw_log.getString("srccountry"), raw_log.getString("srcintf"), raw_log.getString("app"),
						raw_log.getString("msg"), raw_log.getString("recepient"), raw_log.getString("sender"),
						raw_log.getString("service"), raw_log.getString("ref"));
				eventList.add(fw_event);
			}
			return eventList;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public List<FwEvent> getFwTrafficLog(boolean filtered, Date startDate, Date endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<FwEvent> eventList = new ArrayList<FwEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + datesToSting(startDate, endDate));
			}
			while (raw_log.next()) {
				FwEvent fw_event = new FwEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						raw_log.getDate("created"), raw_log.getString("type"), raw_log.getString("subtype"),
						raw_log.getString("level"), raw_log.getString("action"), raw_log.getString("dstip"),
						raw_log.getString("dstcountry"), raw_log.getString("dstintf"), raw_log.getString("srcip"),
						raw_log.getString("srccountry"), raw_log.getString("srcintf"), raw_log.getString("app"),
						raw_log.getString("msg"), raw_log.getString("recepient"), raw_log.getString("sender"),
						raw_log.getString("service"), raw_log.getString("ref"));
				eventList.add(fw_event);
			}
			return eventList;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	private String datesToSting(Date start, Date end) {
		return " created BETWEEN " + start.toString() + " AND " + end.toString();
	}
}
