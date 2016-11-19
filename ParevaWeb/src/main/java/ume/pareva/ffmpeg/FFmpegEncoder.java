package ume.pareva.ffmpeg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import org.apache.commons.lang.StringUtils;
import ume.pareva.sdk.StreamGobbler;

/**
 *
 * @author madan This class is used for ffmpeg encoder for Video
 */
public class FFmpegEncoder {
    
    private static final String scriptPath="/usr/bin/ffmpeg ";

	public FFmpegEncoder() {
	}

	private String videoname;
	private String videoFolder;

	public String getVideoname() {
		return videoname;
	}

	public void setVideoname(String videoname) {
		this.videoname = videoname;
	}

	public String getVideoFolder() {
		return videoFolder;
	}

	public void setVideoFolder(String videoFolder) {
		this.videoFolder = videoFolder;
	}

	public String convertToVideoAndroid(File contentFile, String filename) {
		String[] cmd = new String[25];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "/usr/bin/ffmpeg "; 
		cmd[1] = "-y ";
		cmd[2] = "-i ";
		cmd[3] = contentFile.getPath();
		cmd[4] = "-vcodec";
		cmd[5] = "mpeg4";
		cmd[6] = "-s";
		cmd[7] = "320x176";
		cmd[8] = "-acodec";
		cmd[9] = "aac";
		cmd[10] = "-strict";
		cmd[11] = "-2";
		cmd[12] = "-ac";
		cmd[13] = "1";
		cmd[14] = "-ar";
		cmd[15] = "16000";
		cmd[16] = "-r";
		cmd[17] = "13";
		cmd[18] = "-ab";
		cmd[19] = "32000";
		cmd[20] = "-aspect";
		cmd[21] = "3:2";
		cmd[22] = "-movflags";
		cmd[23] = "faststart ";
		String myfilename = filename + "-andr1144" + ".mp4";
		cmd[24] = contentFile.getParent() + "/" + myfilename;
		//cmd[24] = "/home/meglos/Downloads/" + myfilename;

		System.out.println("CURRENTLY CONVERTING::>> at " + cmd[24]);
		for (String sCmd: cmd) {
			System.out.print(sCmd + " ");
		}
		System.out.println();

		if (execute(cmd)){
			return myfilename;
		}else{		
			return null;
		}

	}

	public String convertmp4To3GPP(File contentFile, String filename) {
		String[] cmd = new String[21];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "usr/bin/ffmpeg ";
		cmd[1] = "-y ";
		cmd[2] = "-i ";
		cmd[3] = contentFile.getPath();
		cmd[4] = "-r";
		cmd[5] = "20";
		//-vf scale=352:-1
		/*cmd[6] = "-vf";
		cmd[7] = "scale=352:-1";*/
		cmd[6] = "-s";
		cmd[7] = "176x144";
		cmd[8] = "-b:v";
		cmd[9] = "200k";
		cmd[10] = "-acodec";
		cmd[11] = "aac";
		cmd[12] = "-strict";
		cmd[13] = "experimental";
		cmd[14] = "-ac";
		cmd[15] = "1";
		cmd[16] = "-ar";
		cmd[17] = "8000";
		cmd[18] = "-ab";
		cmd[19] = "24k";
		
		String myfilename = filename + "-3gpp1144" + ".3gp";
		cmd[20] = contentFile.getParent() + "/" + myfilename;
		//cmd[20] = "/home/meglos/Downloads/" + myfilename;
		
		System.out.println("CURRENTLY CONVERTING::>> at " + cmd[20]);
		for (String sCmd: cmd) {
			System.out.print(sCmd + " ");
		}
		System.out.println();

		if (execute(cmd)){
			return myfilename;
		}else{		
			return null;
		}

	}
	
	
	 public static boolean execute(String[] cmd){
        try{
            Runtime rt= Runtime.getRuntime();

            Process proc = rt.exec(cmd);

            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");
            StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();

            int exitVal = proc.waitFor();
            String sb = outputGobbler.sb.toString();
            String eb = errorGobbler.sb.toString();

            System.out.println("Command Exceute Exit value: " + exitVal);

            proc.destroy();

            return true;
        }
        catch(java.io.IOException e ){System.out.println("IOException "+e);e.printStackTrace();}
        catch(java.lang.InterruptedException e){}
        
        return false;

    }
public String convert3gpTomp4(File contentFile, String filename) {
		String[] cmd = new String[6];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "/usr/bin/ffmpeg "; //path to mmpeg
		cmd[1] = "-y ";
		cmd[2] = "-i ";
		cmd[3] = contentFile.getPath();
		cmd[4] = "  -acodec copy ";
		String myfilename = filename +".mp4";
		cmd[5] = contentFile.getParent() + "/" + myfilename;
		//cmd[24] = "/home/meglos/Downloads/" + myfilename;

		System.out.println("CURRENTLY 3gp to mp4 CONVERTING::>>  "+cmd[3] +" >> TO <<"+ cmd[5]);
		for (String sCmd: cmd) {
			System.out.print(sCmd);
		}
		System.out.println();

		if (execute(cmd)){
                        System.out.println("MYFILENAME== "+myfilename);
			return myfilename;
		}else{		
			return null;
		}

	}
}
