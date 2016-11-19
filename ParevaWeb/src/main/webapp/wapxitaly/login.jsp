<%@include file="commonfunc.jsp"%>
<%
//    System.out.println("smsoptin debug called upon");
    SdcRequest aReqSdc = new SdcRequest(request);
    String resp = "";
    /* TODO output your page here. You may use following sample code. */
    String mobileno = "";
    UmeClubDetails clubdetails = null;

    try {
        mobileno = Misc.parseMsisdn((String) request.getParameter("submsisdn"));
        System.out.println("Msisdn: " + mobileno.toString());
    } catch (Exception e) {
        mobileno = "";
    }

    mobileno = parseMsisdn(mobileno);
    boolean validateMobileNo = checkMsisdn(mobileno);

    SdcMobileClubUser clubuser = null;
    UmeMobileClubUserDao umemobileclubuserdao = null;

    try {
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request, servletContext);
        umemobileclubuserdao = (UmeMobileClubUserDao) ac.getBean("umemobileclubuserdao");
        umeuserdao = (UmeUserDao) ac.getBean("umeuserdao");
        mobileclubdao = (MobileClubDao) ac.getBean("mobileclubdao");
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    if (validateMobileNo) {
        if (club != null) {
            clubUnique = club.getUnique();
        }
        if (clubUnique != null) {
            clubuser = umemobileclubuserdao.getClubUserByMsisdn(mobileno, clubUnique);
        }
        if (clubuser != null && clubuser.getActive()==1) {
            user = umeuserdao.getUser(clubuser.getParsedMobile());
            if(user!=null){
                String wapid = user.getWapId();
                resp = wapid;
            }else{
                resp = "failed--no user information for this msisdn: " + mobileno;
            }
        }else{
            resp = "failed--this user is not active";
        }
    }else{
        resp = "failed--is not validate msisdn or operator choose is not correct";
    }
    
    System.out.println("sms optin resp " + resp);

    out.write(resp);
%>

<%!
    boolean checkMsisdn(String mobileno) {
        boolean validmsisdn = (mobileno != null && !mobileno.trim().equals("") && SdcMisc.validateTel(mobileno) && mobileno.startsWith("39") && (mobileno.length()>=8));
        return validmsisdn;
    }
    String parseMsisdn(String mobileno){
        if (mobileno != null && mobileno.trim().length() > 0) {
        if (mobileno.contains("+")) {
            mobileno = mobileno.replace("+", "").trim();
        }
        }
        if (mobileno != null && mobileno.startsWith("0")) {
            mobileno = "39" + mobileno.substring(1);
        }
        if(mobileno != null && mobileno.startsWith("3") && !mobileno.startsWith("39")) mobileno = "39"+mobileno;
        return mobileno;
    }

%>