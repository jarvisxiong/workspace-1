<%@include file="/WEB-INF/jspf/admin/domains.jspf"%>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=<%=pageEnc%>">
</head>
<body>
<div class="alertHolder"></div>
<div class="statusMsg" style="visibility: hidden; position: absolute;"><%=statusMsg%></div>
<form method="post" action="admin/domains.jsp">
	<div class="portlet box green">
	    <div class="portlet-title">
	        <div class="caption">
	            <i class="fa fa-cogs"></i>
	            Domains
	        </div> 
			<div class="actions">
				<input style="visibility: hidden" type="submit" class="button2" name="delete" value="<%=lp.get(15)%>">
                <a href="javascript:;" class="btn btn-circle btn-default jsFormSubmit" data-action="delete" >
                    <i class="fa fa-remove"></i> 
                    Delete Selected
                </a>
                <a href="javascript:;" class="btn btn-circle btn-default jsAddNewDomain">
                    <i class="fa fa-plus"></i> 
                    ADD NEW DOMAIN
                </a>
                <a class="btn btn-circle btn-icon-only btn-default fullscreen" href="javascript:;" data-original-title="" title=""> </a>
            </div>
	    </div>
	    <div class="portlet-body">
	    	<div class="form-group form-md-line-input form-md-floating-label jsAddDomainHolder" style="display: none;">
                <div class="input-group">

                    <div class="input-group-control">
                        <input type="text" class="form-control" name="addName" />
                        
                        <label for="form_control_1">Enter the new domain name</label>
                    </div> 
                    <span class="input-group-btn btn-right">
                        <div 
                        		class="btn blue-madison jsFormSubmit" 
                        		data-action="add" 
                        		value="ADD NEW DOMAIN"  >
	                        ADD NEW DOMAIN
	                    </div>
	                      
                    </span>
                </div>
            </div> 



	        <div class="table-responsive">
	            <table class="table table-striped table-bordered table-hover">
	                <thead>
	                    <tr>
	                        <th> # </th>
				            <th><%=lp.get(8)%></th>
				            <th><%=lp.get(9)%></th>
				            <th>Default URL</th>
				            <th><%--=lp.get(11)--%>Select for deletion</th>
	                    </tr>
	                </thead>
	                <tbody>
	                	<% for (int i=0; i<list.size(); i++) {
    						sdcd = list.get(i);
						%>
						<tr>
							<td><%=(i+1)%></td> 	
							<td>
								<a data-url="domainDetails.jsp?dmid=<%=sdcd.getUnique()%>" class="jsLoad"><%=sdcd.getName()%></a>
							</td>

							<td>
								<% if (sdcd.getActive() == 1 ) { %>
			                        <span class="label label-sm label-success"> 
			                            <%=sdcd.getActive()%> 
			                        </span>
			                        <% } else { %>
			                        <span class="label label-sm label-default"> 
			                            <%=sdcd.getActive()%> 
			                        </span>
			                    <% } %>
							</td>
							<td><%=sdcd.getDefaultUrl()%></td>
							<td>
								<div class="md-checkbox">
                                    <input type="checkbox" id="checkbox<%=(i+1)%>" class="md-check" name="del_<%=sdcd.getUnique()%>">
                                    <label for="checkbox<%=(i+1)%>">
                                        <span class="inc"></span>
                                        <span class="check"></span>
                                        <span class="box"></span></label>
                                </div>
							</td>
						</tr>
						<% } %>
	                    
	                </tbody>
	            </table>
	        </div>
	    </div>
	</div>


</form>

<script>
	$( document ).ready(function() {

	function submitFrm(action) {
	    var frm = $('form');
	    frm.append('<input type="hidden" class="button2"  name="'+ action +' value="yesss" ">' );
	    frm.submit(function (ev) {
	        $.ajax({
	            type: frm.attr('method'),
	            url: frm.attr('action'),
	            data: frm.serialize(),
	            success: function (data) {
		            var success =  $($.parseHTML(data)).filter(".statusMsg").text(); 
		            alertInfo(success);
	            }
	        });
	        ev.preventDefault();
	    });
	}

	$(".portlet").on("click", ".jsFormSubmit", function() {
		var action = '&' + $(this).data('action')+'=1';
		var frm = $('form');
		var data = frm.serialize()+action;
		var el = $(this).closest(".portlet").children(".portlet-body");
		App.blockUI({
            target: el,
            animate: true,
            overlayColor: 'black',
            message: 'Please Wait, we are either pricessing or the server is out of memory. Again. James?'
            }); 
	        $.ajax({
	            type: frm.attr('method'),
	            url: frm.attr('action'),
	            data: data,
	            success: function (data) {
	            	App.unblockUI(el);
	            	var parsed 	= $.parseHTML(data);
		            var success = $(parsed).filter(".statusMsg").text();
		            var portlet = $(parsed).find(".portlet-body");
		            alertInfo(success);
		            console.log(portlet);
		            $(".portlet-body").html(portlet);
	            }
	        });
	});

	$(".portlet").on("click", ".jsAddNewDomain", function() {
		$(".jsAddDomainHolder").slideToggle(333);
	});	
  
	function alertInfo(alertText) {
        App.alert({ 
            container   : ".alertHolder",   // alerts parent container
            place       : 'append',         // append or prepent in container
            type        : 'info',         // alert's type
            message     : alertText, // alert's message
            close       : true,             // make alert closable
            reset       : true,            // close all previouse alerts first
            icon        : 'fa fa-info',    // icon 
            closeInSeconds: 120,           // auto close after defined seconds 
            focus       : true              // auto scroll to the alert after shown 

            // TODO eventually - if Madan sends me the type of status Message can make it success, fail, info etc.
        });
    }

	    $('body').on("click", ".jsLoad", function(e) {
	        var url = $(this).data("url");
	        $('.contentHolder').load("../admin/" + url);
	    });

    }); 
</script>
</body>
</html>












<%--


<table cellspacing="0" cellpadding="5" border="0" width="98%">
<tr>
<td valign="top" align="center">

    <table cellspacing="0" cellpadding="5" border="0" width="100%">
    <tr>
        <td align="left" valign="bottom" class="big_blue">Domains</td>
        <td align="right" valign="bottom" class="status">&nbsp;</td>
    </tr>
    </table>
 a
 

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>

       <table cellspacing="0" cellpadding="5" border="0" width="100%" class="dotted">
       <tr>
            <th align='middle'>&nbsp;</th>
            <th align='middle'><%=lp.get(8)%></th>
            <th align='middle'><%=lp.get(9)%></th>
            <th align='middle'>Default URL</th>
            <th align='middle'><%=lp.get(11)%></th>
        <tr>

<% 

for (int i=0; i<list.size(); i++) {
    sdcd = list.get(i);

%>

<tr>
	<td align='middle'><%=(i+1)%></td> 	
	<td align='middle'><a href="domainDetails.jsp?dmid=<%=sdcd.getUnique()%>"><%=sdcd.getName()%></a></td>
	<td align='middle'><%=sdcd.getActive()%></td>
	<td align='middle'><%=sdcd.getDefaultUrl()%></td>
	<td align='middle'><input type="checkbox" name="del_<%=sdcd.getUnique()%>"></td>
</tr>

<%
}
%>
       </table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td align="left">
	<table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr>
		<td colspan="2" align="left"><%=lp.get(12)%><br><br></td>
	</tr>
	<tr>
		<td align="left">Name:&nbsp; &nbsp;<input type="text" class="textbox" name="addName" value="" size="30"></td>
		<td align="right"><input type="submit" class="button2" name="add" value="&nbsp;&nbsp;<%=lp.get(14)%>&nbsp;&nbsp;"></td>
	</tr>
	</table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>
<tr><td>
        <table cellspacing="0" cellpadding="0" border="0" width="100%">
	<tr><td align="left"><%=lp.get(13)%><br><br></td></tr>
	<tr><td align="right"><input type="submit" class="button2" name="delete" value="<%=lp.get(15)%>"></td></tr>
	</table>
</td></tr>

<tr><td><img src="/images/grey_dot.gif" height="1" width="100%"></td></tr>

</table>	

--%>