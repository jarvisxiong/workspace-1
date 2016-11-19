package ume.pareva.interstitial;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import ume.pareva.cms.ItemImage;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.cms.VideoClip;
import ume.pareva.cms.VideoClipDao;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pt.util.UmeRequest;
import ume.pareva.sdk.Handset;
import ume.pareva.sdk.HandsetDao;
import ume.pareva.sdk.Misc;
import ume.pareva.userservice.VideoList;


public class InterstitialVideoDetail extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Autowired
	VideoClipDao videoclipdao;

	@Autowired
	MobileClubDao mobileclubdao;

	@Autowired
	HandsetDao handsetdao;

	@Autowired
	UmeRequest umeRequest;

	@Autowired
	VideoList videolist;
   
    public InterstitialVideoDetail() {
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
	
		UmeDomain dmn=umeRequest.getDomain();
		String domain=dmn.getUnique();
		String unique = umeRequest.get("unq");
		//String cat = umeRequest.get("cat");

		String ticket = Misc.hex8Decode(umeRequest.get("tk"));
		String cType = "";
		String tempname = "clip";
		String videoDetailImage="";
		String videoDetailTitle="";
		int index = Integer.parseInt(umeRequest.get("ind", "0"));
		List<Map<String,String>> videos=new ArrayList<Map<String,String>>();

		//	boolean status=userStatus.checkIfUserActiveAndNotSuspended(user, club);
		// 	if(status){
		Handset handset=handsetdao.getHandset(request);
		String ddir = dmn.getDefPublicDir();
		VideoClip item = videoclipdao.getItem(unique, UmeTempCmsCache.clientDomains.get(dmn.getUnique()));
		if (item==null) {
			try { 
				request.getServletContext().getRequestDispatcher("/" + System.getProperty("dir_" + dmn.getUnique() + "_pub") + "/error.jsp").forward(request,response); 
			}
			catch (Exception e) { 
				System.out.println("videodetail Exception "+e); 
			}
			return;    
		}

		ItemImage image = item.getItemImage("webthumb", 0, 0);

		if(image==null)
		{
			image = item.getItemImage("mobthumb", 0, 0);
		}
		if (image!=null) {
			videoDetailImage = image.getPath();
			videoDetailTitle=item.getTitle();
		}

		if (handset.get("playback_3gpp").equals("true") && item.getResourceMap().get("3gp")!=null) cType = "3gp";
		else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mp4")!=null) cType = "mp4";
		else if (handset.get("playback_mp4").equals("true") && item.getResourceMap().get("mpeg")!=null) cType = "mpeg";
		else if (handset.get("playback_flv").equals("true") && item.getResourceMap().get("flv")!=null) cType = "flv";
		else if (handset.get("playback_3g2").equals("true") && item.getResourceMap().get("3g2")!=null) cType = "3g2";
		else if (handset.get("playback_mov").equals("true") && item.getResourceMap().get("mov")!=null) cType = "mov";
		else if ((handset.get("playback_wmv").equals("7") || handset.get("playback_wmv").equals("8") || handset.get("playback_wmv").equals("9"))
				&& item.getResourceMap().get("wmv")!=null) cType = "wmv";


		String dllink = tempname + "." + cType + "?d=" + ddir + "&iunq=" + item.getUnique() + "&axud=1&itype=video&ctype=" + cType;



		if (!ticket.equals("")){ 
			dllink = ticket; 
		}

		response.sendRedirect(dllink);
	}
	
	

}
