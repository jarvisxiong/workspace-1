package ume.pareva.restservices;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xuggle.xuggler.IContainer;

import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.ItemImage;
import ume.pareva.cms.ItemResource;
import ume.pareva.cms.ItemResourceDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.sdk.HandsetDao;

@Component
@Path("/VideoService")
public class VideoService {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private VideoClipDao videoclipdao;

	@Autowired
	BannerAdDao banneraddao;
        
        @Autowired
        ItemResourceDao itemresourcedao;
        
        String videoDirectory=System.getProperty("contenturl");

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private Session openSession() {
		return sessionFactory.openSession();
	}

	public VideoClipDao getVideoclipdao() {
		return videoclipdao;
	}

	public void setVideoclipdao(VideoClipDao videoclipdao) {
		this.videoclipdao = videoclipdao;
	}

	public BannerAdDao getBanneraddao() {
		return banneraddao;
	}

	public void setBanneraddao(BannerAdDao banneraddao) {
		this.banneraddao = banneraddao;
	}
	
	@GET
    @Path("/getvideocategories")
    @RolesAllowed("ADMIN")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getVideoCategories(@QueryParam("domain")String domain) {
		
		HashMap<String,List<String>> serviceUniqueMap=getServiceUniqueMap(domain);
		List<String> hotVideo=serviceUniqueMap.get("promo_hot_video_category_list");
		
            
            //if(domain.equals("8578564937341llun")) System.out.println("ItalyVIDEO  calling GetVideoCategory");

		String category = "",classification="";
		java.util.List categoryList = new ArrayList();
		Session session = openSession();
		Transaction trans = session.beginTransaction();
                
                String classificationquery="SELECT classification from clientServices where aDomain='"+domain+"' AND classification<>'' LIMIT 1";
                System.out.println("getvideos  classificationquery "+classificationquery);
                Query qclassification=session.createSQLQuery(classificationquery).addScalar("classification");
                try{
                   classification=qclassification.list().get(0).toString();
                   //if(domain.equals("8578564937341llun")) System.out.println("ItalyVIDEO classification "+classification);
                }catch(Exception e){classification="";}
                
		for (int i = 0; i < hotVideo.size(); i++) {

			String srvc = (String) hotVideo.get(i);
			//System.out.println("Service Name = " + srvc);

			String sqlstr = "";
                	Query query = null;


			// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sqlstr = "select vc.aUnique as aUnique, ic.aName1 as aCategory, ic.aUnique as aCategoryUnique from videoClips vc, itemCategories ic, selectedCategories sc where ic.aUnique in "
					+ "(select aCategory from selectedCategories where aServiceUnique='"
					+ srvc
					+ "' and aDomainUnique='"+ domain+ "')";
					
                               if(classification!=null && !classification.trim().equalsIgnoreCase("")){
                                  sqlstr+="AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%') AND vc.aClassification='"+classification+"'";
                                }
                                else{
					sqlstr+= "and vc.aCategory=ic.aUnique ";
                                    }
                               
                                sqlstr+= "and sc.aServiceUnique='"+ srvc+ "' and sc.aDomainUnique='"+ domain+ "' group by ic.aUnique";
                        
                        System.out.println("getvideos  inside loop third query  "+sqlstr);
                        //if(domain.equals("8578564937341llun")) System.out.println("ItalyVIDEO  another video query "+sqlstr);
			query = session.createSQLQuery(sqlstr).addScalar("aUnique")
					.addScalar("aCategory").addScalar("aCategoryUnique");
			java.util.List res = query.list();
			if (!res.isEmpty()) {

				for (Object o : res) {
					Object[] row = (Object[]) o;
					String[] props = new String[3];
					props[0] = String.valueOf(row[0]);
					props[1] = String.valueOf(row[1]);
					props[2] = String.valueOf(row[2]);

					HashMap<String, Object> categoryMap = new HashMap<String, Object>();
					VideoClip item = null;

					String img = "";
					int iw = 0;
					int ih = 0;
					String gtitle = "";
					int priceGroup = 0;

					item = getVideoclipdao().getItem(props[0]);

					if (item == null) {
						//System.out.println("VIDEO NOT FOUND");
						session.close();
						return null;
					}

					ItemImage image = item.getItemImage("webthumb", 0,4);
                                        
                                        if(image==null)
                                        {
                                            //image = item.getItemImage("mobthumb", 0,handset.getImageProfile());
                                        	image = item.getItemImage("mobthumb", 0,4);
                                        }
					if (image != null) {
						img = image.getPath();
						iw = image.getWidth();
						ih = image.getHeight();
						categoryMap.put("img", img);
						categoryMap.put("unique", props[2]);
						categoryMap.put("category", props[1]);

					}

					categoryList.add(categoryMap);

				}

			}

		}
		trans.commit();
		session.close();
	//	HashMap hotVideoParameter = new HashMap();
		//hotVideoParameter.put("list", list);
//		hotVideoParameter.put("categoryList", categoryList);
	//	hotVideoParameter.put("number_of_elements", number_of_elements);
	//	hotVideoParameter.put("category", hotVideo.get(0));
		return Response.status(200).entity(categoryList).build();
		//return hotVideoParameter;

	}

	@GET
    @Path("/getvideos")
    @RolesAllowed("ADMIN")
    @Produces({MediaType.APPLICATION_JSON})
   	public Response getVideos(@QueryParam("index")int index,@QueryParam("domain")String domain,@QueryParam("lang")String lang){
		
		HashMap videoParameter=getVideoList(domain);
		List list=(List)videoParameter.get("list");
		int numberOfElements=(int)videoParameter.get("numberOfElements");
		
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		int counter = 0;
		List videos = new ArrayList();
                

		for (int j = index; j < list.size(); j++) {
			Query query = null;
			HashMap<String, Object> videoMap = new HashMap<String, Object>();
			VideoClip item = null;
			String sqlstr = "";
			String img = "";
			int iw = 0;
			int ih = 0;
			String gtitle = "";
			int priceGroup = 0;

			String[] props = null;
			int rand = (int) java.lang.Math.floor(java.lang.Math.random()* list.size());

			//item = videoclipdao.getItem(list.get(rand).toString());
			//videoMap.put("videoUnique", list.get(rand).toString());//a
//			item = videoclipdao.getItem(list.get(rand).toString());
	//		videoMap.put("videoUnique", list.get(rand).toString());
                        
		//	boolean exist=checkIfExist(videos, list.get(rand).toString());            
		//	if(!exist){
	//		item = videoclipdao.getItem(list.get(rand).toString());
			
	//		videoMap.put("videoUnique", list.get(rand).toString());
			
			item = videoclipdao.getItem(list.get(j).toString());
			videoMap.put("videoUnique", list.get(j).toString());//a
//			
			
			if (item == null) {
				//System.out.println("VIDEO NOT FOUND");
				session.close();
				return null;
			}

//			sqlstr = "SELECT count(*) as count FROM itemLog  WHERE aItemUnique='" + item.getUnique() + "'";
//			int count = 0;
//			query = session.createSQLQuery(sqlstr).addScalar("count");
//			java.util.List countList = query.list();
//
//			for (Object o1 : countList) {
//				String row1 = o1.toString();
//				count = Integer.parseInt(String.valueOf(row1));
//				//System.out.println("COUNTING BG : " + count);
//			}
                        
                        ItemResource itemresource=itemresourcedao.getResource(item.getUnique());
			if(itemresource!=null) {
                            if(videoDirectory==null || "".equalsIgnoreCase(videoDirectory)) videoDirectory="/mnt/content";
                            videoDirectory+=itemresource.getDataFile();
                            System.out.println("getvideos debug video location "+videoDirectory);
                            System.out.println("getvideos debug itemResource location  "+itemresource.getDataFile());
                            
                            IContainer container = IContainer.make(); 
                            // we attempt to open up the container
                            int result = container.open(videoDirectory, IContainer.Type.READ, null);
                              if (result<0){
                                  System.out.println("getvideos Exception Failed to open media file "+"VideoService line no. 270 "+item.getUnique());
                                  //throw new RuntimeException("getvideos Exception Failed to open media file");
                              }
                              long duration = container.getDuration()/1000; 
                              int videodurationrandom =60000 + (int) (Math.random() * ((60000 - 2000) + 10000));
                              duration+=videodurationrandom;
                              String videoduration=new SimpleDateFormat("MM:ss").format(new Date(duration));
                              videoMap.put("videolength",videoduration);
                              videoDirectory="";
                        }

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
	
			int viewsNumber = (int)(Math.random() * ((100000-50000)+1)) + 50000;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("webthumb", 0,4);
                        
                           if(image==null)
                                        {
                                          //  image = item.getItemImage("mobthumb", 0,handset.getImageProfile());
                                            image = item.getItemImage("mobthumb", 0,4);
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

				
			}
			
			
			counter = counter + 1;
			//System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == numberOfElements)
				break;
			//}

		}
		trans.commit();
		session.close();
		return Response.status(200).entity(videos).build();
		
	}
	
	public HashMap getVideoList(String domain){
		
		HashMap<String,List<String>> serviceUniqueMap=getServiceUniqueMap(domain);
		List<String> hotVideo=serviceUniqueMap.get("promo_hot_video_category_list");
		
		String category = "",classification="";
		int numberOfElements = 0;
		
		List<String> list = new ArrayList<String>();
		Session session = openSession();
		Transaction trans = session.beginTransaction();
                
                String classificationquery="SELECT classification from clientServices where aDomain='"+domain+"' AND classification<>'' LIMIT 1";
                System.out.println("getvideos  classificationquery "+classificationquery);
                Query qclassification=session.createSQLQuery(classificationquery).addScalar("classification");
                try{
                   classification=qclassification.list().get(0).toString();
                   //if(domain.equals("8578564937341llun")) System.out.println("ItalyVIDEO classification "+classification);
                }catch(Exception e){classification="";}
                
		for (int i = 0; i < hotVideo.size(); i++) {

			String srvc = (String) hotVideo.get(i);
			//System.out.println("Service Name = " + srvc);

			String sqlstr = "";

			sqlstr = "select aCategoryUnique,numberOfElements from clientServices where aUnique='"
					+ srvc + "' and aDomain='" + domain + "'";
                        
                        
                        System.out.println("getvideos  inside loop first query  "+sqlstr);
                	Query query = null;
			query = session.createSQLQuery(sqlstr).addScalar("aCategoryUnique").addScalar("numberOfElements");
			java.util.List fromCategory = query.list();
			if (!fromCategory.isEmpty()) {

				for (Object o : fromCategory) {
					Object[] row = (Object[]) o;

					category = String.valueOf(row[0]);
					numberOfElements = Integer.parseInt(String.valueOf(row[1]));

				}

			}
                        
                        /*
                        SELECT DISTINCT(vc.aUnique) AS aUnique ,vc.aCategory,vc.aClassification,vc.aTitle
FROM videoClips vc, itemCategories ic, selectedCategories sc 
WHERE ic.aUnique=sc.aCategory  
AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%') 
AND aServiceUnique='4412907208341KDS' AND aDomainUnique='8578564937341llun' 
and vc.aClassification='topless' LIMIT 10;
                        
                        
                        */

			sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc "
                                + "WHERE ic.aUnique in"
					+ "(select aCategory from selectedCategories where aServiceUnique='"
					+ srvc
					+ "' and aDomainUnique='"
					+ domain
					+ "')";    
                        
                        
                                if(classification!=null && !classification.trim().equalsIgnoreCase("")){
                                  sqlstr+="AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='"+classification+"'";
                                }
                                else{
					sqlstr+= "and vc.aCategory=ic.aUnique ";
                                    }
                                    //   sqlstr+= "and sc.aServiceUnique='"+ srvc + "' and sc.aDomainUnique='" + domain + "' order by vc.aCreated desc";
                                       sqlstr+= "and sc.aServiceUnique='"+ srvc + "' and sc.aDomainUnique='" + domain + "' order by rand()";
                        //if(domain.equals("8578564937341llun")) System.out.println("ItalyVIDEO  videos query "+sqlstr);
System.out.println("getvideos  inside loop second loop query  "+sqlstr);
			query = session.createSQLQuery(sqlstr).addScalar("aUnique");
			java.util.List result = query.list();
			if (!result.isEmpty()) {

				for (Object o : result) {

					list.add(o.toString());
				}

			}
		}
			HashMap videoParameter=new HashMap();
			videoParameter.put("list",list);
			videoParameter.put("numberOfElements",numberOfElements);
			
			return videoParameter;

		
	}
	
	
	public HashMap<String,List<String>> getServiceUniqueMap(String domain){
		
		List list = (List)UmeTempCmsCache.clientServices.get(String.valueOf(domain));
        
        ArrayList<String> promo_hot_video_list = new ArrayList<String>();
        ArrayList<String> promo_freetext_list = new ArrayList<String>();
        ArrayList<String> promo_hot_bg_list = new ArrayList<String>();
        ArrayList<String> promo_top_bg_list = new ArrayList<String>();
        ArrayList<String> promo_hot_video_category_list = new ArrayList<String>();
        ArrayList<String> promo_banner_list = new ArrayList<String>();
        int number_of_category = 0;
        int hot_video = 0;
        int free_text = 0;
        int hot_bg = 0;
        int top_bg = 0;
        int hot_video_category=0;
        
       
        
        if(list!=null && !list.isEmpty() && list.size()>0) {
        for (int i = 0; i < list.size(); ++i) {
            String[] servicesList = (String[])list.get(i);
            String srvc = servicesList[1];
            String fName = servicesList[3];            
                       
            if (fName.equals("promo_hot_video.jsp")) {
                promo_hot_video_list.add(srvc);
                hot_video = 1;
                continue;
            }
            if (fName.equals("promo_freetext.jsp")) {
                promo_freetext_list.add(srvc);
                free_text = 1;
                continue;
            }
            if (fName.equals("promo_hot_bg.jsp")) {
                promo_hot_bg_list.add(srvc);
                hot_bg = 1;
                continue;
            }
            if (fName.equals("promo_top_bg.jsp")) {
                promo_top_bg_list.add(srvc);
                top_bg = 1;
                continue;
            }
            if (fName.equals("promo_hot_video_category.jsp")) {
                promo_hot_video_category_list.add(srvc);
                hot_video_category = 1;
                continue;
            }
            if (!fName.equals("promo_banner.jsp")) continue;
            promo_banner_list.add(srvc);
        //    session.setAttribute("promo_banner_list", promo_banner_list);
        }
    }
        number_of_category = hot_video + free_text + hot_bg + top_bg+hot_video_category;
        //System.out.println("index_main.jsp list size = " + list.size());
        HashMap<String,List<String>> serviceUniqueMap=new HashMap<String,List<String>>();
        serviceUniqueMap.put("promo_hot_video_list", promo_hot_video_list);
        serviceUniqueMap.put("promo_freetext_list", promo_freetext_list);
        serviceUniqueMap.put("promo_hot_bg_list", promo_hot_bg_list);
        serviceUniqueMap.put("promo_top_bg_list", promo_top_bg_list);
        serviceUniqueMap.put("promo_hot_video_category_list", promo_hot_video_category_list);
        return serviceUniqueMap;
	}


}
