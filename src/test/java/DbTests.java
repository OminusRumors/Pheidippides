import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
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
	
	
	public void setUp(){
		start=Calendar.getInstance();
		end=Calendar.getInstance();
		start.set(2016, 9, 01);
		end.set(2016, 9, 30);
//		start=Cstart.getTime();
//		end=Cend.getTime();
	}
	
	
	public void tearDown(){
		dbc.customQuery("DELETE FROM filtered_ms");
		dbc.customQuery("DELETE FROM filtered_fw WHERE rowid=999");
		try {
			dbc.getConnection().commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getLocalizedMessage());
		}
	}
	
	@Test
	public void testCustStm() throws SQLException{
		
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
	
	@Test
	public void getFwByTypeTest(){
			
		Calendar cal1=Calendar.getInstance();
		cal1.set(2000, 0, 01, 0, 0, 0);
		Calendar cal2=Calendar.getInstance();
		cal2.set(2010, 3, 4, 7, 52, 12);
		List<FwEvent> eventList=dbc.getFwByType("traffic", cal1, cal2);
		
		//set first expected event;
		FwEvent expEvent1=new FwEvent(100, "fwTraffic", cal1, "traffic", "local");
		expEvent1.setLevel("notice");
		expEvent1.setAction("allow");
		
		//set second expected event
		Calendar expCal2=Calendar.getInstance();
		expCal2.set(2000, 3, 04, 06, 25, 2);
		FwEvent expEvent2=new FwEvent(200, "fwTraffic", expCal2, "traffic", "system");
		expEvent2.setLevel("information");
		
		SimpleDateFormat fmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		assertEquals(expEvent1.getKeyId(), eventList.get(0).getKeyId());
		assertEquals(expEvent1.getSourceLog(), eventList.get(0).getSourceLog());
		assertEquals(expEvent1.getType(), eventList.get(0).getType());
		assertEquals(expEvent1.getSubtype(), eventList.get(0).getSubtype());
		assertEquals(expEvent1.getLevel(), eventList.get(0).getLevel());
		assertEquals(expEvent1.getAction(), eventList.get(0).getAction());
		assertEquals(fmt.format(expEvent1.getCreated().getTime()), fmt.format(eventList.get(0).getCreated().getTime()));
		assertEquals(expEvent2.getKeyId(), eventList.get(1).getKeyId());
		assertEquals(expEvent2.getSourceLog(), eventList.get(1).getSourceLog());
		assertEquals(expEvent2.getType(), eventList.get(1).getType());
		assertEquals(expEvent2.getSubtype(), eventList.get(1).getSubtype());
		assertEquals(expEvent2.getLevel(), eventList.get(1).getLevel());
		assertEquals(expEvent2.getAction(), eventList.get(1).getAction());
		assertEquals(fmt.format(expEvent2.getCreated().getTime()), fmt.format(eventList.get(1).getCreated().getTime()));
	}
	
	@Test 
	public void testFwInsert(){
		//declare start-end dates of data retrieval (2016-09-09 17:46:13 t/m )
		Calendar Scal=Calendar.getInstance();
		Scal.set(2016, 8, 9, 17, 46, 13);
		
		List<FwEvent> eventList=dbc.getFwEventLog(false, Scal, Scal);
		
		//test event for filterer. It is the only event that should pass the filterer.
		FwEvent testEvent=new FwEvent(999, "fwEvents", Scal, "Event", "application", "error", "denial");
		eventList.add(testEvent);
		
		Filter filt=new Filter(dbc);
		
		filt.filterFwEvents(eventList);
		List<FwEvent> actList=dbc.getFwEventLog(true, Scal, Scal);
		
		assertEquals(1, actList.size());
		assertEquals(testEvent.getKeyId(), actList.get(0).getKeyId());
		assertEquals(testEvent.getSourceLog(), actList.get(0).getSourceLog());
		assertEquals(testEvent.getType(), actList.get(0).getType());
		assertEquals(testEvent.getSubtype(), actList.get(0).getSubtype());
		assertEquals(testEvent.getCreated().get(0), actList.get(0).getCreated().get(0));
		assertEquals(testEvent.getCreated().get(1), actList.get(0).getCreated().get(1));
		assertEquals(testEvent.getCreated().get(2), actList.get(0).getCreated().get(2));
		assertEquals(testEvent.getCreated().get(3), actList.get(0).getCreated().get(3));
		assertEquals(testEvent.getCreated().get(4), actList.get(0).getCreated().get(4));
		assertEquals(testEvent.getCreated().get(5), actList.get(0).getCreated().get(5));
	}
	
	@Test
	public void testMsInsert(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.YEAR, 2000);
		cal.set(Calendar.MONTH, 0);
		cal.set(Calendar.DATE, 30);
		cal.set(Calendar.HOUR, 14);
		cal.set(Calendar.MINUTE, 50);
		MsEvent event=new MsEvent(888, "security", cal, 0000, "mockkeywords");
		MsEvent e=new MsEvent(889, "mssql", cal, 1234, "testKeywords");
		assertEquals(false, dbc.setMsFiltered(e));
		assertEquals("Insert ms event", false, dbc.setMsFiltered(event));
	}
	
	@Test 
	public void testSelectFwFiltered(){
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
	}
	
	@Test
	public void testMsFilter() throws SQLException{
		List<MsEvent> actList=new ArrayList<MsEvent>();
		List<MsEvent> expList=new ArrayList<>();
		Calendar Scal=Calendar.getInstance();
		Calendar Ecal=Calendar.getInstance();
		//mock random events
		Scal.set(2000, 1, 1, 14, 55, 0);
		actList.add(new MsEvent(998, "security", Scal, 4625, "keywords"));//should be saved
		actList.add(new MsEvent(997, "security", Scal, 5225, "keywords"));//should not be shaved
		expList.add(new MsEvent(998, "security", Scal, 4625, "keywords"));
		Ecal.set(2016, 11, 1, 11, 40, 30);
		actList.add(new MsEvent(996, "security", Ecal, 4776, "keywords"));//should be shaved
		actList.add(new MsEvent(995, "security", Ecal, 7788, "keywords"));//should not be shaved
		expList.add(new MsEvent(996, "security", Ecal, 4776, "keywords"));
		
		Filter f=new Filter(dbc);
		f.filterMsEvents(actList);
		actList.clear();
		actList =dbc.getSecurityLog(true, Scal, Ecal);
		
		assertEquals(expList.get(0), actList.get(0));
		assertEquals(expList.get(1), actList.get(1));
	}
	
}
