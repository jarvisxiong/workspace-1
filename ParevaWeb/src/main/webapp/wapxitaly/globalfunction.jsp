<%!
void doRedirect(HttpServletResponse response, String url) {
        String referer = "headers";
        System.out.println("doRedirect: " + url);
        try {
              response.sendRedirect(url); return;
        }
        catch (Exception e) {
                System.out.println("doRedirect EXCEPTION: " + e);
        }
}
%>