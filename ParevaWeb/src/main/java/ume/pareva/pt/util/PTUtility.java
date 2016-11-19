package ume.pareva.pt.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.springframework.stereotype.Component;

import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.SdcMiscDate;

@Component("ptUtility")
public class PTUtility {

	public List<String> getServiceUniqueList(String domain,String networkCode){

		List<String[]> serviceList = (List<String[]>)UmeTempCmsCache.clientServices.get(domain);
		List<String> videoCategorySrvcList=new ArrayList<String>();

		if(serviceList!=null && !serviceList.isEmpty() && serviceList.size()>0) {
			for (int i = 0; i < serviceList.size(); ++i) {
				String[] serviceParameter = (String[])serviceList.get(i);
				String srvc = serviceParameter[1];
				String fName = serviceParameter[3];            
				String classification=serviceParameter[6];

				//if (fName.equals("promo_hot_video_category.jsp")&&(networkCode.equals("01")||networkCode.equals("03"))&&classification.equals("fullnude")) {
				if (fName.equals("promo_hot_video_category.jsp")&&(networkCode.equals("01")||networkCode.equals("03"))&&classification.equals("topless")) {
					videoCategorySrvcList.add(srvc);
				}else if (fName.equals("promo_hot_video_category.jsp")&&(networkCode.equals("02")||networkCode.equals("06")||networkCode.equals("80"))&&classification.equals("hardcore")) {
					videoCategorySrvcList.add(srvc);
			//	}else if(fName.equals("promo_hot_video_category.jsp")&&networkCode.equals("")&&classification.equals("fullnude")){
				}else if(fName.equals("promo_hot_video_category.jsp")&&networkCode.equals("")&&classification.equals("topless")){
					videoCategorySrvcList.add(srvc);
				}

			}
		}
		return videoCategorySrvcList;
	}
	
	public String xmlDateToSqlDate(String xmlDate){
		DateTimeFormatter parser = ISODateTimeFormat.dateTimeParser();
		DateTime dateTime = parser.parseDateTime(xmlDate);
		return SdcMiscDate.toSqlDate(new Date(dateTime.getMillis()));
	}
	
	public static void main(String[] args ){
		PTUtility ptUtility=new PTUtility();
		try {
			System.out.println(URLDecoder.decode("2016-09-21T07%3A26%3A27.549%2B01%3A00", "UTF-8"));
			System.out.println(ptUtility.xmlDateToSqlDate(URLDecoder.decode("2016-09-21T07%3A26%3A27.549%2B01%3A00", "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
