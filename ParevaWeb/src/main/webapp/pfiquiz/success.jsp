<%@ include file="coreimport.jsp"%>
<%@ include file="ukheader.jsp"%>
<%


String transaction_ref="";
try{
transaction_ref=request.getParameter("sessiontoken");
}catch(Exception e) {
    try{
        transaction_ref=(String) session.getAttribute("sessiontoken");
    }catch(Exception ex){transaction_ref="notfound";}
}

String merchanttoken=(String) request.getAttribute("merchanttoken");
if(merchanttoken==null || merchanttoken.length()<=0){
    merchanttoken=(String) session.getAttribute("merchanttoken");
}

if(merchanttoken==null || merchanttoken.length()<=0){
    if(dmn.getUnique().equalsIgnoreCase("3824583922341llun")) 
    merchanttoken="59E2E445-E8E3-4696-8015-037E7963F716";
    
    else
        merchanttoken="FBEEBAC4-9F42-4DC4-8E5D-B481A30986B0";
}


request.setAttribute("sessiontoken",transaction_ref);
request.setAttribute("merchanttoken",merchanttoken);
response.addHeader("X-PFI-SessionToken", transaction_ref);
response.addHeader("X-PFI-MerchantToken",merchanttoken);


RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/CompetitionSuccess");
 dispatcher.forward(request, response);
%>