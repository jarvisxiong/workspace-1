package ume.pareva.admin.quiz;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.pt.util.UmeRequest;

@WebServlet("/FetchClubByRegion")
public class FetchClubByRegion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private MobileClubDao mobileClubDao;
	private UmeRequest umeRequest;
	
	public UmeRequest getUmeRequest() {
		return umeRequest;
	}

	@Autowired
	public void setUmeRequest(UmeRequest umeRequest) {
		this.umeRequest = umeRequest;
	}

	public MobileClubDao getMobileClubDao() {
		return mobileClubDao;
	}

	@Autowired
	public void setMobileClubDao(MobileClubDao mobileClubDao) {
		this.mobileClubDao = mobileClubDao;
	}
       
    public FetchClubByRegion() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this,
				config.getServletContext());
	}

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		List<MobileClub> mobileClubs=mobileClubDao.getMobileClubByRegion(umeRequest.get("region"));
		String mobileClubJson="";
		for(MobileClub club:mobileClubs){
			mobileClubJson=mobileClubJson+"\""+club.getName()+"\": \""+club.getUnique()+"\",";
		}
		mobileClubJson="{"
				+ mobileClubJson.substring(0, mobileClubJson.length()-1)
				+ "}";
		PrintWriter writer=response.getWriter();
		writer.print(mobileClubJson);
	}

}
