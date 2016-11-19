package ume.pareva.webservice.client;

import java.rmi.RemoteException;



public class FFMpegEncoder {
	
	FFMpegEncoderService_PortType encoder;
	
	public FFMpegEncoder(){     
    	System.out.println("FFMpegEncoder "+ " Calling FFMpegEncoder Constructor ");
        try {
        	encoder = (new FFMpegEncoderService_ServiceLocator()).getFFMpegEncoderServiceImplPort();
            }catch (Exception e) { System.out.println("FFMpegEncoder Constructor Exception "+e); }
    
        }
	
	public String convertToVideo(String filePath, String fileParent, String fileName){
		String convertedFile="";
		try {
			convertedFile = encoder.convertToVideo(filePath, fileParent, fileName);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedFile;
	}
	
	public static void main(String[] arg){
		FFMpegEncoder encoder=new FFMpegEncoder();
		System.out.println(encoder.convertToVideo("test", "test", "test"));
	}

}
