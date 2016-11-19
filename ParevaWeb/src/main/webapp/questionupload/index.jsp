<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Question Upload</title>

<script src="http://cdn.jquerytools.org/1.2.4/full/jquery.tools.min.js"></script>

<script type="text/javascript" src="/lib/swfobject/swfobject.js"></script>

<link rel="stylesheet" href="/lib/jquery/uploadify-2.1.4/uploadify.css"
	media="screen" />
<script type="text/javascript"
	src="/lib/jquery/uploadify-2.1.4/jquery.uploadify.v2.1.4.min.js"></script>

<script>
var questionPackId = '';
	function updateUploader() {
		 var elem = document.getElementById("questionPack");
		
	        if (elem!=null){ //questionPackId = elem[elem.selectedIndex].value;
	        
	        for (var i = 0; i < elem.length; i++) {
	            if (elem.options[i].selected){ 
	            	questionPackId=questionPackId+elem.options[i].value+'?';
	            }
	        }
	        }
	        questionPackId=questionPackId.substring(0,questionPackId.length-1);
	        alert(questionPackId);
	        if (questionPackId=='') {
	            alert("Please select QuestionPack");
	            return;
	        }

		var $upload = $("#file_upload");
		$upload.uploadifySettings('scriptData', {
			'type' : 'csv','questionPackId' : questionPackId
		});
		$upload.uploadifyUpload();
	}

	$(document)
			.ready(
					function() {

						$('#file_upload')
								.uploadify(
										{
											'uploader' : '/lib/jquery/uploadify-2.1.4/uploadify.swf',
											'script' : '/umeadmin/act_upload.jsp',
											'cancelImg' : '/lib/jquery/uploadify-2.1.4/cancel.png',
											'scriptData' : {
												'type' : 'csv',
												'questionPackId' : questionPackId
											},
											'method' : 'GET',
											'fileDesc' : 'Quiz Question',
											'multi' : true,
											'folder' : '/uploads',
											'scriptAccess' : 'always',

											onAllComplete : function(event,
													data) {
												$("#filesUploaded")
														.html(
																"Files Uploaded: "
																		+ data.filesUploaded);
												$("#uploadStatus").show();
											},
											onComplete : function(event, ID,
													fileObj, response, data) {
												//alert(event + ": " + ID + ": " + response);
											},
											onSelect : function(a, b, c) {
												$("#uploadStatus").hide();
											},
											'buttonText' : 'SELECT FILES'
										});

					});
</script>

</head>
<body>

<jsp:include page="/QuestionUpload"/>

	<form action="index.jsp" method="post"
		style="padding: 0px; margin: 0px;">
		<input type="hidden" name="ttype" id="ttype" value="">

		<table cellspacing="0" cellpadding="0" border="0" width="99%">
			<tr>
				<td>
					<table cellspacing="0" cellpadding="4" border="0" width="100%">
						<tr>
							<td align="left" valign="bottom" class="big_blue">Upload
								Question</td>
							<td align="right" valign="bottom" class="status">&nbsp;</td>
						</tr>
					</table>
				</td>
			</tr>
			<tr>
				<td><img src="/images/grey_dot.gif" height="1" width="100%"></td>
			</tr>
			<tr>
				<td>

					<div id="uploadStatus" style="display: none;" class="statusmsg">
						<span> You files were uploaded.<br>
						</span><br /> <br /> <span id="filesUploaded"></span><br />
					</div>

					<table cellspacing="0" cellpadding="6" border="0" width="100%">


						<tr>
							<td>Select files ():</td>
							<td align="left"><input type="file" name="file_upload"
								id="file_upload"></td>
						</tr>
						<tr>
						<select multiple name="questionPack" id="questionPack">
							<option value="">Select QuestionPack</option>
							<c:forEach items="${questionPackList}" var="questionPack">
							<option value="${questionPack.aUnique}">${questionPack.aName}</option>
							</c:forEach>
						</select>
						</tr>

						<tr>
							<td class="grey_12">&nbsp;</td>
							<td><input type="button" name="upload" value="Upload"
								onclick="javascript:updateUploader();" style="width: 150px;">
							</td>
						</tr>

					</table>

				</td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td><img src="/images/grey_dot.gif" height="1" width="100%"></td>
			</tr>
			<tr>
				<td>&nbsp;</td>
			</tr>
			<tr>
				<td class="grey_12">You can user this service to upload videos.
					You can select multiple files at the same time and each one of them
					are added as unique content items.</td>
			</tr>
		</table>
	</form>

</body>
</html>




