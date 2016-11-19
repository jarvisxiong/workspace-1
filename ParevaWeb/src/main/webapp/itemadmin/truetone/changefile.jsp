<%@include file="/WEB-INF/jspf/itemadmin/truetone/changefile.jspf"%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
    <link rel="stylesheet" href="<%=stylesheet%>" type="text/css">
</head>
<body>  

<table cellspacing="0" cellpadding="0" border="0" width="98%">
<tr><td valign="top" align="left">
    <table cellspacing="0" cellpadding="3" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="blue_14"><b>Update Content Files:</b> <span class="grey_12"><b><%=item.getSongName()%></b></span></td>
        <td align="right" valign="bottom" class="status">&nbsp;<%=statusMsg%></td>
    </tr>
    </table>    
</td></tr>
<tr><td valign="top" align="left">
    <% int _curitem = Integer.parseInt(aReq.get("_curitem", "1")); %>
    <%@ include file="/itemadmin/truetone/tabs.jsp" %>
    <br>
</td></tr>

<tr>
<td valign="top" align="left">   

        <form enctype="multipart/form-data" action="<%=fileName%>.jsp" method="post" style="padding: 0px; margin: 0px;">
        <input type="hidden" name="unq" value="<%=unique%>">
        <input type="hidden" name="cat" value="<%=cat%>">
        <input type="hidden" name="si" value="<%=sIndex%>">
        <input type="hidden" name="sfile" value="<%=Misc.hex8Code(sFile)%>">
        <input type="hidden" name="dfile" value="<%=Misc.hex8Code(dFile)%>">
        
        <table cellspacing="0" cellpadding="0" border="0" width="98%">
        <tr><td colspan="2">
    
                <table cellpadding="6" cellspacing="0" border="0">                        
                   <tr>
                        <td class="grey_11"><nobr>Upload source file:</nobr></td>                            
                        <td><input type="file" size="20" name="odata" class="textbox" value=""></td>
                    </tr>
                    <tr><td colspan="2">&nbsp;</td></tr> 

                    <tr>
                        <td class="grey_11"><nobr>Upload data file:</nobr></td>
                        <td><input type="file" size="20" name="ddata" class="textbox" value=""></td>
                    </tr>
                    <tr><td colspan="2">&nbsp;</td></tr> 

                    <tr>
                        <td class="grey_11"><nobr>Upload sample file:</nobr></td>
                        <td><input type="file" size="20" name="sdata" class="textbox" value=""></td>
                    </tr>

                    <tr><td colspan="2">&nbsp;</td></tr>

                    </td></tr>

                </table>
                
            </td></tr>
            <tr>
            <td align="left" valign="bottom" class="red_12">
                <%=statusMsg1%><br>
                <%=statusMsg%><br>
                <%=statusMsg2%>
            </td>
            <td align="right" valign="bottom"><input type="submit" name="submit" value="&nbsp;&nbsp;Upload&nbsp;&nbsp;"></td>
            </tr>
        </table>

        </form>

        <form action="<%=fileName%>.jsp" method="post" style="padding: 0px; margin: 0px;">
        <input type="hidden" name="unq" value="<%=unique%>">
        <input type="hidden" name="cat" value="<%=cat%>">
        <input type="hidden" name="si" value="<%=sIndex%>">
        <input type="hidden" name="sfile" value="<%=Misc.hex8Code(sFile)%>">
        <input type="hidden" name="dfile" value="<%=Misc.hex8Code(dFile)%>">


        <table cellspacing="0" cellpadding="6" border="0" width="100%">
            <tr><td><img src="/images/red_dot.gif" height="1" width="100%"></td></tr> 
           </table> 

            <table cellspacing="0" cellpadding="3" border="0" width="100%">

             <tr>
           <td align="center" class="blue_12">
               Bitrate:&nbsp;
               <select name="bitrate">
                   <option value="16" <% if (bitr.equals("16")){%> selected <% }%>>16 kbps</option>
                   <option value="24" <% if (bitr.equals("24")){%> selected <% }%>>24 kbps</option>
                   <option value="32" <% if (bitr.equals("32")){%> selected <% }%>>32 kbps</option>
                   <option value="40" <% if (bitr.equals("40")){%> selected <% }%>>40 kbps</option>
                   <option value="56" <% if (bitr.equals("56")){%> selected <% }%>>56 kbps</option>
                   <option value="64" <% if (bitr.equals("64")){%> selected <% }%>>64 kbps</option>
                   <option value="96" <% if (bitr.equals("96")){%> selected <% }%>>96 kbps</option>
                   <option value="128" <% if (bitr.equals("128")){%> selected <% }%>>128 kbps</option>                   
               </select>
               &nbsp;

                Mode&nbsp;
               <select name="cmode">
                   <option value="j" <% if (cmode.equals("j")){%> selected <% }%>>Stereo</option>
                   <option value="m" <% if (cmode.equals("m")){%> selected <% }%>>Mono</option>             
               </select>
               <!--
                Freq:&nbsp;
               <select name="freq">
                   <option value="11.025" <% if (freq.equals("11.025")){%> selected <% }%>>11.025 kHz</option>
                   <option value="22.05" <% if (freq.equals("22.05")){%> selected <% }%>>22.050 kHz</option>
                   <option value="44.1" <% if (freq.equals("44.1")){%> selected <% }%>>44.100 kHz</option>                  
               </select>
               -->
               &nbsp;
               <!--
               Duration:&nbsp;
            <input type="text" name="dur" size="5">
            -->
           </td>
           </tr>
            </table>

            <table cellspacing="0" cellpadding="6" border="0" width="100%">
            <tr><td><img src="/images/red_dot.gif" height="1" width="100%"></td></tr> 
            </table> 

            <table cellspacing="0" cellpadding="3" border="0" width="100%">
            <tr><td align="left" class="blue_11"><b>Source file exists: <%=orgExists%></b></td>
             <td align="right" class="blue_11">
                <% if (orgExists){ %>
            <object type="application/x-shockwave-flash" data="/lib/musicplayer.swf?song_url=/itemadmin/truetone/getfile.jsp%3Fdir=<%=Misc.hex8Code("/data/truetones/originals")%>%26file=<%=Misc.hex8Code(unique + ".mp3")%>" width="17" height="17">
            <param name="movie" value="/lib/musicplayer.swf?song_url=/itemadmin/truetone/getfile.jsp%3Fdir=<%=Misc.hex8Code("/data/truetones/originals")%>%26file=<%=Misc.hex8Code(unique + ".mp3")%>" />
            </object>
                <% } %>
            </td>
            </tr>
            <tr><td colspan="2" align="left" class="blue_11">

            <% if (properties!=null) { 


                int bitrate = 0;
                try { bitrate = ((Integer) properties.get("mp3.bitrate.nominal.bps")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                int bytelength = 0;
                try { bytelength = ((Integer) properties.get("mp3.length.bytes")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                long duration = 0;
                try { duration = ((Long) properties.get("duration")).longValue() / 1000000; }
                catch (Exception e) { System.out.println(e); }

                int frequency = 0;
                try { frequency = ((Integer) properties.get("mp3.frequency.hz")).intValue(); }
                catch (Exception e) { System.out.println(e); }

                if (frequency<22000) duration = duration*2;

                int mode = -1;
                try { mode = ((Integer) properties.get("mp3.mode")).intValue(); }
                catch (Exception e) { System.out.println(e); }
                String modeStr = "";
                if (mode==0) modeStr = "Stereo";
                else if (mode==1) modeStr = "Joint";
                else if (mode==2) modeStr = "Dual";
                else if (mode==3) modeStr = "Single";


            %>

                   Rate: <%=bitrate%> Kbps&nbsp;&nbsp;
                   Size: <%=bytelength%> Kb&nbsp;&nbsp;
                   Freq: <%=frequency%> Hz&nbsp;&nbsp;
                   Dur: <%=duration%> s&nbsp;&nbsp;
                   Chnls: <%=properties.get("mp3.channels")%>&nbsp;&nbsp;
                   Mode: <%=modeStr%><br><br>
                   <!--
                   <span class="grey_11">
                       <% 
                       Iterator it = properties.keySet().iterator();
                        while (it.hasNext()) {
                           Object key = it.next(); 
                           out.println(key + ":" + properties.get(key) + "<br>");                           
                        }
                       %>                           
                   </span>
                   -->

           <% } %>
            </td></tr>
            <tr>
           <td align="center" class="blue_12">

                <input type="submit" name="conv_src" value="Convert source">
           </td>
           </tr>
            </table>

            <table cellspacing="0" cellpadding="6" border="0" width="100%">
            <tr><td><img src="/images/red_dot.gif" height="1" width="100%"></td></tr> 
            </table> 

            <table cellspacing="0" cellpadding="3" border="0" width="100%">
            <tr>
            <td align="left" class="blue_11"><b>Data file exists: <%=dataExists%></b></td>
             <td align="right" class="blue_11">
                <% if (dataExists){ %>                    
            <object type="application/x-shockwave-flash" data="/lib/musicplayer.swf?song_url=/itemadmin/truetone/getfile.jsp%3Fdir=<%=Misc.hex8Code("/data/truetones")%>%26file=<%=Misc.hex8Code(dFile)%>" width="17" height="17">
            <param name="movie" value="/lib/musicplayer.swf?song_url=/itemadmin/truetone/getfile.jsp%3Fdir=<%=Misc.hex8Code("/data/truetones")%>%26file=<%=Misc.hex8Code(dFile)%>" />
            </object>
                <% } %>
            </td>
            </tr>
            <tr><td colspan="2" align="left" class="blue_11">

            <% if (dataProps!=null) {                     

                int bitrate = 0;
                try { bitrate = ((Integer) dataProps.get("mp3.bitrate.nominal.bps")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                 int bytelength = 0;
                try { bytelength = ((Integer) dataProps.get("mp3.length.bytes")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                long duration = 0;
                try { duration = ((Long) dataProps.get("duration")).longValue() / 1000000; }
                catch (Exception e) { System.out.println(e); }

                int frequency = 0;
                try { frequency = ((Integer) dataProps.get("mp3.frequency.hz")).intValue(); }
                catch (Exception e) { System.out.println(e); }

                if (frequency<22000) duration = duration*2;

                int mode = -1;
                try { mode = ((Integer) dataProps.get("mp3.mode")).intValue(); }
                catch (Exception e) { System.out.println(e); }
                String modeStr = "";
                if (mode==0) modeStr = "Stereo";
                else if (mode==1) modeStr = "Joint";
                else if (mode==2) modeStr = "Dual";
                else if (mode==3) modeStr = "Single";      


            %>

                   Rate: <%=bitrate%> Kbps&nbsp;&nbsp;
                   Size: <%=bytelength%> Kb&nbsp;&nbsp;
                   Freq: <%=frequency%> Hz&nbsp;&nbsp;
                   Dur: <%=duration%> s&nbsp;&nbsp;
                   Chnls: <%=dataProps.get("mp3.channels")%>&nbsp;&nbsp;
                   Mode: <%=modeStr%><br><br>    

           <% } %>  

            </td></tr>
            <tr>
           <td align="center" class="blue_12">
               <% if (orgExists){ %>
               <input type="submit" name="create_data" value="Create from source">
               &nbsp;
               <% } %>
               <% if (dataExists){ %>
               &nbsp;
                <input type="submit" name="conv_data" value="Convert data">
                <% } %>
           </td>
           </tr>
            </table>

            <table cellspacing="0" cellpadding="6" border="0" width="100%">
            <tr><td><img src="/images/red_dot.gif" height="1" width="100%"></td></tr> 
            </table> 

            <table cellspacing="0" cellpadding="3" border="0" width="100%">
            <tr>
            <td align="left" class="blue_11"><b>Sample file exists: <%=sampleExists%></b></td>
            <td align="right" class="blue_11">
                <% if (sampleExists){ %>
            <object type="application/x-shockwave-flash" data="/lib/musicplayer.swf?song_url=/images/true4ih86tr54e/<%=sFile%>" width="17" height="17">
            <param name="movie" value="/lib/musicplayer.swf?song_url=/images/true4ih86tr54e/<%=sFile%>" />
            </object>
                <% } %>
            </td>
            </tr>
            <tr><td colspan="2" align="left" class="blue_11">

            <% if (sampleProps!=null) {                     

                int bitrate = 0;
                try { bitrate = ((Integer) sampleProps.get("mp3.bitrate.nominal.bps")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                 int bytelength = 0;
                try { bytelength = ((Integer) sampleProps.get("mp3.length.bytes")).intValue() / 1000; }
                catch (Exception e) { System.out.println(e); }

                long duration = 0;
                try { duration = ((Long) sampleProps.get("duration")).longValue() / 1000000; }
                catch (Exception e) { System.out.println(e); }

                int frequency = 0;
                try { frequency = ((Integer) sampleProps.get("mp3.frequency.hz")).intValue(); }
                catch (Exception e) { System.out.println(e); }

                if (frequency<22000) duration = duration*2;

                int mode = -1;
                try { mode = ((Integer) sampleProps.get("mp3.mode")).intValue(); }
                catch (Exception e) { System.out.println(e); }
                String modeStr = "";
                if (mode==0) modeStr = "Stereo";
                else if (mode==1) modeStr = "Joint";
                else if (mode==2) modeStr = "Dual";
                else if (mode==3) modeStr = "Single";                    
            %>

                   Rate: <%=bitrate%> Kbps&nbsp;&nbsp;
                   Size: <%=bytelength%> Kb&nbsp;&nbsp;
                   Freq: <%=frequency%> Hz&nbsp;&nbsp;
                   Dur: <%=duration%> s&nbsp;&nbsp;
                   Chnls: <%=sampleProps.get("mp3.channels")%>&nbsp;&nbsp;
                   Mode: <%=modeStr%><br><br>       

           <% } %>  

            </td></tr>
            <tr>
           <td align="center" class="blue_12">
               <% if (orgExists){ %>
               <input type="submit" name="create_sample" value="Create from source">
               &nbsp;
               <% } %>
                <% if (sampleExists){ %>
               &nbsp;
                <input type="submit" name="conv_sample" value="Convert sample">
                <% } %>
           </td>
           </tr>
            </table>



        </form>
        <br><br>
        </td></tr>
        </table>    
    </body>
</html>



