package events.correlator.Pheidippides.database;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;

import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteConfig.Pragma;

import events.correlator.Pheidippides.models.FwEvent;
import events.correlator.Pheidippides.models.MsEvent;
import events.correlator.Pheidippides.utilities.Helper;

public final class DbConnector {
	private static Connection con;

	final static String queryFsecurity = "SELECT * FROM filtered_ms WHERE sourceLog = 'security' AND ";
	final static String queryFmssql = "SELECT * FROM filtered_ms WHERE sourceLog = 'mssql' AND";
	final static String queryFfwEvents = "SELECT * FROM filtered_fw WHERE sourceLog = 'fwEvents' AND ";
	final static String querFfwTraffic = "SELECT * FROM filtered_fw WHERE sourceLog = 'fwTraffic' AND ";

	final static String querySecurity = "SELECT rowid, * FROM Security_table WHERE ";
	final static String queryMssql = "SELECT rowid, * FROM Mssql_table WHERE ";
	final static String queryFwEvents = "SELECT rowid, * FROM firewall_event_log WHERE ";
	final static String queryFwTraffic = "SELECT rowid, * FROM firewall_traffic_log WHERE ";
	
	final static SimpleDateFormat dateFrmt =new SimpleDateFormat("yyyy-MM-dd");
	final static SimpleDateFormat timeFrmt= new SimpleDateFormat("HH:mm:ss");
	final static SimpleDateFormat datetimeFrmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public DbConnector() throws ClassNotFoundException {
		// load the sqlite-JDBC driver using the current class loader
		Class.forName("org.sqlite.JDBC");
		SQLiteConfig sqLiteConfig = new SQLiteConfig();
		Properties properties = sqLiteConfig.toProperties();
		properties.setProperty(Pragma.DATE_STRING_FORMAT.pragmaName, "yyyy-MM-dd HH:mm:ss");
		con = null;

		try {
			con = DriverManager.getConnection("jdbc:sqlite:C:/Users/George/Desktop/software tools/test.db", properties);
		} catch (SQLException e) {
			System.out.println("Connection failed.");
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	public Connection getConnection(){
		return DbConnector.con;
	}
	
	public ResultSet customQuery(String sql){
		Statement stm=null;
		try{
			con.setAutoCommit(false);
			stm=con.createStatement();
			ResultSet results=stm.executeQuery(sql);
			con.commit();
			return results;
		}
		catch (SQLException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
			return null;
		}
	}

	public List<FwEvent> getFwByType(String type, Calendar start, Calendar end) {
		Statement stm;
		try {
			stm = con.createStatement();
			ResultSet raw_log;
			List<FwEvent> eventList = new ArrayList<FwEvent>();
			String sql;
			
			if (type.equalsIgnoreCase("traffic")) {
				sql = "SELECT * FROM filtered_fw WHERE lower(type)='traffic' AND created BETWEEN " + datesToSting(start, end);
			} else {
				sql = "SELECT * FROM filtered_fw WHERE lower(subtype)=lower('" + type + "') AND created BETWEEN " + 
			datesToSting(start, end);
			}
			
			raw_log=stm.executeQuery(sql);
			while (raw_log.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw_log.getDate("created"));
				
				FwEvent fw_event = new FwEvent(raw_log.getInt("RowId"), raw_log.getString("sourceLog"),
						cal, raw_log.getString("type"), raw_log.getString("subtype"),
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
			System.out.println(e.getLocalizedMessage());
			System.out.println("DbConnector.getFwByType SQLException");
			return null;
		}
	}

	public List<MsEvent> getMsByEventId(int eventId, boolean filtered, Calendar start, Calendar end) {
		Statement stm;
		try {
			stm = con.createStatement();
			ResultSet raw = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();
			String sql;

			if (filtered) {
				sql = "SELECT * FROM filtered_ms WHERE eventId=" + eventId + " AND created BETWEEN " +
			this.datesToSting(start, end) + " ORDER BY created;";
			} else {
				sql = "SELECT * FROM security_table WHERE eventId=" + eventId + " AND created BETWEEN " + 
			this.datesToSting(start, end) + " ORDER BY created;";
			}

			raw = stm.executeQuery(sql);
			while (raw.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw.getDate("created"));
				MsEvent ms_event = new MsEvent(raw.getInt("ROWID"), raw.getString("sourceLog"), cal,
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
			System.out.println("GetByEventId failed.");
			System.out.println(e.getMessage());
			return null;
		}
	}

	public boolean setMsFiltered(MsEvent event) {
		try {
			con.setAutoCommit(false);
			String sql = "INSERT INTO filtered_ms VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prstm = con.prepareStatement(sql);
			SimpleDateFormat frmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			prstm.setInt(1, event.getKeyId());
			prstm.setString(2, event.getSourceLog());	
			prstm.setInt(3, event.getEventId());
			prstm.setString(4, event.getKeywords());
			prstm.setString(5, frmt.format(event.getCreated().getTime()));
			prstm.setString(6, event.getSubjectLogonId());
			prstm.setString(7, event.getHandleId());
			prstm.setString(8, event.getLogonId());
			prstm.setString(9, event.getStatus());
			prstm.setString(10, event.getSubstatus());
			prstm.setInt(11, event.getLogonType());
			prstm.setString(12, event.getTargetDomainName());
			prstm.setString(13, event.getTargetUsername());
			prstm.setString(14, event.getIpAddress());
			prstm.setString(15, event.getApp());
			prstm.executeUpdate();
			con.commit();
			return true;

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
			return false;
		}

	}

	public boolean setFwFiltered(FwEvent event) {
		try {
			con.setAutoCommit(false);
			
			SimpleDateFormat frmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			String sql = "INSERT INTO filtered_fw VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement prstm = con.prepareStatement(sql);
			
			prstm.setInt(1, event.getKeyId());
			prstm.setString(2, event.getSourceLog());
			prstm.setString(3, frmt.format(event.getCreated().getTime()));
			prstm.setString(4, event.getType());
			prstm.setString(5, event.getSubtype());
			prstm.setString(6, event.getLevel());
			prstm.setString(7, event.getAction());
			prstm.setString(8, event.getDstIp());
			prstm.setString(9, event.getDstCountry());
			prstm.setString(10, event.getDstIntf());
			prstm.setString(11, event.getSrcIp());
			prstm.setString(12, event.getSrcCountry());
			prstm.setString(13, event.getSrcIntf());
			prstm.setString(14, event.getApp());
			prstm.setString(15, event.getMsg());
			prstm.setString(16, event.getRecepient());
			prstm.setString(17, event.getSender());
			prstm.setString(18, event.getService());
			prstm.setString(19, event.getRef());
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

	public List<MsEvent> getSecurityLog(boolean filtered, Calendar startDate, Calendar endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();
			
			if (filtered) {
				raw_log = stm.executeQuery(queryFsecurity + " created BETWEEN " + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(querySecurity + " created BETWEEN " + datesToSting(startDate, endDate));
			}
			while (raw_log.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw_log.getDate("created"));
				MsEvent ms_event = new MsEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						cal, raw_log.getInt("eventId"), raw_log.getString("keywords"),
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

	public List<MsEvent> getMssqlLog(boolean filtered, Calendar startDate, Calendar endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<MsEvent> eventList = new ArrayList<MsEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFmssql + " created BETWEEN " + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryMssql + " created BETWEEN " + datesToSting(startDate, endDate));
			}
			
			while (raw_log.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw_log.getDate("created"));
				MsEvent ms_event = new MsEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						cal, raw_log.getInt("eventId"), raw_log.getString("keywords"),
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

	public List<FwEvent> getFwEventLog(boolean filtered, Calendar startDate, Calendar endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<FwEvent> eventList = new ArrayList<FwEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + " created BETWEEN " + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryFwEvents + " created BETWEEN " + datesToSting(startDate, endDate));
			}
			
			while (raw_log.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw_log.getDate("created"));
				FwEvent fw_event = new FwEvent(raw_log.getInt("ROWID"), raw_log.getString("sourceLog"),
						cal, raw_log.getString("type"), raw_log.getString("subtype"),
						raw_log.getString("level"), raw_log.getString("action"));
						fw_event.setMsg(raw_log.getString("msg"));
//						raw_log.getString("dstip"),
//						raw_log.getString("dstcountry"), raw_log.getString("dstintf"), raw_log.getString("srcip"),
//						raw_log.getString("srccountry"), raw_log.getString("srcintf"), raw_log.getString("app"),
//						raw_log.getString("msg"), raw_log.getString("recepient"), raw_log.getString("sender"),
//						raw_log.getString("service"), raw_log.getString("ref"));
				eventList.add(fw_event);
			}
			return eventList;
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public List<FwEvent> getFwTrafficLog(boolean filtered, Calendar startDate, Calendar endDate) {
		try {
			Statement stm = con.createStatement();
			ResultSet raw_log = null;
			List<FwEvent> eventList = new ArrayList<FwEvent>();

			if (filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + " created BETWEEN " + datesToSting(startDate, endDate));
			} else if (!filtered) {
				raw_log = stm.executeQuery(queryFfwEvents + " created BETWEEN " + datesToSting(startDate, endDate));
			}
			
			while (raw_log.next()) {
				Calendar cal=Calendar.getInstance();
				cal.setTime(raw_log.getDate("created"));
				FwEvent fw_event = new FwEvent(raw_log.getInt("keyId"), raw_log.getString("sourceLog"),
						cal, raw_log.getString("type"), raw_log.getString("subtype"),
						raw_log.getString("level"), raw_log.getString("action"), raw_log.getString("dstip"),
						raw_log.getString("dstcountry"), raw_log.getString("dstintf"), raw_log.getString("srcip"),
						raw_log.getString("srccountry"), raw_log.getString("srcintf"), raw_log.getString("app"),
						raw_log.getString("msg"), raw_log.getString("recepient"), raw_log.getString("sender"),
						raw_log.getString("service"), raw_log.getString("ref"));
				eventList.add(fw_event);
			}
			return eventList;
		}
		catch (IllegalArgumentException e){
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		} catch (Exception e) {
			try {
				e.printStackTrace(new PrintStream("C:/Users/George/OneDrive/Graduation Internship/docs/tekst.txt"));
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println("getFwTraffic failed.");
			System.out.println(e.getLocalizedMessage());
		}
		return null;
	}

	private String datesToSting(Calendar start, Calendar end) {
		SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return "'" + dateFormat.format(start.getTime()) + "' AND '" + dateFormat.format(end.getTime()) + "'";
	}
}
