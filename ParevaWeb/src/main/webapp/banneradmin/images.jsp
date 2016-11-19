<%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
<%@ include file="/WEB-INF/jspf/db.jspf" %>

<%
//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String ugid = aReq.getUserGroup();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";
response.setContentType("text/html; charset=" + pageEnc);

String fileName = "images";


BannerAdDao banneraddao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      banneraddao=(BannerAdDao) ac.getBean("banneraddao");
     
      }
      catch(Exception e){
          e.printStackTrace();
      }


//***************************************************************************************************
//String imageDir = System.getProperty("document_root");
String imageDir = System.getProperty("contenturl")+"/www";///var/www";

String imgFormat = "gif";
String imgPath = "/images/ads/";

//Connection con = DBHStatic.getConnection();
//ResultSet rs = null;
Transaction trans=dbsession.beginTransaction();
Query query=null;
String sqlstr = "";
String statusMsg = "";
String statusMsg1 = "";
String bannerHit="";
String bn="";
String imgType = aReq.get("itype");
String fileType = aReq.get("ftype");
String unique = aReq.get("unq");
String restype = aReq.get("restype");
String batype=aReq.get("batype");
String add = aReq.get("add");
String delpic = aReq.get("delpic");
String save = aReq.get("save"); 
String resample = aReq.get("resample"); 
String repl = aReq.get("repl", "1");
String neww = aReq.get("neww");
String newh = aReq.get("newh");

java.util.List xhtml = (java.util.List) Misc.cxt.getAttribute("xhtmlProfiles");

System.out.println(imgType+"imagetype");
 
String contentType = request.getHeader("Content-Type");
if (contentType==null) contentType="";

if (contentType.startsWith("multipart/form-data")) {
    
	String boundary = "";
	try { boundary = contentType.substring((contentType.indexOf("boundary=")+9)); }
	catch (IndexOutOfBoundsException e) {}

	int length = 0;
	try { length = Integer.parseInt(request.getHeader("Content-Length")); }
	catch (NumberFormatException e) {}

	if (!boundary.equals("") && length>0) {
            HashMap map = FileUtil.parseMultiPartFormData(length, boundary,
                    new BufferedLineReader(new InputStreamReader(request.getInputStream(), "iso-8859-1"), 1024));

            unique = (String) map.get("unq");
            imgType = (String) map.get("itype");
            restype = (String) map.get("restype");
            batype=(String) map.get("batype");
            System.out.println(batype+"  batype=");
            if (restype==null) restype = "";
            String[] props2 = (String[]) map.get("img");
            String[] props3 = (String[]) map.get("replimg");
            
            System.out.println(props2);
            System.out.println(props3);
            //System.out.println(props2[0]);
            //System.out.println(props2[2]);            
            //System.out.println("3: " + props3[0]);
            //System.out.println("4: " + props3[2]);

            if (props2!=null && !props2[0].equals("") && !props2[2].equals("")) {
                
                String fName = Misc.generateUniqueId();
                props2[2] = props2[2].toLowerCase();

                if (props2[2].endsWith(".gif")) fName += ".gif";
                else if (props2[2].endsWith(".png")) fName += ".png";
                else fName += ".jpg";

                FileUtil.writeRawToFile(imageDir + imgPath + fName, props2[0], false);
                
                int index = 1;                     
                sqlstr = "SELECT * FROM itemImages WHERE aItemUnique='" + unique + "' AND aType='" + imgType + batype+"' ORDER BY aIndex DESC LIMIT 0,1";
                System.out.println(sqlstr);
                query=dbsession.createSQLQuery(sqlstr).addScalar("aIndex");
                java.util.List imageList=query.list();
               // rs = DBHStatic.getRs(con, sqlstr);
               if(imageList.size()>0){
            	   for(Object o:imageList) {
            		    index=Integer.parseInt(o.toString())+1;
            		    
            		}
               }
               // if (rs.next()) { index = rs.getInt("aIndex")+1; }
               // rs.close();             
                
               System.out.println("BANNER IMAGE UPLOAD IN "+imageDir+"  "+imgPath+"  "+fName);
                File org_upload = new File(imageDir + imgPath + fName);
                BufferedImage srcimg = ImageIO.read(org_upload);
                BufferedImage newimg = null;
                int iw = srcimg.getWidth(null);
                int ih = srcimg.getHeight(null);

                System.out.println("srcimg.getWidth(null)"+srcimg.getWidth(null));
                System.out.println("srcimg.getHeight(null)"+srcimg.getHeight(null));

                String imageGroup = Misc.generateUniqueId();
                
                ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*
                if (iw>=50 && iw<=170 && ih<=300 && ih>=199) {
                    
                   bannerHit="verticalbanner";
                    //vertical banner

        System.out.println(bannerHit+"bannerHitbannerHitbannerHitbannerHit");
                   iw=120;
                   ih=240;

                } else if (iw>=400 && iw<=600 && ih<=100 && ih>=30){
//Full banner
                    bannerHit="fullbanner";
                    iw=468;
                    ih=60;

                } else if (iw>=100 && iw<=230 && ih<=1000 && ih>=450){
//vertical tower
                    bannerHit="verticaltower";
                    iw=160;
                    ih=600;

                } else if (iw>=180 && iw<=320 && ih<=150 && ih>=30){
//halfbanner
                    bannerHit="halfbanner";
                    iw=234;
                    ih=60;

                }

                
                if (bannerHit.equals("")&&!imgType.equals("mob") ){

                    statusMsg1="Image not uploaded, because it does not match the selected banner type  "+batype.toUpperCase();
                    bn="no";
                    //imgType="rrr";

                    try { Misc.cxt.getRequestDispatcher("/banneradmin/images.jsp?itype=web&_curitem=1&unq="+unique).forward(request,response); }
                    catch (Exception e) { System.out.println(e); }
                }
                else if (!bannerHit.equals(batype) && !imgType.equals("mob") ){

                    statusMsg1="Image not uploaded, because it does not match the selected banner type "+batype.toUpperCase();
                    //imgType="rrr";
                    bn="no";
                    try { Misc.cxt.getRequestDispatcher("/banneradmin/images.jsp?itype=web&_curitem=1&unq="+unique).forward(request,response); }
                    catch (Exception e) { System.out.println(e); }

                }
         */  
       ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
       
                if (imgType.equals("web") && !bn.equals("no")) {

                    org_upload.delete();
                    fName = Misc.generateUniqueId() + "." + imgFormat;
                       
                    ImageResize.resample(srcimg, imageDir + imgPath + fName, iw, ih) ;  
                    newimg = ImageIO.read(new File(imageDir + imgPath + fName));
                    iw = newimg.getWidth(null);
                    ih = newimg.getHeight(null);

                    sqlstr = "INSERT INTO itemImages VALUES('" + Misc.generateUniqueId() + "','" + unique + "','" + imgType +batype+ "','" + index + "'"
                             + ",'" + Misc.makeSqlSafe(imgPath + fName) + "','" + MiscDate.now24sql() + "'"
                            + ",'1"  + "','" + imageGroup + "','" + iw + "','" + ih + "')";
                    System.out.println(sqlstr);
                    query=dbsession.createSQLQuery(sqlstr);
                    query.executeUpdate();
                    //DBHStatic.execUpdate(con, sqlstr);

                }
                else if(imgType.equals("mob")) {
                
                    int k = 4;
                    org_upload.delete();                     

                    int sizeIndex = 3;
                    if (restype.equals("full")) sizeIndex = 3;
                    else if (restype.equals("shot")) sizeIndex = 4;
                    else if (restype.equals("thumb")) sizeIndex = 5;
                    else if (restype.equals("icon")) sizeIndex = 6;

                    for (int i=k; i>0; i--) {              
                        fName = Misc.generateUniqueId() + "." + imgFormat;
                        int ww =(((Integer[])SdkTempCache.xhtmlProfiles.get(i-1))[sizeIndex]).intValue();;
                        ImageResize.resample(srcimg, imageDir + imgPath + fName, ww, -1);
                              
                        newimg = ImageIO.read(new File(imageDir + imgPath + fName));
                        iw = newimg.getWidth(null);
                        ih = newimg.getHeight(null);

                        sqlstr = "INSERT INTO itemImages VALUES('" + Misc.generateUniqueId() + "','" + unique + "','" + imgType + "image','" + index + "'"
                             + ",'" + Misc.makeSqlSafe(imgPath + fName) + "','" + MiscDate.now24sql() + "'"
                            + ",'" + i + "','" + imageGroup + "','" + iw + "','" + ih + "')";
                        System.out.println(sqlstr);
                        query=dbsession.createSQLQuery(sqlstr);
                        query.executeUpdate();
                        //DBHStatic.execUpdate(con, sqlstr); 
                    }
                }
                
            }
            else if (props3!=null && !props3[0].equals("") && !props3[2].equals("")) {
                
                String imgunq = (String) map.get("imgunq");
                
                System.out.println("IMG: " + imgunq);
                
                if (!imgunq.equals("")) {
                    
                    String fName = Misc.generateUniqueId();
                    props3[2] = props3[2].toLowerCase();
                    
                    if (props3[2].endsWith(".gif")) fName += ".gif";
                    else if (props3[2].endsWith(".png")) fName += ".png";
                    else fName += ".jpg";              
                    
                    FileUtil.writeRawToFile(imageDir + imgPath + fName, props3[0], false);
                    
                    sqlstr = "SELECT * FROM itemImages WHERE aUnique='" + imgunq + "'";
                    System.out.println(sqlstr);
                    
                    query=dbsession.createSQLQuery(sqlstr).addScalar("aPath");
                    java.util.List imageList=query.list();
                   // rs = DBHStatic.getRs(con, sqlstr);
                   if(imageList.size()>0){
                	   for(Object o:imageList) {
                		   File ff = new File(imageDir + o.toString());
                           System.out.println(ff);
                           if (ff.exists() && !ff.isDirectory()) ff.delete();
                		    
                		}
                   }
                    
                   // rs = DBHStatic.getRs(con, sqlstr);
                   // if (rs.next()) {
                    //    File ff = new File(imageDir + rs.getString("aPath"));
                     //   System.out.println(ff);
                      //  if (ff.exists() && !ff.isDirectory()) ff.delete();
                   // }
                   // rs.close();
                    
                    BufferedImage srcimg = ImageIO.read(new File(imageDir + imgPath + fName));
                    int iw = srcimg.getWidth(null);
                    int ih = srcimg.getHeight(null);
                    
                    sqlstr = "UPDATE itemImages SET aPath='" + imgPath + fName + "', aWidth='" + iw + "',aHeight='" + ih + "' WHERE aUnique='" + imgunq + "'";
                    System.out.println(sqlstr);
                    query=dbsession.createSQLQuery(sqlstr);
                    query.executeUpdate();
                    //DBHStatic.execUpdate(con, sqlstr);                
                }
                
            }
            
       }
}
else if (!resample.equals("")) {

    System.out.println("Inside Resample");
    
    String pic = Misc.hex8Decode(aReq.get("pic"));
    
    if (!pic.equals("")) {
        
        if (neww.equals("")) neww = "-1";
        if (newh.equals("")) newh = "-1";
        int w = -1;
        int h = -1;    
        
        try { w = Integer.parseInt(neww); } catch (NumberFormatException e) {}
        try { h = Integer.parseInt(newh); } catch (NumberFormatException e) {}

        System.out.println(w + ": " + h);

        if (w>0 || h>0) {
            try {                
                String ii = Misc.generateUniqueId() + "." + fileType;                    
                String newPath = imageDir + imgPath + ii;
                
                File org = new File(imageDir + pic);
                ImageResize.resample(org, newPath, w, h);
                
                BufferedImage srcimg = ImageIO.read(new File(newPath));
                int iw = srcimg.getWidth(null);
                int ih = srcimg.getHeight(null);
                
                if (!repl.equals("1")) {
                    int index = 1;                     
                    sqlstr = "SELECT * FROM itemImages WHERE aItemUnique='" + unique + "' ORDER BY aIndex DESC LIMIT 0,1";
                    System.out.println(sqlstr);
                    
                    query=dbsession.createSQLQuery(sqlstr).addScalar("aIndex");
                    java.util.List imageList=query.list();
                   // rs = DBHStatic.getRs(con, sqlstr);
                   if(imageList.size()>0){
                	   for(Object o:imageList) {
                		    index=Integer.parseInt(o.toString())+1;;
                		    
                		}
                   }
                    
                  //  rs = DBHStatic.getRs(con, sqlstr);
                   // if (rs.next()) { index = rs.getInt("aIndex")+1; }
                   // rs.close();      

                    sqlstr = "INSERT INTO itemImages VALUES('" + Misc.generateUniqueId() + "','" + unique + "','" + imgType + "image','" + index + "','" + Misc.makeSqlSafe("/images/javagames/" + ii) + "','" + MiscDate.now24sql() + "')";
                    query=dbsession.createSQLQuery(sqlstr);
                    query.executeUpdate();
                    //DBHStatic.execUpdate(con, sqlstr);   
                } 
                else { 
                     sqlstr = "UPDATE itemImages SET aPath='" + imgPath + ii + "', aWidth='" + iw + "', aHeight='" + ih + "' WHERE aPath='" + pic + "'";
                     System.out.println("11: " + sqlstr);
                     query=dbsession.createSQLQuery(sqlstr);
                     query.executeUpdate();
                     
                     //DBHStatic.execUpdate(con, sqlstr);
                     org.delete();
                }
            } catch (Exception e) {}
        }
        
    }
}
else if (!save.equals("")) {
    Enumeration e = request.getParameterNames();
    for(;e.hasMoreElements();) {
        String elem = (String) e.nextElement();
        if (elem.startsWith("ind_")) {
            sqlstr = "UPDATE itemImages SET aIndex='" + aReq.get(elem) + "' WHERE aImageGroup='" + elem.substring(4) + "'";
            query=dbsession.createSQLQuery(sqlstr);
            query.executeUpdate();
            //DBHStatic.execUpdate(con, sqlstr);
        }
        else if (elem.startsWith("type_")) {
            sqlstr = "UPDATE itemImages SET aType='" + aReq.get(elem) + "' WHERE aImageGroup='" + elem.substring(5) + "'";
            query=dbsession.createSQLQuery(sqlstr);
            query.executeUpdate();           
            //DBHStatic.execUpdate(con, sqlstr);
        }
    }

    statusMsg = "Information saved";
   
}
else if (!delpic.equals("")) {
    
    File ff = null;
    String runq = aReq.get("runq");        
    
    if (!runq.equals("")) {
        ff = new File(imageDir + Misc.hex8Decode(delpic));
        if (ff.exists() && !ff.isDirectory()) ff.delete();
        sqlstr = "UPDATE itemImages SET aPath='',aWidth='0',aHeight='0' WHERE aUnique='" + runq + "'";
        query=dbsession.createSQLQuery(sqlstr);
        query.executeUpdate();
        
        //DBHStatic.execUpdate(con, sqlstr);
    }
    else {
    
        sqlstr = "SELECT * FROM itemImages WHERE aImageGroup='" + delpic + "'";
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aPath");
        java.util.List imageList=query.list();
       // rs = DBHStatic.getRs(con, sqlstr);
       if(imageList.size()>0){
    	   for(Object o:imageList) {
    		   ff = new File(imageDir + o.toString());
               System.out.println(ff);
               if (ff.exists() && !ff.isDirectory()) ff.delete();
    		    
    		}
       }
        
        
     //   rs = DBHStatic.getRs(con, sqlstr);
      //  while (rs.next()) {
       //     ff = new File(imageDir + rs.getString("aPath"));
       //     if (ff.exists() && !ff.isDirectory()) ff.delete();
       // }
       // rs.close();

        sqlstr = "DELETE FROM itemImages WHERE aImageGroup='" + delpic + "'";
        System.out.println(sqlstr);
        query=dbsession.createSQLQuery(sqlstr);
        query.executeUpdate();
        //DBHStatic.execUpdate(con, sqlstr);
   }
            
}

//BannerAd banner = CmsDaoFactory.getBannerAdDao().getBanner(unique, con);
BannerAd banner = banneraddao.getBanner(unique);
Iterator iterator = banner.getImageMap().keySet().iterator();
java.util.List imageGroups = null;
java.util.List images = null;
ItemImageGroup img = null;
ItemImage im = null;

String tabIndex = "1";
if (imgType.equals("mob")) tabIndex = "2";

trans.commit();
dbsession.close();
//DBHStatic.closeConnection(con);
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
<title>UME-Admin</title>
<script language="JavaScript">
function win(urlPath) {
    var winl = (screen.width-200)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    delWin = window.open(urlPath,'mmsdel',settings);
    delWin.focus();
}

function win2(urlPath) {
    var winl = (screen.width-400)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=400,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    uplWin = window.open(urlPath,'uplwin',settings);
    uplWin.focus();
}
</script>
</head>
<body>

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Banner Images:</b> <span class="grey_12"><b><%=banner.getTitle()%></b></span></td>
        <td align="right" valign="bottom" class="status_red">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", tabIndex)); %>
    <%@ include file="/banneradmin/tabs.jsp" %>
    <br>
</td></tr>
   
<tr>
<td valign="top" align="left">   

<form enctype="multipart/form-data" action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="itype" value="<%=imgType%>">

    <table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr bgcolor="#FFFFFF">
           <td align="left" class="grey_12">
           Upload new resource:
           </td>
           <% if (!imgType.equals("web")) { %>
           <td align="center" class="grey_11">
               Resample uploaded image to: &nbsp;
               <select name="restype">
                    <option value="full" <%if (restype.equals("full")){%> selected <%}%>>Full width</option>
                    <option value="shot" <%if (restype.equals("shot")){%> selected <%}%>>Screenshot width</option>
                    <option value="thumb" <%if (restype.equals("thumb")){%> selected <%}%>>Thumbnail width</option>
                    <option value="icon" <%if (restype.equals("icon")){%> selected <%}%>>Icon width</option>
                </select>
           </td>
           <% } else { %>

           <td align="center" class="grey_11">
               Select Banner type: &nbsp;
               <select name="batype">
                    <option value="fullbanner" <%if (batype.equals("fullbanner")){%> selected <%}%>>Full Banner (468x60)</option>
                    <option value="halfbanner" <%if (batype.equals("halfbanner")){%> selected <%}%>>Half Banner (234X60)</option>
                    <option value="verticalbanner" <%if (batype.equals("verticalbanner")){%> selected <%}%>>Vertical Banner (120X240)</option>
                    <option value="verticaltower" <%if (batype.equals("verticaltower")){%> selected <%}%>>Vertical Tower (160X600)</option>
                </select>
           </td>
           <% }%>

           <td align="right">
                 
                <input type="file" size="20" name="img" class="textbox" value="">
                <input type="submit" name="submit" value="Upload">
           </td>
          
        </tr>
  
    
    </table>

</form>
           
</td></tr>


<form action="<%=fileName%>.jsp" method="post">
<input type="hidden" name="unq" value="<%=unique%>">
<input type="hidden" name="itype" value="<%=imgType%>">
    
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td>


    <table cellspacing="0" cellpadding="4" border="0" width="100%">
	<tr bgcolor="#FFFFFF">
           <td align="left" class="grey_12">
           Resample selected image:
           </td>
           <!--
            <td align="left" class="grey_12">
           Replace original: <input type="checkbox" name="repl" value="1" <% if (repl.equals("1")) {%> checked <%}%>>
           </td>
           -->
            <td align="left" class="grey_12">
           <select name="ftype">
               <option value="gif" <% if (fileType.equals("gif")) {%> selected <%}%>>Gif</option>
               <option value="jpg" <% if (fileType.equals("jpg")) {%> selected <%}%>>Jpg</option>
           </select>
           </td>
            <td align="left" class="grey_12"><nobr>
           Width: <input type="text" name="neww" size="4" value="<%=neww%>">
           &nbsp;&nbsp; 
           Height: <input type="text" name="newh" size="4" value="<%=newh%>">
            </nobr>
            </td>
            <td align="left" class="grey_10">
            Leave the other dimension<br>empty to constrain proportions
           </td>
           <td align="right">                
                <input type="submit" name="resample" value="Resample">
           </td>
        </tr>
        
    </table>

</td></tr>

<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>
<tr><td align="right"  class="status" ><b > <%=statusMsg1%> </b></td></tr>
<tr><td>
<br>
<%
String type = "";
String key = "";


while (iterator.hasNext()) {
    key = (String) iterator.next();
    if (!key.startsWith(imgType)) continue;

    imageGroups = (java.util.List) banner.getImageMap().get(key);
    if (imageGroups==null) imageGroups = new ArrayList();
    
    for (int k=0; k<imageGroups.size(); k++) {
        img = (ItemImageGroup) imageGroups.get(k);
        images = (java.util.List) img.getImages();              

%>
        <table cellpadding="2" cellspacing="0" border="1" width="100%" bgcolor="#ffffff">
         <tr bgcolor="#EEEEEE"><td class="blue_11" align="left" colspan="2">
            <table cellspacing="0" cellpadding="2" border="0" width="100%">     
            <tr bgcolor="#DDDDDD">

                   <% if (!imgType.equals("web")) { %>
             <td class="blue_12">
            &nbsp;&nbsp;Index:
              <select name="ind_<%=img.getUnique()%>">
              <%
                 for (int p=1; p<11; p++) {
                %>
              <option value="<%=p%>" <%if (img.getIndex()==p){%> selected <%}%>><%=p%>&nbsp;</option>
                <% } %>
              </select>
            &nbsp;&nbsp;&nbsp;&nbsp;Type:
              <select name="type_<%=img.getUnique()%>">
                <option value="<%=imgType%>image" <%if (key.equals(imgType + "image")){%> selected <%}%>>Image</option>
                <option value="<%=imgType%>icon" <%if (key.equals(imgType + "icon")){%> selected <%}%>>Icon</option>
              </select>
            </td>

                   <%} else{


String wim=img.getType().substring(3).toUpperCase().trim();


%>
<tr bgcolor="#DDDDDD"><td class="blue_12">
          
  &nbsp;&nbsp;&nbsp;Banner Type:
    
    &nbsp;  <%= wim%>
                                   
                   <% }%>

            
              
            </td>
            <td align="right"><a href="javascript:win('del.jsp?igroup=<%=img.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>')"><span class="blue_12">Delete Images</span></a>&nbsp;</td>
            </tr>
            </table>
         </td>
         </tr>  
        <tr bgcolor="#ffffff" class="grey_11">	
         
<%	
	for (int i=0; i<images.size(); i++) {
           im = (ItemImage) images.get(i);         
           
           System.out.println("IMAGES PATH "+im.getPath());
           if (im.getPath().toLowerCase().endsWith("gif")) type = "GIF";
           else if (im.getPath().toLowerCase().endsWith("png")) type = "PNG";
           else type = "JPG";
           
           if (i==2) out.println("</tr><tr>");
        %>		
		
            <td align="center" valign="top" class="grey_11">
                <table cellpadding="4" cellspacing="0" border="0" width="100%"  >
                <tr>
                <td align="center" valign="top" class="grey_11" bgcolor="#EEEEEE">
                    <b>Profile: <%=im.getProfile()%></b>
                </td>
                </tr>
                <tr>
                <td align="center" valign="top" class="grey_11">        
                  <br>
              <% if (!im.getPath().equals("")) { %>
                  <%=im.getWidth()%>x<%=im.getHeight()%>, <%=type%><br>
                  <a href="upload.jsp?imgunq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>&batype=<%=im.getType()%>"><img src="<%=im.getPath()%>" border="0"></a>
                  <br>
                  Select: <input type="radio" name="pic" value="<%=Misc.hex8Code(im.getPath())%>">
                  &nbsp;&nbsp;
                  <a href="javascript:win('del.jsp?picid=<%=Misc.hex8Code(im.getPath())%>&runq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>')">Delete</a>
              <% } else { %>
                <a href="upload.jsp?imgunq=<%=im.getUnique()%>&unq=<%=unique%>&itype=<%=imgType%>&batype=<%=im.getType()%>">[Add Image]</a>
              <% } %>
                </tr>
                </table>
            </td>
<%
} 
%>
	
        </tr>
</table>
<br>
<% }
 }   
%>
</td></tr>

<tr><td align="right"><br><input type="submit" name="save" value="&nbsp;&nbsp;Save&nbsp;&nbsp;"></td></tr>

</form>

</table>


</body>
</html>


