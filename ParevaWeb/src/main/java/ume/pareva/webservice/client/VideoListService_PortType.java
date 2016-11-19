/**
 * VideoListService_PortType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ume.pareva.webservice.client;

public interface VideoListService_PortType extends java.rmi.Remote {
    public java.lang.String[][] getVideos(java.lang.String videoCategoryServiceUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException;
    public java.lang.String[][] getVideoCategories(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException;
    public int getNumberOfVideosToDisplay(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException;
    public int getTotalNumberOfVideos(java.lang.String videoCategoryServiceUnique, java.lang.String domain) throws java.rmi.RemoteException;
    public java.lang.String[][] getVideosExcludingProvidedVideoUnique(java.lang.String videoCategoryServiceUnique, java.lang.String videoUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException;
    public java.lang.String[][] getVideosBySortType(java.lang.String videoCategoryServiceUnique, java.lang.String sortType, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException;
    public java.lang.String[][] searchVideo(java.lang.String videoCategoryServiceUnique, java.lang.String searchString, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException;
    public java.lang.String[] getBanner(java.lang.String bannerSrvc, java.lang.String domain, java.lang.String classification, java.lang.String region) throws java.rmi.RemoteException;
    public java.lang.String[][] getVideosByCategory(java.lang.String categoryUnique, int index, java.lang.String domain, int numberOfElements, java.lang.String lang) throws java.rmi.RemoteException;
    public int getTotalNumberOfVideosInCategory(java.lang.String categoryUnique) throws java.rmi.RemoteException;
}
