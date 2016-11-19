<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="ume.pareva.contentcms.ItemLinkLogDao"%>
<%@page import="ume.pareva.contentcms.ItemLinkLog"%>
<%@page import="ume.pareva.ffmpeg.*"%>

<%@page import="ume.pareva.webservice.client.*"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>

<%


//***************************************************************************************************
AdminRequest aReq = new AdminRequest(request);
String uid = aReq.getUserId();
String msisdn = aReq.getMobile();
String anyxSrvc = aReq.getAnyxService();
String domain = aReq.getDomain();
String lang = aReq.getLanguage();
String stylesheet = System.getProperty("stylesheet_" + lang);
String pageEnc = System.getProperty("lang_" + lang);
if (pageEnc == null) pageEnc = "ISO-8859-1";

HandsetDao handsetdao=null;
ItemLinkLogDao itemlinklogdao=null;

try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      handsetdao=(HandsetDao) ac.getBean("handsetdao");  
      itemlinklogdao=(ItemLinkLogDao) ac.getBean("itemlinklogdao");
      
      
      }
      catch(Exception e){
          e.printStackTrace();
      }

String fileName = "fromDefaultServlet";
//***************************************************************************************************
String crlf = "\r\n";
String dir =System.getProperty("contenturl");// System.getProperty("installDir");
String singleServer ="true"; // System.getProperty("CMS_singleServer");
String dlServer = System.getProperty("CMS_downloadServer");

System.out.println("#############  HEADERS");
Enumeration ee = request.getHeaderNames();
for (;ee.hasMoreElements();) {
    String elem = (String) ee.nextElement();
    System.out.println(elem + ": " + request.getHeader(elem));
}
System.out.println("END #############  HEADERS");

System.out.println("#############  PARAMETERS");
ee = request.getParameterNames();
for (;ee.hasMoreElements();) {
    String elem = (String) ee.nextElement();
    System.out.println(elem + ": " + request.getParameter(elem));
}
System.out.println("END #############  PARAMETERS");

boolean useDrm = false;

//log params
String status = "";
String statusDesc = "";
String dataFile = "";

Handset handset = (Handset)session.getAttribute("handset");

if(handset==null){
try{
handset=handsetdao.getHandset(request);
System.out.println("FromDefaultServlet : handset"+ handset+ "handsetdao "+handsetdao);
session.setAttribute("handset",handset);
}
catch(Exception e){e.printStackTrace();}
}
//String ug = handset.getUserAgent();
int fileSize = 0;
String mimeType = "";
File contentFile = null;

/*
System.out.println("handset: " + handset.getUserAgent());
System.out.println("handset: " + handset.getWidth() + ": " + handset.getHeight());
System.out.println("handset: " + handset.getWallpaperWidth() + ": " + handset.getWallpaperHeight());
*/

        

String sqlstr = "";

String logUnique = aReq.get("i").trim();
String preview = aReq.get("pre");
String ttype = aReq.get("ttype");
String itemUnique = "";
String itype = "";
String cType = "";
String owner = "";
String clientUnique = "";
String clientRef = "";
String ilang = "";
String fileType = "";

String drmBound = "";
String drmHead = "";
String drmEnd = "";
int drmLength = 0;
String[] cmd = null;
String linkCmd =System.getProperty("contenturl")+ "/www/create_direct_links.sh";
System.out.println("linkCmd = "+linkCmd);
String tempLink = "";

ItemResourceDao itemresourcedao=null;
VideoClipDao videoclipdao=null;
GifAnimDao gifanimdao=null;
Misc misc=null;
try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      itemresourcedao=(ItemResourceDao) ac.getBean("itemresourcesdao");
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      gifanimdao=(GifAnimDao) ac.getBean("gifanimdao");
      misc=(Misc) ac.getBean("misc");
      
	  
      }
      catch(Exception e){
          e.printStackTrace();
      }


Query query=null;
Transaction trans=dbsession.beginTransaction();

if (!logUnique.equals("")) {
    sqlstr = "SELECT * FROM itemOrderLog WHERE aUnique='" + logUnique + "'";
    System.out.println(sqlstr);
    query= dbsession.createSQLQuery(sqlstr).addScalar("aItemUnique").addScalar("aItemType").addScalar("aContentType")
            .addScalar("aOwner").addScalar("aResellerUnique").addScalar("aClientRef").addScalar("aLanguage")
            .addScalar("aItemParam");
    
    java.util.List result=query.list();
    
    
    if (result.size()>0) {
        for(Object o:result){
            Object[] row=(Object[]) o;
        itemUnique = String.valueOf(row[0]);
        itype = String.valueOf(row[1]);
        cType = String.valueOf(row[2]);
        owner = String.valueOf(row[3]);
        clientUnique = String.valueOf(row[4]);
        clientRef = String.valueOf(row[5]);
        ilang = String.valueOf(row[6]);
        fileType = String.valueOf(row[7]);
        }
    }
   
}


System.out.println("Preview: " + preview);
System.out.println("itemType: " + itype);

if (itemUnique.equals("") && preview.equals("")) {  return; }

if (itype.equals("video")) {

    VideoClip item = videoclipdao.getItem(itemUnique);
    if (item==null) {  return; }

    ItemResource res = null;
    ItemResourceDao itemresourcesdao=null;
        try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      itemresourcesdao=(ItemResourceDao) ac.getBean("itemresourcesdao");
	  
      }
      catch(Exception e){
          e.printStackTrace();
      }
    
        //System.out.println("teststream  cType "+cType);
    //item.setResourceMap(itemresourcesdao.getResourceMap(item.getUnique()));
    if (!cType.equals(""))
    {
        try{
            boolean smartPhone=handset.isAndroid || handset.isIphone || handset.isSmartPhone;
            //System.out.println("SMARTPHONE ?? "+smartPhone+"  HANDSET BRAND DEVICE "+handset.getBrandName());
            if(smartPhone)
		cType="mp4";
            else cType="mp4";
        res = item.getResourceMap().get(cType).get(0);
        }
        catch(Exception e){e.printStackTrace(); System.out.println("teststream res Exception "+e+" cType="+cType);}
       
    }

    //System.out.println("teststream " + res.toString() + ": cType: " + cType);

    if (res!=null) {
        
        mimeType = res.getMimeType();
        contentFile = new File(dir + res.getDataFile());
        dataFile = contentFile.getName();
        //System.out.println("teststream res!=null: " + contentFile.getPath()+ " Res value "+res.getDataFile() + " dir "+dir+" dataFile "+dataFile+" Parent "+contentFile.getParent());

        if (!contentFile.exists()) { status = "error"; statusDesc = "File not found: " + dataFile; }
        else tempLink = item.getUnique() + "."  + res.getFileExt();  // tempLink = misc.generateUniqueId() + misc.generateLogin(10) + "."  + res.getFileExt();
         //tempLink = item.getUnique() + "."  + res.getFileExt();
        tempLink = item.getUnique() + "."  + cType;
        //System.out.println("teststream tempLink :"+item+" item Unique: "+item.getUnique()+" res "+ res.getFileExt()+" TempLink "+tempLink);
    } else { status = "error"; statusDesc = "Phone not supported"; }        
}



//System.out.println("Content TempLink "+tempLink);
if (!tempLink.equals("")) {
    
    System.out.println("teststream templink "+tempLink);
    ItemLinkLog itemlinklog=null;
    try{
        itemlinklog=itemlinklogdao.getItem(tempLink);
        
        }
        catch(Exception e){System.out.println("teststream Exception "+e);e.printStackTrace();}
    
    
   if(itemlinklog==null){ 
    
   cmd = new String[3];
    if(cType.equals("mp4")){
     //System.out.println("teststream  inside tempLink cType="+cType+" contentFile "+contentFile.getPath());
    
    String convertedFile="";
    if(contentFile.getPath().endsWith("mp4")) {
    	FFMpegEncoder encoder=new FFMpegEncoder();
    	convertedFile=encoder.convertToVideo(contentFile.getPath(),contentFile.getParent(), dataFile);
    	//convertedFile=convertToVideo(contentFile, dataFile);
    }
    //if(contentFile.getPath().endsWith("3gp")) convertedFile=convert3gpTomp4(contentFile, dataFile);
    
        
     //System.out.println("teststream Content Link AFTER  conversion "+convertedFile);
      cmd[1] = contentFile.getParent()+"/"+convertedFile;
      //cmd[1]=contentFile.getPath();
      //tempLink=convertedFile;
    }
  
    else cmd[1]=contentFile.getPath(); //a
    
  
    try {
               
      
        //System.out.println("teststream Content Link AFTER just before conversion  conversion "+cmd[1]+"   linkcmd "+linkCmd);
        cmd[0] = linkCmd;
        //cmd[1] = contentFile.getParent()+"/"+convertedFile;
        //cmd[1]=contentFile.getParent()+"/"+tempLink; 
        //cmd[1]=contentFile.getPath(); //Original Video Storate Directory
 
       //cmd[2] = System.getProperty("document_root") + "/lib/slinks/" + tempLink;
        cmd[2] = System.getProperty("contenturl")+"/www"+ "/lib/slinks/" + tempLink;
        
        //System.out.println("teststream ContentUrl: cmd[1]="+cmd[1] +" --  cmd[2]="+cmd[2]);
        
        try{
        if(execute(cmd)) System.out.println("teststream command successfully executed ");
        }catch(Exception e){System.out.println("teststream ContentUrl exception "+e);e.printStackTrace();}
        
        
        
        itemlinklog=new ItemLinkLog();
        itemlinklog.setUnique(Misc.generateUniqueId());
        itemlinklog.setLink(tempLink);
        itemlinklog.setStatus("1");
        itemlinklog.setLogUnique(logUnique);
        itemlinklogdao.addItem(itemlinklog);
    
        
        System.out.println("Sending Redirect: " + "/lib/slinks/" + tempLink);
         } catch (Exception e) { status = "error"; statusDesc = "IO Error: " + e; System.out.println("Exception:" +e);e.printStackTrace();}
    
   }
        String contentUrl=(String) session.getAttribute("cloudfrontUrl");
        if(contentUrl==null || contentUrl.length()<=0) contentUrl=(String) application.getAttribute("cloudfrontUrl");
        
        
        System.out.println("teststream ContentUrl: "+"http://"+contentUrl +"/lib/slinks/"+ tempLink);
        response.sendRedirect("http://"+contentUrl +"/lib/slinks/"+ tempLink);//
        //response.sendRedirect("/lib/slinks/"+ tempLink);
        
	//response.sendRedirect(contentFile.getPath());

        status = "ok";
        statusDesc = "drm:" + handset.supportsForwardLock() + ", size: " + contentFile.length() + ", " + mimeType;

//System.out.println("Content STATUS DESC :"+statusDesc);

   
    
}
else if (contentFile!=null && contentFile.exists()) {

    try {

        BufferedInputStream fin = new BufferedInputStream(new FileInputStream(contentFile));
        ServletOutputStream sout = response.getOutputStream();

        if (!drmHead.equals("") && !drmEnd.equals("")) {
            drmLength = drmHead.length() + drmEnd.length();

            System.out.println("Setting DRM:");
            System.out.println(drmHead);
            response.setContentType("application/vnd.oma.drm.message; boundary=" + drmBound);
            System.out.println("Setting content type: application/vnd.oma.drm.message; boundary=" + drmBound + ", length: " + (((int) contentFile.length()) + drmLength));
            response.setContentLength((((int) contentFile.length()) + drmLength));

            sout.write(drmHead.getBytes("iso-8859-1"));
        }
        else {
            response.setContentType(mimeType);
            System.out.println("Setting content type: " + mimeType + ", length: " + (int) contentFile.length());
            response.setHeader("Content-Length", "" + contentFile.length());
        }

        byte buf[]=new byte[2048];
        int len;
        while((len=fin.read(buf))>0) {
            sout.write(buf,0,len);
            fileSize += len;
        }
        
        if (!drmHead.equals("") && !drmEnd.equals("")) sout.write(drmEnd.getBytes("iso-8859-1"));

        sout.flush();
        System.out.println("ContentFile Flushed");

        fileSize += drmLength;

        status = "ok";
        statusDesc = "drm:" + handset.supportsForwardLock() + ", size: " + fileSize + ", " + mimeType;

    } catch (Exception e) { status = "error"; statusDesc = "IO Error: " + e; System.out.println(e); e.printStackTrace();}
}

if (singleServer.equals("true") && !clientRef.equals("")) {
    ItemLogger log = new ItemLogger(clientRef);
    log.setStatus(status);
    log.setStatusDesc(statusDesc);
    log.setFile(dataFile);
    log.setFileSize(fileSize);
    log.setMimeType(mimeType);
    log.setDrmSupport("drm.forwardLock: " + handset.supportsForwardLock());
    log.setRetrieveCount(1);
    log.setRetrieved(MiscDate.now24sql());    
    log.update();    
}

sqlstr = "UPDATE itemOrderLog SET aStatus='" + status + "',aRetrieveCount=(aRetrieveCount+1)"
        + ",aStatusDesc='" + statusDesc + "',aRetrieved='" + MiscDate.now24sql() + "'"
        + ",aPhoneUnique='" + handset.getUnique() + "',aUserAgent='" + "useragent" + "'"
        + ",aDataFile='" + Misc.encodeForDb(dataFile) + "'"
        + " WHERE aUnique='" + logUnique + "'"; 
//System.out.println(sqlstr);             
query=dbsession.createSQLQuery(sqlstr);

trans.commit();
dbsession.close();


%>

<%!
boolean execute(String[] cmd){
    try{
        Runtime rt= Runtime.getRuntime();

        //System.out.println("teststream  cmd "+Arrays.toString(cmd));
        Process proc = rt.exec(cmd);

        StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR", true);
        StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT", true);
        errorGobbler.start();
        outputGobbler.start();

        int exitVal = proc.waitFor();
        String sb = outputGobbler.sb.toString();
        String eb = errorGobbler.sb.toString();

        //System.out.println("teststream Command Exceute Exit value: " + exitVal);

        proc.destroy();

        return true;
    }
    catch(java.io.IOException e ){System.out.println("teststream IOException "+e);e.printStackTrace();}
    catch(java.lang.InterruptedException e){System.out.println("teststream Interrupted Exception occurred "+e);}
    
    return false;

}


/**
 * 
 * ffmpeg -i input.mp4 -codec:v libx264 -profile:v baseline -preset slow -b:v 250k -maxrate 250k -bufsize 500k -vf scale=-1:360 
 * -threads 0 -codec:a libvo_aacenc -b:a 96k output.mp4
 * 
 * 
 * 
 */
/* String convertToVideo(File contentFile, String filename) {
		String[] cmd = new String[27];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "/usr/bin/ffmpeg"; 
		cmd[1] = "-y";
		cmd[2] = "-i";
		cmd[3] = contentFile.getPath();
		cmd[4] = "-vcodec"; //video codec. This is an alias for -codec:v
		cmd[5] = "libx264";
		cmd[6] = "-s"; //frame size
		cmd[7] = "320x240";
		cmd[8] = "-acodec";
		cmd[9] = "aac";
		cmd[10] = "-strict";
		cmd[11] = "-2";
		cmd[12] = "-ac";
		cmd[13] = "1";
		cmd[14] = "-ar"; //audio sampling frequency
		cmd[15] = "16000";
		cmd[16] = "-r"; //framerate
		cmd[17] = "24";
		cmd[18] = "-ab";
		cmd[19] = "64000";
                cmd[20] = "-crf";
		cmd[21] = "18";
		cmd[22] = "-aspect"; //video display aspect ratio specified by aspect
		cmd[23] = "16:9";
		cmd[24] = "-movflags";
		cmd[25] = "faststart";
                String myfilename = filename + "ncoded" + ".mp4";
		cmd[26] = contentFile.getParent() + "/" + myfilename;
		//cmd[24] = "/home/meglos/Downloads/" + myfilename;

		System.out.println("teststream Video encoding CURRENTLY CONVERTING at " + cmd[24]);
		for (String sCmd: cmd) {
			System.out.print("teststream "+sCmd + " ");
		}
		System.out.println();

		if (execute(cmd)){
			return myfilename;
		}else{		
			return null;
		}

	} */

String c3gptomp4(File contentFile,String filename) {
    //ffmpeg -i test.3gp -sameq -ab 64k -ar 44100 test.mp4
            
            String[] cmd = new String[15];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "/usr/bin/ffmpeg"; 
		cmd[1] = "-y";
		cmd[2] = "-i";
		cmd[3] = contentFile.getPath();
		cmd[4] = "-q:a";
                cmd[5] = "0";
		cmd[6] = "-ab";
		cmd[7] = "64k";
                cmd[8] = "-acodec";
		cmd[9] = "aac";
		cmd[10] = "-strict";
		cmd[11] = "-2";
                cmd[12] = "-ar";
                cmd[13] = "44100";
                String myfilename = filename + "ncoded" + ".mp4";
		cmd[14] = contentFile.getParent() + "/" + myfilename;
                
               System.out.println("teststream 3gp to mp4 Video encoding CURRENTLY CONVERTING at " + cmd[14]);
		for (String sCmd: cmd) {
			System.out.print("teststream "+sCmd + " ");
		}
		System.out.println();

		if (execute(cmd)){
			return myfilename;
		}else{		
			return null;
		}
                
                
}

%>