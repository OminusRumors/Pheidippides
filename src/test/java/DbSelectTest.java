import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

import events.correlator.Pheidippides.database.DbConnector;
import events.correlator.Pheidippides.models.MsEvent;

public class DbSelectTest {
	String sql;
	DbConnector dbc;
	Date start, end;
	
	public DbSelectTest(){
		try{
			dbc=new DbConnector();
		}
		catch(ClassNotFoundException e){
			System.out.println(e.getMessage());
		}
	}
	
	protected void setUp(){
		Calendar Cstart=Calendar.getInstance();
		Calendar Cend=Calendar.getInstance();
		Cstart.set(2016, 9, 01);
		Cend.set(2016, 9, 30);
		start=Cstart.getTime();
		end=Cend.getTime();
	}
	
	@Test
	public void testMsSelectStm() throws SQLException{
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
}
