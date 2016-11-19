<%@page import="javax.servlet.RequestDispatcher"%>
<%@page import="java.util.Enumeration"%>
<%
  Enumeration parameterList = request.getParameterNames();
  while( parameterList.hasMoreElements() )
  {
    String sName = parameterList.nextElement().toString();
   System.out.println("IEIndex: index.jsp "+ sName+":"+request.getParameter(sName));
      
    }  
%>
<html>
<body>
<h2>Hello World!</h2>

<form>
<input type="button" id="button1" value="button"/>

</form>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.2.0/jquery.min.js"></script>
<script>
$( document ).ready(function() {
    alert( "ready!" );
    
    
    
    
});

$( '#button1' ).on('click',function() {
    var personalLinkData={
			sessionToken: "6C69D950-B831-28DD-F1B6-F6381C50C74E",
		   merchantToken: "FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0"
	};
	$.ajax({
		type:'POST',
	   url:'http://pfi.imimobile.net/msisdnlookup/ajax/carrier',
	   dataType: "jsonp",
	   data:personalLinkData,
	   success:function(result){
		   alert(result);
	   },
	   error:function(response){
		   alert("error");
		}
	   
	   
   });
});

</script>
</body>
</html>






<%
  
RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/IMIIndex");
 dispatcher.forward(request, response);
%>

