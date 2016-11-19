package ume.pareva.userservice;

import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;

import ume.pareva.cms.CampaignHitCounterDao;
import ume.pareva.pojo.CampaignHitCounter;
import ume.pareva.util.DateUtil;



public class CopyOfLandingPage {
	
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
		List<String> landingPagesWithinTimeFrame=new ArrayList<String>();
		List landingPageList=getLandingPageList(domain,campaignId,networkId);
		
		if(networkId.equals("")){
			landingPage=getLandingPage(landingPageList,domain,campaignId);
		}else{
		landingPagesWithinTimeFrame=getLandingPageListWithinTimeFrame(landingPageList,false);
                
                if(landingPagesWithinTimeFrame.size()<=0){
                    if(null!=campaignId && !"".equalsIgnoreCase(campaignId)){
                        
                landingPageList=getLandingPageListCampaign(domain,campaignId,networkId);
                landingPagesWithinTimeFrame=getLandingPageListWithinTimeFrame(landingPageList,false);
                        
                    }              
                    
                    
                }
                
			if(landingPagesWithinTimeFrame.size()<=0){
	
			landingPageList=getLandingPageList(domain,"",networkId);
			landingPagesWithinTimeFrame=getLandingPageListWithinTimeFrame(landingPageList,false);
			if(landingPagesWithinTimeFrame.size()<=0){
        	
        		landingPageList=getLandingPageList(domain,"","all");
        		landingPagesWithinTimeFrame=getLandingPageListWithinTimeFrame(landingPageList,false);
    			if(landingPagesWithinTimeFrame.size()<=0){
        		
        			landingPageList=getLandingPageList(domain,"","");
        			landingPagesWithinTimeFrame=getLandingPageListWithinTimeFrame(landingPageList,true);
        		}
        	}
        }
		
        landingPage=getLandingPage(landingPagesWithinTimeFrame,domain,campaignId);
		}
		return landingPage;
	}
	
	public List<String> getLandingPageListWithinTimeFrame(List landingPageList, boolean isDefault){
		List<String> validLandingPages=new ArrayList<String>();
		if(landingPageList.size()>0){
			
			for(Object o:landingPageList){
				 
				    Object [] row=(Object[]) o;
				    String startTime = String.valueOf(row[1]);
				    String endTime = String.valueOf(row[2]);
				    java.sql.Time currentTime=new Time(System.currentTimeMillis());
				    if(!isDefault){
				    try {
						if(DateUtil.isTimeBetweenTwoTime(startTime, endTime, currentTime.toString())){
							validLandingPages.add(String.valueOf(row[0]));
						}
					} catch (ParseException e) {
					
						e.printStackTrace();
					}
				    }else
				    	validLandingPages.add(String.valueOf(row[0]));
				    	
			}
		}
		return validLandingPages;
	}
        
        
        
        //============ CampaignId LandingPage ==============================
        public List getLandingPageListCampaign(String domain,String campaignId,String networkId){
		List landingPageList=new ArrayList();
		java.sql.Time currentTime=new Time(System.currentTimeMillis());
		Session session = null;
                try{
                session=openSession();
		Transaction trans = session.beginTransaction();
		String sqlstr="SELECT * FROM domainsLandingPages WHERE aDomainUnique='"+domain+"' AND aStatus='1'";
		
                if(!networkId.equals("unknown") || !networkId.trim().equalsIgnoreCase("")){
                     sqlstr+=" AND aNetwork='"+networkId+"'";
                }	
         
                System.out.println("Landing Page Query = "+sqlstr);
		Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage").addScalar("aStartTime").addScalar("aEndTime");
                //query.setCacheable(true);
                //query.setCacheRegion("query.landingpage");
		landingPageList =query.list();
		
		trans.commit();
		
                
                }
                catch(Exception e){}
                finally{
                    session.close();
                }
		
		return landingPageList;
       
	}
        
        
        //===============END CampaignID LandingPage ===========================
	
	
	public List getLandingPageList(String domain,String campaignId,String networkId){
		List landingPageList=new ArrayList();
		java.sql.Time currentTime=new Time(System.currentTimeMillis());
                Session session = null;
                try{
		session = openSession();
		Transaction trans = session.beginTransaction();
		String sqlstr="";
		
		if(!campaignId.equals("")){
			sqlstr="SELECT * FROM domainsLandingPages d, mobileClubCampaignsLandingPages m, domainsTemplates dt WHERE d.aLandingPage=m.aLandingPageCode AND "
                                        + " d.aTemplateName=dt.templateName  AND d.aDomainUnique=dt.aDomainUnique AND " +
					"m.aCampaignUnique='"+campaignId+"' AND d.aDomainUnique='"+domain+"'  AND dt.aDomainUnique='"+domain+"' AND dt.status='A' ";
		}
		 else
			sqlstr="SELECT * FROM domainsLandingPages d, domainsTemplates dt WHERE d.aDomainUnique='"+domain+"' AND d.aTemplateName=dt.templateName AND dt.status='A'";
		
		if (networkId.equals(""))
                sqlstr+=" AND aNetwork='default' AND aStatus='1'";
		else
			sqlstr+=" AND aNetwork='"+networkId+"' AND aStatus='1'";/* AND '"+currentTime+"' between aStartTime and aEndTime AND aStatus='1'";
*/               
                if(campaignId.equals("3802460641KDS"))
        System.out.println("LandingPage Query = "+sqlstr);
		//Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage").addScalar("aStartTime").addScalar("aEndTime");
        Query query=session.createSQLQuery(sqlstr).addScalar("aLandingPage",StandardBasicTypes.STRING).addScalar("aStartTime",StandardBasicTypes.STRING).addScalar("aEndTime",StandardBasicTypes.STRING);
                
                //query.setCacheable(true);
                //query.setCacheRegion("query.landingpage");
                            
                
		landingPageList =query.list();
		
       		trans.commit();		
                }catch(Exception e){}
		
                finally{
                    session.close();;
                }
		return landingPageList;
       
	}
	
	public String getLandingPage(List<String> landingPageList,String domain,String campaignId){
		
		CampaignHitCounter campaignHitCounter=new CampaignHitCounter();
		campaignHitCounter.setaDomainUnique(domain);
		campaignHitCounter.setCampaignId(campaignId); //
		
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
	

	
	public String initializeLandingPage(String domain){
		return initializeLandingPage(domain,"","default");
		
	}
	
	public String initializeLandingPage(String domain,String campaignId){
		return initializeLandingPage(domain,campaignId,"all");
		
	}
}
