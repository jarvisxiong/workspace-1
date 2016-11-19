package ume.pareva.webservice;

import java.io.File;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

@WebService
public interface FFMpegEncoderService {
	
	@WebMethod
	public String convertToVideo(@WebParam(name = "filepath") String filePath, @WebParam(name = "parentfile") String fileParent, @WebParam(name = "filename") String filename);
	

}

