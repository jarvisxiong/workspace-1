<%@page import="ume.pareva.smsapi.ZaSmsSubmit"%>
<%@include file="coreimport.jsp"%>
<%@include file="db.jsp"%>

<%
UmeSessionParameters aReq=new UmeSessionParameters(request);
UmeSmsDaoExtension umesmsdaoextension=null;
UmeSmsDao umesmsdao=null;
try{
    ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
    umesmsdaoextension=(UmeSmsDaoExtension) ac.getBean("umesmsdaoextension");
    umesmsdao=(UmeSmsDao) ac.getBean("umesmsdao");
    
    
}

catch(Exception e){e.printStackTrace();}

String sqlquery="SELECT aFromNumber,aToNumber,aMsgBody from smsMsgLog WHERE aToNumber like '+%'order by aCreated desc LIMIT 2";
System.out.println("missedsms "+sqlquery);
Transaction trans=dbsession.beginTransaction();

Query query=null;
    try{
        query=dbsession.createSQLQuery(sqlquery).addScalar("aFromNumber").addScalar("aToNumber").addScalar("aMsgBody");
       java.util.List querylist=query.list();
       
       if(querylist.size()>0)
       {
           for(Object o:querylist)
           {
              Object[] row=(Object[]) o;
              String fromNumber= String.valueOf(row[0]);
              String toNumber=String.valueOf(row[1]);
              String msgbody=String.valueOf(row[2]);
              
            
              
              String msisdnno=toNumber.replace("+","");
                System.out.println("missedsms "+fromNumber+" "+toNumber+" "+msgbody+"  msisdn "+msisdnno);
              
              ZaSmsSubmit welcomesms = new ZaSmsSubmit(aReq);
                welcomesms.setSmsAccount("sts");
                //welcomesms.setUmeUser(umeuser);
                 welcomesms.setToNumber(msisdnno.trim());
                welcomesms.setFromNumber(fromNumber);
                welcomesms.setMsgBody(msgbody);
                
                System.out.println("missedsms "+welcomesms.toString());
                
                try { 
                     //welcomesms.setCampaignId(Integer.parseInt("1630"));
                    welcomesms.setCampaignId(Integer.parseInt("1630"));
                   
                   } catch (NumberFormatException e) {}
                welcomesms.setMsgCode1("");
                
                String resp = "";
                try{
                SdcSmsGateway gw=new SdcSmsGateway();
                gw.setAccounts(welcomesms.getSmsAccount());
                gw.setMsisdnFormat(4);
                resp=umesmsdaoextension.send(welcomesms, gw);
                System.out.println("missedsms "+welcomesms.getToNumber()+" "+" response is "+resp);
                
                if(resp.equalsIgnoreCase("success"))
                {
                    
                    String upquery="UPDATE smsMsgLog set aToNumber='"+welcomesms.getToNumber()+"',aStatus='"+resp+"' WHERE aToNumber='"+toNumber+"'";
                            //+ " AND aUnique='"+welcomesms.getLogUnique()+"'";
                    System.out.println("missedsms "+"update query "+upquery);
                    Query query1=dbsession.createSQLQuery(upquery);
                    query1.executeUpdate();
                    
                    

                }
                }
                catch(Exception e){System.out.println("missedsms "+e);e.printStackTrace();}
                      
           }
       }
             
        trans.commit();
        dbsession.close();
    }
    
    catch(Exception eE)
    {
        System.out.println("missedsms "+"Exception "+ eE);
	eE.printStackTrace();
        
    }




%>