<%!
void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        System.out.println("doRedirect: " + url);
        try {
              response.sendRedirect(url);
        }
        catch (Exception e) {
                System.out.println("doRedirect EXCEPTION: " + e);
        }
}
%>