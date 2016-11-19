package ume.pareva.za;


import java.util.LinkedHashMap;
import java.util.Map;

public class ZAActiveSession {
	Map<String,ZAActiveUser> activeUserSession=new LinkedHashMap<String,ZAActiveUser>();
	Map<String,Integer> activeSessionPerUser=new LinkedHashMap<String,Integer>();
	private static ZAActiveSession activeSession;

	private ZAActiveSession(){}

	public static ZAActiveSession getActiveSession(){
		if(activeSession!=null)
			return activeSession;
		else{
			activeSession=new ZAActiveSession();
			return activeSession;
		}
	}

	public Map<String, ZAActiveUser> getActiveUserSession() {
		return activeUserSession;
	}

	public void setActiveUserSession(Map<String, ZAActiveUser> activeUserSession) {
		this.activeUserSession = activeUserSession;
	}

	public Map<String, Integer> getActiveSessionPerUser() {
		return activeSessionPerUser;
	}

	public void setActiveSessionPerUser(Map<String, Integer> activeSessionPerUser) {
		this.activeSessionPerUser = activeSessionPerUser;
	}

}
