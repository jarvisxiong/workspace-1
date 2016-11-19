<%@ include file="global-wap-header.jsp" %>
<%@ include file="xhtmlhead.jsp" %><%

//Connection con = DBHStatic.getConnection();
//ResultSet rs = null;

Transaction trans=dbsession.beginTransaction();

java.util.List image_list=(java.util.List)request.getAttribute("promo_hot_bg_list");
java.util.List images=new ArrayList();

PrintWriter writer = response.getWriter();
Map<String, Object> context = new HashMap();


for(int i=0;i<image_list.size();i++){
	HashMap<String,Object> imageMap=new HashMap<String,Object>();
	
	String srvc = (String)image_list.get(i);
	System.out.println("Service Name = "+srvc);
	
String sqlstr = "";

//String srvc = aReq.get("srvc");
BgImage item = null;
String priceGroup = "";
int picdir = 64;
String pictype = ".jpg";
        
java.util.List list = new ArrayList();
String[] props = null;

sqlstr = "SELECT aPromoUnique,aJavaUnique FROM clientPromoPages"
            + " INNER JOIN bgImages bg ON bg.aUnique=clientPromoPages.aJavaUnique"
            + " WHERE aServiceUnique='" + srvc + "' AND aJavaUnique!='' AND clientPromoPages.aDomain='" + domain + "'"
            + " AND bg.aStatus='1' ORDER BY aIndex";

//System.out.println(sqlstr);


Query query=null; 
query=dbsession.createSQLQuery(sqlstr).addScalar("aJavaUnique").addScalar("aPromoUnique");
java.util.List result=query.list();
if(!result.isEmpty()) {
            	
            for(Object o:result)
            {
            Object[] row=(Object[]) o;
            props = new String[1];
            props[0] = String.valueOf(row[0]);
            //props[1] = String.valueOf(row[1]);
            list.add(props);
			
            }

        }


//rs = DBHStatic.getRs(con, sqlstr);


//while (rs.next()) {
  //  props = new String[1];
   // props[0] = rs.getString("aJavaUnique");
    //list.add(props);
//}
//rs.close();

if (list.size()>0) {
    int rand = (int) java.lang.Math.floor(java.lang.Math.random() * list.size());
    props = (String[]) list.get(rand);
    if (props!=null && !props[0].equals("")) {
        item = bgimagedao.getItem(props[0]);
    }      
}

if (item==null) { 
    System.out.println("ITEM NOT FOUND"); 
    dbsession.close();
    return;
}

sqlstr = "SELECT count(*) as count"
        + " FROM itemLog "
        + " WHERE aItemUnique='" + item.getUnique() + "'";
int count = 0;
query=dbsession.createSQLQuery(sqlstr).addScalar("count");
java.util.List countList=query.list();

for(Object o1:countList)
            {
				String row1=o1.toString();
				count = Integer.parseInt(String.valueOf(row1)); 
                                System.out.println("COUNTING BG : "+count);
			}
//rs = DBHStatic.getRs(con, sqlstr);
//while (rs.next()) {
  //  count = rs.getInt("count");

//}
//rs.close();

String price = (String) UmeTempCmsCache.domainPriceGroups.get(domain + "_" + item.getPriceGroup());

if (handset.getImageProfile()==4) picdir = 128;
else if (handset.getImageProfile()==3)  picdir = 80;
else if (handset.getImageProfile()==2) { picdir = 64; pictype = ".gif"; }
else { picdir = 40; pictype = ".gif"; }


//DBHStatic.closeConnection(con);

String title = Misc.utfToUnicode(Misc.hex8Decode(aReq.get("ttl")), pageEnc);
String bg = aReq.get("bg");
String font = aReq.get("fnt");
if(!bg.equals(""))
    bg = Misc.replaceChars(bg, "#", "");
if(!font.equals(""))
    font = Misc.replaceChars(font, "#", "");
String imgWidth = aReq.get("iw");
String style1 = "style=\"background-color:#" + bg +"; color:#" + font +";\"";
String style2 = "style=\"color:" + font +";\"";

int ratingNumber = 80 + (int)(Math.random() * ((100 - 80) + 1));
int viewsNumber = 65000 + count;//+ (int)(Math.random() * ((80000 - 65000) + 1));
imageMap.put("picdir",picdir);
imageMap.put("pictype",pictype);
imageMap.put("unique_item",item.getUnique());
imageMap.put("title",title);
imageMap.put("item_title",item.getTitle());
imageMap.put("ratingNumber",ratingNumber);
imageMap.put("viewsNumber",viewsNumber);
images.add(imageMap);
}
trans.commit();
//dbsession.close();

System.out.println("Categories = "+request.getAttribute("number_of_category"));
context.put("number_of_category",request.getAttribute("number_of_category"));
String include_header=(String)request.getAttribute("include_header");
context.put("contenturl", "http://"+dmn.getContentUrl());
if(include_header.equals("true")){
	context.put("images",images);
	String header_logo=(String)dImages.get("img_header1_" + handset.getImageProfile());
	context.put("header_logo",header_logo);
	context.put("include_header",include_header);
	PebbleEngine mexico_engine=(PebbleEngine)this.getServletContext().getAttribute("mexico_engine");
	mexico_engine.getTemplate("wallpaper").evaluate(writer, context);
	include_header="false";
	request.setAttribute("include_header",include_header);
}else {
	System.out.println("inside promo_hot_bg");
	context.put("images",images);
	//String header_logo=(String)dImages.get("img_header1_" + handset.getImageProfile());
	//context.put("header_logo",header_logo);
	context.put("include_header",include_header);
	PebbleEngine mexico_engine=(PebbleEngine)this.getServletContext().getAttribute("mexico_engine");
	mexico_engine.getTemplate("wallpaper").evaluate(writer, context);
	
}
%>
