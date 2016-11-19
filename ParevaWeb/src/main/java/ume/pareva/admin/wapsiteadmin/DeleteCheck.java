package ume.pareva.admin.wapsiteadmin;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.pt.util.UmeRequest;

@WebServlet("/DeleteCheck")
public class DeleteCheck extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	WapSiteAdminDao wapSiteAdminDao;
	
	@Autowired
	UmeRequest umeRequest;
	
       
    public DeleteCheck() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	processRequest(request,response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processRequest(request,response);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String landingPage=umeRequest.get("landingPage");
		String domain=umeRequest.get("domain");
		List<Map<String,String>> campaignList=wapSiteAdminDao.checkIfLandingPageUsedByCampaigns(landingPage);
		PrintWriter writer=response.getWriter();
		String landingPageCampaigns="";
		if(campaignList.size()>0){
			String campaignListInJson="";
			for(Map<String,String> campaignMap:campaignList){ 
				campaignListInJson=campaignListInJson+"\"cid\": \""+campaignMap.get("aUnique")+"\",\"campaign\": \""+campaignMap.get("aCampaign")+"\",";
			}
			landingPageCampaigns="{"
					+ "\"landingPage\": \""+landingPage+"\","
					+ "\"campaigns\":[{"
					+ campaignListInJson.substring(0, campaignListInJson.length()-1)
					+ "}]}";
			
			writer.print(landingPageCampaigns);
		}else{
			int deletedRows=wapSiteAdminDao.deleteLandingPage(landingPage, domain);
			if(deletedRows>0)
				writer.print("{\"deleted\":\"true\"}");
			else
				writer.print("{\"deleted\":\"false\"}");
		}
	}
	

}
