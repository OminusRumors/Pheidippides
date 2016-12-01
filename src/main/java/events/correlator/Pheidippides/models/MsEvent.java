package events.correlator.Pheidippides.models;

import java.util.Calendar;

public class MsEvent extends Event{

	private int eventId;
	private String keywords;
	private String subjectLogonId;
	private String handleId;
	private String logonId;
	private String status;
	private String substatus;
	private int logonType;
	private String targetUsername;
	private String targetDomainName;
	private String ipAddress;
	
	public MsEvent(int keyId, String sourceLog, Calendar created, int eventId, String keywords, String subjectLogonId,
			String handleId, String logonId, String status, String substatus, int logonType, String targetUsername,
			String targetDomainName, String ipAddress) {
		super(keyId, sourceLog, created);
		this.eventId = eventId;
		this.keywords = keywords;
		this.subjectLogonId = subjectLogonId;
		this.handleId = handleId;
		this.logonId = logonId;
		this.status = status;
		this.substatus = substatus;
		this.logonType = logonType;
		this.targetUsername = targetUsername;
		this.targetDomainName = targetDomainName;
		this.ipAddress = ipAddress;
	}

	public MsEvent(int keyId, String sourceLog, Calendar created, int eventId, String keywords) {
		super(keyId, sourceLog, created);
		this.eventId = eventId;
		this.keywords = keywords;
	}

	public int getEventId(){
		return eventId;
	}
	
	public String getKeywords(){
		return keywords;
	}
	
	public String getSubjectLogonId() {
		return subjectLogonId;
	}

	public void setSubjectLogonId(String subjectLogonId) {
		this.subjectLogonId = subjectLogonId;
	}

	public String getHandleId() {
		return handleId;
	}

	public void setHandleId(String handleId) {
		this.handleId = handleId;
	}

	public String getLogonId() {
		return logonId;
	}

	public void setLogonId(String logonId) {
		this.logonId = logonId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubstatus() {
		return substatus;
	}

	public void setSubstatus(String substatus) {
		this.substatus = substatus;
	}

	public int getLogonType() {
		return logonType;
	}

	public void setLogonType(int logonType) {
		this.logonType = logonType;
	}

	public String getTargetUsername() {
		return targetUsername;
	}

	public void setTargetUsername(String targetUsername) {
		this.targetUsername = targetUsername;
	}

	public String getTargetDomainName() {
		return targetDomainName;
	}

	public void setTargetDomainName(String targetDomainName) {
		this.targetDomainName = targetDomainName;
	}
	
	public String getIpAddress(){
		return ipAddress;
	}
	
	public void setIpAddress(String ipAddress){
		this.ipAddress = ipAddress;
	}

	@Override
	public String toString() {
		return "MsEvent " + super.toString() + " [eventId=" + eventId + ", keywords=" + keywords + ", subjectLogonId=" + subjectLogonId
				+ ", handleId=" + handleId + ", logonId=" + logonId + ", status=" + status + ", substatus=" + substatus
				+ ", logonType=" + logonType + ", targetUsername=" + targetUsername + ", targetDomainName="
				+ targetDomainName + ", ipAddress=" + ipAddress + "]";
	}
	
	
	
}
