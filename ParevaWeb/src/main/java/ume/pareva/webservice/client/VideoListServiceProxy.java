package ume.pareva.webservice.client;

public class VideoListServiceProxy implements ume.pareva.webservice.client.VideoListService_PortType {
  private String _endpoint = null;
  private ume.pareva.webservice.client.VideoListService_PortType videoListService_PortType = null;
  
  public VideoListServiceProxy() {
    _initVideoListServiceProxy();
  }
  
  public VideoListServiceProxy(String endpoint) {
    _endpoint = endpoint;
    _initVideoListServiceProxy();
  }
  
  private void _initVideoListServiceProxy() {
    try {
      videoListService_PortType = (new ume.pareva.webservice.client.VideoListService_ServiceLocator()).getVideoListServiceImplPort();
      if (videoListService_PortType != null) {
        if (_endpoint != null)
          ((javax.xml.rpc.Stub)videoListService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint = (String)((javax.xml.rpc.Stub)videoListService_PortType)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    _endpoint = endpoint;
    if (videoListService_PortType != null)
      ((javax.xml.rpc.Stub)videoListService_PortType)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ume.pareva.webservice.client.VideoListService_PortType getVideoListService_PortType() {
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType;
  }
  
  public java.lang.String[][] getVideos(java.lang.String videoCategoryServiceUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getVideos(videoCategoryServiceUnique, index, domain, numberOfElements, lang);
  }
  
  public java.lang.String[][] getVideoCategories(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getVideoCategories(videoCategoryServiceUnique, domain);
  }
  
  public int getNumberOfVideosToDisplay(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getNumberOfVideosToDisplay(videoCategoryServiceUnique, domain);
  }
  
  public int getTotalNumberOfVideos(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getTotalNumberOfVideos(videoCategoryServiceUnique, domain);
  }
  
  public java.lang.String[][] getVideosExcludingProvidedVideoUnique(java.lang.String videoCategoryServiceUnique, java.lang.String videoUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getVideosExcludingProvidedVideoUnique(videoCategoryServiceUnique, videoUnique, index, domain, numberOfElements, lang);
  }
  
  public java.lang.String[][] getVideosBySortType(java.lang.String videoCategoryServiceUnique, java.lang.String sortType, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getVideosBySortType(videoCategoryServiceUnique, sortType, index, domain, numberOfElements, lang);
  }
  
  public java.lang.String[][] searchVideo(java.lang.String videoCategoryServiceUnique, java.lang.String searchString, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.searchVideo(videoCategoryServiceUnique, searchString, index, domain, numberOfElements, lang);
  }
  
  public java.lang.String[] getBanner(java.lang.String bannerSrvc, java.lang.String domain, java.lang.String classification, java.lang.String region) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getBanner(bannerSrvc, domain, classification, region);
  }
  
  public java.lang.String[][] getVideosByCategory(java.lang.String categoryUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getVideosByCategory(categoryUnique, index, domain, numberOfElements, lang);
  }
  
  public int getTotalNumberOfVideosInCategory(java.lang.String categoryUnique) throws java.rmi.RemoteException{
    if (videoListService_PortType == null)
      _initVideoListServiceProxy();
    return videoListService_PortType.getTotalNumberOfVideosInCategory(categoryUnique);
  }
  
  
}