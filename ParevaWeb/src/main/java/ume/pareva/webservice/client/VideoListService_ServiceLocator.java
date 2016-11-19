/**
 * VideoListService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ume.pareva.webservice.client;

public class VideoListService_ServiceLocator extends org.apache.axis.client.Service implements ume.pareva.webservice.client.VideoListService_Service {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VideoListService_ServiceLocator() {
    }


    public VideoListService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public VideoListService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for VideoListServiceImplPort
    private java.lang.String VideoListServiceImplPort_address = "http://pareva.umelimited.com:8080/VideoListService";

    public java.lang.String getVideoListServiceImplPortAddress() {
        return VideoListServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String VideoListServiceImplPortWSDDServiceName = "VideoListServiceImplPort";

    public java.lang.String getVideoListServiceImplPortWSDDServiceName() {
        return VideoListServiceImplPortWSDDServiceName;
    }

    public void setVideoListServiceImplPortWSDDServiceName(java.lang.String name) {
        VideoListServiceImplPortWSDDServiceName = name;
    }

    public ume.pareva.webservice.client.VideoListService_PortType getVideoListServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(VideoListServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getVideoListServiceImplPort(endpoint);
    }

    public ume.pareva.webservice.client.VideoListService_PortType getVideoListServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ume.pareva.webservice.client.VideoListServiceImplPortBindingStub _stub = new ume.pareva.webservice.client.VideoListServiceImplPortBindingStub(portAddress, this);
            _stub.setPortName(getVideoListServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setVideoListServiceImplPortEndpointAddress(java.lang.String address) {
        VideoListServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ume.pareva.webservice.client.VideoListService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ume.pareva.webservice.client.VideoListServiceImplPortBindingStub _stub = new ume.pareva.webservice.client.VideoListServiceImplPortBindingStub(new java.net.URL(VideoListServiceImplPort_address), this);
                _stub.setPortName(getVideoListServiceImplPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("VideoListServiceImplPort".equals(inputPortName)) {
            return getVideoListServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.pareva.ume/", "VideoListService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.pareva.ume/", "VideoListServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("VideoListServiceImplPort".equals(portName)) {
            setVideoListServiceImplPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
