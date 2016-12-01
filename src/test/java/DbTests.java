import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import events.correlator.Pheidippides.database.DbConnector;
import events.correlator.Pheidippides.filter.Filter;
import events.correlator.Pheidippides.models.FwEvent;
import events.correlator.Pheidippides.models.MsEvent;
import events.correlator.Pheidippides.utilities.Helper;

public class DbTests {
	String sql;
	DbConnector dbc;
	Calendar start, end;
	SimpleDateFormat sdf;
	
	public DbTests(){
		try{
			sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dbc=new DbConnector();
		}
		catch(ClassNotFoundException e){
			System.out.println(e.getMessage());
		}
	}
	
	protected void setUp(){
		start=Calendar.getInstance();
		end=Calendar.getInstance();
		start.set(2016, 9, 01);
		end.set(2016, 9, 30);
//		start=Cstart.getTime();
//		end=Cend.getTime();
	}
	
	protected void tearDown(){
		dbc.customQuery("DELETE * FROM filtered_ms");
		dbc.customQuery("DELETE * FROM filtered_fw");
	}
	
	@Test
	public void testCustStm() throws SQLException{
		try{
			dbc=new DbConnector();
		}
		catch(ClassNotFoundException e){
			System.out.println("Test's connection to db failed.");
			System.out.println(e.toString());
		}
		
		ResultSet eventList=dbc.customQuery("select * from security_table where eventid=4768 and TimeCreated between '2016-09-01' and '2016-09-30' order by TimeCreated;");
		
		eventList.next();
		String shouldBe1="moo02";
		String is1=eventList.getString("targetusername");
		
		eventList.next();
		String shouldBe2="DVE";
		String is2=eventList.getString("targetusername");
		
		assertEquals("Checking targetDomainName1",shouldBe1,is1);
		assertEquals("Checking targetDomainName2",shouldBe2,is2);
	}
	
	@Test @Ignore
	public void testMsInsert(){
		try{
			dbc=new DbConnector();
		}
		catch(Exception e){
			System.out.println(e.getLocalizedMessage());
		}
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 30);
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 50);
		MsEvent event=new MsEvent(0, "security", cal, 0000, "mockkeywords");
		MsEvent e=new MsEvent(1, "mssql", cal, 1234, "testKeywords");
		assertEquals(true, dbc.setMsFiltered(e));
		assertEquals("Insert ms event", true, dbc.setMsFiltered(event));
	}
	
	@Test @Ignore 
	public void testSelectFwFiltered(){
		try{
			dbc=new DbConnector();
		}
		catch(Exception e){
			e.getStackTrace();
			System.out.println("Select filtered firewall events method failed.");
			System.out.println(e.getLocalizedMessage());
		}
		
		List<FwEvent> eventList=dbc.getFwTrafficLog(true, start, end);
		
		//actual events
		FwEvent e1=new FwEvent(eventList.get(0).getKeyId(), eventList.get(0).getSourceLog(), eventList.get(0).getCreated(),
				eventList.get(0).getType(), eventList.get(0).getSubtype());
		FwEvent e2=new FwEvent(eventList.get(1).getKeyId(), eventList.get(1).getSourceLog(), eventList.get(1).getCreated(),
				eventList.get(1).getType(), eventList.get(1).getSubtype());
		
		//expected events
		Calendar cal=Calendar.getInstance();
		cal.set(2016, 8, 1);
		FwEvent eE1= new FwEvent(1, "fwTraffic", cal, "Traffic", "forward");
		cal.set(2000, 0, 1, 14, 0, 0);
		FwEvent eE2=new FwEvent(2, "fwEvents", cal, "event", "system");
		
		assertEquals(eE1, e1);
		assertEquals(eE2, e2);
		// TODO: fix date/calendar conversion in DbConnector
	}
	
	@Test
	public void testMsFilter() throws SQLException{
		List<MsEvent> eventList=new ArrayList<MsEvent>();
		List<MsEvent> expList=new ArrayList<>();
		Calendar cal=Calendar.getInstance();
		
		//mock random events
		long mils=1480588830329L;
		cal.set(2000, 1, 1, 14, 55, 0);
		cal.setTimeInMillis(mils);
		eventList.add(new MsEvent(0, "security", cal, 4625, "keywords"));//should be saved
		eventList.add(new MsEvent(1, "security", cal, 5225, "keywords"));//should not be shaved
		expList.add(new MsEvent(0, "security", cal, 4625, "keywords"));
		cal.set(2016, 11, 1, 11, 40, 30);
		eventList.add(new MsEvent(2, "security", cal, 4776, "keywords"));//should be shaved
		eventList.add(new MsEvent(3, "security", cal, 7788, "keywords"));//should not be shaved
		expList.add(new MsEvent(2, "security", cal, 4776, "keywords"));
		
		Filter f=new Filter(dbc);
		f.filterMsEvents(eventList);
		
		String sql="select * from filtered_ms";
		ResultSet rs=dbc.customQuery(sql);
		List<MsEvent> actList =new ArrayList<>();
		
		while(rs.next()){
			//put events in list
			java.sql.Date date=rs.getDate("created");
			Helper hlp=new Helper();
			actList.add(new MsEvent(rs.getInt("rowid"), rs.getString("sourceLog"), hlp.dateToCal(date), rs.getInt("eventid"), 
					rs.getString("keywords")));
		}
		
		assertEquals(expList, actList);
	}
	
}
