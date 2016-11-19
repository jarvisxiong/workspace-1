package ume.pareva.es.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;

import javax.servlet.http.Cookie; //
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ume.pareva.cms.ItemTypeTicket;
import ume.pareva.cms.MobileClub;
import ume.pareva.cms.MobileClubDao;
import ume.pareva.cms.UmeClubDetails;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;

/**
 * Servlet implementation class IndexMain
 */
//@WebServlet("/IndexMain")
public class IndexMainEs extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public IndexMainEs() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processIndexMain(request, response);
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        processIndexMain(request, response);
    }

    protected void processIndexMain(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        HttpSession session = request.getSession();
        RequestDispatcher rd = null;
        UmeSessionParameters aReq = (UmeSessionParameters) request.getAttribute("aReq");

        try {
            if (aReq == null) {
                aReq = new UmeSessionParameters(request);
            }
            //System.out.println("SpainVIDEO REQUEST SESSION ATTRIBUTE VALUES in INDEXMAIN.JAVA FOR ES  " + aReq.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
        String domain = (String) request.getAttribute("domain");
        String domainName = System.getProperty(domain + "_url");
        UmeDomain dmn = aReq.getDomain();
        UmeUser user = aReq.getUser();
        
           if(user==null) {
            user=(UmeUser) request.getAttribute("user");
            
        }
        //System.out.println("SpainVIDEO index main domain " + domainName);
        String subpage = aReq.get("subpage");
        Map<String, String> dParamMap = (Map<String, String>) request.getAttribute("dParamMap");

        MobileClubDao mobileclubdao = (MobileClubDao) request.getAttribute("mobileclubdao");
        MobileClub club = (MobileClub) request.getAttribute("club");

        UmeClubDetails userclubdetails = null;
        if (club != null) {
            userclubdetails = UmeTempCmsCache.umeClubDetailsMap.get(club.getUnique());
        }
        boolean isThirdParty = userclubdetails != null && userclubdetails.getServedBy().equalsIgnoreCase("ThirdParty");

        boolean subscribe = false;
        if (subpage.equals("subscribe") || subpage.equals("confirm")) {
            subscribe = true;
            subpage = dParamMap.get("subscribe");
        }
        if (subpage == null || (!subpage.equals("") && UmeTempCmsCache.subPageTypes.get(subpage) == null)) {
            subpage = "";
        }

        Cookie ck = new Cookie("_MXMSUBPAGE", subpage);
        ck.setPath("/");
        ck.setMaxAge(-1);
        response.addCookie(ck);
        //System.out.println("index main PART 2 line no. 82 " + domainName);
        String id = aReq.get("id").trim();
        if (!id.equals("")) {
            String cookieDomain = domainName;
            if (cookieDomain.indexOf(".") > -1) {
                cookieDomain = cookieDomain.substring(cookieDomain.indexOf("."));
            }
            if (cookieDomain.indexOf(".") == -1) {
                cookieDomain = domainName;
            }
            ck = new Cookie("JIMICLUBID", id);
            ck.setDomain(cookieDomain);
            ck.setPath("/");
            ck.setMaxAge(31536000);
            response.addCookie(ck);
            //doMsisdnTrace( request, msisdn, "In index_html via personal link" );
        }
        //System.out.println("SpainVIDEO index main PART 3 line no. 95 " + domainName);

        String adultcontent = "";
        String referer = "";
        try {
            //This is for NullPointer Exception
            adultcontent = dParamMap.get("adultcontent");
            //System.out.println("SpainVIDEO index main PART 4 line no. 104 " + domainName);
        } catch (Exception e) {
        }
        if (adultcontent == null) {
            adultcontent = "0";
        }

        /*
         if (adultcontent.equals("1")) {
         try{
         referer = request.getHeader("referer");
         }catch(Exception e){}
            
         if (referer==null) referer = "";
         System.out.println("Referer: " + referer);
         System.out.println("DomainName: " + domainName + ": " + referer.indexOf(domainName) + ": " + referer.indexOf("simulator.jsp"));
            
         if (referer.indexOf(domainName)==-1 || referer.indexOf("simulator.jsp")>-1) {
         response.sendRedirect("http://" + domainName + "/disclaimer.jsp");
         return;
         }
         }
         */
        //System.out.println("index main PART 5 line no. 120 " + domainName);
        String item = aReq.get("i");
        String unq = aReq.get("uq");
        String itype = aReq.get("tp");

        //System.out.println("SpainVIDEO INDEX MAIN AFTER READING ITEM UNQ AND TYPE================");
        if (!unq.equals("") && !itype.equals("")) {

            String target = "";
            if (itype.equals("1")) {
                target = "ptonedetail.jsp";
            } else if (itype.equals("2")) {
                target = "mtonedetail.jsp";
            } else if (itype.equals("3")) {
                target = "mtonedetail.jsp";
            } else if (itype.equals("4")) {
                target = "bgdetail.jsp";
            } else if (itype.equals("5")) {
                target = "propics.jsp";
            } else if (itype.equals("6")) {
                target = "gamedetail.jsp";
            } else if (itype.equals("7")) {
                target = "mtonedetail.jsp";
            }

//            System.out.println("WAP Forwarding: " + target + "?unq=" + unq);
//            System.out.println("Sending redirect: http://" + domainName + "/" + target + "?unq=" + unq);
            response.sendRedirect("http://" + domainName + "/" + target + "?unq=" + unq);
            return;
        } else if (!item.equals("")) {

            String sqlstr = "";
            String ticket = "";

            if (itype.equals("")) {
                String[] itemDetails = new ItemTypeTicket().getDetails(item);
                item = String.valueOf(itemDetails[0]);
                itype = String.valueOf(itemDetails[1]);
                ticket = String.valueOf(itemDetails[2]);
                if (item == null || item.trim().length() <= 0) {
                    item = "";
                }
            }

            if (!item.equals("")) {
                if (itype.equals("master")) {
                    itype = "mtonedetail.jsp";
                } else if (itype.equals("real")) {
                    itype = "mtonedetail.jsp";
                } else if (itype.equals("true")) {
                    itype = "mtonedetail.jsp";
                } else if (itype.equals("fun")) {
                    itype = "mtonedetail.jsp";
                } else if (itype.equals("bg")) {
                    itype = "bgdetail.jsp";
                } else if (itype.equals("java")) {
                    itype = "gamedetail.jsp";
                } else if (itype.equals("poly")) {
                    itype = "ptonedetail.jsp";
                } else {
                    itype = "";
                }
            }

            if (!itype.equals("")) {
                try {
                    rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/" + itype + "?unq=" + item + "&tk=" + Misc.hex8Code(ticket));
                    rd.forward(request, response);
                    //application.getRequestDispatcher("/" + dmn.getDefPublicDir() + "/" + itype + "?unq=" + item + "&tk=" + Misc.hex8Code(ticket)).forward(request,response); 
                    return;
                } catch (Exception e) {
                    System.out.println("IndexMainEs Exception line no. 212 " + e);
                }
                return;
            }

        }

        //System.out.println("SpainVIDEO READING THE CLIENT SERVICES NOW WITH DPMAIN+SSUBPAGE  " + domain + "  " + subpage);
        String[] props = null;
        java.util.List list = UmeTempCmsCache.clientServices.get(domain + subpage);

        //System.out.println("indexmain check: "+domain + subpage+ " list size "+list.get(0).toString()+ " "+list.get(1).toString()+" "+list.get(2).toString() );
        java.util.List mylist = UmeTempCmsCache.clientServices.get(domain + subpage);
        java.util.List promo_hot_video_list = new ArrayList();
        java.util.List promo_freetext_list = new ArrayList();
        java.util.List promo_hot_bg_list = new ArrayList();
        java.util.List promo_top_bg_list = new ArrayList();
        java.util.List promo_hot_video_category_list = new ArrayList();
        java.util.List promo_banner_list = new ArrayList();
        int number_of_category = 0;
        int hot_video = 0;
        int free_text = 0;
        int hot_bg = 0;
        int top_bg = 0;
        int hot_video_category = 0;

        //if (!isThirdParty) {

        if(list!=null && !list.isEmpty() && list.size()>0) {
            for (int i = 0; i < list.size(); i++) {
                String[] servicesList = (String[]) list.get(i);
//            for (int j = 0; j < servicesList.length; j++) {
//                System.out.println("SpainVIDEO Services " + j + " = " + servicesList[j]);
//            }
                //String sqlstr = "";
                String srvc = servicesList[1];
                String fName = servicesList[3];
                //System.out.println("SpainVIDEO fname in IndexMAIN "+fName);
                if (fName.equals("promo_hot_video.jsp")) {
                    promo_hot_video_list.add(srvc);
                    hot_video = 1;
                } else if (fName.equals("promo_freetext.jsp")) {
                    promo_freetext_list.add(srvc);
                    free_text = 1;
                } else if (fName.equals("promo_hot_bg.jsp")) {
                    promo_hot_bg_list.add(srvc);
                    hot_bg = 1;
                } else if (fName.equals("promo_top_bg.jsp")) {
                    promo_top_bg_list.add(srvc);
                    top_bg = 1;
                } else if (fName.equals("promo_hot_video_category.jsp")) {
                    promo_hot_video_category_list.add(srvc);
                    hot_video_category = 1;
                    continue;
                } else if (fName.equals("promo_banner.jsp")) {
                    promo_banner_list.add(srvc);
                    session.setAttribute("promo_banner_list", promo_banner_list);
                }
            }
    }
        //}

        number_of_category = hot_video + free_text + hot_bg + top_bg + hot_video_category;
        //System.out.println("index_main.jsp list size = " + list.size());
        request.setAttribute("list", list);
        session.setAttribute("promo_hot_video_list", promo_hot_video_list);
        session.setAttribute("promo_freetext_list", promo_freetext_list);
        session.setAttribute("promo_hot_bg_list", promo_hot_bg_list);
        session.setAttribute("promo_top_bg_list", promo_top_bg_list);
        session.setAttribute("promo_hot_video_category_list", promo_hot_video_category_list);

//        System.out.println("promo_hot_video_list size = " + promo_hot_video_list.size());
//        System.out.println("promo_freetext_list size = " + promo_freetext_list.size());
//        System.out.println("promo_hot_bg_list size = " + promo_hot_bg_list.size());
//        System.out.println("promo_top_bg_list size = " + promo_top_bg_list.size());
//        System.out.println("promo_hot_video_category_list size = " + promo_hot_video_category_list.size());
        session.setAttribute("include_header", "true");
        request.setAttribute("number_of_category", number_of_category);

        //doMsisdnTrace( request, msisdn, "Rendering index_html with sub page " + subpage );
        if (!subpage.equals("") || user == null || !mobileclubdao.isActive(user, club)) {
            rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_subscribe.jsp");
            rd.include(request, response);
        } else {
            if (promo_hot_video_list.size() > 0) {
                number_of_category = number_of_category - 1;
                session.setAttribute("number_of_category", number_of_category);
                rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video.jsp");
                rd.include(request, response);
            }

            if (promo_freetext_list.size() > 0) {
                number_of_category = number_of_category - 1;
                session.setAttribute("number_of_category", number_of_category);
                rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_freetext.jsp");
                rd.include(request, response);
            }

            if (promo_hot_bg_list.size() > 0) {
                number_of_category = number_of_category - 1;
                session.setAttribute("number_of_category", number_of_category);
                rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_bg.jsp");
                rd.include(request, response);
            }

            if (promo_top_bg_list.size() > 0) {
                number_of_category = number_of_category - 1;
                session.setAttribute("number_of_category", number_of_category);
                rd = getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_top_bg.jsp");
                rd.include(request, response);
            }
            if (promo_hot_video_category_list.size() > 0) {
                session.setAttribute("number_of_category", (Object) (--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video_category.jsp");
                rd.include(request, response);
            }
            if (isThirdParty) {
                session.setAttribute("redirectto", "http://" + dmn.getRedirectUrl() + "/?m=" + Misc.encrypt(user.getParsedMobile()));
                request.setAttribute("redirectto", "http://" + dmn.getRedirectUrl() + "/?m=" + Misc.encrypt(user.getParsedMobile()));
            }
        }
    }
}
