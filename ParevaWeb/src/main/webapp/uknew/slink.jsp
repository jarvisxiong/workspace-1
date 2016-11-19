

<%@page import="ume.pareva.sdk.StreamGobbler"%>
<%@page import="ume.pareva.sdk.Misc"%>
<%
String linkCmd ="mnt/content/www/create_direct_links.sh";
String[] cmd = null;
 cmd = new String[3];
 cmd[0] = linkCmd;
  cmd[1] = "/mnt/content/data/videoclips/7664254688931KDS/8617530769931KDS.mp4";
  cmd[2]="/mnt/content/www/lib/slinks/9617530769931KDS.mp4";
  
   try {

            System.out.println("ContentUrl Executing command to create shortcut:");
            for (int i=0; i<cmd.length; i++) System.out.print(cmd[i] + " ");
            System.out.println();

            Runtime rt= Runtime.getRuntime();
            System.out.println(cmd)
            Process proc = rt.exec(cmd);
            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();

            int exitVal = proc.waitFor();
            String sb = outputGobbler.sb.toString();
            String eb = errorGobbler.sb.toString();
            
            System.out.println("ContentUrl Output Gobbler "+sb);
            System.out.println("ContentUrl Error Gobbler "+eb);
            

            System.out.println("ContentUrl Command Exceute Exit value: " + exitVal);

            proc.destroy();

           
        }
        catch(Exception e ) { System.out.println("Execute Error: " + e); e.printStackTrace(); }
  


%>