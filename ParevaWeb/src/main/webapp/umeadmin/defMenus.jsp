<%@ include file="/WEB-INF/jspf/umeadmin/defmenus.jspf" %>

        
<%
    int cCount = 0;
    String link="";

    String langName = "";

    //System.out.println("UG: " + sdcuser.getUserGroup());
    //System.out.println("CAT.size: " + Anyxsdc.categoryList.size());
	
    for (int i=0; i<anyxsdc.getCategoryList().size(); i++) {
        sdccat = anyxsdc.getCategoryList().get(i);

        //System.out.println("CAT: " + sdccat.getName() + ": " + sdccat.getDomains());

        if (sdccat.getDomains().indexOf(domain)==-1) continue;
        if (sdccat.getVisibility()==0) continue;
        if (sdccat.getName().equals("Admin Services") && sdcuser.getAdminGroup()<5) continue;        
        if (!sdccat.getUserGroups().equals("") && sdccat.getUserGroups().indexOf(sdcuser.getUserGroup())==-1) continue;

        //System.out.println("CAT: " + sdccat.getName());

        langName = sdccat.getNameMap().get(langCode);
        if (langName==null || langName.equals("")) langName = sdccat.getName();

        //if (!langName.equals("Main Page")) continue;

        if (!sdccat.getPage().equals("")) link = sdccat.getPage() + "?lang=" + langCode;
        else link = "";
            
%>        
    <li class="nav-item">
        <a class="nav-link nav-toggle" data-url="<%=link%>">
            <span class="title"><%=langName%></span>
            <span class="arrow"></span>
        </a>            
<%
        
	String menuItem="";
	int sCount = 0;

	for (int k=0; k<anyxsdc.getServiceList().size(); k++) {
            sdcsrvc = anyxsdc.getServiceList().get(k);
            
            //System.out.println("SRVC: " + sdcsrvc.getName() + ": " + sdcsrvc.getCategory()  + ": " + sdccat.getUnique());
            if (sdcsrvc.getActive()!=1) continue;
            if (!sdcsrvc.getCategory().equals(sdccat.getUnique())) continue;
                
            //if (services[k][15]==null || services[k][16]==null) continue;
            //if (!services[k][15].equals(cat[0]) || services[k][8].indexOf("1")==-1) continue;
            
            boolean authnOk = false;

            if (sdcsrvc.getVisibility()==2 || sdcsrvc.getVisibility()==3 || sdcsrvc.getVisibility()==6 || sdcsrvc.getVisibility()==7) authnOk = true;
            else if (sdcsrvc.getVisibility()==1 || sdcsrvc.getVisibility()==5) {
                if (sdcsrvc.getServiceType()==SdcService.ServiceType.ANYXADMIN) {
                    if (sdcuser.getAdminGroup()>=5 && sdcuser.getAdminGroup()>=sdcsrvc.getSecLevel()) authnOk = true;
                }
                else if (sdcuser.getSecLevel()>=sdcsrvc.getSecLevel()) {
                    for (int p=0; p<sdcsrvc.getPackageList().size(); p++) {
                        sdcpack = sdcsrvc.getPackageList().get(p);
                        if (sdcgroup!=null && sdcgroup.getPackageMap().get(sdcpack.getUnique())!=null) {
                            authnOk = true; break;
                        }
                    }
                }
            }

            if (!authnOk) continue;

            menuItem = "";
            link = "";

            menulist = anyxsdc.getServiceMenusByService().get(sdcsrvc.getUnique());
            if (menulist!=null) {
                for (int w=0; w<menulist.size(); w++) {
                    sdcmenu = menulist.get(w);
                    if (sdcmenu.getIndex().equals("0") && sdcmenu.getLanguageCode().equals(langCode)) { menuItem = sdcmenu.getName(); }
                }
            }

            if (menuItem==null || menuItem.equals("")) menuItem = sdcsrvc.getName();

            if (!sdcsrvc.getDefaultPage().equals("")) {
                link = "/" + sdcsrvc.getDirectory() + "/" + sdcsrvc.getDefaultPage()  + "?lang=" + langCode;
                if (!sdcsrvc.getDefParameters().equals("")) link += "&" + sdcsrvc.getDefParameters();
            }
            
            sCount++;
            if (sCount==1) out.println("<ul class='sub-menu'>");
%>            
        <li class="nav-item">
            <a class="nav-link nav-toggle" data-url="<%=link%>"> 
                <span class="title"><%=menuItem%></span>
                <span class="arrow"></span>
            </a>
            
<%

        int curlevel = -1;
        int newlevel = -1;
        int ind = 0;

        if (menulist!=null) {
            for (int w=0; w<menulist.size(); w++) {
                sdcmenu = menulist.get(w);

                if (sdcmenu.getIndex().equals("0") || !sdcmenu.getLanguageCode().equals(langCode)) continue;

                link = "/" + sdcsrvc.getDirectory() + "/" + sdcmenu.getTargetPage() + "?lang=" + langCode;

                ind = 0; newlevel = 0;
                while(true) {
                    ind = sdcmenu.getIndex().indexOf(".",ind);
                    if (ind==-1) break;
                    ind++; newlevel++;
                }

                //System.out.println("newlevel: " + newlevel + ", curlevel: " + curlevel + ": " + sdcmenu.getName());

                if (newlevel==curlevel) out.println("</li>");
                else if (newlevel>curlevel) out.println("<ul class='sub-menu'>");
                else if (newlevel<curlevel) out.println("</li></ul></li>");
                //System.out.println("cur: " + curMenus[p][3] + ":"  + menuItem + ": " + curlevel + ": new: " + newlevel);
                curlevel=newlevel;

%>
                <li class="nav-item  ">
                    <a class="nav-link nav-toggle" data-url="<%=link%>">
                        <span class="title"><%=sdcmenu.getName()%></span>
                    </a>
  
        <%  }
          }  %><!-- menulist for-loop-->
        
        <% if (newlevel>-1) { for (int p=0; p<newlevel+1; p++) out.println("</li></ul>"); } %>
        </li>
    <%}%><!-- services for-loop -->
    <% if (sCount>0) out.println("</ul>"); %>
        </li>
<%}%> <!-- cat for-loop -->  
</li>

</ul>

<script type="text/javascript">
$( document ).ready(function() {

    function loadPageEmbed(pageUrl) {
        var myHtml = '<object data="'+ pageUrl +'">' 
                +'  <embed src="'+ pageUrl + '"></embed>' 
                +'    Error: Embedded data could not be displayed.'
                +'    </object>'
     $('body').find('.contentHolder').html(myHtml);
    //$('.contentHolder embed').attr("src", pageUrl).hide().show();
    }

    function loadPageIframe(pageUrl) {http://www.w3schools.com
        var myHtml = '<iframe width="100%" height="1500px;" style="border: 0;" src="'+ pageUrl +'"></iframe>';
              
        $('body').find('.contentHolder').html(myHtml);
    //$('.contentHolder embed').attr("src", pageUrl).hide().show();
    }

    function loadPageJQ(pageUrl) {
        $('.contentHolder').load(pageUrl);
    }

    $(".nav-item a").on("click", function() {
        var url = $(this).data("url");
        console.log(url);

        if (url.indexOf("wapsiteadmin") >= 0) {
            loadPageIframe(url);
            console.log('1');
            stop = 1;
        } else if (url.indexOf("clubadmin") >= 0 || url.indexOf("domains") >= 0) {
            var loadJQ = true;
        } else {
            var loadJQ = false;
        }

        if (url.length > 0  ) {
            if (stop == 1) {
                console.log('return')
                return;
            } else if (loadJQ) {
                loadPageJQ(url);
            } else {
                loadPageEmbed(url);
            }
        }
    });
});
</script>
