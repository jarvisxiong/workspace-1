<%@include file="coreimport.jsp"%>
<%
UmeSessionParameters httprequest = new UmeSessionParameters(request);
UmeDomain dmn = (UmeDomain) request.getAttribute("umedomain");
TemplateEngine templateengine=null;
PebbleEngine engine=null;
UmeTempCache tempcache=null;

try{
    
      ServletContext servletContext = (ServletContext) request.getSession().getServletContext();
      ApplicationContext ac = RequestContextUtils.getWebApplicationContext(request,servletContext);
      templateengine=(TemplateEngine) ac.getBean("templateengine");
      engine=(PebbleEngine) ac.getBean("pebbleEngine");
      tempcache=(UmeTempCache) ac.getBean("umesdc");
}
catch(Exception e){}

Map<String,TemplatePojo> templates=new HashMap<String,TemplatePojo>();

    
templates=tempcache.getTemplateMap(); 
TemplatePojo template=templates.get(dmn.getUnique());
String templatefolder=template.getTemplateFolder();
FileLoader irequiz_template_loader = new FileLoader();
//za_template_loader.setPrefix("/opt/pareva/templates/parsed/ZA/x-rated");
irequiz_template_loader.setPrefix(templatefolder.trim());
irequiz_template_loader.setSuffix(template.getSuffix().trim());
engine = new PebbleEngine(irequiz_template_loader);
EscaperExtension escaper = engine.getExtension(EscaperExtension.class);
escaper.setAutoEscaping(false);
%>
