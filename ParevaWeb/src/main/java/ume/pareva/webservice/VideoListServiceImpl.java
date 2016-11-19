package ume.pareva.webservice;


import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;

import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;
import org.springframework.web.context.support.WebApplicationContextUtils;


@WebService(endpointInterface="ume.pareva.webservice.VideoListService",
serviceName="VideoListService")

@Service("videolistservice")
public class VideoListServiceImpl extends SpringBeanAutowiringSupport implements VideoListService {
	
	@Resource
        private WebServiceContext context;

        public Object fetchBean(String beanId) {
        Object bean = null;
        try {
            ServletContext servletContext = (ServletContext) context.getMessageContext().get("javax.xml.ws.servlet.context");
            WebApplicationContext webApplicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
            bean = webApplicationContext.getAutowireCapableBeanFactory().getBean(beanId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bean;
    }

	@Override
	public String[][] getVideoCategories(String videoCategoryServiceUnique,String domain) {	
            ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
            return parevavideolist.getVideoCategories(videoCategoryServiceUnique,domain);
	}
	

	@Override
	public int getNumberOfVideosToDisplay(String videoCategoryServiceUnique,String domain) {
	ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
        return parevavideolist.getNumberOfVideosToDisplay(videoCategoryServiceUnique, domain);
	}

	@Override
	public String[][] getVideos(String videoCategoryServiceUnique, int index,String domain, int numberOfElements, String lang) {
	ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
        return parevavideolist.getVideos(videoCategoryServiceUnique,index,domain,numberOfElements,lang);	
	}

	@Override
	public int getTotalNumberOfVideos(String videoCategoryServiceUnique,String domain) {
            ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
           return parevavideolist.getTotalNumberOfVideos(videoCategoryServiceUnique,domain);
	}

	@Override
	public String[][] getVideosBySortType(String videoCategoryServiceUnique, String sortType, int index, String domain, int numberOfElements, String lang) {
	ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
         return   parevavideolist.getVideosBySortType(videoCategoryServiceUnique,sortType,index,domain,numberOfElements,lang);
	}

	@Override
	public String[][] searchVideo(String videoCategoryServiceUnique, String searchString, int index, String domain, int numberOfElements, String lang) {
	ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
        return parevavideolist.searchVideo(videoCategoryServiceUnique, searchString,index, domain, numberOfElements,lang);
	}
	
	public int getTotalNumberOfSearchedVideos(String videoCategoryServiceUnique,String domain,String classification,String searchString){
	ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
        return parevavideolist.getTotalNumberOfSearchedVideos(videoCategoryServiceUnique,domain,classification,searchString);
	}
	
	public String[] getBanner(String bannerSrvc, String domain,String classification,String region) {
            ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
            return parevavideolist.getBanner(bannerSrvc,domain,classification,region);

	}

	@Override
	public String[][] getVideosByCategory(String categoryUnique, int index,
			String domain, int numberOfElements, String lang) {
		 ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
         return parevavideolist.getVideosByCategory(categoryUnique, index, domain, numberOfElements, lang);
	}

	@Override
	public int getTotalNumberOfVideosInCategory(String categoryUnique) {
		 ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
         return parevavideolist.getTotalNumberOfVideosInCategory(categoryUnique);
	}

	@Override
	public String[][] getVideosExcludingProvidedVideoUnique(String videoCategoryServiceUnique,
			String videoUnique, int index, String domain, int numberOfElements,
			String lang) {
		ParevaVideoList parevavideolist=(ParevaVideoList) fetchBean("parevavideolist");
        return parevavideolist.getVideos(videoCategoryServiceUnique, videoUnique, index, domain, numberOfElements, lang);
	}
}
