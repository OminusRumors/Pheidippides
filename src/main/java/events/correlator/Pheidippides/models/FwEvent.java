package events.correlator.Pheidippides.models;

import java.util.Calendar;

public class FwEvent extends Event{
	
	private String type;
	private String subtype;
	private String level;
	private String action;
	private String dstIp;
	private String dstCountry;
	private String dstIntf;
	private String srcIp;
	private String srcCountry;
	private String srcIntf;
	private String app;
	private String msg;
	private String recepient;
	private String sender;
	private String service;
	private String ref;
	
	public FwEvent(int keyId, String sourceLog, Calendar created, String type, String subtype, String level,
			String action, String dstIp, String dstCountry, String dstIntf, String srcIp, String srcCountry,
			String srcIntf, String app, String msg, String recepient, String sender, String service, String ref) {
		super(keyId, sourceLog, created);
		this.type = type;
		this.subtype = subtype;
		this.level = level;
		this.action = action;
		this.dstIp = dstIp;
		this.dstCountry = dstCountry;
		this.dstIntf = dstIntf;
		this.srcIp = srcIp;
		this.srcCountry = srcCountry;
		this.srcIntf = srcIntf;
		this.app = app;
		this.msg = msg;
		this.recepient = recepient;
		this.sender = sender;
		this.service = service;
		this.ref = ref;
	}

	public FwEvent(int keyId, String sourceLog, Calendar created, String type, String subtype) {
		super(keyId, sourceLog, created);
		this.type = type;
		this.subtype = subtype;
	}

	public FwEvent(int keyId, String sourceLog, Calendar created, String type, String subtype, String action) {
		super(keyId, sourceLog, created);
		this.type = type;
		this.subtype = subtype;
		this.action = action;
	}

	public FwEvent(int keyId, String sourceLog, Calendar created, String type, String subtype, String level,
			String action) {
		super(keyId, sourceLog, created);
		this.type = type;
		this.subtype = subtype;
		this.level = level;
		this.action = action;
	}
	
	public String getType(){
		return type;
	}
	
	public String getSubtype(){
		return subtype;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getDstIp() {
		return dstIp;
	}

	public void setDstIp(String dstIp) {
		this.dstIp = dstIp;
	}

	public String getDstCountry() {
		return dstCountry;
	}

	public void setDstCountry(String dstCountry) {
		this.dstCountry = dstCountry;
	}

	public String getDstIntf() {
		return dstIntf;
	}

	public void setDstIntf(String dstIntf) {
		this.dstIntf = dstIntf;
	}

	public String getSrcIp() {
		return srcIp;
	}

	public void setSrcIp(String srcIp) {
		this.srcIp = srcIp;
	}

	public String getSrcCountry() {
		return srcCountry;
	}

	public void setSrcCountry(String srcCountry) {
		this.srcCountry = srcCountry;
	}

	public String getSrcIntf() {
		return srcIntf;
	}

	public void setSrcIntf(String srcIntf) {
		this.srcIntf = srcIntf;
	}

	public String getApp() {
		return app;
	}

	public void setApp(String app) {
		this.app = app;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getRecepient() {
		return recepient;
	}

	public void setRecepient(String recepient) {
		this.recepient = recepient;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	@Override
	public String toString() {
		return "FwEvent " + super.toString() + " [type=" + type + ", subtype=" + subtype + ", level=" + level + ", action=" + action + ", dstIp="
				+ dstIp + ", dstCountry=" + dstCountry + ", dstIntf=" + dstIntf + ", srcIp=" + srcIp + ", srcCountry="
				+ srcCountry + ", srcIntf=" + srcIntf + ", app=" + app + ", msg=" + msg + ", recepient=" + recepient
				+ ", sender=" + sender + ", service=" + service + ", ref=" + ref + "]";
	}

		
}
