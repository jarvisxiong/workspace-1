package ume.pareva.userservice;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.pojo.CampaignHitCounter;



public class LandingPage_campaign {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private CampaignHitCounterDao campaignhitcounterdao;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session openSession() {
		return sessionFactory.openSession();
	}
	
	public String initializeLandingPage(String domain,String campaignId,String networkId){
		String landingPage="";
		List<String> landingPageList=getLandingPageList(domain,campaignId,networkId);
		landingPage=getLandingPage(landingPageList,domain,campaignId);

		return landingPage;
	}
	
	public List<String> getLandingPageList(String domain,String campaignId,String networkId){
		List<String> landingPageList=new ArrayList<String>();
		java.sql.Time currentTime=new Time(System.currentTimeMillis());
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		String sqlstr="";
		
		if(!campaignId.equals(""))
			sqlstr="SELECT * FROM domainsLandingPages d, mobileClubCampaignsLandingPages m WHERE d.aUnique=m.aLandingPageCode AND " +
					"m.aCampaignUnique='"+campaignId+"' AND aDomainUnique='"+domain+"' ";
		 else
			sqlstr="SELECT * FROM domainsLandingPages d WHERE aDomainUnique='"+domain+"' ";
		
		if (networkId.equals(""))
            sqlstr+=" AND aNetwork='default' AND aStatus='1'";
		else
			sqlstr+=" AND aNetwork='"+networkId+"' AND '"+currentTime+"' between aStartTime and aEndTime AND aStatus='1'";
               
        System.out.println("Landing Page Query = "+sqlstr);
		Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage");
		landingPageList =query.list();
		
		if(landingPageList!=null && landingPageList.size()<=0){
			trans.commit();
			session.close();
			landingPageList=getLandingPageList(domain,"",networkId);
        	if(landingPageList!=null && landingPageList.size()<=0){
        		trans.commit();
    			session.close();
        		landingPageList=getLandingPageList(domain,"","all");
        		if(landingPageList!=null && landingPageList.size()<=0){
        			trans.commit();
        			session.close();
        			landingPageList=getLandingPageList(domain,"","");
        		}
        	}
        }
        
		if(landingPageList==null){
        	sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' AND aNetwork='default' ";
        	System.out.println("Landing Page Query = "+sqlstr);
        	query=session.createSQLQuery(sqlstr).addScalar("aLandingPage");
        	landingPageList=query.list();
        }
        
      
		trans.commit();
		session.close();
		
		return landingPageList;
       
	}
	
	public String getLandingPage(List<String> landingPageList,String domain,String campaignId){
		
		CampaignHitCounter campaignHitCounter=new CampaignHitCounter();
		campaignHitCounter.setaDomainUnique(domain);
		campaignHitCounter.setCampaignId(campaignId);
		
		int hitCounter=0;
		try{
			hitCounter=campaignhitcounterdao.getHitCounter(campaignHitCounter);
		}catch(Exception e){hitCounter=0;}
		
		int index=0;
		try{
			index=hitCounter%landingPageList.size();
		}catch(Exception e){index=0;}
		
		return landingPageList.get(index).toString();
		
	}
	
/*	

	
	public String getLandingPage(String domain,String networkid){
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		String landingPageName="";
		java.sql.Time currentTime=new Time(System.currentTimeMillis());
                String sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' ";
                
                if (networkid.equals("default"))
                    sqlstr+=" AND aNetwork='"+networkid+"' AND aStatus='1'";
                else if(networkid!=null && !networkid.trim().equalsIgnoreCase("") && networkid.trim().length()>0)
                 sqlstr+=" AND aNetwork='"+networkid+"' AND '"+currentTime+"' between aStartTime and aEndTime AND aStatus='1'";
                
		
		System.out.println("Landing Page Query = "+sqlstr);
		Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage");
		java.util.List landingPageList=query.list();
		for(Object landingPage:landingPageList)
		{
			landingPageName=landingPage.toString();
		}
		trans.commit();
		session.close();
		return landingPageName;
		
	}
	
	public String getLandingPage(String domain,String campaignId,String networkid){
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		java.sql.Time currentTime=new Time(System.currentTimeMillis());
		
		String sqlstr="";
		//Query Including CampaignId 
		 if(!campaignId.equals(""))
			 sqlstr="select * from domainsLandingPages d, mobileClubCampaigns m, mobileClubCampaignsLandingPages mc where d.aUnique=mc.aLandingPageCode " +
					"and m.aUnique=mc.aCampaignUnique and aDomainUnique='"+domain+"' AND campaignId='"+campaignId+"'";
		 else
			  sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' ";
                
//                
//                	sqlstr+=" AND campaignId='"+campaignId+"'";
                
                if (networkid.equals("default"))
                    sqlstr+=" AND aNetwork='"+networkid+"' AND aStatus='1'";
                
                else if(networkid!=null && !networkid.trim().equalsIgnoreCase("") && networkid.trim().length()>0)
                 sqlstr+=" AND aNetwork='"+networkid+"' AND '"+currentTime+"' between aStartTime and aEndTime AND aStatus='1'";
                
                
		
		System.out.println("Landing Page Query = "+sqlstr);
		Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage");
		java.util.List landingPageList=query.list();
                
                      if(landingPageList==null)
                {
                   sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' AND aNetwork='default' ";
                   landingPageList=query.list();
                }
                      
                if(landingPageList!=null && landingPageList.size()<=0)
                {
                   sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' AND aNetwork='default' ";
                   landingPageList=query.list();
                }
            
                
		
		CampaignHitCounter campaignHitCounter=new CampaignHitCounter();
		campaignHitCounter.setaDomainUnique(domain);
		campaignHitCounter.setCampaignId(campaignId);
		int hitCounter=0;
                try{
                hitCounter=campaignhitcounterdao.getHitCounter(campaignHitCounter);
                }catch(Exception e){hitCounter=0;}
		
		int index=0;
                try{
                index=hitCounter%landingPageList.size();
                }catch(Exception e){index=0;}
		//for(Object landingPage:landingPageList)
	//	{
		//	landingPageName=landingPage.toString();
	//	}
		trans.commit();
		session.close();
		return landingPageList.get(index).toString();
		
	}*/


	
	
	public String initializeLandingPage(String domain){
		return initializeLandingPage(domain,"","all");
		
	}
	
	public String initializeLandingPage(String domain,String campaignId){
		return initializeLandingPage(domain,campaignId,"all");
		
	}
}
