
<%@page import="ume.pareva.dao.SdcRequest"%>
<%@page import="java.util.Enumeration"%>
<%@ page import="ume.pareva.sdk.*" %>
<%
//***************************************************************************************************
 Enumeration enumer=request.getAttributeNames();
       while(enumer.hasMoreElements())
       {
           String attrib=(String) enumer.nextElement();
           //System.out.println("==ADMIN_PRI/anyxindex.jsp Received..Attribute "+attrib+" : "+request.getAttribute(attrib));
           session.setAttribute(attrib,request.getAttribute(attrib));
           
       }
          Enumeration enumerpa=request.getParameterNames();
       while(enumerpa.hasMoreElements())
       {
           String param=(String) enumerpa.nextElement();
           //System.out.println("==ADMIN_PRI/anyxindex.jsp Received..Parameter "+param+" : "+request.getParameter(param));
           
       }

AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String domain = aReq.getDomain();
String anyxSrvc = aReq.getAnyxService();
String anyxPage = aReq.getAnyxPage();
String exp = aReq.getAnyxExp();
String anyxMenu = aReq.getAnyxMenu();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "umeadmin";
//***************************************************************************************************

String topFrame = aReq.get("top");
if (topFrame.equals("")) topFrame = "top.jsp";

String publicDir = System.getProperty("dir_" + domain + "_pub");
String menuDir = publicDir;
String sDir = System.getProperty("dir_" + anyxSrvc);		
if (sDir==null || sDir.equals("")) sDir = publicDir;

int topframeHeight = 114;
						
if (!uid.equals("")) {					
    menuDir = System.getProperty("dir_" + domain + "_pri");
    topframeHeight = 120;
}		
	System.out.println("SDIR  Value "+sDir);
if (!sDir.startsWith("/")) sDir = "/" + sDir;
if (!sDir.endsWith("/") && !anyxPage.startsWith("/")) anyxPage = "/" + anyxPage;		
else if (sDir.endsWith("/") && anyxPage.startsWith("/")) anyxPage = anyxPage.substring(1);

%>
<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
  <title>UME Admin Panel</title>
 <link href="http://fonts.googleapis.com/css?family=Open+Sans:400,300,600,700&subset=all" rel="stylesheet" type="text/css" />
<link href="/static/AdminPanel/assets/global/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
<link href="/static/AdminPanel/assets/global/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
<!-- END GLOBAL MANDATORY STYLES -->
<!-- BEGIN THEME GLOBAL STYLES -->
<link href="/static/AdminPanel/assets/global/css/components-md.min.css" rel="stylesheet" id="style_components" type="text/css" />
<link href="/static/AdminPanel/assets/global/css/plugins-md.min.css" rel="stylesheet" type="text/css" />
<!-- END THEME GLOBAL STYLES -->
<!-- BEGIN THEME LAYOUT STYLES -->
<link href="/static/AdminPanel/assets/layouts/layout/css/layout.min.css" rel="stylesheet" type="text/css" />
<link href="/static/AdminPanel/assets/layouts/layout/css/themes/darkblue.min.css" rel="stylesheet" type="text/css" id="style_color" />
  <script type="text/javascript" src="http://pubnub.github.io/eon/v/eon/0.0.10/eon.js"></script>
    <link type="text/css" rel="stylesheet" href="http://pubnub.github.io/eon/v/eon/0.0.10/eon.css" />

  <style>
 .contentHolder embed,
 object {
  min-height: 1000px;
  width: 100%;
  height: 100%;
 }
     .alertHolder {
        min-height: 49px;
        margin-top: -20px;
        margin-bottom: -20px;
    }
    iframe {
      width: 100%;
      height: 100%;
      min-height: 1000px;
      border: none;
    }
  </style>
</head>
<body class="page-header-fixed page-sidebar-closed-hide-logo page-content-white page-md">
    <!-- BEGIN HEADER -->
    <div class="page-header navbar navbar-fixed-top">
      <div class="page-header-inner ">
        <!-- BEGIN LOGO -->
        <div class="page-logo">
            <a href="#">
              <img src="/static/AdminPanel/assets/global/img/logo/smallLogo.png" alt="UME limited logo" /> 
            </a>
            <%-- MADAN - could you please make these vars work in this file, and not the other?

            User: <%=sdcuser.getFirstName() %>
Time of Login: <%=session.getAttribute("user_login_time") %> --%>
            <div class="menu-toggler sidebar-toggler"> </div>
        </div>
      </div>
    </div>
    <!-- END HEADER -->
    <!-- BEGIN HEADER & CONTENT DIVIDER -->
    <div class="clearfix"> </div>
    <!-- END HEADER & CONTENT DIVIDER -->
    <!-- BEGIN CONTAINER -->
   <div class="page-container">
        <!-- BEGIN SIDEBAR -->
        <div class="page-sidebar-wrapper">
            <!-- BEGIN SIDEBAR -->
            <!-- DOC: Set data-auto-scroll="false" to disable the sidebar from auto scrolling/focusing -->
            <!-- DOC: Change data-auto-speed="200" to adjust the sub menu slide up/down speed -->
            <div class="page-sidebar navbar-collapse collapse " >

              <ul class="sideBarHolder page-sidebar-menu  page-header-fixed" 
                  data-keep-expanded="false" 
                  data-auto-scroll="true" 
                  data-slide-speed="200" 
                  style="padding-top: 20px">
                  <%--
                  Load the file with JS load here, include gives errors :( 
                  <object data="../umeadmin/defMenus.jsp?anyx_exp=<%=exp%>&menusid=<%=anyxSrvc%>&anyx_menu=<%=anyxMenu%>&lang=<%=lang%>">
                    <embed src="../umeadmin/defMenus.jsp?anyx_exp=<%=exp%>&menusid=<%=anyxSrvc%>&anyx_menu=<%=anyxMenu%>&lang=<%=lang%>"></embed>
                    Error with embedded loading.
                  </object>--%>
              </ul>
            </div>
        </div>
        <!-- END SIDEBAR -->
            <!-- BEGIN CONTENT -->
            <div class="page-content-wrapper">
                <!-- BEGIN CONTENT BODY -->
                <div class="contentHolder page-content">
                  <object data="">
                    <embed src=""></embed>
                    Error with embedded loading.
                  </object>
                </div><!-- END CONTENT BODY -->
            </div><!-- END CONTENT -->

    </div>
<!-- BEGIN CORE PLUGINS -->
<script src="/static/AdminPanel/assets/global/plugins/jquery.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/js.cookie.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/global/plugins/jquery.blockui.min.js" type="text/javascript"></script>
<!-- END CORE PLUGINS -->

<!-- BEGIN THEME GLOBAL SCRIPTS -->
<script src="/static/AdminPanel/assets/global/scripts/app.min.js" type="text/javascript"></script>
<!-- END THEME GLOBAL SCRIPTS -->
<!-- BEGIN THEME LAYOUT SCRIPTS -->
<script src="/static/AdminPanel/assets/layouts/layout/scripts/layout.min.js" type="text/javascript"></script>
<script src="/static/AdminPanel/assets/layouts/layout/scripts/demo.min.js" type="text/javascript"></script>
<!-- END THEME LAYOUT SCRIPTS -->

<script type='text/javascript'>
$( document ).ready(function() {

  function loadPage(pageUrl) {
    //$('.contentHolder ').load(pageUrl);
     $('.contentHolder object').attr("data", pageUrl).hide().show();
    $('.contentHolder embed').attr("src", pageUrl).hide().show();
  }

  function loadPageEmbed(pageUrl) {
    var myHtml = '<object data="'+ pageUrl +'">' 
                +'  <embed src="'+ pageUrl + '"></embed>' 
                +'  Error: Embedded data could not be displayed.'
                +'</object>';
    $('.contentHolder').html(myHtml);
    //$('.contentHolder embed').attr("src", pageUrl).hide().show();
  }

  function loadPageJQ(pageUrl) {
    $('.contentHolder').load(pageUrl);
  }

  $('.sideBarHolder').load('../umeadmin/defMenus.jsp?anyx_exp=<%=exp%>&menusid=<%=anyxSrvc%>&anyx_menu=<%=anyxMenu%>&lang=<%=lang%>');
  loadPageJQ('../umeadmin/index.jsp?<%=aReq.getQueryString()%>');

  $('.contentHolder').contents().find("a").on("click", function() {
    alert('boobs');
  });

});


</script>
</body>


</html>		
    
