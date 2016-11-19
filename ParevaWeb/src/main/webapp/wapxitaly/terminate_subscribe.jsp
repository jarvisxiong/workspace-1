<%@include file="commonfunc.jsp"%>
<%
    try {
        
        String msisdn = user.getParsedMobile();
        UmeMobileClubUserDao umemobileclubuserdao = (UmeMobileClubUserDao) request.getAttribute("umemobileclubuserdao");
        SdcMobileClubUser clubUser = umemobileclubuserdao.getClubUser(user.getUnique(), club.getUnique());
        
        campaigndao.log("IPXLink", clubUser.getLandingpage(), clubUser.getUserUnique(), clubUser.getParsedMobile(), null,
                                    club.getWapDomain(), clubUser.getCampaign(), clubUser.getClubUnique(), "SMSLINKSTOP", 0, request, response, clubUser.getNetworkCode());
        
        application.getRequestDispatcher("/" + System.getProperty("dir_" + domain + "_pub") + "/terminate.jsp").forward(request, response);
    } catch (Exception e) {
        System.out.println(e);
    }
%>