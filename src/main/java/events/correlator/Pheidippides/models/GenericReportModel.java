package events.correlator.Pheidippides.models;

import java.util.LinkedHashMap;
import java.util.Map;

public class GenericReportModel {

	private Map<String, String> reportData;
	
	public GenericReportModel(){
		this.reportData=new LinkedHashMap<String, String>();
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
	
	public void deleteElement(String key){
		this.reportData.remove(key);
	}
}
