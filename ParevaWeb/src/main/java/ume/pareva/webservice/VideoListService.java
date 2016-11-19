package ume.pareva.webservice;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import ume.pareva.cms.BannerAd;

@WebService
public interface VideoListService {

	@WebMethod
	public int getNumberOfVideosToDisplay(@WebParam(name = "videoCategoryServiceUnique") String videoCategoryServiceUnique, @WebParam(name = "domain") String domain);
	
	@WebMethod
	public int getTotalNumberOfVideos(@WebParam(name = "videoCategoryServiceUnique") String videoCategoryServiceUnique, @WebParam(name = "domain") String domain);
	
	@WebMethod
	public String[][] getVideoCategories(@WebParam(name = "videoCategoryServiceUnique") String videoCategoryServiceUnique, @WebParam(name = "domain") String domain);
	
	@WebMethod
	public String[][] getVideos(@WebParam(name = "videoCategoryServiceUnique") String videoCategoryServiceUnique,
						@WebParam(name = "index") int index, @WebParam(name = "domain") String domain,
						@WebParam(name = "numberOfElements") int numberOfElements, @WebParam(name = "lang") String lang);
	
	@WebMethod
	public String[][] getVideosExcludingProvidedVideoUnique(@WebParam(name = "videoCategoryServiceUnique") String videoCategoryServiceUnique,@WebParam(name = "videoUnique") String videoUnique,
						@WebParam(name = "index") int index, @WebParam(name = "domain") String domain,
						@WebParam(name = "numberOfElements") int numberOfElements, @WebParam(name = "lang") String lang);
	
	
	@WebMethod
	public String[][] getVideosBySortType(@WebParam(name = "videoCategoryServiceUnique")String videoCategoryServiceUnique, @WebParam(name = "sortType") String sortType,
											@WebParam(name = "index") int index, @WebParam(name = "domain") String domain,
											@WebParam(name = "numberOfElements") int numberOfElements, @WebParam(name = "lang") String lang);

	@WebMethod
	public String[][] searchVideo(@WebParam(name = "videoCategoryServiceUnique")String videoCategoryServiceUnique, @WebParam(name = "searchString") String searchString,
											@WebParam(name = "index") int index, @WebParam(name = "domain") String domain,
											@WebParam(name = "numberOfElements") int numberOfElements, @WebParam(name = "lang") String lang);

	@WebMethod
	public String[] getBanner(@WebParam(name = "bannerSrvc") String bannerSrvc, @WebParam(name = "domain")String domain,
								@WebParam(name = "classification")String classification,@WebParam(name = "region")String region);
	
	@WebMethod
	public String[][] getVideosByCategory(@WebParam(name = "categoryUnique") String categoryUnique,
						@WebParam(name = "index") int index, @WebParam(name = "domain") String domain,
						@WebParam(name = "numberOfElements") int numberOfElements, @WebParam(name = "lang") String lang);
	
	@WebMethod
	public int getTotalNumberOfVideosInCategory(@WebParam(name="categoryUnique") String categoryUnique);
	
}
