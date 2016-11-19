<%@page import="com.google.gson.Gson"%>

<script>
function redirect(sisterDomain,domains){
	
	var redirectForm = document.createElement("form");
  	//alert(JSON.parse(domains));
  	redirectForm.target = "_self";
  	redirectForm.method = "POST"; // or "post" if appropriate
  	redirectForm.action = sisterDomain;

  	var notSubscribedClubsInput = document.createElement("input");
  	notSubscribedClubsInput.type = "hidden";
  	notSubscribedClubsInput.name = "notSubscribedClubs";
  	notSubscribedClubsInput.value = JSON.parse(domains);

  	redirectForm.appendChild(notSubscribedClubsInput);
  	document.body.appendChild(redirectForm);

	  
	  // window.open(sisterDomain,"_self");
	
	    redirectForm.submit();
 
	
}

</script>