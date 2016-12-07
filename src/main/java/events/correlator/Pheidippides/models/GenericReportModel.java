package events.correlator.Pheidippides.models;

import java.util.HashMap;
import java.util.Map;

public class GenericReportModel {

	private Map<String, String> reportData;
	
	public GenericReportModel(){
		this.reportData=new HashMap<String, String>();
	}

	public Map<String, String> getReportData() {
		return reportData;
	}

	public void setReportData(Map<String, String> reportData) {
		this.reportData = reportData;
	}
	
	public void setElement(String key, String value){
		this.reportData.put(key, value);
	}
	
	public String getElement(String key){
		return this.reportData.get(key);
	}
}
