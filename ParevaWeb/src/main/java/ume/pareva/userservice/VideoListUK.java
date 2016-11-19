package ume.pareva.userservice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;

import ume.pareva.cms.BannerAdDao;
import ume.pareva.cms.ContentSet;
import ume.pareva.cms.ItemImage;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.sdk.Handset;

public class VideoListUK {
	
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private VideoClipDao videoclipdao;

	@Autowired
	BannerAdDao banneraddao;

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

	public HashMap getVideoCategory(java.util.List hotVideo, String domain,
			Handset handset) {

		String category = "";
		int number_of_elements = 0;
		java.util.List list = new ArrayList();
		java.util.List categoryList = new ArrayList();
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		for (int i = 0; i < hotVideo.size(); i++) {

			String srvc = (String) hotVideo.get(i);
			System.out.println("Service Name = " + srvc);

			String sqlstr = "";

			sqlstr = "select aCategoryUnique,numberOfElements from clientServices where aUnique='"
					+ srvc + "' and aDomain='" + domain + "'";
			Query query = null;
			query = session.createSQLQuery(sqlstr).addScalar("aCategoryUnique")
					.addScalar("numberOfElements");
			java.util.List fromCategory = query.list();
			if (!fromCategory.isEmpty()) {

				for (Object o : fromCategory) {
					Object[] row = (Object[]) o;

					category = String.valueOf(row[0]);
					number_of_elements = Integer.parseInt(String
							.valueOf(row[1]));

				}

			}

			sqlstr = "select distinct(vc.aUnique) as aUnique from videoClips vc, itemCategories ic, selectedCategories sc where ic.aUnique in"
					+ "(select aCategory from selectedCategories where aServiceUnique='"
					+ category
					+ "' and aDomainUnique='"
					+ domain
					+ "')"
					+ "and vc.aCategory=ic.aUnique and sc.aServiceUnique='"
					+ category + "' and sc.aDomainUnique='" + domain + "'";

			query = session.createSQLQuery(sqlstr).addScalar("aUnique");
			java.util.List result = query.list();
			if (!result.isEmpty()) {

				for (Object o : result) {

					list.add(o.toString());
				}

			}

			// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			sqlstr = "select vc.aUnique as aUnique, ic.aName1 as aCategory, ic.aUnique as aCategoryUnique from videoClips vc, itemCategories ic, selectedCategories sc where ic.aUnique in "
					+ "(select aCategory from selectedCategories where aServiceUnique='"
					+ category
					+ "' and aDomainUnique='"
					+ domain
					+ "') and "
					+ "vc.aCategory=ic.aUnique and sc.aServiceUnique='"
					+ category
					+ "' and sc.aDomainUnique='"
					+ domain
					+ "' group by ic.aUnique";
			query = session.createSQLQuery(sqlstr).addScalar("aUnique")
					.addScalar("aCategory").addScalar("aCategoryUnique");
			java.util.List res = query.list();
			if (!result.isEmpty()) {

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
						System.out.println("VIDEO NOT FOUND");
						session.close();
						return null;
					}

					ItemImage image = item.getItemImage("mobthumb", 0,
							handset.getImageProfile());
                                        
                                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
					if (image != null) {
						img = image.getPath();
						iw = image.getWidth();
						ih = image.getHeight();
						categoryMap.put("img", img);
						categoryMap.put("unique", props[2]);
						categoryMap.put("category", props[1]);
						categoryMap.put("numberOfElements", number_of_elements);

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
		hotVideoParameter.put("number_of_elements", number_of_elements);
		hotVideoParameter.put("category", category);
		return hotVideoParameter;

	}

	public java.util.List getVideos(int index, java.util.List list,
			String domain, Handset handset, int number_of_elements, String lang,String pfitagHeaders,String Refrence_id,UmeDomain dmn,
			String imiMsisdn,String imiAddCode_,String imi_price) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		int counter = 0;
		java.util.List videos = new ArrayList();

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
			//int rand = (int) java.lang.Math.floor(java.lang.Math.random()
			//		* list.size());
			//String videoUnique=list.get(rand).toString();

			
			String videoUnique=list.get(j).toString();

			
			item = videoclipdao.getItem(videoUnique);
			videoMap.put("videoUnique", videoUnique);
			videoMap.put("categoryUnique",item.getCategory());
			videoMap.put("numberOfElements", number_of_elements);
			videoMap.put("index",index);

			if (item == null) {
				System.out.println("VIDEO NOT FOUND");
				session.close();
				return null;
			}

			sqlstr = "SELECT count(*) as count" + " FROM itemLog "
					+ " WHERE aItemUnique='" + item.getUnique() + "'";
			int count = 0;
			query = session.createSQLQuery(sqlstr).addScalar("count");
			java.util.List countList = query.list();

			for (Object o1 : countList) {
				String row1 = o1.toString();
				count = Integer.parseInt(String.valueOf(row1));
				System.out.println("COUNTING BG : " + count);
			}

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
			int viewsNumber = 65000 + count;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("mobthumb", 0,
					handset.getImageProfile());
                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

			}
			
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			String data_serviceid ="196"; 
		    String data_content_id = item.getUnique();
		    String data_reference = Refrence_id;
		    String data_content_location = "http://"+dmn.getDefaultUrl()
		    //  + "/videodetail.jsp?unq=" + props[0]+"&srvc=" + srvc+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_;
		    + "/videodetail.jsp?unq=" + videoUnique+"&srvc=&cat="+item.getCategory()+"&noe="+number_of_elements+"&data-reference="+data_reference+"&cntid=" + data_content_id+"&msisdn=" + imiMsisdn+"&s=" + imiAddCode_;
		//    String thumbnail_path = topthumbnail_path;
		    //String thumbnail_path="http://"+dmn.getContentUrl()+img;
		    //String content_price="<div id=\""+data_content_id+"\"><div class=\"priceInfo\"><font size=\"2\" color=\"white\">click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";
		    String content_price="<thumbclickable><a class =\"billable block video-link\" id=\"videoId_"+j+"\" href=\"javascript:void(0)\" data-clickable=\"true\"></a> </thumbclickable><div id=\""+data_content_id+"\"> <div class=\"priceInfo\" style=\"display: none;\"><font size=\"2\" color=\"white\">Click to buy and <b>&nbsp;<span class=\"content-price\">"+imi_price+"</span>&nbsp;</b> will be added to this mobile's bill</font></div></div>";

//		    DataDefination datadefination = new DataDefination(
//		      classtype.thumbnail, data_serviceid, data_reference,
//		      data_content_location, data_content_id, thumbnail_path,
//		      content_price);
		//    
//		    datadefination.setImageCss("thumbnail videoCover");
		//
		StringBuffer values=new StringBuffer();


		String space=" ", type="thumbnail";
		 values.append("<img src=\" http://"+dmn.getContentUrl()+img+"\"  alt=\""+"stream18 videos"+"\" />");
		   // values.append("<img src=\""+thumbnail_path+"\"  alt=\""+"stream18 videos"+"\" />");
		    values.append("<div class=\""+type+"\""+space);
		    values.append("data-serviceid=\""+data_serviceid+"\""+space );
		    values.append("data-content-location=\""+data_content_location+"\""+space );
		    values.append("data-reference=\""+data_reference+"\""+space );
		    values.append("data-content-id=\""+data_content_id+"\">"+space );    
		    //values.append("</div>");
		    
		    videoMap.put("imi",values.toString());
			
			
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			counter = counter + 1;
			System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == number_of_elements)
				break;

		}
		trans.commit();
		session.close();
		return videos;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public HashMap getVideosByCategory(int index, String categoryUnique,String videoUnique,
			String domain, Handset handset, int number_of_elements, String lang,String pfitagHeaders,String Refrence_id,UmeDomain dmn,
			String imiMsisdn,String imiAddCode_,String imi_price) {
		
		Session session = openSession();
		Transaction trans = session.beginTransaction();
		
		String sqlstr="select vc.aUnique as videoUnique from videoClips vc where vc.aCategory='"+categoryUnique+"' and vc.aUnique <>'"+videoUnique+"'"; 
		System.out.println("video category query "+sqlstr);			
		Query query = null;
		query = session.createSQLQuery(sqlstr).addScalar("videoUnique");
		java.util.List list = query.list();
		java.util.List videoList=getVideos(index,list,domain,handset,number_of_elements,lang,pfitagHeaders,Refrence_id,dmn,imiMsisdn,imiAddCode_,imi_price);
		HashMap videoByCategory=new HashMap();
		videoByCategory.put("listSize", list.size());
		videoByCategory.put("videos", videoList);
		
		return videoByCategory;
		
	}

	
	
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	public java.util.List getBanner(java.util.List bannerSrvc, String domain,
			Handset handset) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		java.util.List uniqueBanners = new ArrayList();
		if (bannerSrvc.size() > 0) {
			String bannerType = "mobimage";

			int number_of_banners = 0;

			String sqlstr = "select numberOfElements from clientServices where aUnique='"
					+ bannerSrvc.get(0)
					+ "' and aDomain='"
					+ domain
					+ "' and aStatus='1'";
			System.out.println("Number of Banner Query = " + sqlstr);
			Query query = null;
			query = session.createSQLQuery(sqlstr)
					.addScalar("numberOfElements");
			java.util.List bannerNumbers = query.list();

			for (Object o1 : bannerNumbers) {
				String row1 = o1.toString();
				number_of_banners = Integer.parseInt(row1);
				System.out.println("number of banners : " + number_of_banners);
			}

			List<ume.pareva.cms.BannerAd> bannerList = banneraddao.getActiveBanners(domain, bannerType,4,"topless","UK");
			System.out.println("BAnnerList: " + bannerList);

			ume.pareva.cms.BannerAd bannerItem = null;

			if (bannerList.size() > number_of_banners) {
				int banner_counter = 0;
				do {

					bannerItem = bannerList.get((int) Math.floor(Math.random()
							* bannerList.size()));

					if (banner_counter == 0) {
						uniqueBanners.add(bannerItem);
						banner_counter++;
					} else {
						if (!uniqueBanners.contains(bannerItem)) {
							uniqueBanners.add(bannerItem);
							banner_counter++;
						}
					}

				} while (uniqueBanners.size() < number_of_banners);
			} else if (bannerList.size() > 0) {
				int banner_counter = 0;
				do {

					bannerItem = bannerList.get((int) Math.floor(Math.random()
							* bannerList.size()));

					if (banner_counter == 0) {
						uniqueBanners.add(bannerItem);
						banner_counter++;
					} else {
						if (!uniqueBanners.contains(bannerItem)) {
							uniqueBanners.add(bannerItem);
							banner_counter++;
						}
					}

				} while (uniqueBanners.size() < bannerList.size());

			}

		}
		trans.commit();
		session.close();
		return uniqueBanners;
	}

	public HashMap searchVideos(String search, String domain, Handset handset,
			Map<String, String> dParamMap, String lang, int number_of_elements,
			int index) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		int counter = 0;
		HashMap searchMap = new HashMap();
		java.util.List videos = new ArrayList();
		ContentSet cs = new ContentSet();

		cs.setDomain(domain);
		cs.setHandset(handset);

		cs.setSearchString(search);

		cs.setClassification("topless");
		// Don't remove this, this is for classification
		// if (dParamMap.get("classification")!=null)
		// cs.setClassification(dParamMap.get("classification"));

		videoclipdao.populate(cs);
		java.util.List searchList = cs.getList();

		for (int i = index; i < searchList.size(); i++) {
			HashMap<String, Object> videoMap = new HashMap<String, Object>();
			VideoClip item = null;

			String img = "";
			int iw = 0;
			int ih = 0;
			String gtitle = "";
			int priceGroup = 0;
			String sqlstr = "";
			Query query = null;
			item = (VideoClip) searchList.get(i);

			if (item == null) {
				System.out.println("VIDEO NOT FOUND");
				// session.close();
				return null;
			}

			sqlstr = "SELECT count(*) as count" + " FROM itemLog "
					+ " WHERE aItemUnique='" + item.getUnique() + "'";
			int count = 0;
			query = session.createSQLQuery(sqlstr).addScalar("count");
			java.util.List countList = query.list();

			for (Object o1 : countList) {
				String row1 = o1.toString();
				count = Integer.parseInt(String.valueOf(row1));
				System.out.println("COUNTING BG : " + count);
			}

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
			int viewsNumber = 65000 + count;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("mobthumb", 0,
					handset.getImageProfile());
                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

			}
			counter = counter + 1;
			System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == number_of_elements)
				break;

			System.out.println("Search Result Size : " + searchList.size());
		}
		searchMap.put("videos", videos);
		searchMap.put("searchList", searchList);
		trans.commit();
		session.close();
		java.util.List test = (java.util.List) searchMap.get("videos");
		System.out.println("size from class = " + test.size());
		return searchMap;
	}

	public HashMap getRecentVideos(Handset handset, String domain, int index,
			String lang, int number_of_elements) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		HashMap newVideoMap = new HashMap();
		java.util.List videos = new ArrayList();

		ContentSet cs = new ContentSet();
		cs.setSortString("new");
		cs.setHandset(handset);
		cs.setDomain(domain);
		// cs.setItemsPerPage(5);
		// cs.setNumberOfTopPages(1);

		cs.setClassification("topless");

		// if (dParamMap.get("classification")!=null){
		// cs.setClassification(dParamMap.get("classification"));
		// }

		videoclipdao.populate(cs);
		java.util.List newVideoList = cs.getList();
		VideoClip item = null;
		// ItemImage image = null;

		// System.out.println("List.size: " + list.size() + ", " +
		// cs.getPageCount());

		int counter = 0;
		for (int i = index; i < newVideoList.size(); i++) {
			HashMap<String, Object> videoMap = new HashMap<String, Object>();
			// VideoClip item = null;

			String img = "";
			int iw = 0;
			int ih = 0;
			String gtitle = "";
			int priceGroup = 0;
			String sqlstr = "";
			Query query = null;
			item = (VideoClip) newVideoList.get(i);

			if (item == null) {
				System.out.println("VIDEO NOT FOUND");
				// session.close();
				return null;
			}

			sqlstr = "SELECT count(*) as count" + " FROM itemLog "
					+ " WHERE aItemUnique='" + item.getUnique() + "'";
			int count = 0;
			query = session.createSQLQuery(sqlstr).addScalar("count");
			java.util.List countList = query.list();

			for (Object o1 : countList) {
				String row1 = o1.toString();
				count = Integer.parseInt(String.valueOf(row1));
				System.out.println("COUNTING BG : " + count);
			}

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
			int viewsNumber = 65000 + count;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("mobthumb", 0,
					handset.getImageProfile());
                        
                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

			}
			counter = counter + 1;
			System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == number_of_elements)
				break;

			System.out.println("Search Result Size : " + newVideoList.size());
		}
		newVideoMap.put("videos", videos);
		newVideoMap.put("newVideoList", newVideoList);
		trans.commit();
		session.close();
		// java.util.List test=(java.util.List)searchMap.get("videos");
		// System.out.println("size from class = "+ test.size());
		return newVideoMap;

	}

	public HashMap getTopVideos(Handset handset, String domain, int index,
			String lang, int number_of_elements) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		HashMap topVideoMap = new HashMap();
		java.util.List videos = new ArrayList();

		ContentSet cs = new ContentSet();
		cs.setSortString("top");
		cs.setHandset(handset);
		cs.setDomain(domain);
		// cs.setItemsPerPage(5);
		// cs.setNumberOfTopPages(1);

		cs.setClassification("topless");

		// if (dParamMap.get("classification")!=null){
		// cs.setClassification(dParamMap.get("classification"));
		// }

		videoclipdao.populate(cs);
		java.util.List topVideoList = cs.getList();
		VideoClip item = null;
		// ItemImage image = null;

		// System.out.println("List.size: " + list.size() + ", " +
		// cs.getPageCount());

		int counter = 0;
		for (int i = index; i < topVideoList.size(); i++) {
			HashMap<String, Object> videoMap = new HashMap<String, Object>();
			// VideoClip item = null;

			String img = "";
			int iw = 0;
			int ih = 0;
			String gtitle = "";
			int priceGroup = 0;
			String sqlstr = "";
			Query query = null;
			item = (VideoClip) topVideoList.get(i);

			if (item == null) {
				System.out.println("VIDEO NOT FOUND");
				// session.close();
				return null;
			}

			sqlstr = "SELECT count(*) as count" + " FROM itemLog "
					+ " WHERE aItemUnique='" + item.getUnique() + "'";
			int count = 0;
			query = session.createSQLQuery(sqlstr).addScalar("count");
			java.util.List countList = query.list();

			for (Object o1 : countList) {
				String row1 = o1.toString();
				count = Integer.parseInt(String.valueOf(row1));
				System.out.println("COUNTING BG : " + count);
			}

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
			int viewsNumber = 65000 + count;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("mobthumb", 0,
					handset.getImageProfile());
                        
                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

			}
			counter = counter + 1;
			System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == number_of_elements)
				break;

			System.out.println("Search Result Size : " + topVideoList.size());
		}
		topVideoMap.put("videos", videos);
		topVideoMap.put("topVideoList", topVideoList);
		trans.commit();
		session.close();
		// java.util.List test=(java.util.List)searchMap.get("videos");
		// System.out.println("size from class = "+ test.size());
		return topVideoMap;

	}

	public HashMap getPopularVideos(Handset handset, String domain, int index,
			String lang, int number_of_elements) {

		Session session = openSession();
		Transaction trans = session.beginTransaction();
		HashMap popularVideoMap = new HashMap();
		java.util.List videos = new ArrayList();

		ContentSet cs = new ContentSet();
		cs.setSortString("popular");
		cs.setHandset(handset);
		cs.setDomain(domain);
		// cs.setItemsPerPage(5);
		// cs.setNumberOfTopPages(1);

		cs.setClassification("topless");

		// if (dParamMap.get("classification")!=null){
		// cs.setClassification(dParamMap.get("classification"));
		// }

		videoclipdao.populate(cs);
		java.util.List popularVideoList = cs.getList();
		VideoClip item = null;
		// ItemImage image = null;

		// System.out.println("List.size: " + list.size() + ", " +
		// cs.getPageCount());

		int counter = 0;
		for (int i = index; i < popularVideoList.size(); i++) {
			HashMap<String, Object> videoMap = new HashMap<String, Object>();
			// VideoClip item = null;

			String img = "";
			int iw = 0;
			int ih = 0;
			String gtitle = "";
			int priceGroup = 0;
			String sqlstr = "";
			Query query = null;
			item = (VideoClip) popularVideoList.get(i);

			if (item == null) {
				System.out.println("VIDEO NOT FOUND");
				// session.close();
				return null;
			}

			sqlstr = "SELECT count(*) as count" + " FROM itemLog "
					+ " WHERE aItemUnique='" + item.getUnique() + "'";
			int count = 0;
			query = session.createSQLQuery(sqlstr).addScalar("count");
			java.util.List countList = query.list();

			for (Object o1 : countList) {
				String row1 = o1.toString();
				count = Integer.parseInt(String.valueOf(row1));
				System.out.println("COUNTING BG : " + count);
			}

			int ratingNumber = 80 + (int) (Math.random() * ((100 - 80) + 1));
			int viewsNumber = 65000 + count;

			videoMap.put("ratingNumber", ratingNumber);
			videoMap.put("viewsNumber", viewsNumber);

			gtitle = item.getDescription("title", lang);
			videoMap.put("gtitle", gtitle);
			String price = (String) UmeTempCmsCache.domainPriceGroups
					.get(domain + "_" + item.getPriceGroup());
			ItemImage image = item.getItemImage("mobthumb", 0,
					handset.getImageProfile());
                        
                            if(image==null)
                                        {
                                            image = item.getItemImage("webthumb", 0,
							handset.getImageProfile());
                                        }
			if (image != null) {
				img = image.getPath();
				iw = image.getWidth();
				ih = image.getHeight();
				videoMap.put("img", img);

			}
			counter = counter + 1;
			System.out.println("COUNTER = " + counter);
			videos.add(videoMap);
			if (counter == number_of_elements)
				break;

			System.out.println("Search Result Size : "
					+ popularVideoList.size());
		}
		popularVideoMap.put("videos", videos);
		popularVideoMap.put("popularVideoList", popularVideoList);
		trans.commit();
		session.close();
		// java.util.List test=(java.util.List)searchMap.get("videos");
		// System.out.println("size from class = "+ test.size());
		return popularVideoMap;

	}

}
