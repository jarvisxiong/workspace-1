 <%@ include file="/WEB-INF/jspf/coreimport.jspf" %>
 <%@ include file="/WEB-INF/jspf/db.jspf" %>
 <%@page import="ume.pareva.contentcms.ItemLinkLogDao"%>
<%@page import="ume.pareva.contentcms.ItemLinkLog"%>
<%@page import="ume.pareva.webservice.client.FFMpegEncoder"%>

 <%
            
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
//LangProps lp = LangProps.getFromContext(service.getUnique(), fileName, langcode, domain, application, true);
//***************************************************************************************************



VideoClipDao videoclipdao=null;
Misc misc=null;
MastertoneDao mastertonedao=null;
ItemResourceDao itemresourcesdao=null;
GifAnimDao gifanimdao=null;
ItemImageDao itemimagesdao=null;
BgImageDao bgimagesdao=null;
ItemCategoryDao itemcategorydao=null;
ItemLinkLogDao itemlinklogdao=null;
QuestionPackDao questionPackDao=null;
QuestionDao questionDao=null;
CSVParseUtil csvParseUtil=null;
QuestionInPackDao questionInPackDao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      
      videoclipdao=(VideoClipDao) ac.getBean("videoclipdao");
      misc=(Misc) ac.getBean("misc");
      mastertonedao=(MastertoneDao) ac.getBean("mastertonedao");
      itemresourcesdao=(ItemResourceDao) ac.getBean("itemresourcesdao");
      gifanimdao=(GifAnimDao) ac.getBean("gifanimdao");
      itemimagesdao=(ItemImageDao) ac.getBean("itemimagesdao");
      bgimagesdao=(BgImageDao) ac.getBean("bgimagesdao");
      itemcategorydao=(ItemCategoryDao) ac.getBean("itemcategorydao");
      itemlinklogdao=(ItemLinkLogDao) ac.getBean("itemlinklogdao");
      questionPackDao=(QuestionPackDao) ac.getBean("questionPackDao");
      questionDao=(QuestionDao) ac.getBean("questionDao");
      csvParseUtil=(CSVParseUtil) ac.getBean("csvparseutil");
      questionInPackDao=(QuestionInPackDao) ac.getBean("questionInPackDao");
      }
      catch(Exception e){
          e.printStackTrace();
      }



String lamecmd = "/usr/local/bin/lame";
//String ffmpegcmd = "C:/ffmpeg/bin/ffmpeg";
String ffmpegcmd = "/usr/bin/ffmpeg";
String fveccmd = "/usr/bin/fvec/fvec";
//String soxcmd = "/usr/local/bin/sox";

System.out.println("======= ACT UPLOAD CALLED ========= ");

//java.sql.Connection con = null;

String owner = aReq.get("owner");
String type = aReq.get("type");
String itemUnique = aReq.get("itemunq");
String toneType = aReq.get("ttype","1");
String cat = aReq.get("cat");
String title = aReq.get("title", "");
String classification = aReq.get("classf");
String timUrl = aReq.get("timUrl");
String make3gp = aReq.get("make3gp");
System.out.println("make3gp: "+make3gp);
int ttype = Integer.parseInt(toneType);

Calendar c = new GregorianCalendar();
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

String defRelease = sdf.format(c.getTime());
System.out.println("defRelease: " + defRelease);
c.add(Calendar.YEAR,1);
String defExpire= sdf.format(c.getTime());
System.out.println("defExpire: " + defExpire);

boolean isMultipart = ServletFileUpload.isMultipartContent(request);

System.out.println("Multi: " + isMultipart);
System.out.println("Owner: " + owner);
System.out.println("Type: " + type);
System.out.println("ToneType: " + ttype);

if (isMultipart) {

    ServletFileUpload upload = new ServletFileUpload();

    FileItemIterator iter = upload.getItemIterator(request);
    
    while (iter.hasNext()) {
        FileItemStream item = iter.next();
        String name = item.getFieldName();
        InputStream stream = item.openStream();
        
        if (item.isFormField()) {
            try {
                System.out.println("Form field " + name + " with value " + Streams.asString(stream) + " detected.");
            if (name.equalsIgnoreCase("filename") && title.equals("")) {
                title = (String) Streams.asString(stream);
                if (title.indexOf(".")>-1) title = title.substring(0, title.indexOf("."));
            }
            } catch (Exception e) { System.out.println(e); }

        } else {
            System.out.println("File field " + name + " with file name "
                + item.getName() + " detected.");

            if (name.equalsIgnoreCase("filedata") && title.equals("")) {
                title = item.getName();
                if (title.indexOf(".")>-1) title = title.substring(0, title.indexOf("."));
            }

            // Process the input stream   

            String fieldName = item.getFieldName();            
            String contentType = item.getContentType();
            

            System.out.println("field: " + fieldName + ", ctype: " + contentType);
            
            if (type.equals("game")) {
                
                String datadir = "/var/lib/tomcat7/webapps/ROOT/data/upload/" + owner;
                
                File targetDir = new File(datadir);
                if (!targetDir.exists()) targetDir.mkdirs();
                
                File datafile = new File(datadir + "/" + item.getName());                
                
                FileUtil.writeToFile(datafile, stream);
                if (!datafile.exists()) return;  
                
                if (item.getName().toLowerCase().endsWith(".zip")) {                                
                    String[] cmd= new String[5];
                    cmd[0] = System.getProperty("CMD_unzip");
                    cmd[1] = "-o";
                    cmd[2] = datafile.getPath();
                    cmd[3] = "-d";
                    cmd[4] = targetDir.getPath();
                    Misc.execute(cmd);
                }       
            
            } 
            else if (type.equals("master")) {

                int code = misc.getSmsCode("masterTones");
                String smsCode = "" + code;
                if (code==-1) smsCode = "";

                String unique = Misc.generateUniqueId();
                String mp3Unique = Misc.generateUniqueId();
                itemUnique = Misc.generateUniqueId();

                String datadir = "/var/lib/tomcat7/webapps/ROOT/data/mastertones/" + owner;
                String previewdir = System.getProperty("document_root") + "/preview/" + owner;
                String wapdir = datadir + "/wap";

                File dir = new File(datadir);
                if (!dir.exists()) dir.mkdirs();
                dir = new File(previewdir);
                if (!dir.exists()) dir.mkdirs();
                dir = new File(wapdir);
                if (!dir.exists()) dir.mkdirs();

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;
                if (ext.equals("mp3")) mimeType = "audio/mpeg";
                else if (ext.equals("m4a")) { mimeType = "audio/m4a"; cType = "mp4"; }
                else if (ext.equals("mp4")) mimeType = "audio/mp4";                                
                else if (ext.equals("aac")) mimeType = "audio/aac";
                else if (ext.equals("awb")) mimeType = "audio/amr-wb";
                else if (ext.equals("amr")) mimeType = "audio/amr";

                if (!mimeType.equals("")) {

                    String song = "";
                    String artist = "";
                    String album = "";
                    boolean addMp3 = false;

                    File datafile = new File(datadir + "/" + unique + "." + ext);
                    FileUtil.writeToFile(datafile, stream);
                    if (!datafile.exists()) return;                    

                    if (cType.equals("mp3")) {
                        Map sampleProps = null;
                        if (datafile!=null && datafile.exists() && !datafile.isDirectory()) {
                            try {
                                AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(datafile);
                                if (baseFileFormat instanceof TAudioFileFormat) sampleProps = ((TAudioFileFormat) baseFileFormat).properties();
                            } catch (Exception e) { System.out.println(e); }
                        }

                        System.out.println("sampleProps: " + sampleProps);
                        if (sampleProps!=null) {
                            song = (String) sampleProps.get("title");
                            artist = (String) sampleProps.get("author");
                            album = (String) sampleProps.get("album");
                        }
                    }
                    else {
                        // create mp3 from src
                        if (mastertonedao.createFromSrc(datafile, datadir, mp3Unique + ".mp3")==1) {
                            datafile = new File(datadir + "/" + mp3Unique + ".mp3");
                            addMp3 = true;
                        }
                    }

                    mastertonedao.createFlv(datafile, previewdir, unique + ".flv");                    
                    mastertonedao.createPreviewFromSrc(datafile, wapdir, unique + ".mp3");

                    if (song==null || song.equals("")) song = "Unknown Title";
                    if (artist==null || artist.equals("")) artist = "Unknown Artist";
                    if (album==null) album = "";

                    System.out.println("song: " + song);
                    System.out.println("artist: " + artist);
                    System.out.println("album: " + album);

                    ItemResource res = new ItemResource();
                    res.setUnique(unique);
                    res.setItemUnique(itemUnique);
                    res.setContentType(cType);
                    res.setFileExt(ext);
                    res.setDataFile("/data/mastertones/" + owner + "/" + unique + "." + ext);
                    res.setMimeType(mimeType);
                    itemresourcesdao.saveItem(res);

                    if (addMp3) {
                        res = new ItemResource();
                        res.setUnique(mp3Unique);
                        res.setItemUnique(itemUnique);
                        res.setContentType("mp3");
                        res.setFileExt("mp3");
                        res.setDataFile("/data/mastertones/" + owner + "/" + mp3Unique + ".mp3");
                        res.setMimeType("audio/mpeg");
                        itemresourcesdao.saveItem(res);
                    }

                    Mastertone ii = new Mastertone();
                    ii.setUnique(itemUnique);
                    ii.setName1(smsCode);
                    ii.setName2(smsCode);
                    ii.setName3(smsCode);
                    ii.setName4(smsCode);
                    ii.setSongName(Misc.caps(song));
                    ii.setArtist(Misc.caps(artist));
                    ii.setAlbum(Misc.caps(album));
                    ii.setPreviewFile(unique);
                    ii.setOriginalPreview(datafile.getName());
                    ii.setCategory(cat);
                    ii.setPriceGroup(1);
                    ii.setStatus(1);
                    ii.setOwner(owner);
                    ii.setToneType(toneType);
                    ii.setOwnerReference(item.getName());
                    ii.setRelease(sdf.parse(defRelease));
                    ii.setExpire(sdf.parse(defExpire));
                    mastertonedao.saveItem(ii);
                }



                System.out.println("itemUnique: " + itemUnique + ", " + ext);


            }
            else if (type.equals("anim")) {

                int code = misc.getSmsCode("gifAnims");
                String smsCode = "" + code;
                if (code==-1) smsCode = "";

                System.out.println("Received Gif Animation");
                String datadir = "/var/lib/tomcat7/webapps/ROOT/data/gifanims";
                String previewdir = "/var/lib/tomcat7/webapps/ROOT/images/gifanims";
                String origdir = datadir + "/originals/" + owner;

                File dir = new File(origdir);
                if (!dir.exists()) dir.mkdirs();

                dir = new File(previewdir + "/80/");
                if (!dir.exists()) dir.mkdirs();
                dir = new File(previewdir + "/110/");
                if (!dir.exists()) dir.mkdirs();

                File srcfile = new File(origdir + "/" + item.getName());
                FileUtil.writeToFile(srcfile, stream);

                if (srcfile.exists() && !srcfile.isDirectory()) {

                    String unique = Misc.generateUniqueId();

                    ImageResize.resampleGif(srcfile, datadir + "/128_" + unique + ".gif", 128, 128);
                    ImageResize.resampleGif(srcfile, datadir + "/240_" + unique + ".gif", 240, 240);
                    ImageResize.resamplePreviewGif(srcfile, previewdir + "/80/" + unique + ".gif", 80, 80);
                    ImageResize.resamplePreviewGif(srcfile, previewdir + "/110/" + unique + ".gif", 110, 110);

                    GifAnim anim = new GifAnim();
                    anim.setUnique(unique);
                    anim.setName1(smsCode);
                    anim.setName2(smsCode);
                    anim.setName3(smsCode);
                    anim.setName4(smsCode);
                    anim.setTitle(title);
                    anim.setPicName(unique + ".gif");
                    anim.setPriceGroup(1);
                    anim.setStatus(1);
                    anim.setRequestCount(0);
                    anim.setCategory(cat);
                    anim.setLastAccessed(new Date());
                    anim.setCreated(new Date());
                    anim.setOwner(owner);
                    anim.setOriginalFile(item.getName());
                    anim.setCopyright("");
                    anim.setRelease(sdf.parse(defRelease));
                    anim.setExpire(sdf.parse(defExpire));
                    anim.setClassification(classification);

                    gifanimdao.saveItem(anim);

                }

            }
            //TODO
            else if (type.equals("video")) {

                int code = misc.getSmsCode("videoClips");                
                String smsCode = "" + code;
                if (code==-1) smsCode = "";

                String unique = Misc.generateUniqueId();
                itemUnique = Misc.generateUniqueId();

                String datadir = System.getProperty("contenturl")+"/data/videoclips/" + owner;
                String previewdir = System.getProperty("contenturl")+"/images/item";
                
                /**************************************************************************************************/
                
                System.out.println("Data Directory: "+datadir);
                
                
                /**************************************************************************************************/
                
                File dir = new File(datadir);
                if (!dir.exists()) dir.mkdirs();
                
                int width = 0;
                int height = 0;
                try { width = Integer.parseInt(aReq.get("width", "0")); } catch (NumberFormatException e) {}
                try { height = Integer.parseInt(aReq.get("height", "0")); } catch (NumberFormatException e) {}

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;
                if (ext.equals("mpg") || ext.equals("mpeg") || ext.equals("mpe") || ext.equals("mp2") || ext.equals("mpa")) {
                    cType = "mpeg"; mimeType = "video/mpeg";
                }
                else if (ext.equals("mov") || ext.equals("qt")) { cType = "mov"; mimeType = "video/quicktime"; }
                else if (ext.equals("avi")) mimeType = "video/x-msvideo";
                else if (ext.equals("mp4")) mimeType = "video/mp4";
                else if (ext.equals("3gp") || ext.equals("3gpp")) { cType = "3gp"; mimeType = "video/3gpp"; }
                else if (ext.equals("3g2") || ext.equals("3gpp2")) { cType = "3g2"; mimeType = "video/3gpp2"; }
                else if (ext.equals("wmv")) mimeType = "video/x-ms-wmv";
                else if (ext.equals("flv")) mimeType = "video/x-flv";

                if (!mimeType.equals("")) {

                    File datafile = new File(datadir + "/" + unique + "." + ext);
                    FileUtil.writeToFile(datafile, stream);
					System.out.println("File Size in MB: "+datafile.length()/(1024*1024));
                    String screenShotUnique = Misc.generateUniqueId();
                    String imageGroup = Misc.generateUniqueId();
					if(datafile.length()/(1024*1024)>10){
						System.out.println("Encoding File");
						FFMpegEncoder ffmpegEncoder=new FFMpegEncoder();
						String encodedFileName=ffmpegEncoder.convertToVideo(datafile.getPath(),datadir,unique);
						if(encodedFileName!=null){
							datafile.delete();
							unique=encodedFileName.substring(0,encodedFileName.lastIndexOf("."));
							datafile=new File(datadir + "/" + unique + "." + ext);
						}
						
						
					}
                    // create a screenshot
                   /* String[] cmd = new String[14];
                     cmd[0] = ffmpegcmd;
                    cmd[1] = "-i";
                    cmd[2] = datadir + "/" + unique + "." + ext;
                    cmd[3] = "-y";
                    cmd[4] = "-f";
                    cmd[5] = "image2";
                    cmd[6] = "-ss";
                    cmd[7] = "8";
                    cmd[8] = "-sameq";
                    cmd[9] = "-t";
                    cmd[10] = "0.001";
                    cmd[11] = "-s";
                    cmd[12] = "80x70";
                    cmd[13] = previewdir + "/" + screenShotUnique + ".jpg";
                    Misc.execute(cmd);
                    */ 
                    String[] cmd = new String[15];
                    cmd[0] = ffmpegcmd;
                    cmd[1] = "-itsoffset";
                    cmd[2] = "-4";
                    cmd[3] = "-i";
                    cmd[4] = datadir + "/" + unique + "." + ext;
                    cmd[5] = "-vcodec";
                    cmd[6] = "mjpeg";
                    cmd[7] = "-vframes";
                    cmd[8] = "1";
                    cmd[9] = "-an";
                    cmd[10] = "-f";
                    cmd[11] = "rawvideo";
                    cmd[12] = "-s";
                    cmd[13] = "80x70";
                    cmd[14] = previewdir + "/" + screenShotUnique + ".jpg";
                 	Misc.execute(cmd);
                    
                    
                    /* cmd = new String[15];
                    cmd[0] = ffmpegcmd;
                    cmd[1] = "-i";
                    cmd[2] = datadir + "/" + unique + "." + ext;
                    cmd[3] = "-itsoffset";
                    cmd[4] = "-4";
                    cmd[5] = "-vcodec";
                    cmd[6] = "mjpeg";
                    cmd[7] = "-vframes";
                    cmd[8] = "1";
                    cmd[9] = "-an";
                    cmd[10] = "-f";
                    cmd[11] = "rawvideo";
                    cmd[12] = "-s";
                    cmd[13] = "80x70";
                    cmd[14] = previewdir + "/" + screenShotUnique + ".jpg";
                    Misc.execute(cmd);
 */
                    /* cmd = new String[10];
                    cmd[0] = fveccmd;
                    cmd[1] = datadir + "/" + unique + "." + ext;
                    cmd[2] = "-tn";
                    cmd[3] = previewdir + "/" + screenShotUnique + ".jpg";
                    cmd[4] = "-tw";
                    cmd[5] = "80";
                    cmd[6] = "-th";
                    cmd[7] = "70";
                    cmd[8] = "-tt";
                    cmd[9] = "00:00:05";
                    Misc.execute(cmd); */

                    
                    ItemImage img = new ItemImage();
                    img.setItemUnique(itemUnique);
                    img.setType("webthumb");
                    img.setIndex(1);
                    img.setPath("/images/item/" + screenShotUnique + ".jpg");
                    img.setImageGroup(imageGroup);
                    img.setProfile(0);
                    img.setWidth(80);
                    img.setHeight(70);
                    itemimagesdao.addItem(img);

                    File srcPreview = new File(previewdir + "/" + screenShotUnique + ".jpg");
                    
                    imageGroup = Misc.generateUniqueId();
                    
                    screenShotUnique = Misc.generateUniqueId();
                    FileUtil.copyFile(srcPreview, previewdir + "/" + screenShotUnique + ".jpg");
                    img.setType("mobthumb");                    
                    img.setPath("/images/item/" + screenShotUnique + ".jpg");
                    img.setImageGroup(imageGroup);
                    img.setProfile(1);
                    itemimagesdao.addItem(img);

                    screenShotUnique = Misc.generateUniqueId();
                    FileUtil.copyFile(srcPreview, previewdir + "/" + screenShotUnique + ".jpg");
                    img.setPath("/images/item/" + screenShotUnique + ".jpg");
                    img.setProfile(2);
                    itemimagesdao.addItem(img);

                    screenShotUnique = Misc.generateUniqueId();
                    FileUtil.copyFile(srcPreview, previewdir + "/" + screenShotUnique + ".jpg");
                    img.setPath("/images/item/" + screenShotUnique + ".jpg");
                    img.setProfile(3);
                    itemimagesdao.addItem(img);

                    screenShotUnique = Misc.generateUniqueId();
                    FileUtil.copyFile(srcPreview, previewdir + "/" + screenShotUnique + ".jpg");
                    img.setPath("/images/item/" + screenShotUnique + ".jpg");
                    img.setProfile(4);
                    itemimagesdao.addItem(img);
                    
                    ItemResource res = new ItemResource();
                    res.setUnique(unique);
                    res.setItemUnique(itemUnique);
                    res.setContentType(cType);
                    res.setFileExt(ext);
                    res.setDataFile("/data/videoclips/" + owner + "/" + unique + "." + ext);
                    res.setMimeType(mimeType);
                    res.setWidth(width);
                    res.setHeight(height);
                    itemresourcesdao.saveItem(res);
                    
                    if(make3gp.equals("true")){
                    	unique=Misc.generateUniqueId();
                    	ext="3gp";
                    	mimeType = "video/3gpp";
                    	cType=ext;
                    	cmd = new String[19];
                        cmd[0] = ffmpegcmd;
                        cmd[1] = "-i";
                        cmd[2] = datafile.getAbsolutePath();
                        cmd[3] = "-s";
                        cmd[4] = "qcif";
                        cmd[5] = "-vcodec";
                        cmd[6] = "h263";
                        cmd[7] = "-acodec";
                        cmd[8] = "libvo_aacenc";
                        cmd[9] = "-ac";
                        cmd[10] = "1";
                        cmd[11] = "-ar";
                        cmd[12] = "8000";
                        cmd[13] = "-r";
                        cmd[14] = "25";
                        cmd[15] = "-ab";
                        cmd[16] = "32";
                        cmd[17] = "-y";
                        cmd[18] = datadir + "/" + unique + "." + ext;
                    	Misc.execute(cmd);
                        
                        res = new ItemResource();
                        res.setUnique(unique);
                        res.setItemUnique(itemUnique);
                        res.setContentType(cType);
                        res.setFileExt(ext);
                        res.setDataFile("/data/videoclips/" + owner + "/" + unique + "." + ext);
                        res.setMimeType(mimeType);
                        res.setWidth(width);
                        res.setHeight(height);
                       itemresourcesdao.saveItem(res);

                    }


                    VideoClip ii = new VideoClip();
                    ii.setUnique(itemUnique);
                    ii.setName1(smsCode);
                    ii.setName2(smsCode);
                    ii.setName3(smsCode);
                    ii.setPriceGroup(1);
                    ii.setTitle(title);
                    ii.setCategory(cat);
                    ii.setCreated(new Date());
                    ii.setLastAccessed(new Date());
                    ii.setOwner(owner);
                    ii.setDataFile1(timUrl.trim());
                    ii.setStatus(1);
                    ii.setRelease(sdf.parse(defRelease));
                    ii.setExpire(sdf.parse(defExpire));
                    ii.setClassification(classification);
                    videoclipdao.saveItem(ii);
                    
                    
                    
                    ItemLinkLog itemlinklog=null;
                        try {
                        	cmd = new String[3];
                        	String linkCmd =System.getProperty("contenturl")+ "/www/create_direct_links.sh";
                        	//String linkCmd =System.getProperty("contenturl")+ "/create_direct_links.sh";
                        	
                        	String tempLink = itemUnique + "."  + cType;  
                          
                            //System.out.println("teststream Content Link AFTER just before conversion  conversion "+cmd[1]+"   linkcmd "+linkCmd);
                            cmd[0] = linkCmd;
                            cmd[1] = System.getProperty("contenturl")+"/data/videoclips/" + owner + "/" + unique + "." + ext;
                            //cmd[1]=contentFile.getParent()+"/"+tempLink; 
                            //cmd[1]=contentFile.getPath(); //Original Video Storate Directory
                     
                           //cmd[2] = System.getProperty("document_root") + "/lib/slinks/" + tempLink;
                            cmd[2] = System.getProperty("contenturl")+"/www"+ "/lib/slinks/" + tempLink;
                            //cmd[2] = System.getProperty("contenturl")+ "/lib/slinks/" + tempLink;
                            
                            //System.out.println("teststream ContentUrl: cmd[1]="+cmd[1] +" --  cmd[2]="+cmd[2]);
                            
                            try{
                            if(execute(cmd)) {
                            	System.out.println("teststream command successfully executed ");
                            	itemlinklog=new ItemLinkLog();
                                itemlinklog.setUnique(Misc.generateUniqueId());
                                itemlinklog.setLink(tempLink);
                                itemlinklog.setStatus("1");
                                itemlinklog.setLogUnique(Misc.generateUniqueId());
                                itemlinklogdao.addItem(itemlinklog);
                            }
                            }catch(Exception e){System.out.println("teststream ContentUrl exception "+e);e.printStackTrace();}
                            
                            
                            
                            

                        } catch (Exception e) {System.out.println("Exception:" +e);e.printStackTrace();}

                    
                    
                    
                }
                

                
                System.out.println("itemUnique: " + itemUnique + ", " + width + ", " + height + ", " + ext);

                
            }
            else if (type.equals("bg")) {

                int code = misc.getSmsCode("bgImages");                
                String smsCode = "" + code;
                if (code==-1) smsCode = "";

                System.out.println("Received Bg Image");
                String baseDir = System.getProperty("document_root");
                String destDir = System.getProperty("installDir") + "/data/wallpapers/" + owner;
                String watermark128 = System.getProperty("installDir") + "/data/mxm_watermark_128.gif";
                String watermark110 = System.getProperty("installDir") + "/data/mxm_watermark_110.gif";
                String watermark80 = System.getProperty("installDir") + "/data/mxm_watermark_80.gif";
                String thumbDir = baseDir + "/images/bgs2";

                String unique = Misc.generateUniqueId();
                itemUnique = Misc.generateUniqueId();

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;

                if (ext.equals("jpg") || ext.equals("jpeg")) {
                    cType = "jpg";
                    mimeType = "image/jpeg";
                }
                else if (ext.equals("gif")) mimeType = "image/gif";
                else if (ext.equals("png")) mimeType = "image/png";

                File dir = new File(destDir);
                if (!dir.exists()) dir.mkdirs();

                File srcfile = new File(destDir + "/" + unique + "." + ext);
                FileUtil.writeToFile(srcfile, stream);

                if (srcfile.exists() && !srcfile.isDirectory()) {

                    System.out.println("Storing ItemResource");

                    BufferedImage orgImage = ImageIO.read(srcfile);
                    System.out.println("width:  " + orgImage.getWidth(null));
                    System.out.println("height:  " + orgImage.getHeight(null));


                    BufferedImage mxm128 = ImageIO.read(new File(watermark128));
                    BufferedImage mxm110 = ImageIO.read(new File(watermark110));
                    BufferedImage mxm80 = ImageIO.read(new File(watermark80));

                    ImageResize.resample(orgImage, thumbDir + "/128/" + itemUnique + ".jpg", 128, 128, mxm128);

                    ImageResize.resample(orgImage, thumbDir + "/110/" + itemUnique + ".jpg", 110, 110, mxm110);

                    ImageResize.resample(orgImage, thumbDir + "/80/" + itemUnique + ".jpg", 80, 80, mxm80);

                    BufferedImage img64 = (BufferedImage) ImageResize.resample(orgImage, thumbDir + "/64/" + itemUnique + ".gif", 64, 64, mxm80);

                    ImageResize.resample(img64, thumbDir + "/64/" + itemUnique + ".jpg", 64, 64);

                    BufferedImage img40 = (BufferedImage) ImageResize.resample(orgImage, thumbDir + "/40/" + itemUnique + ".gif", 40, 40);

                    ImageResize.resample(img40, thumbDir + "/40/" + itemUnique + ".jpg", 40, 40);
                    
                    ItemResource res = new ItemResource();
                    res.setUnique(unique);
                    res.setItemUnique(itemUnique);
                    res.setContentType(cType);
                    res.setFileExt(ext);
                    res.setDataFile("/data/wallpapers/" + owner + "/" + unique + "." + ext);
                    res.setMimeType(mimeType);
                    System.out.println("width:  " + orgImage.getWidth(null));
                    System.out.println("height:  " + orgImage.getHeight(null));

                    res.setWidth(orgImage.getWidth(null));
                    res.setHeight(orgImage.getHeight(null));
                    itemresourcesdao.saveItem(res);

                    
                    BgImage ii = new BgImage();
                    ii.setUnique(itemUnique);
                    ii.setName1(smsCode);
                    ii.setName2(smsCode);
                    ii.setName3(smsCode);
                    ii.setName4(smsCode);
                    ii.setTitle(title);
                    ii.setPicName("");
                    ii.setPriceGroup(1);
                    ii.setStatus(1);
                    ii.setRequestCount(0);
                    ii.setCategory(cat);
                    ii.setLastAccessed(new Date());
                    ii.setCreated(new Date());
                    ii.setOwner(owner);
                    ii.setOriginalFile(unique + "." + ext);
                    ii.setOriginalType(2);
                    ii.setRelease(sdf.parse(defRelease));
                    ii.setExpire(sdf.parse(defExpire));
                    ii.setClassification(classification);
                    
                    bgimagesdao.saveItem(ii);
                    

                }

            }
            else if (type.equals("bgres")) {

                String unique = Misc.generateUniqueId();

                BgImage ii = bgimagesdao.getItem(itemUnique);
                if (ii==null) return;

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;

                if (ext.equals("jpg") || ext.equals("jpeg")) {
                    cType = "jpg";
                    mimeType = "image/jpeg";
                }
                else if (ext.equals("gif")) mimeType = "image/gif";
                else if (ext.equals("png")) mimeType = "image/png";

                String destDir = System.getProperty("installDir") + "/data/wallpapers/" + ii.getOwner();

                File dir = new File(destDir);
                if (!dir.exists()) dir.mkdirs();

                File srcfile = new File(destDir + "/" + unique + "." + ext);
                FileUtil.writeToFile(srcfile, stream);

                if (srcfile.exists() && !srcfile.isDirectory()) {

                    System.out.println("Storing ItemResource");
                    BufferedImage orgImage = ImageIO.read(srcfile);
                  
                    ItemResource res = new ItemResource();
                    res.setUnique(unique);
                    res.setItemUnique(itemUnique);
                    res.setContentType(cType);
                    res.setFileExt(ext);
                    res.setDataFile("/data/wallpapers/" + ii.getOwner() + "/" + unique + "." + ext);
                    res.setMimeType(mimeType);
                    res.setWidth(orgImage.getWidth(null));
                    res.setHeight(orgImage.getHeight(null));
                    itemresourcesdao.saveItem(res);


                }
            }
            else if (type.equals("videores")) {

                String unique = Misc.generateUniqueId();

                VideoClip ii = videoclipdao.getItem(itemUnique);
                if (ii==null) return;

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;

                if (ext.equals("mpg") || ext.equals("mpeg") || ext.equals("mpe") || ext.equals("mp2") || ext.equals("mpa")) {
                    cType = "mpeg"; mimeType = "video/mpeg";
                }
                else if (ext.equals("mov") || ext.equals("qt")) { cType = "mov"; mimeType = "video/quicktime"; }
                else if (ext.equals("avi")) mimeType = "video/x-msvideo";
                else if (ext.equals("mp4")) mimeType = "video/mp4";
                else if (ext.equals("3gp") || ext.equals("3gpp")) { cType = "3gp"; mimeType = "video/3gpp"; }
                else if (ext.equals("3g2") || ext.equals("3gpp2")) { cType = "3g2"; mimeType = "video/3gpp2"; }
                else if (ext.equals("wmv")) mimeType = "video/x-ms-wmv";
                else if (ext.equals("flv")) mimeType = "video/x-flv";
                
                if (!mimeType.equals("")) {

                   // String destDir = System.getProperty("installDir") + "/data/videoclips/" + ii.getOwner();
                    String destDir = "/var/lib/tomcat7/webapps/ROOT/data/videoclips/" + ii.getOwner();

                    File dir = new File(destDir);
                    if (!dir.exists()) dir.mkdirs();

                    File srcfile = new File(destDir + "/" + unique + "." + ext);
                    FileUtil.writeToFile(srcfile, stream);

                    if (srcfile.exists() && !srcfile.isDirectory()) {

                        System.out.println("Storing ItemResource");

                        ItemResource res = new ItemResource();
                        res.setUnique(unique);
                        res.setItemUnique(itemUnique);
                        res.setContentType(cType);
                        res.setFileExt(ext);
                        res.setDataFile("/data/videoclips/" + ii.getOwner() + "/" + unique + "." + ext);
                        res.setMimeType(mimeType);
                        res.setWidth(0);
                        res.setHeight(0);
                        itemresourcesdao.saveItem(res);
                    
                    }
                }
            }
            else if (type.equals("masterres")) {

                String unique = Misc.generateUniqueId();

                Mastertone ii = mastertonedao.getItem(itemUnique);
                if (ii==null) return;

                String ext = "";
                String cType = "";
                String mimeType = "";
                if (item.getName().indexOf(".")>-1) ext = item.getName().substring(item.getName().lastIndexOf(".")+1);

                ext = ext.toLowerCase();
                cType = ext;
                if (ext.equals("mp3")) mimeType = "audio/mpeg";
                else if (ext.equals("m4a")) { mimeType = "audio/m4a"; cType = "mp4"; }
                else if (ext.equals("mp4")) mimeType = "audio/mp4";
                else if (ext.equals("aac")) mimeType = "audio/aac";
                else if (ext.equals("awb")) mimeType = "audio/amr-wb";
                else if (ext.equals("amr")) mimeType = "audio/amr";

                if (!mimeType.equals("")) {

                    String destDir = System.getProperty("installDir") + "/data/mastertones/" + ii.getOwner();

                    File dir = new File(destDir);
                    if (!dir.exists()) dir.mkdirs();

                    File srcfile = new File(destDir + "/" + unique + "." + ext);
                    FileUtil.writeToFile(srcfile, stream);

                    if (srcfile.exists() && !srcfile.isDirectory()) {

                        System.out.println("Storing ItemResource");

                        ItemResource res = new ItemResource();
                        res.setUnique(unique);
                        res.setItemUnique(itemUnique);
                        res.setContentType(cType);
                        res.setFileExt(ext);
                        res.setDataFile("/data/mastertones/" + ii.getOwner() + "/" + unique + "." + ext);
                        res.setMimeType(mimeType);
                        res.setWidth(0);
                        res.setHeight(0);
                        itemresourcesdao.saveItem(res);

                    }
                }
            }
            
            else if (type.equals("csv")) {
            	String[] questionPacks=null;
            	String unique = Misc.generateUniqueId();
            	File srcFile=new File("/opt/"+unique+".csv");
            	FileUtil.writeToFile(srcFile, stream);
            	String questionPackId = aReq.get("questionPackId");
            	if(questionPackId.contains("?"))
            		questionPacks=questionPackId.split("\\?");
            	else
            		questionPacks[0]=questionPackId;
            	if(srcFile.exists()){
            		java.util.List<Question> questionList=csvParseUtil.parseCSV(srcFile.getPath());
            		questionDao.saveQuestions(questionList);
            		for(int i=0;i<questionPacks.length;i++){
            			java.util.List<QuestionInPack> questionPackList=new ArrayList<QuestionInPack>();
            			for(Question question:questionList){
            				QuestionInPack questionInPack=new QuestionInPack();
            				questionInPack.setQuestionPackId(questionPacks[i]);
            				questionInPack.setQuestionId(question.getQuestionId());
            				questionPackList.add(questionInPack);
            			}
            			questionInPackDao.saveQuestionInPack(questionPackList);
            		}
            	}
            	
            	
            	
            }
            
            if (!itemUnique.equals("") && (type.equals("master") || type.equals("bg") || type.equals("video") || type.equals("anim"))) {
                                
                String[] clientArray = request.getParameterValues("client");
                if (clientArray!=null) {
                	Query query=null;
                    
                   // con = DBH.getConnection();
                    String sqlstr = "";
                    
                    for (int i=0; i<clientArray.length; i++) {
                        sqlstr = "INSERT INTO clientContent VALUES('" + Misc.generateUniqueId() + "','" + clientArray[i] + "','" + itemUnique + "'"
                                + ",'" + type + "','" + MiscDate.now24sql() + "','1','" + MiscDate.now24sql() + "')";
                        query=dbsession.createSQLQuery(sqlstr);
                        int stats=query.executeUpdate();
                       // DBH.execUpdate(con, sqlstr);                       
                        
                    }
                    dbsession.close();
                    
                   // DBH.closeConnection(con);
                }
                
            }
        }
    }
    
    UmeTempCmsCache.itemCategoryMap = itemcategorydao.getCategoryMap();
    
    /*
    java.util.List<ItemCategory> iclist = null;
    ItemCategory ic = null;
    String catType = "";
    
    if (!cat.equals("")) {
        
        if (type.equals("master")) {
            if (toneType.equals("1")) catType = "mastertone";
            else if (toneType.equals("4")) catType = "truetone";
            else if (toneType.equals("5")) catType = "funtone";            
        }
        else if (type.equals("anim")) catType = "gifanim";
        else if (type.equals("video")) catType = "video";
        else if (type.equals("bg")) catType = "bgimage";
        
        if (!catType.equals("")) {
            iclist = UmeTempCmsCache.itemCategoryMap.get(catType);
            if (iclist!=null) {
                for (int i=0; i<iclist.size(); i++) {
                    ic = iclist.get(i);
                    if (ic.getUnique().equals(cat)) {
                        ic.setItemCount(itemcategorydao.getItemCount(cat, catType));
                        break;
                    }
                }
            }
        }
    }
 */
}

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

%>
ok