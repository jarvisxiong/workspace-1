<%
    Integer hitsCount = 
      (Integer)application.getAttribute("hitCounter");
    if( hitsCount ==null || hitsCount == 0 ){
     
       hitsCount = 1;
    }else{
     
       hitsCount += 1;
       
    }
 //System.out.println("UKCOUNTER "+" HitsCount : "+hitsCount);
    application.setAttribute("hitCounter", hitsCount);
%>