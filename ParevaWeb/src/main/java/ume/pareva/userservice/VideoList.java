package ume.pareva.userservice;

import com.xuggle.xuggler.IContainer;
import com.xuggle.xuggler.IContainer.Type;
import java.io.PrintStream;
import java.rmi.RemoteException;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ume.pareva.cms.BannerAd;
import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.ContentSet;
import ume.pareva.cms.ItemImage;
import ume.pareva.cms.ItemResource;
import ume.pareva.cms.ItemResourceDao;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;
import ume.pareva.webservice.client.VideoListServiceClient;


@Component("videolist")
public class VideoList
{
  @Autowired
  private SessionFactory sessionFactory;
  @Autowired
  private VideoClipDao videoclipdao;
  @Autowired
  BannerAdDao banneraddao;
  @Autowired
  ItemResourceDao itemresourcedao;
  @Autowired
  VideoListServiceClient videolistserviceclient;
  
  public VideoList() {}
  
  String videoDirectory = System.getProperty("contenturl");
  
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
  


  public HashMap getVideoCategory(List hotVideo, String domain, Handset handset)
  {
    String category = "";String classification = "";
    int number_of_elements = 0;
    List list = new ArrayList();
    List categoryList = new ArrayList();
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    
    String classificationquery = "SELECT classification from clientServices where aDomain='" + domain + "' AND classification<>'' LIMIT 1";
    System.out.println("getvideos  classificationquery " + classificationquery);
    Query qclassification = session.createSQLQuery(classificationquery).addScalar("classification");
    try {
      classification = qclassification.list().get(0).toString();
    } catch (Exception e) {
      classification = "";
    }
    for (int i = 0; i < hotVideo.size(); i++) {
      String srvc = (String)hotVideo.get(i);
      

      String sqlstr = "";
      
      sqlstr = "select aCategoryUnique,numberOfElements from clientServices where aUnique='"+ srvc + "' and aDomain='" + domain + "'";      

      System.out.println("getvideos  inside loop first query  " + sqlstr);
      Query query = null;
      query = session.createSQLQuery(sqlstr).addScalar("aCategoryUnique").addScalar("numberOfElements");
      List fromCategory = query.list();
       if (!fromCategory.isEmpty()) {
        for (Object o : fromCategory) {
        	Object[] row = (Object[])o;
          
          category = String.valueOf(row[0]);
          number_of_elements = Integer.parseInt(String.valueOf(row[1]));
        }
      }
     sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc WHERE ic.aUnique in(select aCategory from selectedCategories where aServiceUnique='" + srvc + "' and aDomainUnique='" + domain + "')";
    
      if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
        sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%')  AND vc.aClassification='" + classification + "'";
      }
      else {
        sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
      }
      
      sqlstr = sqlstr + "and sc.aServiceUnique='" + srvc + "' and sc.aDomainUnique='" + domain + "' order by rand()";
      
      System.out.println("getvideos  inside loop second loop query  " + sqlstr);
      query = session.createSQLQuery(sqlstr).addScalar("aUnique");
      List result = query.list();
      if (!result.isEmpty())
      {
        for (Object o : result)
        {
          list.add(o.toString());
        }
      }
      


      sqlstr = "select vc.aUnique as aUnique, ic.aName1 as aCategory, ic.aUnique as aCategoryUnique from videoClips vc, itemCategories ic, selectedCategories sc where ic.aUnique in (select aCategory from selectedCategories where aServiceUnique='" + srvc + "' and aDomainUnique='" + domain + "')";
      
      if ((classification != null) && (!classification.trim().equalsIgnoreCase(""))) {
        sqlstr = sqlstr + "AND vc.aCategory LIKE CONCAT('%',ic.aUnique,'%') AND vc.aClassification='" + classification + "'";
      }
      else {
        sqlstr = sqlstr + "and vc.aCategory=ic.aUnique ";
      }
      
      sqlstr = sqlstr + "and sc.aServiceUnique='" + srvc + "' and sc.aDomainUnique='" + domain + "' group by ic.aUnique";
      
      System.out.println("getvideos  inside loop third query  " + sqlstr);
      
      query = session.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aCategory").addScalar("aCategoryUnique");
      List res = query.list();
      if (!result.isEmpty())
      {
        for (Object o : res) {
          Object[] row = (Object[])o;
          String[] props = new String[3];
          props[0] = String.valueOf(row[0]);
          props[1] = String.valueOf(row[1]);
          props[2] = String.valueOf(row[2]);
          
          HashMap<String, Object> categoryMap = new HashMap();
          VideoClip item = null;
          
          String img = "";
          int iw = 0;
          int ih = 0;
          String gtitle = "";
          int priceGroup = 0;
          
          item = getVideoclipdao().getItem(props[0]);
          
          if (item == null)
          {
            session.close();
            return null;
          }
          
          ItemImage image = item.getItemImage("webthumb", 0, 4);
          
          if (image == null)
          {
            image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
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
    HashMap hotVideoParameter = new HashMap();
    hotVideoParameter.put("list", list);
    hotVideoParameter.put("categoryList", categoryList);
    hotVideoParameter.put("number_of_elements", Integer.valueOf(number_of_elements));
    hotVideoParameter.put("category", hotVideo.get(0));
    return hotVideoParameter;
  }
  



  public List getVideos(int index, List list, String domain, Handset handset, int number_of_elements, String lang)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    int counter = 0;
    List videos = new ArrayList();
    

    for (int j = index; j < list.size(); j++) {
      Query query = null;
      HashMap<String, Object> videoMap = new HashMap();
      VideoClip item = null;
      String sqlstr = "";
      String img = "";
      int iw = 0;
      int ih = 0;
      String gtitle = "";
      int priceGroup = 0;
      
      String[] props = null;
      int rand = (int)Math.floor(Math.random() * list.size());
      

      item = videoclipdao.getItem(list.get(j).toString());
      videoMap.put("videoUnique", list.get(j).toString());
      

      if (item == null)
      {
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
        videoMap.put("videolength", videoduration);
        videoDirectory = "";
      }
      
      int ratingNumber = 80 + (int)(Math.random() * 21.0D);
      
      int viewsNumber = (int)(Math.random() * 50001.0D) + 50000;
      
      videoMap.put("ratingNumber", Integer.valueOf(ratingNumber));
      videoMap.put("viewsNumber", Integer.valueOf(viewsNumber));
      
      gtitle = item.getDescription("title", lang);
      videoMap.put("gtitle", gtitle);
      String price = 
        (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
      ItemImage image = item.getItemImage("webthumb", 0, 4);
      
      if (image == null)
      {
        image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
      }
      if (image != null) {
        img = image.getPath();
        iw = image.getWidth();
        ih = image.getHeight();
        videoMap.put("img", img);
      }
      



      counter++;
      
      videos.add(videoMap);
      if (counter == number_of_elements) {
        break;
      }
    }
    
    trans.commit();
    session.close();
    return videos;
  }
  


  public boolean checkIfExist(List videos, String videoUnique)
  {
    boolean exist = false;
    for (int i = 0; i < videos.size(); i++) {
      HashMap videoMap = (HashMap)videos.get(i);
      if (videoMap.get("videoUnique").equals(videoUnique)) {
        exist = true;
        break;
      }
    }
    
    return exist;
  }
  
  public List getBanner(List bannerSrvc, UmeDomain domain, Handset handset)
  {
    return getBanner(bannerSrvc, domain, handset, "", "");
  }
  


  public List getBanner(List bannerSrvc, UmeDomain domain, Handset handset, String classification, String region)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    List uniqueBanners = new ArrayList();
    if (bannerSrvc.size() > 0) {
      String bannerType = "mobimage";
      
      int number_of_banners = 3;
      
      /*
      String sqlstr = "select numberOfElements from clientServices where aUnique='" + 
        bannerSrvc.get(0) + 
        "' and aDomain='" + 
        domain.getUnique() + 
        "' and aStatus='1'";
      System.out.println("Number of Banner Query = " + sqlstr);
      Query query = null;
      query = session.createSQLQuery(sqlstr)
        .addScalar("numberOfElements");
      List bannerNumbers = query.list();
      
      for (Object o1 : bannerNumbers) {
        String row1 = o1.toString();
        number_of_banners = Integer.parseInt(row1);
      }
      */


      List<BannerAd> bannerList = banneraddao.getActiveBanners(domain.getUnique(), bannerType, handset.getXhtmlProfile(), classification, region);
      

      BannerAd bannerItem = null;
      


      if (bannerList.size() > number_of_banners) {
        int banner_counter = 0;
        do
        {
          bannerItem = (BannerAd)bannerList.get((int)Math.floor(Math.random() * 
            bannerList.size()));
          
          if (banner_counter == 0) {
            uniqueBanners.add(bannerItem);
            banner_counter++;
          }
          else if (!uniqueBanners.contains(bannerItem)) {
            uniqueBanners.add(bannerItem);
            banner_counter++;
          }
        } while (
                  uniqueBanners.size() < number_of_banners);
      } else if (bannerList.size() > 0) {
        int banner_counter = 0;
        do
        {
          bannerItem = (BannerAd)bannerList.get((int)Math.floor(Math.random() * 
            bannerList.size()));
          
          if (banner_counter == 0) {
            uniqueBanners.add(bannerItem);
            banner_counter++;
          }
          else if (!uniqueBanners.contains(bannerItem)) {
            uniqueBanners.add(bannerItem);
            banner_counter++;
          }
        } while (
            uniqueBanners.size() < bannerList.size());
      }
    }
    

    trans.commit();
    session.close();
    return uniqueBanners;
  }
  


  public HashMap searchVideos(String search, String domain, Handset handset, Map<String, String> dParamMap, String lang, int number_of_elements, int index)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    int counter = 0;
    HashMap searchMap = new HashMap();
    List videos = new ArrayList();
    ContentSet cs = new ContentSet();
    String csClassification="topless";
    
    cs.setDomain(domain);
    cs.setHandset(handset);
    
    cs.setSearchString(search);
    MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
    if(club!=null){
        csClassification=club.getClassification();
    }
    
    cs.setClassification(csClassification);
    videoclipdao.populate(cs);
    List searchList = cs.getList();
    
    for (int i = index; i < searchList.size(); i++) {
      HashMap<String, Object> videoMap = new HashMap();
      VideoClip item = null;
      
      String img = "";
      int iw = 0;
      int ih = 0;
      String gtitle = "";
      int priceGroup = 0;
      String sqlstr = "";
      Query query = null;
      item = (VideoClip)searchList.get(i);
      
      if (item == null) {
        System.out.println("VIDEO NOT FOUND line no. 379 VideoList.java");
        
        return null;
      }
      videoMap.put("videoUnique", item.getUnique());
    
      /*
      sqlstr = "SELECT count(*) as count FROM itemLog  WHERE aItemUnique='" + 
        item.getUnique() + "'";
      int count = 0;
      query = session.createSQLQuery(sqlstr).addScalar("count");
      List countList = query.list();
      
      for (Object o1 : countList) {
        String row1 = o1.toString();
        count = Integer.parseInt(String.valueOf(row1));
      }
      */

      int ratingNumber = 80 + (int)(Math.random() * 21.0D);
      int viewsNumber = 65000 + ratingNumber;
      
      videoMap.put("ratingNumber", Integer.valueOf(ratingNumber));
      videoMap.put("viewsNumber", Integer.valueOf(viewsNumber));
      
      gtitle = item.getDescription("title", lang);
      videoMap.put("gtitle", gtitle);
      String price = (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
      ItemImage image = item.getItemImage("webthumb", 0, 4);
      
      if (image == null)
      {
        image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
      }
      if (image != null) {
        img = image.getPath();
        iw = image.getWidth();
        ih = image.getHeight();
        videoMap.put("img", img);
      }
      
      counter++;
      
      videos.add(videoMap);
      if (counter == number_of_elements) {
        break;
      }
    }
    
    searchMap.put("videos", videos);
    searchMap.put("searchList", searchList);
    trans.commit();
    session.close();
    List test = (List)searchMap.get("videos");
    
    return searchMap;
  }
  

  public HashMap getRecentVideos(Handset handset, String domain, int index, String lang, int number_of_elements)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    HashMap newVideoMap = new HashMap();
    List videos = new ArrayList();
    String csClassification="topless";
    ContentSet cs = new ContentSet();
    cs.setSortString("new");
    cs.setHandset(handset);
    cs.setDomain(domain);
    
    MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
    if(club!=null){
        csClassification=club.getClassification();
    }
    
    cs.setClassification(csClassification);

    videoclipdao.populate(cs);
    List newVideoList = cs.getList();
    VideoClip item = null;
  
    int counter = 0;
    for (int i = index; i < newVideoList.size(); i++) {
      HashMap<String, Object> videoMap = new HashMap();
      

      String img = "";
      int iw = 0;
      int ih = 0;
      String gtitle = "";
      int priceGroup = 0;
      String sqlstr = "";
      Query query = null;
      
      int rand = (int)Math.floor(Math.random() * newVideoList.size());
      item = (VideoClip)newVideoList.get(rand);
      
      if (item == null) {
        System.out.println("VIDEO NOT FOUND line no. 476 VideoList.java");
        
        return null;
      }
      
      ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
      if (itemresource != null) {
        if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
        videoDirectory += itemresource.getDataFile();
        System.out.println("getvideos debug recent video location " + videoDirectory);
        System.out.println("getvideos debug recent itemResource location  " + itemresource.getDataFile());
        
        IContainer container = IContainer.make();
        
        int result = container.open(videoDirectory, IContainer.Type.READ, null);
        if (result < 0) {
          System.out.println("getvideos Exception Failed to open media file");
        }
        
        long duration = container.getDuration() / 1000L;
        int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
        duration += videodurationrandom;
        String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
        videoMap.put("videolength", videoduration);
        videoDirectory = "";
      }
    
      videoMap.put("videoUnique", item.getUnique());
      
      /*
      sqlstr = "SELECT count(*) as count FROM itemLog  WHERE aItemUnique='" + item.getUnique() + "'";
      int count = 0;
      query = session.createSQLQuery(sqlstr).addScalar("count");
      List countList = query.list();
      
      for (Object o1 : countList) {
        String row1 = o1.toString();
        count = Integer.parseInt(String.valueOf(row1));
      }
      */

      int ratingNumber = 80 + (int)(Math.random() * 21.0D);
      int viewsNumber = 65000 + ratingNumber;
      
      videoMap.put("ratingNumber", Integer.valueOf(ratingNumber));
      videoMap.put("viewsNumber", Integer.valueOf(viewsNumber));
      
      gtitle = item.getDescription("title", lang);
      videoMap.put("gtitle", gtitle);
      
      String price = (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
      ItemImage image = item.getItemImage("webthumb", 0, 4);
      
      if (image == null)
      {
        image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
      }
      if (image != null) {
        img = image.getPath();
        iw = image.getWidth();
        ih = image.getHeight();
        videoMap.put("img", img);
      }
      
      counter++;
      
      videos.add(videoMap);
      if (counter == number_of_elements) {
        break;
      }
    }
    
    newVideoMap.put("videos", videos);
    newVideoMap.put("newVideoList", newVideoList);
    trans.commit();
    session.close();
    

    return newVideoMap;
  }
  


  public HashMap getTopVideos(Handset handset, String domain, int index, String lang, int number_of_elements)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    HashMap topVideoMap = new HashMap();
    List videos = new ArrayList();
    
    String csClassification="topless";
    ContentSet cs = new ContentSet();
    cs.setSortString("top");
    cs.setHandset(handset);
    cs.setDomain(domain);
    

    MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
    if(club!=null){
        csClassification=club.getClassification();
    }
    
    cs.setClassification(csClassification);

    videoclipdao.populate(cs);
    List topVideoList = cs.getList();
    VideoClip item = null;
  
    int counter = 0;
    for (int i = index; i < topVideoList.size(); i++) {
      HashMap<String, Object> videoMap = new HashMap();
      

      String img = "";
      int iw = 0;
      int ih = 0;
      String gtitle = "";
      int priceGroup = 0;
      String sqlstr = "";
      Query query = null;
      
      int rand = (int)Math.floor(Math.random() * topVideoList.size());
      item = (VideoClip)topVideoList.get(rand);
      
      if (item == null) {
        System.out.println("VIDEO NOT FOUND line no. 574 videoList.java");
        
        return null;
      }
      videoMap.put("videoUnique", item.getUnique());
      
      /*
      sqlstr = "SELECT count(*) as count FROM itemLog  WHERE aItemUnique='" + 
        item.getUnique() + "'";
      int count = 0;
      query = session.createSQLQuery(sqlstr).addScalar("count");
      List countList = query.list();
      
      for (Object o1 : countList) {
        String row1 = o1.toString();
        count = Integer.parseInt(String.valueOf(row1));
      }
      
              */
      
      ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
      if (itemresource != null) {
        if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
        videoDirectory += itemresource.getDataFile();
        System.out.println("getvideos debug top video location " + videoDirectory);
        System.out.println("getvideos debug top itemResource location  " + itemresource.getDataFile());
        
        IContainer container = IContainer.make();
        
        int result = container.open(videoDirectory, IContainer.Type.READ, null);
        if (result < 0) {
          System.out.println("getvideos Exception Failed to open media file");
        }
        
        long duration = container.getDuration() / 1000L;
        int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
        duration += videodurationrandom;
        String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
        videoMap.put("videolength", videoduration);
        videoDirectory = "";
      }
      
      int ratingNumber = 80 + (int)(Math.random() * 21.0D);
      int viewsNumber = 65000 + ratingNumber;
      
      videoMap.put("ratingNumber", Integer.valueOf(ratingNumber));
      videoMap.put("viewsNumber", Integer.valueOf(viewsNumber));
      
      gtitle = item.getDescription("title", lang);
      videoMap.put("gtitle", gtitle);
      String price = 
        (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
      ItemImage image = item.getItemImage("webthumb", 0, 4);
      
      if (image == null)
      {
        image = item.getItemImage("mobthumb", 0, handset.getImageProfile());
      }
      if (image != null) {
        img = image.getPath();
        iw = image.getWidth();
        ih = image.getHeight();
        videoMap.put("img", img);
      }
      
      counter++;
      
      videos.add(videoMap);
      if (counter == number_of_elements) {
        break;
      }
    }
    
    topVideoMap.put("videos", videos);
    topVideoMap.put("topVideoList", topVideoList);
    trans.commit();
    session.close();
    

    return topVideoMap;
  }
  


  public HashMap getPopularVideos(Handset handset, String domain, int index, String lang, int number_of_elements)
  {
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    HashMap popularVideoMap = new HashMap();
    List videos = new ArrayList();
    
    String csClassification="topless";
    ContentSet cs = new ContentSet();
    cs.setSortString("popular");
    cs.setHandset(handset);
    cs.setDomain(domain);
    
MobileClub club = UmeTempCmsCache.mobileClubMap.get(domain);
    if(club!=null){
        csClassification=club.getClassification();
    }
    
    cs.setClassification(csClassification);

    videoclipdao.populate(cs);
    List popularVideoList = cs.getList();
    VideoClip item = null;
    

    int counter = 0;
    for (int i = index; i < popularVideoList.size(); i++) {
      HashMap<String, Object> videoMap = new HashMap();
      

      String img = "";
      int iw = 0;
      int ih = 0;
      String gtitle = "";
      int priceGroup = 0;
      String sqlstr = "";
      Query query = null;
      
      int rand = (int)Math.floor(Math.random() * popularVideoList.size());
      item = (VideoClip)popularVideoList.get(rand);
      
      if (item == null) {
        System.out.println("VIDEO NOT FOUND line no. 672 videoList.java");
        
        return null;
      }
      
      ItemResource itemresource = itemresourcedao.getResource(item.getUnique());
      if (itemresource != null) {
        if ((videoDirectory == null) || ("".equalsIgnoreCase(videoDirectory))) videoDirectory = "/mnt/content";
        videoDirectory += itemresource.getDataFile();
        System.out.println("getvideos debug popular video location " + videoDirectory);
        System.out.println("getvideos debug popular itemResource location  " + itemresource.getDataFile());
        
        IContainer container = IContainer.make();
        
        int result = container.open(videoDirectory, IContainer.Type.READ, null);
        if (result < 0) {
          System.out.println("getvideos popular Exception Failed to open media file");
        }
        
        long duration = container.getDuration() / 1000L;
        int videodurationrandom = 60000 + (int)(Math.random() * 68000.0D);
        duration += videodurationrandom;
        String videoduration = new SimpleDateFormat("MM:ss").format(new Date(duration));
        videoMap.put("videolength", videoduration);
        videoDirectory = "";
      }
      videoMap.put("videoUnique", item.getUnique());
      
      /*
      sqlstr = "SELECT count(*) as count FROM itemLog  WHERE aItemUnique='" + 
        item.getUnique() + "'";
      int count = 0;
      query = session.createSQLQuery(sqlstr).addScalar("count");
      List countList = query.list();
      
      for (Object o1 : countList) {
        String row1 = o1.toString();
        count = Integer.parseInt(String.valueOf(row1));
        System.out.println("COUNTING BG : " + count);
      }
      */
      
      int ratingNumber = 80 + (int)(Math.random() * 21.0D);
      int viewsNumber = 65000 + ratingNumber;
      
      videoMap.put("ratingNumber", Integer.valueOf(ratingNumber));
      videoMap.put("viewsNumber", Integer.valueOf(viewsNumber));
      
      gtitle = item.getDescription("title", lang);
      videoMap.put("gtitle", gtitle);
      String price = 
        (String)UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());
      ItemImage image = item.getItemImage("webthumb", 0, 4);
      
      if (image == null)
      {
        image = item.getItemImage("mobthumb", 0, 
          handset.getImageProfile());
      }
      if (image != null) {
        img = image.getPath();
        iw = image.getWidth();
        ih = image.getHeight();
        videoMap.put("img", img);
      }
      
      counter++;
      System.out.println("COUNTER = " + counter);
      videos.add(videoMap);
      if (counter == number_of_elements) {
        break;
      }
      System.out.println("Search Result Size : " + 
        popularVideoList.size());
    }
    popularVideoMap.put("videos", videos);
    popularVideoMap.put("popularVideoList", popularVideoList);
    trans.commit();
    session.close();
    

    return popularVideoMap;
  }
  


  public List getHotVideo(List srvcList, String domain, Handset handset, String lang)
  {
    String srvc = (String)srvcList.get(0);
    VideoClip item = null;
    String img = "";
    int iw = 0;
    int ih = 0;
    String gtitle = "";
    int priceGroup = 0;
    String sqlstr = "";
    Query query = null;
    List hotVideoList = new ArrayList();
    List hotVideoUniqueList = new ArrayList();
    

    List list = new ArrayList();
    String[] props = null;
    
    Session session = openSession();
    Transaction trans = session.beginTransaction();
    
    sqlstr = "SELECT aPromoUnique,clientPromoPages.aJavaUnique as aJavaUnique FROM clientPromoPages INNER JOIN clientContent ON clientPromoPages.aJavaUnique=clientContent.aItemUnique INNER JOIN videoClips vc ON vc.aUnique=clientPromoPages.aJavaUnique WHERE aServiceUnique='" + 
    

      srvc + "' AND clientPromoPages.aJavaUnique!='' AND clientPromoPages.aDomain='" + domain + "'" + 
      " AND aItemStatus='1' AND aClientUnique='" + (String)UmeTempCmsCache.clientDomains.get(domain) + "' AND vc.aStatus='1' ORDER BY aIndex";
    
    System.out.println("Hot Video Query: >" + sqlstr);
    
    query = session.createSQLQuery(sqlstr).addScalar("aPromoUnique").addScalar("aJavaUnique");
    List res = query.list();
    if (!res.isEmpty())
    {
      for (Object o : res) {
        Object[] row = (Object[])o;
        props = new String[2];
        props[0] = String.valueOf(row[0]);
        props[1] = String.valueOf(row[1]);
        list.add(props);
      }
    }
    
    if (list.size() > 0) {
      int rand = (int)Math.floor(Math.random() * list.size());
      props = (String[])list.get(rand);
      hotVideoUniqueList.add(props[0]);
      hotVideoList = getVideos(0, hotVideoList, domain, handset, 1, lang);
    }
    
    return hotVideoList;
  }
  
  public HashMap getVideoCategory(String videoCategoryServiceUnique, String domain, Handset handset)
  {
    HashMap hotVideoParameter = new HashMap();
    try {
      int numberOfElements = videolistserviceclient.getNumberOfVideosToDisplay(videoCategoryServiceUnique, domain);
      List<Map<String, String>> categoryList = videolistserviceclient.getVideoCategories(videoCategoryServiceUnique, domain);
      int totalNumberOfVideos = videolistserviceclient.getTotalNumberOfVideos(videoCategoryServiceUnique, domain);
      hotVideoParameter.put("numberOfElements", Integer.valueOf(numberOfElements));
      hotVideoParameter.put("categoryList", categoryList);
      hotVideoParameter.put("totalNumberOfVideos", Integer.valueOf(totalNumberOfVideos));
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return hotVideoParameter;
  }
  
  public List<Map<String, String>> getVideos(int index, String videoCategoryServiceUnique, String domain, Handset handset, int numberOfElements, String lang)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideos(videoCategoryServiceUnique, index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  
  public List<Map<String, String>> getVideos(int index, String videoCategoryServiceUnique,String videoUnique, String domain, Handset handset, int numberOfElements, String lang)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideos(videoCategoryServiceUnique, videoUnique, index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  
  public List<Map<String, String>> getPopularVideos(String videoCategoryServiceUnique, String domain, int index, String lang, int numberOfElements)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideosBySortType(videoCategoryServiceUnique, "popular", index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  

  public List<Map<String, String>> getTopVideos(String videoCategoryServiceUnique, String domain, int index, String lang, int numberOfElements)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideosBySortType(videoCategoryServiceUnique, "top", index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  

  public List<Map<String, String>> getRecentVideos(String videoCategoryServiceUnique, String domain, int index, String lang, int numberOfElements)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideosBySortType(videoCategoryServiceUnique, "new", index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  

  public List<Map<String, String>> searchVideos(String videoCategoryServiceUnique, String searchString, int index, String domain, int numberOfElements, String lang)
  {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.searchVideos(videoCategoryServiceUnique, searchString, index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
  
  public List<String> getBanner(String bannerSrvc, String domain, String classification, String region)
  {
    List<String> bannerUnique = new ArrayList();
    try {
      bannerUnique = videolistserviceclient.getBanner(bannerSrvc, domain, classification, region);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return bannerUnique;
  }
  
  public List<Map<String, String>> getVideosByCategory(String categoryUnique, int index, String domain, int numberOfElements, String lang) {
    List<Map<String, String>> videos = new ArrayList();
    try {
      videos = videolistserviceclient.getVideosByCategory(categoryUnique, index, domain, numberOfElements, lang);
    }
    catch (RemoteException e) {
      e.printStackTrace();
    }
    return videos;
  }
}