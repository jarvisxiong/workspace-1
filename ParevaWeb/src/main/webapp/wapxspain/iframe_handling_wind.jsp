<%@include file="coreimport_iframe.jsp" %>
<%
    try{
        SdcRequest aReq = new SdcRequest(request);
        String pageEnc = aReq.getEncoding();
        response.setContentType("text/html; charset=" + pageEnc);
        Document doc;

        try {

            String vodafoneRedirectURL = aReq.get("vodafoneRedirectURL");
            //doc = Jsoup.connect(vodafoneRedirectURL).timeout(1000).get();
            URL url;
            url = new URL(vodafoneRedirectURL);
            URLConnection conn = url.openConnection();
 
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));
 
            String inputLine="";
            String html="";

            String referenceParam = "";
 
            while ((inputLine = br.readLine()) != null) {
                if(inputLine.trim().contains("return this.href + '&rnd=")){
                    Pattern pattern = Pattern.compile("'(.*?)'");
                    Matcher matcher = pattern.matcher(inputLine);
                    if (matcher.find())
                    {
                        referenceParam = matcher.group(1).trim();
                    }
                }
                html+=inputLine;
            }
            br.close();
            doc = Jsoup.parse(html);
            Elements newsHeadlines = doc.select("a");
            System.out.println("Amokachi String debug vodafoneRedirectURL: " + vodafoneRedirectURL + "---msisdn: " + aReq.get("msisdn"));
            out.write("http://95.110.188.135" + newsHeadlines.attr("href").toString()+ referenceParam);
        } catch (IOException e) {
            out.write("false");
        }
    }catch(Exception e){
        out.write("false");
    }
%>