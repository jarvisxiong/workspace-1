package ume.pareva.webservice.client;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.springframework.stereotype.Service;

@Service("videolistserviceclient")
public class VideoListServiceClient {
	
	private static final Logger logger = LogManager.getLogger(VideoListServiceClient.class);

	VideoListService_PortType videoListServicePort;

	public VideoListServiceClient(){     
		System.out.println("VideoListService "+ " Calling VideoListService Constructor ");
		try {
			videoListServicePort = (new VideoListService_ServiceLocator()).getVideoListServiceImplPort();
		}catch (Exception e) { System.out.println("VideoListService Constructor Exception "+e); }

	}

	public List<Map<String,String>> getVideoCategories(String videoCategoryServiceUnique,String domain) throws RemoteException{
		List<Map<String,String>> categoryList=new ArrayList<Map<String,String>>();
		String[][] categories=videoListServicePort.getVideoCategories(videoCategoryServiceUnique, domain);
		if(categories!=null){
			for(int i=0;i<categories.length;i++){
				Map<String,String> categoryMap=new HashMap<String,String>();
				for(int j=0;j<3;j++){
					if(j==0)
						categoryMap.put("img",categories[i][j]);
					else if(j==1)
						categoryMap.put("category",categories[i][j]);
					else
						categoryMap.put("unique",categories[i][j]);
				}
				categoryList.add(categoryMap);
			}
		}
		return categoryList;
	}

	public int getNumberOfVideosToDisplay(String videoCategoryServiceUnique,String domain) throws RemoteException{
		return videoListServicePort.getNumberOfVideosToDisplay(videoCategoryServiceUnique, domain);
	}

	public List<Map<String,String>> getVideos(String videoCategoryServiceUnique,int index,String domain,int numberOfElements, String lang) throws RemoteException{
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		String[][] videos=videoListServicePort.getVideos(videoCategoryServiceUnique, index, domain, numberOfElements, lang);
		if(videos!=null){
			videoList=arrayToList(videos);
		}
		return videoList;

	}
	
	public List<Map<String,String>> getVideos(String videoCategoryServiceUnique,String videoUnique,int index,String domain,int numberOfElements, String lang) throws RemoteException{
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		String[][] videos=videoListServicePort.getVideosExcludingProvidedVideoUnique(videoCategoryServiceUnique, videoUnique, index, domain, numberOfElements, lang);
		if(videos!=null){
			videoList=arrayToList(videos);
		}
		return videoList;

	}


	public List<Map<String,String>> getVideosBySortType(String videoCategoryServiceUnique,String sortType,int index,String domain,int numberOfElements, String lang) throws RemoteException{
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		String[][] videos=videoListServicePort.getVideosBySortType(videoCategoryServiceUnique, sortType, index, domain, numberOfElements, lang);
		if(videos!=null){
			videoList=arrayToList(videos);
		}
		return videoList;

	}

	public int getTotalNumberOfVideos(String videoCategoryServiceUnique,String domain) throws RemoteException{
		return videoListServicePort.getTotalNumberOfVideos(videoCategoryServiceUnique, domain);
	}

	public List<Map<String,String>> searchVideos(String videoCategoryServiceUnique,String searchString,int index,String domain,int numberOfElements, String lang) throws RemoteException{
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		String[][] videos = videoListServicePort.searchVideo(videoCategoryServiceUnique, searchString, index, domain, numberOfElements, lang);
		if(videos!=null){
			videoList=arrayToList(videos);
		}
		return videoList;
	}


	public List<String> getBanner(String bannerSrvc,String domain, String classification, String region) throws RemoteException{
		List<String> bannerUniqueList=new ArrayList<String>();
		String[] bannerUniqueArray=videoListServicePort.getBanner(bannerSrvc, domain, classification, region);
		if(bannerUniqueArray!=null && bannerUniqueArray.length>0){
			for(int i=0;i<bannerUniqueArray.length;i++){
				bannerUniqueList.add(bannerUniqueArray[i]);
			}
		}
		return bannerUniqueList;
	}

	public List<Map<String,String>> arrayToList(String[][] videos){
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		if(videos.length>0 && videos!=null){
			for(int i=0;i<videos.length;i++){
				Map<String,String> videoMap=new HashMap<String,String>();
				for(int j=0;j<7;j++){
					if(j==0)
						videoMap.put("videoUnique",videos[i][j]);
					else if(j==1)
						videoMap.put("videolength",videos[i][j]);
					else if(j==2)
						videoMap.put("ratingNumber",videos[i][j]);
					else if(j==3)
						videoMap.put("viewsNumber",videos[i][j]);
					else if(j==4)
						videoMap.put("gtitle",videos[i][j]);
					else if(j==5)
						videoMap.put("img",videos[i][j]);
					else if(j==6)
						try{
							videoMap.put("searchListSize",videos[i][j]);
						}catch(Exception e){};
				}
				videoList.add(videoMap);
			}
		}
		return videoList;
	}
	
	public List<Map<String,String>> getVideosByCategory(String categoryUnique,int index,String domain,int numberOfElements, String lang) throws RemoteException{
		List<Map<String,String>> videoList=new ArrayList<Map<String,String>>();
		String[][] videos=videoListServicePort.getVideosByCategory(categoryUnique, index, domain, numberOfElements, lang);
		if(videos!=null){
			videoList=arrayToList(videos);
		}
		return videoList;

	}
	
	public int getTotalNumberOfVideosInCategory(String categoryUnique) throws RemoteException{
		return videoListServicePort.getTotalNumberOfVideosInCategory(categoryUnique);
	}
	
	

	public static void main(String[] arg){

		VideoListServiceClient videoListService=new VideoListServiceClient();
		//list.add("8942564098441KDS");
		//videoListService.getVideos(0,list, "7145556704541llun", 10, "");
		try {
			//logger.info("CATEGORIES");
			System.out.println("*******************************Categories Start****************************************************");
			List<Map<String,String>> categoryList=videoListService.getVideoCategories("8942564098441KDS", "430016812093171CDS");
			if(!categoryList.isEmpty()){
				for(Map<String,String> categoryMap:categoryList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Category Image: "+categoryMap.get("img"));
					System.out.println("Category Name: "+categoryMap.get("category"));
					System.out.println("Category Unique: "+categoryMap.get("unique"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}
			System.out.println("*******************************Categories End****************************************************");

			System.out.println("*******************************Number of Videos To Display Start****************************************************");
			System.out.println("Number Of Videos To Display in Site: "+videoListService.getNumberOfVideosToDisplay("8942564098441KDS", "430016812093171CDS"));
			System.out.println("*******************************Number of Videos To Display End****************************************************");

			System.out.println("*******************************Videos Start****************************************************");

			List<Map<String,String>> videoList=videoListService.getVideos("8942564098441KDS","1798561548041KDS", 10, "430016812093171CDS", 10, "");
			if(!videoList.isEmpty()){
				for(Map<String,String> videoMap:videoList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}
			System.out.println("*******************************Videos End****************************************************");

			System.out.println("*******************************Total Number of Videos Start****************************************************");
			System.out.println("Total Number of Videos in Category: "+videoListService.getTotalNumberOfVideos("8942564098441KDS", "430016812093171CDS"));
			System.out.println("*******************************Total Number of Videos End****************************************************");

			System.out.println("*******************************Recent Videos Start****************************************************");
			List<Map<String,String>> newVideoList=videoListService.getVideosBySortType("8942564098441KDS","new", 10, "430016812093171CDS", 10, "");
			if(!newVideoList.isEmpty()){
				for(Map<String,String> videoMap:newVideoList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}
			System.out.println("*******************************Recent Videos End****************************************************");

			System.out.println("*******************************Top Videos Start****************************************************");

			List<Map<String,String>> topVideoList=videoListService.getVideosBySortType("8942564098441KDS","top", 10, "430016812093171CDS", 10, "");
			if(!topVideoList.isEmpty()){
				for(Map<String,String> videoMap:topVideoList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}
			System.out.println("*******************************Top Videos End****************************************************");

			System.out.println("*******************************Popular Videos Start****************************************************");

			List<Map<String,String>> popularVideoList=videoListService.getVideosBySortType("8942564098441KDS","popular", 10, "430016812093171CDS", 10, "");
			if(!popularVideoList.isEmpty()){
				for(Map<String,String> videoMap:popularVideoList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}

			System.out.println("*******************************Popular Videos End****************************************************");


			System.out.println("*******************************Searched Videos Start****************************************************");

			List<Map<String,String>> searchedVideoList=videoListService.searchVideos("8942564098441KDS","Reaming", 0, "430016812093171CDS", 10, "");
			if(!searchedVideoList.isEmpty()){
				for(Map<String,String> videoMap:searchedVideoList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("Search Size: "+videoMap.get("searchListSize"));
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}else
				System.out.println("No Item Found");

			System.out.println("*******************************Searched Videos End****************************************************");
			System.out.println("*******************************Banner Start****************************************************");
			List<String> bannerUniqueList=videoListService.getBanner("6958577550931KDS", "430016812093171CDS", "topless", "ZA");
			for(String bannerUnique: bannerUniqueList){
				System.out.println("Banner Unique: "+bannerUnique);
			}

			System.out.println("*******************************Banner End****************************************************");
			
			
			System.out.println("*******************************Videos By Category Start****************************************************");

			List<Map<String,String>> videoByCategoryList=videoListService.getVideosByCategory("2622735688931KDS", 10, "430016812093171CDS", 10, "");
			if(!videoByCategoryList.isEmpty()){
				for(Map<String,String> videoMap:videoByCategoryList){
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
					System.out.println("Video Image: "+videoMap.get("img"));
					System.out.println("Video Unique: "+videoMap.get("videoUnique"));
					System.out.println("Video Length: "+videoMap.get("videolength"));
					System.out.println("Video Title: "+videoMap.get("gtitle"));
					System.out.println("Video Rating: "+videoMap.get("ratingNumber"));
					System.out.println("Video Views: "+videoMap.get("viewsNumber"));
					System.out.println("Video By Category Size: "+videoMap.get("searchListSize"));
					
					System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
				}
			}
			System.out.println("*******************************Videos By Category End****************************************************");
			
			System.out.println("*******************************Total Number of Videos in Category Start****************************************************");

			System.out.println("Total Number of Vidoes in Category: "+videoListService.getTotalNumberOfVideosInCategory("2622735688931KDS"));
			System.out.println("*******************************Total Number of Videos in Category End****************************************************");


		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
