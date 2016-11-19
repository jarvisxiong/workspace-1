package ume.pareva.webservice;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainer.Type;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.BannerAd;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.ItemImage;
import ume.pareva.cms.ItemResource;
import ume.pareva.cms.ItemResourceDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;

@Component("parevavideolist")
public class ParevaVideoList
{
  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private VideoClipDao videoclipdao;
  @Autowired
  BannerAdDao banneraddao;
  @Autowired
  ItemResourceDao itemresourcedao;
  
  public ParevaVideoList() {}
  
  String videoDirectory = System.getProperty("contenturl");
  
  public SessionFactory getSessionFactory() {
    return sessionFactory;
  }
  
  public void setSessionFactory(SessionFactory sessionFactory)
  {
    this.sessionFactory = sessionFactory;
  }
  
  private Session openSession() {
    return sessionFactory.openSession();
  }
  
  public String[][] getVideoCategories(String videoCategoryServiceUnique, String domain) {
    String[][] categories = null;
    String classification = getClassification(videoCategoryServiceUnique);
    String sqlstr = "select vc.aUnique as aUnique, ic.aName1 as aCategory, ic.aUnique as aCategoryUnique from videoClips vc, itemCategories ic, selectedCategories sc where ic.aUnique in (select aCategory from selectedCategories where aServiceUnique='" + 
    
      videoCategoryServiceUnique + 
      "' and aDomainUnique='" + domain + "')";
    
    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%') AND vc.aClassification='" + classification + "'";
    }
    else {
      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
    }
    
    sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' group by ic.aUnique";
    
    System.out.println("VIDEOWEBSERVICE  GetVideoCategories Query  " + sqlstr);
    
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("aUnique")
        .addScalar("aCategory").addScalar("aCategoryUnique");
      List res = query.list();
      
      if (!res.isEmpty()) {
        categories = new String[res.size()][];
        int counter = 0;
        for (Object o : res) {
          Object[] row = (Object[])o;
          String[] props = new String[3];
          props[0] = String.valueOf(row[0]);
          props[1] = String.valueOf(row[1]);
          props[2] = String.valueOf(row[2]);
          
          HashMap<String, Object> categoryMap = new HashMap();
          VideoClip item = null;
          
          String img = "";
          




          item = videoclipdao.getItem(props[0]);
          
          if (item == null)
          {
            session.close();
            return null;
          }
          
          ItemImage image = item.getItemImage("webthumb", 0, 4);
          
          if (image == null)
          {
            image = item.getItemImage("mobthumb", 0, 4);
          }
          if (image != null) {
            props[0] = image.getPath();
          }
          






          categories[counter] = props;
          counter++;
        }
      }
      trans.commit();
    }
    finally {
      session.close(); }
    
    return categories;
  }
  
  private String getClassification(String videoCategoryServiceUnique)
  {
    String classification = "";
    String classificationquery = "SELECT classification from clientServices where aUnique='" + videoCategoryServiceUnique + "'";
    System.out.println("VIDEOWEBSERVICE  classificationquery " + classificationquery);
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query qclassification = session.createSQLQuery(classificationquery).addScalar("classification");
      try {
        classification = qclassification.list().get(0).toString();
      } catch (Exception e) {
        classification = "";
      }
      trans.commit();
    }
    finally {
      session.close();
    }
    return classification;
  }
  
  public int getNumberOfVideosToDisplay(String videoCategoryServiceUnique, String domain) {
    int numberOfElements = 5;
    String sqlstr = "select numberOfElements from clientServices where aUnique='" + videoCategoryServiceUnique + "' and aDomain='" + domain + "'";
    System.out.println("VIDEOWEBSERVICE  inside loop first query  " + sqlstr);
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar(
        "numberOfElements");
      List numberOfVideosList = query.list();
      if (!numberOfVideosList.isEmpty())
      {
        for (Object o : numberOfVideosList) {
          numberOfElements = Integer.parseInt(o.toString());
        }
      }
      

      trans.commit();
    }
    catch (Exception localException) {}
    
    finally {
      session.close();
    }
    return numberOfElements;
  }
  
  public String[][] getVideos(String videoCategoryServiceUnique, int index, String domain, int numberOfElements, String lang) {
    return getVideos(videoCategoryServiceUnique,"",index,domain,numberOfElements,lang);
	  /*String[][] videos = null;
    String classification = getClassification(videoCategoryServiceUnique);
    String sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
    

      videoCategoryServiceUnique + 
      "' and aDomainUnique='" + 
      domain + 
      "')";
    

    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
    }
    else {
      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
    }
    
    sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' order by rand() limit " + index + "," + numberOfElements;
    
    System.out.println("VIDEOWEBSERVICE  GETVIDEOS Query  " + sqlstr);
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("aUnique");
      List videoUniqueList = query.list();
      if (!videoUniqueList.isEmpty()) {
        videos = new String[videoUniqueList.size()][];
        int counter = 0;
        for (Object o : videoUniqueList)
        {
          query = null;
          String[] props = new String[6];
          VideoClip item = null;
          sqlstr = "";
          String img = "";
          int iw = 0;
          int ih = 0;
          String gtitle = "";
          int priceGroup = 0;
          


          item = videoclipdao.getItem(o.toString());
          
          props[0] = o.toString();
          


          if (item == null) {
            session.close();
            return null;
          }
          
          ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
          if (itemresource != null) {
            if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
            videoDirectory += itemresource.getDataFile();
            System.out.println("getvideos debug video location " + videoDirectory);
            System.out.println("getvideos debug itemResource location  " + itemresource.getDataFile());
            
            IContainer container = IContainer.make();
            
            int result = container.open(videoDirectory, IContainer.Type.READ, null);
            if (result < 0) {
              System.out.println("getvideos Exception Failed to open media file ParevaVideoList line no. 260 for domain " + domain + " serviceunique- " + videoCategoryServiceUnique);
            }
            
            long duration = container.getDuration() / 1000L;
            int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
            duration += videodurationrandom;
            String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
            props[1] = videoduration;
            
            videoDirectory = "";
          }
          
          int ratingNumber = 80 + (int)(Math.random() * 21.0D);
          int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
          gtitle = item.getDescription("title", lang);
          
          props[2] = String.valueOf(ratingNumber);
          props[3] = String.valueOf(viewsNumber);
          props[4] = gtitle;
          



          String price = 
            (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
          ItemImage image = item.getItemImage("webthumb", 0, 4);
          
          if (image == null)
          {
            image = item.getItemImage("mobthumb", 0, 4);
          }
          if (image != null) {
            img = image.getPath();
            iw = image.getWidth();
            ih = image.getHeight();
            props[5] = img;
          }
          




          videos[(counter++)] = props;
        }
      }
      trans.commit();

    }
    catch (Exception localException) {}finally
    {
      session.close();
    }
    


    return videos;*/
  }
  
  
  public String[][] getVideos(String videoCategoryServiceUnique, String videoUnique, int index, String domain, int numberOfElements, String lang) {
	    String[][] videos = null;
	    String classification = getClassification(videoCategoryServiceUnique);
	    String sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
	        videoCategoryServiceUnique + "' and aDomainUnique='" + domain + "')";
	    

	    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
	      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
	    }
	    else {
	      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
	    }
	    
	    if(videoUnique.equals(""))
	    	sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' order by rand() limit " + index + "," + numberOfElements;
	    else
	    	sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' and vc.aUnique!='"+videoUnique+"' order by rand() limit " + index + "," + numberOfElements;
	    	
	    System.out.println("VIDEOWEBSERVICE  GETVIDEOS Query  " + sqlstr);
	    Session session = null;
	    try {
	      session = openSession();
	      Transaction trans = session.beginTransaction();
	      Query query = session.createSQLQuery(sqlstr).addScalar("aUnique");
	      List videoUniqueList = query.list();
	      if (!videoUniqueList.isEmpty()) {
	        videos = new String[videoUniqueList.size()][];
	        int counter = 0;
                System.out.println("getvideos debug video location OUTSIDE line no. 348" + videoDirectory);
	        for (Object o : videoUniqueList)
	        {
                  videoDirectory="";
	          query = null;
	          String[] props = new String[6];
	          VideoClip item = null;
	          sqlstr = "";
	          String img = "";
	          int iw = 0;
	          int ih = 0;
	          String gtitle = "";
	          int priceGroup = 0;
	          


	          item = videoclipdao.getItem(o.toString());
	          
	          props[0] = o.toString();  //unique
	          


	          if (item == null) {
	            session.close();
	            return null;
	          }
	          
	          ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
	          if (itemresource != null) {
                      //videoDirectory="";
	            //if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) 
                        //videoDirectory = "/mnt/content";
	            videoDirectory ="/mnt/content"+ itemresource.getDataFile();
	            System.out.println("getvideos debug video location inside loop line no. 379: " + videoDirectory);
	            System.out.println("getvideos debug itemResource location line no. 380  " + itemresource.getDataFile());
	            
	            IContainer container = IContainer.make();
	            
	            int result = container.open(videoDirectory, IContainer.Type.READ, null);
	            if (result < 0) {
	              System.out.println("getvideos Exception Failed to open media file ParevaVideoList line no. 386 for domain " + domain + " serviceunique- " + videoCategoryServiceUnique+" videodirectory is "+videoDirectory);
	            }
	            
	            long duration = container.getDuration() / 1000L;
	            int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
	            duration += videodurationrandom;
	            String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
	            props[1] = videoduration;  //duration
	            
	            videoDirectory = "";
	          }
	          
	          int ratingNumber = 80 + (int)(Math.random() * 21.0D);
	          int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
	          gtitle = item.getDescription("title", lang);
	          
	          props[2] = String.valueOf(ratingNumber);
	          props[3] = String.valueOf(viewsNumber);
	          props[4] = gtitle;
	          



	          String price = 
	            (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
	          ItemImage image = item.getItemImage("webthumb", 0, 4);
	          
	          if (image == null)
	          {
	            image = item.getItemImage("mobthumb", 0, 4);
	          }
	          if (image != null) {
	            img = image.getPath();
	            iw = image.getWidth();
	            ih = image.getHeight();
	            props[5] = img;
	          }
	          




	          videos[(counter++)] = props;
	        }
	      }
	      trans.commit();

	    }
	    catch (Exception localException) {}finally
	    {
	      session.close();
	    }
	    


	    return videos;
	  }
  
  public int getTotalNumberOfVideos(String videoCategoryServiceUnique, String domain) {
    String classification = getClassification(videoCategoryServiceUnique);
    int totalVideos = 0;
    String sqlstr = "select count(distinct(vc.aUnique)) as totalVideos from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
    

      videoCategoryServiceUnique + 
      "' and aDomainUnique='" + 
      domain + 
      "')";
    

    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
    }
    else {
      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
    }
    
    sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "'";
    
    System.out.println("getvideos  inside loop second loop query  " + sqlstr);
    Session session = null;
    try
    {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("totalVideos", StandardBasicTypes.INTEGER);
      List count = query.list();
      
      if (!count.isEmpty()) {
        for (Object o : count) {
          totalVideos = Integer.parseInt(o.toString());
        }
      }
      trans.commit();
    }
    catch (Exception localException) {}finally {
      session.close();
    }
    return totalVideos;
  }
  
  public String[][] getVideosBySortType(String videoCategoryServiceUnique, String sortType, int index, String domain, int numberOfElements, String lang) {
    String[][] videos = null;
    String classification = getClassification(videoCategoryServiceUnique);
    String sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
    

      videoCategoryServiceUnique + 
      "' and aDomainUnique='" + 
      domain + 
      "')";
    

    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
    }
    else {
      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
    }
    

    if (sortType.equals("new")) {
      sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' order by vc.aCreated desc limit " + index + "," + numberOfElements;
    } else if ((sortType.equals("popular")) || (sortType.equals("top"))) {
      sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' order by rand() limit " + index + "," + numberOfElements;
    }
    System.out.println("getvideos  inside loop second loop query  " + sqlstr);
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("aUnique");
      List videoUniqueList = query.list();
      if (!videoUniqueList.isEmpty()) {
        videos = new String[videoUniqueList.size()][];
        int counter = 0;
         System.out.println("getvideos debug video location OUTSIDE line no. 522" + videoDirectory);
        for (Object o : videoUniqueList)
        {
            videoDirectory="";
          query = null;
          String[] props = new String[6];
          VideoClip item = null;
          sqlstr = "";
          String img = "";
          int iw = 0;
          int ih = 0;
          String gtitle = "";
          int priceGroup = 0;
          


          item = videoclipdao.getItem(o.toString());
          
          props[0] = o.toString();
          


          if (item == null) {
            session.close();
            return null;
          }
          
          ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
          if (itemresource != null) {
            if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
            videoDirectory += itemresource.getDataFile();
            System.out.println("getvideos debug video location line no. 553 " + videoDirectory);
            System.out.println("getvideos debug itemResource location line no. 554  " + itemresource.getDataFile());
            
            IContainer container = IContainer.make();
            
            int result = container.open(videoDirectory, IContainer.Type.READ, null);
            if (result < 0) {
              System.out.println("getvideos Exception Failed to open media file line no.558 ParevaVideoList for domain " + domain + " servicecategory " + videoCategoryServiceUnique);
            }
            
            long duration = container.getDuration() / 1000L;
            int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
            duration += videodurationrandom;
            String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
            props[1] = videoduration;
            
            videoDirectory = "";
          }
          
          int ratingNumber = 80 + (int)(Math.random() * 21.0D);
          int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
          gtitle = item.getDescription("title", lang);
          
          props[2] = String.valueOf(ratingNumber);
          props[3] = String.valueOf(viewsNumber);
          props[4] = gtitle;
          



          String price = 
            (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
          ItemImage image = item.getItemImage("webthumb", 0, 4);
          
          if (image == null)
          {
            image = item.getItemImage("mobthumb", 0, 4);
          }
          if (image != null) {
            img = image.getPath();
            iw = image.getWidth();
            ih = image.getHeight();
            props[5] = img;
          }
          




          videos[(counter++)] = props;
        }
        trans.commit();
      }
    }
    catch (Exception localException) {}
    finally {
      session.close();
    }
    


    return videos;
  }
  
  public String[][] searchVideo(String videoCategoryServiceUnique, String searchString, int index, String domain, int numberOfElements, String lang) {
    String[][] videos = null;
    String classification = getClassification(videoCategoryServiceUnique);
    int totalVideosSearched = getTotalNumberOfSearchedVideos(videoCategoryServiceUnique, domain, classification, searchString);
    if (totalVideosSearched > 0) {
      String sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
      

        videoCategoryServiceUnique + 
        "' and aDomainUnique='" + 
        domain + 
        "')";
      

      if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
        sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
      }
      else {
        sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
      }
      
      sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' and vc.aTitle LIKE '%" + searchString + "%' order by rand() limit " + index + "," + numberOfElements;
      
      System.out.println("getvideos  inside loop second loop query  " + sqlstr);
      Session session = null;
      try {
        session = openSession();
        Transaction trans = session.beginTransaction();
        Query query = session.createSQLQuery(sqlstr).addScalar("aUnique");
        List videoUniqueList = query.list();
        if (!videoUniqueList.isEmpty()) {
          videos = new String[videoUniqueList.size()][];
          int counter = 0;
          for (Object o : videoUniqueList)
          {
            query = null;
            String[] props = new String[7];
            VideoClip item = null;
            sqlstr = "";
            String img = "";
            int iw = 0;
            int ih = 0;
            String gtitle = "";
            int priceGroup = 0;
            


            item = videoclipdao.getItem(o.toString());
            
            props[0] = o.toString();
            


            if (item == null) {
              session.close();
              return null;
            }
            
            ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
            if (itemresource != null) {
              if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
              videoDirectory += itemresource.getDataFile();
              System.out.println("getvideos debug video location " + videoDirectory);
              System.out.println("getvideos debug itemResource location  " + itemresource.getDataFile());
              
              IContainer container = IContainer.make();
              
              int result = container.open(videoDirectory, IContainer.Type.READ, null);
              if (result < 0) {
                System.out.println("getvideos Exception Failed to open media file");
              }
              
              long duration = container.getDuration() / 1000L;
              int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
              duration += videodurationrandom;
              String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
              props[1] = videoduration;
              
              videoDirectory = "";
            }
            
            int ratingNumber = 80 + (int)(Math.random() * 21.0D);
            int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
            gtitle = item.getDescription("title", lang);
            
            props[2] = String.valueOf(ratingNumber);
            props[3] = String.valueOf(viewsNumber);
            props[4] = gtitle;
            



            String price = 
              (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
            ItemImage image = item.getItemImage("webthumb", 0, 4);
            
            if (image == null)
            {
              image = item.getItemImage("mobthumb", 0, 4);
            }
            if (image != null) {
              img = image.getPath();
              iw = image.getWidth();
              ih = image.getHeight();
              props[5] = img;
            }
            


            props[6] = String.valueOf(totalVideosSearched);
            

            videos[(counter++)] = props;
          }
          trans.commit();
        }
      }
      catch (Exception localException) {}finally {
        session.close();
      }
    }
    

    return videos;
  }
  
  public int getTotalNumberOfSearchedVideos(String videoCategoryServiceUnique, String domain, String classification, String searchString)
  {
    int totalVideos = 0;
    String sqlstr = "select count(distinct(vc.aUnique)) as totalVideos from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + 
    

      videoCategoryServiceUnique + 
      "' and aDomainUnique='" + 
      domain + 
      "')";
    

    if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
      sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
    }
    else {
      sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
    }
    
    sqlstr = sqlstr + "and sc.aServiceUnique='" + videoCategoryServiceUnique + "' and sc.aDomainUnique='" + domain + "' and vc.aTitle LIKE '%" + searchString + "%'";
    
    System.out.println("getvideos  inside loop second loop query  " + sqlstr);
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("totalVideos", StandardBasicTypes.INTEGER);
      List count = query.list();
      
      if (!count.isEmpty()) {
        for (Object o : count) {
          totalVideos = Integer.parseInt(o.toString());
        }
      }
      trans.commit();
    }
    catch (Exception localException) {}finally {
      session.close();
    }
    
    return totalVideos;
  }
  
  public String[] getBanner(String bannerSrvc, String domain, String classification, String region)
  {
    List<String> uniqueBanners = new ArrayList();
    BannerAd bannerItem = null;
    Session session = null;
    try {
      session = openSession();
      Transaction trans = session.beginTransaction();
      
      String bannerType = "mobimage";
      
      int numberOfBanners = 3; // 0;
      
      /*
      String sqlstr = "select numberOfElements from clientServices where aUnique='" + 
        bannerSrvc + 
        "' and aDomain='" + 
        domain + 
        "' and aStatus='1'";
      System.out.println("Number of Banner Query = " + sqlstr);
      Query query = null;
      query = session.createSQLQuery(sqlstr)
        .addScalar("numberOfElements");
      List bannerNumbers = query.list();
      
      for (Object o1 : bannerNumbers) {
        String row1 = o1.toString();
        numberOfBanners = Integer.parseInt(row1);
      }
      */


      List<BannerAd> bannerList = banneraddao.getActiveBanners(domain, bannerType, 4, classification, region);
      





      if (bannerList.size() > numberOfBanners) {
        int bannerCounter = 0;
        do
        {
          bannerItem = (BannerAd)bannerList.get((int)Math.floor(Math.random() * 
            bannerList.size()));
          
          if (bannerCounter == 0) {
            uniqueBanners.add(bannerItem.getUnique());
            bannerCounter++;
          }
          else if (!uniqueBanners.contains(bannerItem.getUnique())) {
            uniqueBanners.add(bannerItem.getUnique());
            bannerCounter++;
          }
        } while (
        
          uniqueBanners.size() < numberOfBanners);
      } else if (bannerList.size() > 0) {
        int bannerCounter = 0;
        do
        {
          bannerItem = (BannerAd)bannerList.get((int)Math.floor(Math.random() * 
            bannerList.size()));
          
          if (bannerCounter == 0) {
            uniqueBanners.add(bannerItem.getUnique());
            bannerCounter++;
          }
          else if (!uniqueBanners.contains(bannerItem.getUnique())) {
            uniqueBanners.add(bannerItem.getUnique());
            bannerCounter++;
          }
        } while (
           uniqueBanners.size() < bannerList.size());
      }
      


      trans.commit();
    }
    catch (Exception localException) {}
    finally {
      session.close();
    }
    
    return (String[])uniqueBanners.toArray(new String[uniqueBanners.size()]);
  }
  
  public String[][] getVideosByCategory(String categoryUnique, int index, String domain, int numberOfElements, String lang) {
    String[][] videos = null;
    Session session = null;
    int totalVideosInCategory = getTotalNumberOfVideosInCategory(categoryUnique);
    if (totalVideosInCategory > 0) {
      try
      {
        session = openSession();
        Transaction trans = session.beginTransaction();
        
        String sqlstr = "select distinct vc.aUnique as videoUnique from videoClips vc where vc.aCategory like '%" + categoryUnique + "%' and vc.aUnique <>'' order by rand() limit " + index + "," + numberOfElements;
        System.out.println(domain+" GETVideosByCategory video category query " + sqlstr);
        Query query = null;
        query = session.createSQLQuery(sqlstr).addScalar("videoUnique");
        List videoUniqueList = query.list();
        if (!videoUniqueList.isEmpty()) {
          videos = new String[videoUniqueList.size()][];
          int counter = 0;
          for (Object o : videoUniqueList)
          {
           videoDirectory="";
            query = null;
            String[] props = new String[7];
            VideoClip item = null;
            sqlstr = "";
            String img = "";
            int iw = 0;
            int ih = 0;
            String gtitle = "";
            int priceGroup = 0;
            


            item = videoclipdao.getItem(o.toString());
            
            props[0] = o.toString();
            


            if (item == null) {
              session.close();
              return null;
            }
            
            ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
            if (itemresource != null) {
              if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
              videoDirectory += itemresource.getDataFile();
              System.out.println("getvideos debug video location " + videoDirectory);
              System.out.println("getvideos debug itemResource location  " + itemresource.getDataFile());
              
              IContainer container = IContainer.make();
              
              int result = container.open(videoDirectory, IContainer.Type.READ, null);
              if (result < 0) {
                System.out.println("getvideos Exception Failed to open media file ParevaVideoList line no. 926 for domain " + domain + " categoryUnique- " + categoryUnique);
              }
              
              long duration = container.getDuration() / 1000L;
              int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
              duration += videodurationrandom;
              String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
              props[1] = videoduration;
              
              videoDirectory = "";
            }
            
            int ratingNumber = 80 + (int)(Math.random() * 21.0D);
            int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
            gtitle = item.getDescription("title", lang);
            
            props[2] = String.valueOf(ratingNumber);
            props[3] = String.valueOf(viewsNumber);
            props[4] = gtitle;
            



            String price = 
              (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
            ItemImage image = item.getItemImage("webthumb", 0, 4);
            
            if (image == null)
            {
              image = item.getItemImage("mobthumb", 0, 4);
            }
            if (image != null) {
              img = image.getPath();
              iw = image.getWidth();
              ih = image.getHeight();
              props[5] = img;
            }
            


            props[6] = String.valueOf(totalVideosInCategory);
            

            videos[(counter++)] = props;
          }
        }
        trans.commit();

      }
      catch (Exception localException) {}finally
      {
        session.close();
      }
    }
    

    return videos;
  }
  
  public int getTotalNumberOfVideosInCategory(String categoryUnique) {
    int totalVideos = 0;
    String sqlstr = "select count(vc.aUnique) as totalVideos from videoClips vc where vc.aCategory like '%" + categoryUnique + "%' and vc.aUnique <>''";
    System.out.println("Total Number Of Videos in Category Query " + sqlstr);
    Session session = null;
    try
    {
      session = openSession();
      Transaction trans = session.beginTransaction();
      Query query = session.createSQLQuery(sqlstr).addScalar("totalVideos", StandardBasicTypes.INTEGER);
      List count = query.list();
      
      if (!count.isEmpty()) {
        for (Object o : count) {
          totalVideos = Integer.parseInt(o.toString());
        }
      }
      trans.commit();
    }
    catch (Exception localException) {}
    finally {
      session.close();
    }
    return totalVideos;
  }
}