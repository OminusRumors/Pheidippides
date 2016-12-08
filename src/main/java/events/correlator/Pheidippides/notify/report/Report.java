package events.correlator.Pheidippides.notify.report;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.Collator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import events.correlator.Pheidippides.database.DbConnector;
import events.correlator.Pheidippides.models.GenericReportModel;
import events.correlator.Pheidippides.models.FwEvent;

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
		
		String sql = "SELECT dstip, count(dstip) FROM firewall_traffic_log WHERE type='traffic' AND (action='deny' OR action LIKE 'bloc%' )"
				+ " AND created BETWEEN '" + start + "' AND '" + end + "' GROUP BY dstip ORDER BY count(dstip) DESC LIMIT " 
				+ Integer.toString(top) + ";";
		ResultSet result = dbc.customQuery(sql);

		try {
			while (result.next()) {
				if (result.getString(1) != "255.255.255.255") {
					resultMap.put(result.getString(1), result.getInt(2));
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
	
	public List<GenericReportModel> attacksPerAddress(String start, String end){
		String sql="SELECT dstip, count(dstip) as nrOfAttacks, ref, msg FROM firewall_traffic_log WHERE type='anomaly' AND created BETWEEN '"
				+ start + "' AND '" + end + "' GROUP BY dstip;";
		List<GenericReportModel> reportList=new LinkedList<>();
		GenericReportModel report;
		
		try{
			ResultSet result=dbc.customQuery(sql);
			
			while(result.next()){
				report=new GenericReportModel();
				report.setElement("dstip", result.getString("dstip"));
				report.setElement("nrOfAttacks", result.getString("nrOfAttacks"));
				report.setElement("msg", result.getString("msg"));
				report.setElement("ref", result.getString("ref"));
				reportList.add(report);
			}
			return reportList;
		}
		catch (Exception e){
			System.out.println(e.getLocalizedMessage());
			return null;
		}
	}
	
	
	public GenericReportModel unknownDestinations(int top, String start, String end){
		String csvFile="C:/Users/George/Desktop/software tools/eclipse_workspace/Pheidippides/util/popular_destinations.csv";
		String line="";
		GenericReportModel grm=new GenericReportModel();
		Collection<String> knownHosts=new TreeSet<String>(Collator.getInstance());
		
		if (top<=0){
			top=10;
		}
		
		try(BufferedReader br=new BufferedReader(new FileReader(csvFile))){
			
			//retrieve all the hosts from the file.
			while ((line=br.readLine())!=null){
				knownHosts.add(line);
			}
			
			String sql="SELECT hostname, count(hostname) AS count FROM firewall_traffic_log WHERE hostname IS NOT NULL AND created BETWEEN '"
					+ start + "' AND '" + end + "' GROUP BY hostname";// LIMIT " + Integer.toString(top);
			ResultSet results=dbc.customQuery(sql);
			
			while (results.next()){
				grm.setElement(results.getString(1), results.getString(2));
			}
			
			List<String> temp=new ArrayList<>();
			
			System.out.println("Known hosts found: ");
			for (String host : grm.getReportData().keySet()){
				for (String fileHost : knownHosts){
					if (host.toLowerCase().contains(fileHost.toLowerCase())){
						System.out.println(host);
//						grm.deleteElement(host);
						temp.add(host);
						break;
					}
				}
			}
			
			for (String s : temp){
				if (grm.getReportData().containsKey(s)){
					grm.deleteElement(s);
				}
			}
			
			GenericReportModel g=new GenericReportModel();
			g.setReportData(sortByValue(grm.getReportData()));
			 
			System.out.println(g.getReportData().entrySet());
			
			return g;
					
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage());
			GenericReportModel g=new GenericReportModel();
			g.setElement("File not found", e.getLocalizedMessage());
			return g;
		} catch (IOException e) {
			System.out.println(e.getLocalizedMessage());
			GenericReportModel g=new GenericReportModel();
			g.setElement("File not found", e.getLocalizedMessage());
			return g;
		} catch (SQLException e) {
			System.out.println(e.getLocalizedMessage());
			GenericReportModel g=new GenericReportModel();
			g.setElement("File not found", e.getLocalizedMessage());
			return g;
		}
	}

	private static Map<String, String> sortByValue(Map<String, String> map) {
	    Set<Entry<String, String>> mapEntries=map.entrySet();
	    List<Entry<String, String>> aList=new LinkedList<Entry<String, String>>(mapEntries);
	    
	    Collections.sort(aList, new Comparator<Entry<String, String>>() {
	    	@Override
	    	public int compare(Entry<String, String> e1, Entry<String, String> e2){
	    		return Integer.valueOf(e2.getValue()).compareTo(Integer.valueOf(e1.getValue()));
	    	}
		});
	    Map<String,String> aMap2 = new LinkedHashMap<String, String>();
        for(Entry<String,String> entry: aList) {
            aMap2.put(entry.getKey(), entry.getValue());
        }
        
        return aMap2;
	}
	
}
