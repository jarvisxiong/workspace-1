<%@ include file="global-wap-header.jsp" %>

<%

//Connection con = DBHStatic.getConnection();
//ResultSet rs = null;
Transaction trans=dbsession.beginTransaction();

java.util.List freetext_list=(java.util.List)request.getAttribute("promo_freetext_list");
java.util.List freetexts=new ArrayList();

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();

for(int i=0;i<freetext_list.size();i++){
	HashMap<String,Object> freetextMap=new HashMap<String,Object>();
	
	String srvc = (String)freetext_list.get(i);
	System.out.println("Service Name = "+srvc);

String sqlstr = "";
String promoUnique = "";
String txt = "";
String img = "";
int ih = 0;
//String srvc = aReq.get("srvc");

sqlstr = "SELECT * FROM clientFreeTexts WHERE aServiceUnique='" + srvc + "' AND aDomain='" + domain + "'";
//System.out.println(sqlstr);

Query query=null; 
query=dbsession.createSQLQuery(sqlstr).addScalar("aUnique").addScalar("aTitle");
java.util.List result=query.list();
if(!result.isEmpty()) {
            	
            for(Object o:result)
            {
            Object[] row=(Object[]) o;
            
            promoUnique = String.valueOf(row[0]);
            txt = Misc.utfToUnicode(String.valueOf(row[1]), pageEnc, false);
            //props = new String[1];
            //props[0] = String.valueOf(row[0]);
            //props[1] = String.valueOf(row[1]);
            //list.add(props);
			
            }

        }


//rs = DBHStatic.getRs(con, sqlstr);
//if (rs.next()) {
 //   promoUnique = rs.getString("aUnique");
  //  txt = Misc.utfToUnicode(rs.getString("aTitle"), pageEnc, false);
//}
//rs.close();

sqlstr = "SELECT * FROM clientPromoImages WHERE aPromoUnique='" + promoUnique + "_prm' AND aProfile='" + handset.getImageProfile() + "'";
//System.out.println("PROMO_FREETEXT: " + sqlstr);

query=null; 
query=dbsession.createSQLQuery(sqlstr).addScalar("aImage").addScalar("aWidth").addScalar("aHeight");
java.util.List result1=query.list();
if(!result1.isEmpty()) {
            	
            for(Object o:result1)
            {
            Object[] row=(Object[]) o;
            img = String.valueOf(row[0]);
            ih = (int) ((handset.getFullWidth() / Double.parseDouble(String.valueOf(row[1]))) * Double.parseDouble(String.valueOf(row[2])));
          //  promoUnique = String.valueOf(row[0]);
           // txt = Misc.utfToUnicode(String.valueOf(row[1]);, pageEnc, false);
            //props = new String[1];
            //props[0] = String.valueOf(row[0]);
            //props[1] = String.valueOf(row[1]);
            //list.add(props);
			
            }

        }



//rs = DBHStatic.getRs(con, sqlstr);
//if (rs.next()) {
 //   img = rs.getString("aImage");
  //  ih = (int) ((handset.getFullWidth() / rs.getDouble("aWidth")) * rs.getDouble("aHeight"));
//}
//rs.close();

System.out.println("IMG: " + img);

//DBHStatic.closeConnection(con);

txt = Misc.replaceChars(txt, "<link>", "freetext.jsp?srvc=" + srvc);
if (!img.equals("")) {
    txt = Misc.replaceChars(txt, "<main-image>", "<img src=\"/images/javagames/promos/" + img + "\"");
}
txt = Misc.replaceChars(txt, "<anyxid>", wapid);
txt = Misc.replaceChars(txt, "<xprof>", xprof);

String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")), pageEnc);
String bg = aReq.get("bg");
String font = aReq.get("fnt");
String style1 = "style=\"background-color:" + bg +"; color:" + font +";\"";
String style2 = "style=\"color:" + font +";\"";

freetexts.add(txt);
}

trans.commit();
//dbsession.close();

System.out.println("Categories = "+request.getAttribute("number_of_category"));
context.put("number_of_category",request.getAttribute("number_of_category"));
String include_header=(String)session.getAttribute("include_header");
context.put("contenturl","http://"+ dmn.getContentUrl());
if(include_header.equals("true")){
	context.put("freetexts",freetexts);
	//String header_logo=(String)dImages.get("img_header1_" + handset.getImageProfile());
	//context.put("header_logo",header_logo);
	context.put("include_header",include_header);
	PebbleEngine mexico_engine=(PebbleEngine)this.getServletContext().getAttribute("mexico_engine");
	mexico_engine.getTemplate("freetext").evaluate(writer, context);
	include_header="false";
	request.setAttribute("include_header",include_header);
}else {
	System.out.println("inside freetext");
	context.put("freetexts",freetexts);
	//String header_logo=(String)dImages.get("img_header1_" + handset.getImageProfile());
	//context.put("header_logo",header_logo);
	context.put("include_header",include_header);
	PebbleEngine mexico_engine=(PebbleEngine)this.getServletContext().getAttribute("mexico_engine");
	mexico_engine.getTemplate("freetext").evaluate(writer, context);
	//include_header="false";
	//session.setAttribute("include_header",include_header);
	//context.put("include_header",include_header);
}
//System.out.println("TEXT: " + txt);


%>






