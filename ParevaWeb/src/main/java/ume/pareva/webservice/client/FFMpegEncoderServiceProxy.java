package ume.pareva.webservice.client;

public class FFMpegEncoderServiceProxy implements ume.pareva.webservice.client.FFMpegEncoderService_PortType {
  private String _endpoint = null;
  private ume.pareva.webservice.client.FFMpegEncoderService_PortType fFMpegEncoderService_PortType = null;
  
  public FFMpegEncoderServiceProxy() {
    _initFFMpegEncoderServiceProxy();
  }
  
  public FFMpegEncoderServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initFFMpegEncoderServiceProxy();
  }
  
  private void _initFFMpegEncoderServiceProxy() {
    try {
      fFMpegEncoderService_PortType = (new ume.pareva.webservice.client.FFMpegEncoderService_ServiceLocator()).getFFMpegEncoderServiceImplPort();
      if (fFMpegEncoderService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)fFMpegEncoderService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)fFMpegEncoderService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (fFMpegEncoderService_PortType != null)
      ((javax.xml.rpc.Stub)fFMpegEncoderService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ume.pareva.webservice.client.FFMpegEncoderService_PortType getFFMpegEncoderService_PortType() {
    if (fFMpegEncoderService_PortType == null)
      _initFFMpegEncoderServiceProxy();
    return fFMpegEncoderService_PortType;
  }
  
  public java.lang.String convertToVideo(java.lang.String filepath, java.lang.String parentfile, java.lang.String filename) throws java.rmi.RemoteException{
    if (fFMpegEncoderService_PortType == null)
      _initFFMpegEncoderServiceProxy();
    return fFMpegEncoderService_PortType.convertToVideo(filepath, parentfile, filename);
  }
  
  
}