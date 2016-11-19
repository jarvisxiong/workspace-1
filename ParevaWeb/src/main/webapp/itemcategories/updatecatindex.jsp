<%@include file="/WEB-INF/jspf/coreimport.jspf"%>
<%@include file="/WEB-INF/jspf/db.jspf"%>
<%
   AdminRequest aReq = new AdminRequest(request);
   String itemlist[] = aReq.get("itemlist[]").split(",");
   Query query=null;
   
   ItemCategoryDao itemcategorydao=null;

 try{
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      
          itemcategorydao=(ItemCategoryDao) ac.getBean("itemcategorydao");
	  
     }
      catch(Exception e){
          e.printStackTrace();
      }
Transaction trans=dbsession.beginTransaction();

 
   String sqlstr = "";
   
   for (int i=0; i<itemlist.length; i++) {
       sqlstr = "UPDATE itemCategories SET aIndex='" + i + "' WHERE aUnique='" + itemlist[i] + "'";
       System.out.println(sqlstr);
      query=dbsession.createSQLQuery(sqlstr);
      query.executeUpdate();
   }
   trans.commit();
   dbsession.close();
   
   UmeTempCmsCache.itemCategoryMap =itemcategorydao.getCategoryMap();
%>