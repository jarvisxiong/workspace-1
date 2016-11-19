<%
SessionFactory sessionfactory=null;
Session dbsession=null;
try{
ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
sessionfactory = (SessionFactory) ac.getBean("sessionFactory");
dbsession=sessionfactory.openSession();
}
catch(Exception e){
    e.printStackTrace();
}
%>
