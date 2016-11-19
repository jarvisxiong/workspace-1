package ume.pareva.webservice;

import java.io.File;

import javax.jws.WebService;

import org.apache.commons.lang.StringUtils;

import ume.pareva.sdk.StreamGobbler;

@WebService(endpointInterface="ume.pareva.webservice.FFMpegEncoderService",
serviceName="FFMpegEncoderService")

public class FFMpegEncoderServiceImpl implements FFMpegEncoderService {

	@Override
	public String convertToVideo(String filePath, String fileParent, String filename) {
		String[] cmd = new String[27];
		filename = StringUtils.substringBefore(filename, ".");
	
		
		cmd[0] =  "/usr/bin/ffmpeg"; 
		cmd[1] = "-y";
		cmd[2] = "-i";
		cmd[3] = filePath;
		cmd[4] = "-vcodec"; //video codec. This is an alias for -codec:v
		cmd[5] = "libx264";
		cmd[6] = "-s"; //frame size
		cmd[7] = "320x240";
		cmd[8] = "-acodec";
		cmd[9] = "aac";
		cmd[10] = "-strict";
		cmd[11] = "-2";
		cmd[12] = "-ac";
		cmd[13] = "1";
		cmd[14] = "-ar"; //audio sampling frequency
		cmd[15] = "16000";
		cmd[16] = "-r"; //framerate
		cmd[17] = "24";
		cmd[18] = "-ab";
		cmd[19] = "64000";
                cmd[20] = "-crf";
		cmd[21] = "18";
		cmd[22] = "-aspect"; //video display aspect ratio specified by aspect
		cmd[23] = "4:3";
		cmd[24] = "-movflags";
		cmd[25] = "faststart";
                String myfilename = filename + "ncoded" + ".mp4";
		cmd[26] = fileParent + "/" + myfilename;
		//cmd[24] = "/home/meglos/Downloads/" + myfilename;

		System.out.println("webserviceteststream Video encoding CURRENTLY CONVERTING at " + cmd[24]);
		for (String sCmd: cmd) {
			System.out.print("teststream "+sCmd + " ");
		}
		System.out.println();

		if (execute(cmd)){
			return myfilename;
		}else{		
			return null;
		}

	}
	
	
	public boolean execute(String[] cmd){
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
	

}
