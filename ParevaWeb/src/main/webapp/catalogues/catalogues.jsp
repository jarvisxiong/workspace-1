 <%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
  <%@ include file="/WEB-INF/jspf/db.jspf" %><%
  
  
		  ClientDao clientdao=null;
  UmeUserGroupDao umeusergroupdao=null;
  UmeLanguagePropertyDao langpropdao=null;
Misc misc=null;
   try{
        ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
        ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
        langpropdao=(UmeLanguagePropertyDao) ac.getBean("umelanguagepropertydao");
        clientdao=(ClientDao) ac.getBean("clientdao");
        misc=(Misc) ac.getBean("misc");
        umeusergroupdao=(UmeUserGroupDao) ac.getBean("umeusergroupdao");
       
        }
        catch(Exception e){
            e.printStackTrace();
        }

//***************************************************************************************************
SdcRequest aReq = new SdcRequest(request);
UmeUser umeuser = aReq.getUser();
UmeDomain dmn = aReq.getDomain();
SdcService service = aReq.getService();

String domain = dmn.getUnique();
String langcode = aReq.getLanguage().getLanguageCode();

String stylesheet = aReq.getStylesheet();
String pageEnc = aReq.getEncoding();
response.setContentType("text/html; charset=" + pageEnc);

String fileName = request.getServletPath();
fileName = fileName.substring(fileName.lastIndexOf("/")+1);
fileName = fileName.substring(0,fileName.lastIndexOf("."));
SdcLanguageProperty lp = langpropdao.get(fileName, service, aReq.getLanguage(), dmn);
//***************************************************************************************************


String imageDir = System.getProperty("document_root") + "/images/javagames";
String durl = System.getProperty(domain + "_url");

String ugid = umeuser.getUserGroup();

Query query=null;
Transaction trans=dbsession.beginTransaction();
String sqlstr = "";
String[] props = null;
String statusMsg = "";
java.util.List list = new ArrayList();
Hashtable ccont = new Hashtable();
String elem = "";
String value = "";
String dateTitle = "Authorized:";
String iTable = "";
Client ug = null;

int pageCount = 0;
int maxCount = 20;
int count = 0;
boolean error = false;

SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


String authSel = aReq.get("authsel");
String unauthSel = aReq.get("unauthsel");
String authAll = aReq.get("authall");
String unauthAll = aReq.get("unauthall");
String client = aReq.get("cunq");
String listType = aReq.get("ltype", "unauth");
String cType = aReq.get("ctype", "master");
String delete = aReq.get("del");
String save = aReq.get("save");
int sIndex = Integer.parseInt(aReq.get("si", "0"));

String after = aReq.get("after");
String before = aReq.get("before");

if (umeuser.getAdminGroup()!=9) client = ugid;

Hashtable<String,String> cTypeTables = new Hashtable<String,String>();
cTypeTables.put("java", "javaGames");
cTypeTables.put("javaimg", "promoimage");
cTypeTables.put("bg", "bgImages");
cTypeTables.put("video", "videoClips");
cTypeTables.put("videoimg", "mobthumb");
cTypeTables.put("master", "masterTones");
cTypeTables.put("anim", "gifAnims");
cTypeTables.put("true", "trueTones");
cTypeTables.put("poly", "allTones");


if (!authSel.equals("")) {
    Enumeration e = request.getParameterNames();    
    for (;e.hasMoreElements();) {
        elem = (String) e.nextElement();        
        if (elem.startsWith("sel_")) {
            value = aReq.get(elem);
            sqlstr = "UPDATE clientContent SET aModified='" + MiscDate.now24sql() + "',aItemStatus='1' WHERE"
                    + " aItemUnique='" + value + "' AND aClientUnique='" + client + "' AND aContentType='" + cType + "'";
            System.out.println(sqlstr);
            query=dbsession.createSQLQuery(sqlstr);
            if (query.executeUpdate()==0) {
                sqlstr = "INSERT INTO clientContent VALUES('" + misc.generateUniqueId() + "','" + client + "','" + value + "','" + cType + "'"
                        + ",'" + MiscDate.now24sql() + "','1','" + MiscDate.now24sql() + "')";
                System.out.println(sqlstr);
                query=dbsession.createSQLQuery(sqlstr);
                query.executeUpdate();
            }
        }
    }    
}
else if (!authAll.equals("")) {    
    sqlstr = "SELECT * FROM " + cTypeTables.get(cType) + " WHERE aStatus='1'";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique");
    java.util.List cTypeTableList=query.list();
   
    for(Object cTypes:cTypeTableList){
    
        value = cTypes.toString();
        sqlstr = "UPDATE clientContent SET aModified='" + MiscDate.now24sql() + "',aItemStatus='1' WHERE"
                + " aItemUnique='" + value + "' AND aClientUnique='" + client + "' AND aContentType='" + cType + "'";
        System.out.println(sqlstr);
        query=dbsession.createSQLQuery(sqlstr);
        if (query.executeUpdate()==0) {
            sqlstr = "INSERT INTO clientContent VALUES('" + misc.generateUniqueId() + "','" + client + "','" + value + "','" + cType + "'"
                    + ",'" + MiscDate.now24sql() + "','1','" + MiscDate.now24sql() + "')";
            System.out.println(sqlstr);
            query=dbsession.createSQLQuery(sqlstr);
            query.executeUpdate();
       
        }
    } 
         
}
else if (!unauthSel.equals("")) {
    Enumeration e = request.getParameterNames();    
    String pUnique = "";
    for (;e.hasMoreElements();) {
        elem = (String) e.nextElement();             
        if (elem.startsWith("sel_")) {
            value = aReq.get(elem);
            sqlstr = "UPDATE clientContent SET aItemStatus='0', aModified='" + MiscDate.now24sql() + "' WHERE aItemUnique='" + value + "' AND aClientUnique='" + client + "'"
                    + " AND aContentType='" + cType + "'";
            System.out.println(sqlstr);
            query=dbsession.createSQLQuery(sqlstr);
            query.executeUpdate();
            
        }
    }    
}
else if (!unauthAll.equals("")) {
    String pUnique = "";
    
    sqlstr = "UPDATE clientContent SET aItemStatus='0', aModified='" + MiscDate.now24sql() + "' WHERE aClientUnique='" + client + "'"
            + " AND aContentType='" + cType + "'";
    System.out.println(sqlstr);
    query=dbsession.createSQLQuery(sqlstr);
    query.executeUpdate();
       
}


// Item lists start
if ((listType.equals("auth") || listType.equals("unauth")) && !client.equals("")) { 
    
    if (listType.equals("unauth")) {
        
        String junq = "";
        
        String sqlstr2 = "SELECT * FROM clientContent WHERE aContentType='" + cType + "' AND aClientUnique='" + client + "'";
        System.out.println(sqlstr2);
        query=dbsession.createSQLQuery(sqlstr2).addScalar("aItemUnique").addScalar("aUnique");
        java.util.List clientContentList=query.list();
        
        for(Object clientContent:clientContentList) {
      
    	   Object [] row=(Object[]) clientContent;
    	   junq = String.valueOf(row[0]);
                      
            if (ccont.get(junq)!=null) {
                sqlstr = "DELETE FROM clientContent WHERE aUnique='" + String.valueOf(row[1]) + "'";
                System.out.println(sqlstr);
                query=dbsession.createSQLQuery(sqlstr);
                query.executeUpdate();
            }
            else ccont.put(String.valueOf(row[0]), String.valueOf(row[1]));
        }
         

        sqlstr2 = "SELECT * FROM " + cTypeTables.get(cType);

        
        System.out.println(sqlstr2);
        query=dbsession.createSQLQuery(sqlstr2).addScalar("aUnique");
        java.util.List cTypeTableList=query.list();
        for(Object c:cTypeTableList) {
          
            junq = c.toString();
            if (ccont.get(junq)==null) {
                sqlstr2 = "INSERT INTO clientContent VALUES('" + misc.generateUniqueId() + "','" + client + "','" + junq + "','" + cType + "'"
                        + ",'" + MiscDate.now24sql() + "','0','" + MiscDate.now24sql() + "')";
                System.out.println(sqlstr2);
                query=dbsession.createSQLQuery(sqlstr2);
                query.executeUpdate();
            }
        }
    
        
    }
    
    sqlstr = "SELECT * FROM clientContent INNER JOIN " + cTypeTables.get(cType) + " ON " + cTypeTables.get(cType) + ".aUnique=aItemUnique"
                + " WHERE aContentType='" + cType + "' AND aClientUnique='" + client + "'";
        
    if (listType.equals("auth")) {
        sqlstr += " AND aItemStatus='1'";
        if (!after.equals("")) { sqlstr += " AND aModified>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND aModified<'" + before + "'"; }

        sqlstr += " AND " + cTypeTables.get(cType) + ".aStatus='1'"
                + " ORDER BY aModified DESC LIMIT " + sIndex + "," + maxCount;
    }
    else {
        dateTitle = "Item added:";
        sqlstr += " AND aItemStatus='0'";
        if (!after.equals("")) { sqlstr += " AND " + cTypeTables.get(cType) + ".aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND " + cTypeTables.get(cType) + ".aCreated<'" + before + "'"; }

        sqlstr += " AND " + cTypeTables.get(cType) + ".aStatus='1'"
                + " ORDER BY " + cTypeTables.get(cType) + ".aCreated DESC LIMIT " + sIndex + "," + maxCount;
    }
    
    System.out.println(sqlstr);
    
    if (cType.equals("java")) { 
    	 query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aTitle").addScalar("aOwner").addScalar("aPath").addScalar("aModified").addScalar("javaGames.aCreated");
         java.util.List javaList=query.list();
         for(Object j:javaList) {                          
        
        	Object [] row=(Object[]) j;
            props = new String[6];
            props[0] = String.valueOf(row[0]);
            props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
            props[2] = String.valueOf(row[2]);
            props[4] = String.valueOf(row[4]);
            props[5] = String.valueOf(row[5]);
            
            sqlstr = "SELECT * FROM itemImages WHERE aItemUnique='" + props[0] + "' AND aType='promoimage' AND aProfile='1'";
           
            query=dbsession.createSQLQuery(sqlstr);
            java.util.List imageList=query.list();
            if (imageList.size()>0) { props[3] = String.valueOf(row[3]); }
           
            
            list.add(props);
        }
      
    }
    else if (cType.equals("bg")) {
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aName2").addScalar("aOwner").addScalar("aModified").addScalar("bgImages.aCreated");
         java.util.List bgList=query.list();
         for(Object b:bgList) {                          
         
        	Object [] row=(Object[]) b;
            props = new String[6];
            props[0] = String.valueOf(row[0]);
            props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
            props[2] = String.valueOf(row[2]);
            props[3] = "/images/bgs2/40/" + props[0] + ".gif";
            props[4] = String.valueOf(row[3]);
            props[5] = String.valueOf(row[4]);
            list.add(props);
        }
        
        
    }
    else if (cType.equals("video")) { 
                      
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aTitle").addScalar("aOwner").addScalar("aModified").addScalar("videoClips.aCreated");
        java.util.List videoList=query.list();
        for(Object v:videoList) {                          
      
       	Object [] row=(Object[]) v;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
           props[2] = String.valueOf(row[2]);
           props[4] = String.valueOf(row[3]);
           props[5] = String.valueOf(row[4]);
           
           sqlstr = "SELECT * FROM itemImages WHERE aItemUnique='" + props[0] + "' AND aType='mobthumb' AND aProfile='2'";
           query=dbsession.createSQLQuery(sqlstr).addScalar("aPath");
           java.util.List imageList=query.list();
           if (imageList.size()>0) { props[3] = String.valueOf(row[0]); }
           list.add(props);
       }
    }
    else if (cType.equals("master")) {
                      
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("aToneType")
    			.addScalar("aModified").addScalar("masterTones.aCreated").addScalar("masterTones.aPreviewFile");
        java.util.List masterList=query.list();
        for(Object m:masterList) {                          
       
       	Object [] row=(Object[]) m;
           props = new String[7];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = String.valueOf(row[4]);
           props[4] = String.valueOf(row[5]);
           props[5] = String.valueOf(row[6]);
           props[6] = String.valueOf(row[7]);
           
           if (props[3].equals("4")) props[3] = "/images/music_green.png";
           else if (props[3].equals("5")) props[3] = "/images/music_yellow.png";
           else props[3] = "/images/music_blue.png";

           list.add(props);
       }
    	
    }
    else if (cType.equals("anim")) {
        
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aName2").addScalar("aOwner").addScalar("aPicName").addScalar("aModified").addScalar("gifAnims.aCreated");
        java.util.List animList=query.list();
        for(Object a:animList) {                          
      
       	Object [] row=(Object[]) a;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
           props[2] = String.valueOf(row[2]);
           props[3] = "/images/gifanims/80/" + String.valueOf(row[3]);
           props[4] = String.valueOf(row[4]);
           props[5] = String.valueOf(row[5]);
           list.add(props);
       }
    	
        
    }
    else if (cType.equals("true")) { 
                
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("aModified").addScalar("trueTones.aCreated");
        java.util.List trueList=query.list();
        for(Object t:trueList) {                          
      
       	Object [] row=(Object[]) t;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = "";
           props[4] = String.valueOf(row[4]);
           props[5] = String.valueOf(row[5]);
           list.add(props);
       }
    	
        
    }
    else if (cType.equals("poly")) { 
                
    	query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("aModified").addScalar("allTones.aCreated");
        java.util.List polyList=query.list();
        for(Object p:polyList) {                          
       
       	Object [] row=(Object[]) p;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = "";
           props[4] = String.valueOf(row[4]);
           props[5] = String.valueOf(row[5]);
           list.add(props);
       }
    	
    	
        
    }
    
    
    sqlstr = "SELECT count(*) FROM clientContent INNER JOIN " + cTypeTables.get(cType) + " ON " + cTypeTables.get(cType) + ".aUnique=aItemUnique"
                + " WHERE aContentType='" + cType + "' AND aClientUnique='" + client + "'";
            
    if (!sqlstr.equals("")) {
        if (listType.equals("auth")) {
            sqlstr += " AND aItemStatus='1'";
            if (!after.equals("")) { sqlstr += " AND aModified>'" + after + "'"; }
            if (!before.equals("")) { sqlstr += " AND aModified<'" + before + "'"; }
        }
        else {
            sqlstr += " AND aItemStatus='0'";
            if (!after.equals("")) { sqlstr += " AND " + cTypeTables.get(cType) + ".aCreated>'" + after + "'"; }
            if (!before.equals("")) { sqlstr += " AND " + cTypeTables.get(cType) + ".aCreated<'" + before + "'"; }
        }
        
        sqlstr += " AND " + cTypeTables.get(cType) + ".aStatus='1'";
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List contentList=query.list();
        if (contentList.size()>0) {
        	
        	for(Object c:contentList) {
        		count=Integer.parseInt(c.toString());
        	}
        	 
        }
        
        
        System.out.println("COUNT: " + count);
    }
}

else if (listType.equals("all")) {
    
    dateTitle = "Item added:";
    
    if (cType.equals("java") || cType.equals("video")) {
        iTable = cTypeTables.get(cType);
        
        sqlstr = "SELECT * FROM " + iTable + " WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND " + iTable + ".aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND " + iTable + ".aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aTitle").addScalar("aOwner").addScalar("aModified").addScalar(iTable+".aCreated").addScalar("aPath");
        java.util.List polyList=query.list();
        for(Object p:polyList) {                          
       
       	Object [] row=(Object[]) p;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
           props[2] = String.valueOf(row[2]);
           props[4] = "";
           props[5] = String.valueOf(row[4]);
           
           sqlstr = "SELECT * FROM itemImages WHERE aItemUnique='" + props[0] + "' AND aType='" + cTypeTables.get(cType + "img") + "' AND aProfile='1'";
           
           query=dbsession.createSQLQuery(sqlstr);
           java.util.List imageList=query.list();
           if (imageList.size()>0) {
           	
        	   props[3] = String.valueOf(row[5]);
           	 
           }
           
           list.add(props);
       }
        
        
        	

        System.out.println("LIST.SIZE::: " + list.size());
        
        sqlstr = "SELECT count(*) FROM " + iTable + " WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND " + iTable + ".aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND " + iTable + ".aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List cList=query.list();
        if (cList.size()>0) {
        	
        	for(Object c:cList) {
        		count=Integer.parseInt(c.toString());
        	}
        	 
        }
        
        
    }
    else if (cType.equals("bg")) {
        sqlstr = "SELECT * FROM bgImages WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND bgImages.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND bgImages.aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aName2").addScalar("aOwner").addScalar("bgImages.aCreated");
        java.util.List bgList=query.list();
        for(Object b:bgList) {                          
        
       	Object [] row=(Object[]) b;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
           props[2] = String.valueOf(row[2]);
           props[3] = "/images/bgs2/40/" + props[0] + ".gif";
           props[4] = "";
           props[5] = String.valueOf(row[3]);
           list.add(props);
       }
        
        
        
        sqlstr = "SELECT count(*) FROM bgImages WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND bgImages.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND bgImages.aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List bgCount=query.list();
        if (bgCount.size()>0) {
        	
        	for(Object b:bgCount) {
        		count=Integer.parseInt(b.toString());
        	}
        	 
        }
        
        
    }
    else if (cType.equals("master")) {

        sqlstr = "SELECT * FROM masterTones WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND masterTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND masterTones.aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("aToneType")
    			.addScalar("masterTones.aCreated");
        java.util.List masterList=query.list();
        for(Object m:masterList) {                          
       
       	Object [] row=(Object[]) m;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = String.valueOf(row[4]);
           props[4] = "";
           props[5] = String.valueOf(row[5]);
           
           if (props[3].equals("4")) props[3] = "/images/music_green.png";
           else if (props[3].equals("5")) props[3] = "/images/music_yellow.png";
           else props[3] = "/images/music_blue.png";

           list.add(props);
       }
        
        
        
        sqlstr = "SELECT count(*) FROM masterTones WHERE aStatus='1'";

        
        if (!after.equals("")) { sqlstr += " AND masterTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND masterTones.aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List masterCount=query.list();
        if (masterCount.size()>0) {
        	
        	for(Object m:masterCount) {
        		count=Integer.parseInt(m.toString());
        	}
        	 
        }
        
        
    }
    else if (cType.equals("anim")) {
        sqlstr = "SELECT * FROM gifAnims WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND gifAnims.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND gifAnims.aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aName2").addScalar("aOwner").addScalar("aPicName").addScalar("gifAnims.aCreated");
        java.util.List animList=query.list();
        for(Object a:animList) {                          
      
       	Object [] row=(Object[]) a;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1]), pageEnc);
           props[2] = String.valueOf(row[2]);
           props[3] = "/images/gifanims/80/" + String.valueOf(row[3]);
           props[4] = "";
           props[5] = String.valueOf(row[4]);
           list.add(props);
       }
        
        
        
        
        sqlstr = "SELECT count(*) FROM gifAnims WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND gifAnims.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND gifAnims.aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List animCount=query.list();
        if (animCount.size()>0) {
        	
        	for(Object a:animCount) {
        		count=Integer.parseInt(a.toString());
        	}
        	 
        }
        
        
    }    
    else if (cType.equals("true")) {
        sqlstr = "SELECT * FROM trueTones WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND trueTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND trueTones.aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("trueTones.aCreated");
        java.util.List trueList=query.list();
        for(Object t:trueList) {                          
      
       	Object [] row=(Object[]) t;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = "";
           props[4] = "";
           props[5] = String.valueOf(row[4]);
           list.add(props);
       }
        
        
        
        
        sqlstr = "SELECT count(*) FROM trueTones WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND trueTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND trueTones.aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List trueCount=query.list();
        if (trueCount.size()>0) {
        	
        	for(Object t:trueCount) {
        		count=Integer.parseInt(t.toString());
        	}
        	 
        }
        
       
    }
    else if (cType.equals("poly")) {
        sqlstr = "SELECT * FROM allTones WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND allTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND allTones.aCreated<'" + before + "'"; }
                    
        sqlstr += " ORDER BY aCreated DESC LIMIT " + sIndex + "," + maxCount;
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aSongName").addScalar("aArtist").addScalar("aOwner").addScalar("allTones.aCreated");
        java.util.List polyList=query.list();
        for(Object p:polyList) {                          
      
       	Object [] row=(Object[]) p;
           props = new String[6];
           props[0] = String.valueOf(row[0]);
           props[1] = misc.utfToUnicode(String.valueOf(row[1])+ ", " + String.valueOf(row[2]), pageEnc);
           props[2] = String.valueOf(row[3]);
           props[3] = "";
           props[4] = "";
           props[5] = String.valueOf(row[4]);
           list.add(props);
       }
        
       
        
        sqlstr = "SELECT count(*) FROM allTones WHERE aStatus='1'";
        
        if (!after.equals("")) { sqlstr += " AND allTones.aCreated>'" + after + "'"; }
        if (!before.equals("")) { sqlstr += " AND allTones.aCreated<'" + before + "'"; }
        
        query=dbsession.createSQLQuery(sqlstr);
        java.util.List polyCount=query.list();
        if (polyCount.size()>0) {
        	
        	for(Object p:polyCount) {
        		count=Integer.parseInt(p.toString());
        	}
        	 
        }
        
       
    }
    
    
    if (!client.equals("")) {
        sqlstr = "SELECT * FROM clientContent WHERE aContentType='" + cType + "' AND aClientUnique='" + client + "' AND aItemStatus='1'";
        System.out.println(sqlstr);
        
        query=dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aUnique");
        java.util.List contentList=query.list();
        for(Object c:contentList) {                          
       
       	Object [] row=(Object[]) c;
       		ccont.put(String.valueOf(row[0]),String.valueOf(row[1])); 
       }
        
        
    }
}

    
    
    
java.util.List<Client> clients = clientdao.getAllClients("ADC1177659328952","name");

Map<String,UmeUserGroup> owners = umeusergroupdao.getUserGroupMap();


pageCount = count/maxCount;
if ((count%maxCount)>0) pageCount++;

%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
<link rel="stylesheet" href="<%=stylesheet%>" type="text/css"/>
<link rel="stylesheet" href="/lib/previewplay.css" media="screen" />

<script src="/lib/global_anyx.js" language="javascript"></script>
<script type="text/javascript" src="/lib/JavaScriptFlashGateway.js"></script>

<script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

<script type="text/javascript" src="/lib/jquery/jquery.ui/jquery-ui-1.8.2.custom.min.js"></script>
<link rel="stylesheet" href="/lib/jquery/jquery.ui/css/smoothness/jquery-ui-1.8.2.custom.css" media="screen"/>

<script type="text/javascript" src="/lib/jquery/timepicker/jquery-ui-timepicker-addon.js"></script>
<link rel="stylesheet" href="/lib/jquery/timepicker/timepicker.css" media="screen"/>

<script language="javascript">
function submitForm (thisForm, ind) {
    thisForm.si.value=ind;
    thisForm.submit();
}
function win(urlPath) {
    var winl = (screen.width-200)/2;
    var wint = (screen.height-100)/2;
    var settings = 'height=100,width=200,directories=no,resizable=no,status=no,scrollbars=no,menubar=no,location=no,top=' + wint + ',left=' + winl;
    delWin = window.open(urlPath,'mmsdel',settings);
    delWin.focus();
}
function checkAll (thisForm) {
    var c;
    if (thisForm.checkstat.value=='0') { thisForm.checkstat.value='1'; c = true; }
    else { thisForm.checkstat.value='0'; c = false; }
    
    for (var i=0; i< thisForm.elements.length; i++) {
        var tp = thisForm.elements[i].type;        
        if (tp=='checkbox') { thisForm.elements[i].checked=c; }
    }
}
function selectAll(id) {
    document.getElementById(id).focus();
    document.getElementById(id).select();
}

$(function() {
    $("#after").datepicker({
        dateFormat: "yy-mm-dd"
    });
    $("#before").datepicker({
        dateFormat: "yy-mm-dd"
    });

});

function clearDates() {
    $("#after").val('');
    $("#before").val('');
}

</script>

</head>
<body>
<div class="previewsample"><script type="text/javascript" src="/lib/previewplay.js"></script></div>
<form action="<%=fileName%>.jsp" method="post" name="form1">
<input type="hidden" name="si" value="<%=sIndex%>">
<input type="hidden" name="checkstat" value="0">
        
<table cellspacing="0" cellpadding="2" border="0" width="95%">
<tr>
    <td valign="top" align="left">
        <table cellspacing="0" cellpadding="3" border="0" width="100%">
            <tr>
                <td align="left" valign="bottom" class="big_blue">Catalogue Management
                </td>
                <td align="right" valign="bottom" class="status_red">&nbsp;<b><%=statusMsg%></b>
                </td>
            </tr>
        </table>

</td></tr>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

	<table cellpadding="4" cellspacing="0" border="0" width="100%">	
        <tr>
	<td class="grey_11">Client:</td>
	<td class="grey_11" colspan="2" width="80%"><nobr>
            <select name="cunq">
                <option value="">[Select]</option>
            <% 
               for (int i=0; i<clients.size(); i++) { 
                   ug = clients.get(i);   
                   if (umeuser.getAdminGroup()!=9 && !ugid.equals(ug.getUnique())) continue;
            %>                
                    <option value="<%=ug.getUnique()%>" <% if (client.equals(ug.getUnique())){ %> selected <% } %>><%=ug.getName()%></option>               
            <% } %>
            </select>       
            </nobr>
	</td>        
        </tr>    
        
        <tr>
	<td class="grey_11">List Type:</td>
	<td class="grey_11" colspan="2">
            <select name="ltype">
                <option value="">[Select]</option>
                <option value="auth" <% if (listType.equals("auth")){ %> selected <% } %>>Authorized Content</option> 
                <option value="all" <% if (listType.equals("all")){ %> selected <% } %>>Whole Library</option>  
                <option value="unauth" <% if (listType.equals("unauth")){ %> selected <% } %>>Unauthorized Content</option> 
            </select>
	</td>       
        </tr>  
        <tr>
	<td class="grey_11"><%=dateTitle%></td>
	<td class="grey_11" colspan="2">
            After:&nbsp;
            <input type="text" name="after" id="after" value="<%=after%>"/>

            &nbsp;&nbsp;
            Before:&nbsp;
            <input type="text" name="before" id="before" value="<%=before%>"/>
            &nbsp;&nbsp;<a href="#" onClick="javascript:clearDates();">[Clear Dates]</a>
        </td>       
        </tr>  
        <tr>
	<td class="grey_11">Content Type:</td>
	<td class="grey_11">
            <select name="ctype">
                <option value="java" <% if (cType.equals("java")){ %> selected <% } %>>Java Games</option>  
                <option value="bg" <% if (cType.equals("bg")){ %> selected <% } %>>Bg Images</option>             
                <option value="video" <% if (cType.equals("video")){ %> selected <% } %>>Videos</option>
                <option value="anim" <% if (cType.equals("anim")){ %> selected <% } %>>Animations</option>
                <option value="master" <% if (cType.equals("master")){ %> selected <% } %>>Master Tones</option>
                <!--<option value="true" <% if (cType.equals("true")){ %> selected <% } %>>True/Fun Tones</option>-->
                <option value="poly" <% if (cType.equals("poly")){ %> selected <% } %>>Poly/Mono Tones</option>
                <!--<option value="theme" <% if (cType.equals("theme")){ %> selected <% } %>>Themes</option>-->
            </select>
	</td>   
            <td align="right"><input type="button" name="get" value="GET" style="width:150px;" onClick="javascript:submitForm(this.form,0);"></td>
        </tr>    
      
        </table>

</td></tr>
<% if (list.size()>0 && !client.equals("")) { %>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

<tr><td align="left">
        <table cellpadding="2" cellspacing="0" border="0" width="100%">
        <tr>
            <td align="left">
                <nobr>                
                <input type="submit" name="unauthsel" value="UNAUTHORIZE SELECTED" <% if (listType.equals("unauth")) { %>disabled<%}%>>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" name="unauthall" value="UNAUTHORIZE ALL" <% if (listType.equals("unauth")) { %>disabled<%}%>>
                </nobr>
            </td>
            <td align="right">
                <nobr>
                <input type="submit" name="authall" value="AUTHORIZE ALL" <% if (listType.equals("auth")) { %>disabled<%}%>>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" name="authsel" value="AUTHORIZE SELECTED" <% if (listType.equals("auth")) { %>disabled<%}%>>
                </nobr>
            </td>
        </tr>
        </table>
</td></tr>
<% } %>
<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td><img src="/images/glass_dot.gif" height="5" width="1"></td></tr>
<% if (pageCount>1) { %>

    <tr>
    <td align="left" class="grey_11">
    <% if (sIndex>0) { %>
            <a href="javascript:submitForm(document.form1,'<%=(sIndex-maxCount)%>');"><span class="blue_11">PREVIOUS</span></a> |
    <% } else { %>
            PREVIOUS |
    <% }
            if (((sIndex+maxCount)/maxCount)<pageCount) { %>
    <a href="javascript:submitForm(document.form1,'<%=(sIndex+maxCount)%>');"><span class="blue_11">NEXT</span></a> |
    <% } else { %>
            NEXT |
    <%
            }

    for (int k=0; k<pageCount; k++) {
        int newIndex = k*maxCount;
        if (newIndex==sIndex) { out.print("<span class='lightgrey_11' style='font-family: Verdana;'><b>" + (k+1) + "</b></span>"); }
        else {
    %>
                <a href="javascript:submitForm(document.form1,'<%=newIndex%>');"><span class="blue_11" style="font-family: Verdana;"><b><%=(k+1)%></b></span></a>
    <%
                }
            }
    %>

    </td>
</tr>
<% } %>

<tr><td>    

        <table cellpadding="4" cellspacing="0" border="0" width="100%" class="tableview">            
            
<% if (listType.equals("all")) {%>        
            
    <tr>
        <th>&nbsp;</th>
        <th align="left">Title</th>    
        <th align="center">Provider</th>   
        <th align="center">Item Added</th>
        <% if (!client.equals("")) {%>
        <th align="center">Status</th>
           
        <th align="right"><a href="javascript:checkAll(document.form1);"><span class="blue_11">Select/Unselect All</span></a></th>        
        
        <% } else {%>
        <th align="center">&nbsp;</th>        
        <th align="left">&nbsp;</th>
        <%}%>
    </tr>
    
    <%  
        String row = "";
        String stat = "";
        String owner = "";
        
        for (int i=0; i<list.size(); i++) {
            try {
                props = (String[]) list.get(i);
                owner = "";

                if (row.equals("row2")) row = "";
                else row = "row2";

                try { owner = (String) owners.get(props[2]).getName(); } catch (NullPointerException e) { System.out.println(e); }
                System.out.println("owner: " + owner);
                if (owner==null || owner.equals("")) owner = "Mixmobile";
            
    %>
    <tr class="<%=row%>">
        <td align="left"><% if (props[3]!=null && !props[3].equals("")){%><img src="<%=props[3]%>" border="0"><%}%>&nbsp;</td>
        <td align="left"><%=props[1]%></td>
        <td align="center" class="grey_11"><%=owner%></td>      
        <td align="center" class="grey_10"><%=sdf.format(sqldf.parse(props[5]))%></td>
        <% if (!client.equals("")) {
            if (ccont.get(props[0])!=null) { stat = "<span class=\"green_11\"><b>Authorized</b></span>"; }
            else { stat = "<span class=\"red_11\"><b>Unauthorized</b></span>"; }
        %>
            <td align="center"><%=stat%></td>      
             
            <td align="right"><input type="checkbox" name="sel_<%=props[0]%>" value="<%=props[0]%>"></td>
        <% } else {%>
        <td align="center">&nbsp;</td>       
        <td align="left">&nbsp;</td>
        <%}%>
    </tr>
     <%     } catch (Exception exex) { System.out.println(exex);
                System.out.println(props[0] + ", " + props[1] + ", " + props[2] + ", " + props[3] + ", " + props[4] + ", " + props[5]);
            }
        } %>

<%} else {%>

    <tr>
        <th>&nbsp;</th>
        <th align="left">Title</th>
        <% if (cType.equals("master")){%>
        <th align="center">Listen</th>
        <%}%>
        <th align="center">Provider</th>
        <th align="center">
            <% if (listType.equals("auth")){%>Authorized
            <%} else {%>Item Added
            <%}%>            
        </th>
        
        <th align="right"><a href="javascript:checkAll(document.form1);"><span class="blue_11">Select/Unselect All</span></a></th>                
    </tr>
    
    <%  
        String row = "";
        String stat = "";
        String owner = "";
        String flashid = "";
            
        for (int i=0; i<list.size(); i++) {
            try {
                props = (String[]) list.get(i);

                if (row.equals("row2")) row = "";
                else row = "row2";

                owner = "";
                try { owner = (String) owners.get(props[2]).getName(); } catch (NullPointerException e) {}
                if (owner==null || owner.equals("")) owner = "Mixmobile";
            
    %>
    <tr class="<%=row%>">
        <td align="left"><% if (props[3]!=null && !props[3].equals("")){%><img src="<%=props[3]%>" border="0"><%}%>&nbsp;</td>
        <td align="left"><%=props[1]%></td>
        <% if (cType.equals("master")){
            flashid = "flashid_" + misc.generateUniqueId();
        %>
        <td align="center">
            <a href="javascript:void(0)" onclick="javascript:playSample('preview/<%=props[2]%>/<%=props[6]%>.flv', '<%=flashid%>','<%=durl%>')">
            <img id="<%=flashid%>" src="/images/media_play_blue.gif" alt="" border="0"/></a>
        </td>
        <%}%>
        <td align="center" class="grey_11"><%=owner%></td>      
        <td align="center" class="grey_10">
            <% 
                if (listType.equals("auth")){ out.println(sdf.format(sqldf.parse(props[4]))); }
                else { out.println(sdf.format(sqldf.parse(props[5]))); }
            %>
        </td>      
        
        <td align="right"><input type="checkbox" name="sel_<%=props[0]%>" value="<%=props[0]%>" <% if (error && aReq.get("sel_" + props[0]).equals(props[0])){%> checked <% } %>></td>
       
    </tr>
     <%  } catch (Exception exex) { System.out.println(exex);
                System.out.println(props[0] + ", " + props[1] + ", " + props[2] + ", " + props[3] + ", " + props[4] + ", " + props[5]);
         }

       } %>



<% } %>
        </table>
        
</td></tr>
<% if (list.size()>0 && !client.equals("")) { %>
<tr><td align="left">
        <table cellpadding="2" cellspacing="0" border="0" width="100%">
        <tr>
            <td align="left">
                <nobr>                
                <input type="submit" name="unauthsel" value="UNAUTHORIZE SELECTED" <% if (listType.equals("unauth")) { %>disabled<%}%>>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" name="unauthall" value="UNAUTHORIZE ALL" <% if (listType.equals("unauth")) { %>disabled<%}%>>
                </nobr>                
            </td>
            <td align="right">
                <nobr>
                <input type="submit" name="authall" value="AUTHORIZE ALL" <% if (listType.equals("auth")) { %>disabled<%}%>>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <input type="submit" name="authsel" value="AUTHORIZE SELECTED" <% if (listType.equals("auth")) { %>disabled<%}%>>
                </nobr>
            </td>
        </tr>
        </table>
</td></tr>
<% } %>
</table>

</form>
</body>
</html>

<% 
trans.commit();
dbsession.close();

%>


