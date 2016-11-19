/**
 * FFMpegEncoderService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package ume.pareva.webservice.client;

public class FFMpegEncoderService_ServiceLocator extends org.apache.axis.client.Service implements ume.pareva.webservice.client.FFMpegEncoderService_Service {

    public FFMpegEncoderService_ServiceLocator() {
    }


    public FFMpegEncoderService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public FFMpegEncoderService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for FFMpegEncoderServiceImplPort
    private java.lang.String FFMpegEncoderServiceImplPort_address = "http://pareva.umelimited.com:8080/FFMpegEncoderService";

    public java.lang.String getFFMpegEncoderServiceImplPortAddress() {
        return FFMpegEncoderServiceImplPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String FFMpegEncoderServiceImplPortWSDDServiceName = "FFMpegEncoderServiceImplPort";

    public java.lang.String getFFMpegEncoderServiceImplPortWSDDServiceName() {
        return FFMpegEncoderServiceImplPortWSDDServiceName;
    }

    public void setFFMpegEncoderServiceImplPortWSDDServiceName(java.lang.String name) {
        FFMpegEncoderServiceImplPortWSDDServiceName = name;
    }

    public ume.pareva.webservice.client.FFMpegEncoderService_PortType getFFMpegEncoderServiceImplPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(FFMpegEncoderServiceImplPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getFFMpegEncoderServiceImplPort(endpoint);
    }

    public ume.pareva.webservice.client.FFMpegEncoderService_PortType getFFMpegEncoderServiceImplPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            ume.pareva.webservice.client.FFMpegEncoderServiceImplPortBindingStub _stub = new ume.pareva.webservice.client.FFMpegEncoderServiceImplPortBindingStub(portAddress, this);
            _stub.setPortName(getFFMpegEncoderServiceImplPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setFFMpegEncoderServiceImplPortEndpointAddress(java.lang.String address) {
        FFMpegEncoderServiceImplPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (ume.pareva.webservice.client.FFMpegEncoderService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                ume.pareva.webservice.client.FFMpegEncoderServiceImplPortBindingStub _stub = new ume.pareva.webservice.client.FFMpegEncoderServiceImplPortBindingStub(new java.net.URL(FFMpegEncoderServiceImplPort_address), this);
                _stub.setPortName(getFFMpegEncoderServiceImplPortWSDDServiceName());
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
        if ("FFMpegEncoderServiceImplPort".equals(inputPortName)) {
            return getFFMpegEncoderServiceImplPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://webservice.pareva.ume/", "FFMpegEncoderService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://webservice.pareva.ume/", "FFMpegEncoderServiceImplPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("FFMpegEncoderServiceImplPort".equals(portName)) {
            setFFMpegEncoderServiceImplPortEndpointAddress(address);
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
