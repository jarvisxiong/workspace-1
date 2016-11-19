<%-- 
    Document   : videoEx
    Created on : 18-Apr-2012, 12:38:13
    Author     : madan
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<body>
<link href="http://vjs.zencdn.net/c/video-js.css" rel="stylesheet">
<script src="http://vjs.zencdn.net/c/video.js"></script>


<!-- <video id="my_video_1" class="video-js vjs-default-skin" controls
  preload="auto" width="640" height="264" poster="my_video_poster.png"
  data-setup="{}">
  <source src="0018983651031KDS.mp4" type="video/mp4" />
  
</video>
-->
<!--<video id="my_video_1" class="video-js vjs-default-skin" controls
  preload="auto" width="320" height="240"  data-setup="{}">
  <source src="0018983651031KDS.mp4" type='video/mp4'>
  
</video>-->
<!--<EMBED SRC="0065153097131KDS.3gp" loop="1" height="??" width="??" autostart="true"></EMBED>-->
 
<video width="320" height="240" controls="controls">
  <source src="0065153097131KDS.3gp" type="video/3gpp" />
  <object data="0065153097131KDS.3gp" width="320" height="240">
<embed src="0065153097131KDS.3gp" width="320" height="240" autostart="false" >
Your browser does not support video
</embed>
</object>
</video> 

</body>
</html>
