package ume.pareva.pt.util;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class ActiveUser implements HttpSessionBindingListener {
	private ActiveSession activeSession;
	HttpSession session;
	String subscriptionId;
	int sessionCounter;

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public ActiveUser(String subscriptionId,HttpSession session) {
		this.subscriptionId = subscriptionId;
		this.session=session;
		activeSession=ActiveSession.getActiveSession();
	}

	public void valueBound(HttpSessionBindingEvent event) {
		ActiveUser activeUser=(ActiveUser)event.getValue();
		String subscriptionId=activeUser.getSubscriptionId();
		if(activeSession.getActiveSessionPerUser().get(subscriptionId)!=null){
			sessionCounter=activeSession.getActiveSessionPerUser().get(subscriptionId);
			sessionCounter=sessionCounter+1;
		}
		else{
			sessionCounter=1;
		}
		if(sessionCounter<=5){
			activeSession.getActiveSessionPerUser().put(subscriptionId,sessionCounter);
			activeSession.getActiveUserSession().put(session.getId(),(ActiveUser)event.getValue());
		}
	}

	public void valueUnbound(HttpSessionBindingEvent event) {
		ActiveUser activeUser=(ActiveUser)event.getValue();
		String subscriptionId=activeUser.getSubscriptionId();
		if(activeSession.getActiveSessionPerUser().get(subscriptionId)!=null){
			sessionCounter=activeSession.getActiveSessionPerUser().get(subscriptionId);
			sessionCounter=sessionCounter-1;
			activeSession.getActiveSessionPerUser().put(subscriptionId,sessionCounter);
			activeSession.getActiveUserSession().remove(session.getId());
		}
		
	}

}
