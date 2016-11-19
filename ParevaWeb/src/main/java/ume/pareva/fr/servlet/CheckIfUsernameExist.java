package ume.pareva.fr.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.MobileClub;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeMobileClubUserDao;
import ume.pareva.pojo.SdcMobileClubUser;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;

@WebServlet("/CheckIfUsernameExist")
public class CheckIfUsernameExist extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	@Autowired
	UmeRequest umeRequest;
	
	@Autowired
	UmeMobileClubUserDao umemobileclubuserdao;
	
       
    public CheckIfUsernameExist() {
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
		String message="";
		PrintWriter writer=response.getWriter();
		String username=umeRequest.get("email");
		boolean valid = EmailValidator.getInstance().isValid(username.trim());
		if(!valid){
			//Please enter a valid email address
			message="S'il vous plaît, mettez une adresse email valide.";
		}else{
			UmeDomain dmn=umeRequest.getDomain();
			MobileClub club = UmeTempCmsCache.mobileClubMap.get(dmn.getUnique());
			SdcMobileClubUser clubUser=	umemobileclubuserdao.getClubUserByParam1(username, club.getUnique());
			if(clubUser!=null){
				//Email Already Exists, Please provide another email address
				message="Cet e-mail déjà utilisée. S'il vous plaît fournir une autre adresse e-mail";
			}
		}
		writer.print(message);
	}
	
	

}
