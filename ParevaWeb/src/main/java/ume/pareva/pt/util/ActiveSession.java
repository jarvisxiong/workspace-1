package ume.pareva.pt.util;


import java.util.LinkedHashMap;
import java.util.Map;

public class ActiveSession {
	Map<String,ActiveUser> activeUserSession=new LinkedHashMap<String,ActiveUser>();
	Map<String,Integer> activeSessionPerUser=new LinkedHashMap<String,Integer>();
	private static ActiveSession activeSession;

	private ActiveSession(){}

	public static ActiveSession getActiveSession(){
		if(activeSession!=null)
			return activeSession;
		else{
			activeSession=new ActiveSession();
			return activeSession;
		}
	}

	public Map<String, ActiveUser> getActiveUserSession() {
		return activeUserSession;
	}

	public void setActiveUserSession(Map<String, ActiveUser> activeUserSession) {
		this.activeUserSession = activeUserSession;
	}

	public Map<String, Integer> getActiveSessionPerUser() {
		return activeSessionPerUser;
	}

	public void setActiveSessionPerUser(Map<String, Integer> activeSessionPerUser) {
		this.activeSessionPerUser = activeSessionPerUser;
	}

}
