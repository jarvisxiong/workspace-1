package ume.pareva.au;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import ume.pareva.cms.ItemTypeTicket;
import ume.pareva.cms.UmeTempCmsCache;
import ume.pareva.dao.UmeSessionParameters;
import ume.pareva.pojo.UmeDomain;
import ume.pareva.pojo.UmeUser;
import ume.pareva.sdk.Misc;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 *
 * @author madan
 */
public class IndexMain extends HttpServlet {

    static final Logger logger = LogManager.getLogger(IndexMain.class.getName());
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadContext.put("ROUTINGKEY", "AU");
        
        
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        RequestDispatcher rd = null;
        UmeSessionParameters aReq = (UmeSessionParameters)request.getAttribute("aReq");
        try {
            if (aReq == null) {
                aReq = new UmeSessionParameters(request);
            }
            logger.info("PROCESS REQUEST -- REQUEST SESSION ATTRIBUTE VALUES in INDEXMAIN.JAVA:  " + aReq.toString());
        }
        catch (Exception ex) {
            logger.fatal("ERROR PROCESSING REQUEST {}", ex.getMessage());
            logger.fatal("ERROR PROCESSING REQUEST ",ex);
        }
        
        String domain = (String)request.getAttribute("domain");
        String domainName = System.getProperty(String.valueOf(domain) + "_url");
        UmeDomain dmn = aReq.getDomain();
        UmeUser user = aReq.getUser();
        if(user==null) {
            user=(UmeUser) request.getAttribute("user");
            
        }
        if(domain==null) domain=dmn.getUnique();
        
        String subpage = aReq.get("subpage");
        Map dParamMap = (Map)request.getAttribute("dParamMap");
        boolean subscribe = false;
        if (subpage.equals("subscribe") || subpage.equals("confirm")) {
            subscribe = true;
            subpage = (String)dParamMap.get("subscribe");
        }
        if (!(subpage != null && (subpage.equals("") || UmeTempCmsCache.subPageTypes.get(subpage) != null))) {
            subpage = "";
        }
        Cookie ck = new Cookie("_MXMSUBPAGE", subpage);
        ck.setPath("/");
        ck.setMaxAge(-1);
        response.addCookie(ck);
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
        }
        String adultcontent = "";
        String referer = "";
        
        try {
            adultcontent = (String)dParamMap.get("adultcontent");
        }
        catch (Exception var17_18) {
            // empty catch block
        }
        if (adultcontent == null) {
            adultcontent = "0";
        }
        if (adultcontent.equals("1")) {
            try {
                referer = request.getHeader("referer");
            }
            catch (Exception var17_19) {
                // empty catch block
            }
            if (referer == null) {
                referer = "";
            }
            //System.out.println("Referer: " + referer);
            //System.out.println("DomainName: " + domainName + ": " + referer.indexOf(domainName) + ": " + referer.indexOf("simulator.jsp"));
            if (referer.indexOf(domainName) == -1 || referer.indexOf("simulator.jsp") > -1) {
                response.sendRedirect("http://" + domainName + "/disclaimer.jsp");
                return;
            }
        }
        
        String item = aReq.get("i");
        String unq = aReq.get("uq");
        String itype = aReq.get("tp");
        if (!(unq.equals("") || itype.equals(""))) {
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
        }
        
        if (!item.equals("")) {
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
                itype = itype.equals("master") ? "mtonedetail.jsp" : (itype.equals("real") ? "mtonedetail.jsp" : (itype.equals("true") ? "mtonedetail.jsp" : (itype.equals("fun") ? "mtonedetail.jsp" : (itype.equals("bg") ? "bgdetail.jsp" : (itype.equals("java") ? "gamedetail.jsp" : (itype.equals("poly") ? "ptonedetail.jsp" : ""))))));
            }
            if (!itype.equals("")) {
                try {
                    rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/" + itype + "?unq=" + item + "&tk=" + Misc.hex8Code((String)ticket));
                    rd.forward((ServletRequest)request, (ServletResponse)response);
                    return;
                }
                catch (Exception e) {
                    System.out.println("AUdebug IndexMain Exception line no. 159 "+e); e.printStackTrace();
                    return;
                }
            }
        }
        
        Object props = null;
        System.out.println("AUdebug before client Services --- "+domain+" subpage is UmeTempCmsCache size is -- "+subpage+UmeTempCmsCache.clientServices.size()+" the list VALUES should be "+UmeTempCmsCache.clientServices.get(domain + subpage).toString());
        List list = (List)UmeTempCmsCache.clientServices.get(domain + subpage);
        System.out.println("AUdebug the size of the list is ---- "+list.size());
        List mylist = (List)UmeTempCmsCache.clientServices.get(String.valueOf(domain) + subpage);
        System.out.println("AUdebug the size of the MYLIST is ---- "+mylist.size());
        ArrayList<String> promo_hot_video_list = new ArrayList<String>();
        ArrayList<String> promo_freetext_list = new ArrayList<String>();
        ArrayList<String> promo_hot_bg_list = new ArrayList<String>();
        ArrayList<String> promo_top_bg_list = new ArrayList<String>();
        ArrayList<String> promo_hot_video_category_list = new ArrayList<String>();
        ArrayList<String> promo_banner_list = new ArrayList<String>();
        int number_of_category = 0;
        int hot_video = 0;
        int free_text = 0;
        int hot_bg = 0;
        int top_bg = 0;
        int hot_video_category=0;
        System.out.println("AUdebug BEFORE list comparison "+domain+"  subpage "+subpage);
        
        if(list!=null && list.size()>0) {
        for (int i = 0; i < list.size(); ++i) {
            String[] servicesList = (String[])list.get(i);
            System.out.println("AUdebug inside Loop the serviceList is "+servicesList);
            String srvc = servicesList[1];
            String fName = servicesList[3];
            System.out.println("AUdebug  IndexMain "+srvc+"  fname "+fName);

            if (fName.equals("promo_hot_video.jsp")) {
                promo_hot_video_list.add(srvc);
                hot_video = 1;
                continue;
            }
            if (fName.equals("promo_freetext.jsp")) {
                promo_freetext_list.add(srvc);
                free_text = 1;
                continue;
            }
            if (fName.equals("promo_hot_bg.jsp")) {
                promo_hot_bg_list.add(srvc);
                hot_bg = 1;
                continue;
            }
            if (fName.equals("promo_top_bg.jsp")) {
                promo_top_bg_list.add(srvc);
                top_bg = 1;
                continue;
            }
            if (fName.equals("promo_hot_video_category.jsp")) {
                promo_hot_video_category_list.add(srvc);
                hot_video_category = 1;
                continue;
            }
            if (!fName.equals("promo_banner.jsp")) continue;
            promo_banner_list.add(srvc);
            session.setAttribute("promo_banner_list", promo_banner_list);
        }
    }
        number_of_category = hot_video + free_text + hot_bg + top_bg+hot_video_category;
        System.out.println("AUdebug index_main.jsp list size = " + list.size()+" noofcategory "+number_of_category);
        request.setAttribute("list", (Object)list);
        session.setAttribute("promo_hot_video_list", promo_hot_video_list);
        session.setAttribute("promo_freetext_list", promo_freetext_list);
        session.setAttribute("promo_hot_bg_list", promo_hot_bg_list);
        session.setAttribute("promo_top_bg_list", promo_top_bg_list);
        session.setAttribute("promo_hot_video_category_list", promo_hot_video_category_list);
        System.out.println("AUdebug promo_hot_video_list size = " + promo_hot_video_list.size());
        System.out.println("AUdebug promo_freetext_list size = " + promo_freetext_list.size());
        System.out.println("AUdebug promo_hot_bg_list size = " + promo_hot_bg_list.size());
        System.out.println("AUdebug promo_top_bg_list size = " + promo_top_bg_list.size());
        System.out.println("AUdebug promo_hot_video_category_list size = " + promo_hot_video_category_list.size());
        session.setAttribute("include_header", (Object)"true");
        request.setAttribute("number_of_category", (Object)number_of_category);
        boolean authnOk = false;
        String menuItem = "";
        String link = "";
        
        
        if (request.getAttribute("simulateVideo")!=null && request.getAttribute("simulateVideo").toString().equals("video")) {
            session.setAttribute("number_of_category", (Object)(--number_of_category));
            rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video.jsp");
            rd.include((ServletRequest)request, (ServletResponse)response);
//        } else if (!(subpage.equals("") && user != null)) {
//            rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_subscribe.jsp");
//            rd.include((ServletRequest)request, (ServletResponse)response);
        } else {
            if (promo_hot_video_list.size() > 0) {
                session.setAttribute("number_of_category", (Object)(--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            if (promo_freetext_list.size() > 0) {
                session.setAttribute("number_of_category", (Object)(--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_freetext.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            if (promo_hot_bg_list.size() > 0) {
                session.setAttribute("number_of_category", (Object)(--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_bg.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            if (promo_top_bg_list.size() > 0) {
                session.setAttribute("number_of_category", (Object)(--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_top_bg.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
            if (promo_hot_video_category_list.size() > 0) {
                System.out.println("AUdebug  now on categorly list size "+promo_hot_video_category_list.size());
                session.setAttribute("number_of_category", (Object)(--number_of_category));
                rd = this.getServletContext().getRequestDispatcher("/" + dmn.getDefPublicDir() + "/promo_hot_video_category.jsp");
                rd.include((ServletRequest)request, (ServletResponse)response);
            }
        
    }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
