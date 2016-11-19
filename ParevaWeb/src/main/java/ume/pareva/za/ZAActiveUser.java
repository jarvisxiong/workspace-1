package ume.pareva.za;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ZAActiveUser implements HttpSessionBindingListener {
	private ZAActiveSession activeSession;
	HttpSession session;
	String wapid;
	int sessionCounter;

	public String getWapid() {
		return wapid;
	}

	public void setWapid(String wapid) {
		this.wapid = wapid;
	}

	public ZAActiveUser(String wapid,HttpSession session) {
		this.wapid = wapid;
		this.session=session;
		activeSession=ZAActiveSession.getActiveSession();
	}

	public void valueBound(HttpSessionBindingEvent event) {
		ZAActiveUser activeUser=(ZAActiveUser)event.getValue();
		String wapid=activeUser.getWapid();
		if(activeSession.getActiveSessionPerUser().get(wapid)!=null){
			sessionCounter=activeSession.getActiveSessionPerUser().get(wapid);
			sessionCounter=sessionCounter+1;
		}
		else{
			sessionCounter=1;
		}
		if(sessionCounter<=5){
			activeSession.getActiveSessionPerUser().put(wapid,sessionCounter);
			activeSession.getActiveUserSession().put(session.getId(),(ZAActiveUser)event.getValue());
		}
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		ZAActiveUser activeUser=(ZAActiveUser)event.getValue();
		String wapid=activeUser.getWapid();
		if(activeSession.getActiveSessionPerUser().get(wapid)!=null){
			sessionCounter=activeSession.getActiveSessionPerUser().get(wapid);
			sessionCounter=sessionCounter-1;
			activeSession.getActiveSessionPerUser().put(wapid,sessionCounter);
			activeSession.getActiveUserSession().remove(session.getId());
		}
		
	}

}
