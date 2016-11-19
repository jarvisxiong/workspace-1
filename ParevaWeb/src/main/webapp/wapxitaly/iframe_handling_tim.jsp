<%@include file="coreimport_iframe.jsp" %>
<%
    try{
        SdcRequest aReq = new SdcRequest(request);
        String pageEnc = aReq.getEncoding();
        response.setContentType("text/html; charset=" + pageEnc);

        Document doc;
        try {
            String vodafoneRedirectURL = aReq.get("vodafoneRedirectURL");
            System.out.println("IPX String debug vodafoneRedirectURL: " + vodafoneRedirectURL + "---msisdn: " + aReq.get("msisdn") + "--" + (new Date()) );
            
            
            doc = Jsoup.connect(vodafoneRedirectURL).get();
            Elements newsHeadlines = doc.select("a");
            System.out.println("******************************IPX debug iframe handling ***************************");
            System.out.println("IPX String debug vodafoneRedirectURL: " + vodafoneRedirectURL + "---msisdn: " + aReq.get("msisdn") + "--" + (new Date()) );
            System.out.println("IPX String debug html: " + newsHeadlines.attr("href").toString());
            //System.out.println("IPX String debug html: " + doc.outerHtml());
            System.out.println("******************************IPX debug iframe handling ***************************");
            out.write(newsHeadlines.attr("href").toString());
        //out.write(doc.outerHtml());
        } catch (IOException e) {
            out.write("false");
        }
    }catch(Exception e){
        out.write("false");
    }
%>