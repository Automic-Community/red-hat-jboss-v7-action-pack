//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.12.04 at 03:31:00 PM ICT 
//

package com.uc4.ara.feature.jbossv6.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ServerInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServerInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="server-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="launch-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="product-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="product-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="release-codename" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="release-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="management-major-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="management-minor-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="management-micro-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}anySimpleType" default="Server Info" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerInfo", propOrder = {
    "serverName",
    "launchType",
    "productName",
    "productVersion",
    "releaseCodename",
    "releaseVersion",
    "managementMajorVersion",
    "managementMinorVersion",
    "managementMicroVersion"
})
@XmlSeeAlso({
    StandaloneServerInfo.class,
    DomainServerInfo.class
})
public class ServerInfo {

    @XmlElement(name = "server-name", required = true)
    protected String serverName;
    @XmlElement(name = "launch-type", required = true)
    protected String launchType;
    @XmlElement(name = "product-name", required = true)
    protected String productName;
    @XmlElement(name = "product-version", required = true)
    protected String productVersion;
    @XmlElement(name = "release-codename", required = true)
    protected String releaseCodename;
    @XmlElement(name = "release-version", required = true)
    protected String releaseVersion;
    @XmlElement(name = "management-major-version", required = true)
    protected String managementMajorVersion;
    @XmlElement(name = "management-minor-version", required = true)
    protected String managementMinorVersion;
    @XmlElement(name = "management-micro-version", required = true)
    protected String managementMicroVersion;
    @XmlAttribute
    @XmlSchemaType(name = "anySimpleType")
    protected String name;

    /**
     * Gets the value of the serverName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServerName() {
        return serverName;
    }

    /**
     * Sets the value of the serverName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServerName(String value) {
        this.serverName = value;
    }

    /**
     * Gets the value of the launchType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLaunchType() {
        return launchType;
    }

    /**
     * Sets the value of the launchType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLaunchType(String value) {
        this.launchType = value;
    }

    /**
     * Gets the value of the productName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the productVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductVersion() {
        return productVersion;
    }

    /**
     * Sets the value of the productVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductVersion(String value) {
        this.productVersion = value;
    }

    /**
     * Gets the value of the releaseCodename property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReleaseCodename() {
        return releaseCodename;
    }

    /**
     * Sets the value of the releaseCodename property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseCodename(String value) {
        this.releaseCodename = value;
    }

    /**
     * Gets the value of the releaseVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReleaseVersion() {
        return releaseVersion;
    }

    /**
     * Sets the value of the releaseVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReleaseVersion(String value) {
        this.releaseVersion = value;
    }

    /**
     * Gets the value of the managementMajorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagementMajorVersion() {
        return managementMajorVersion;
    }

    /**
     * Sets the value of the managementMajorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagementMajorVersion(String value) {
        this.managementMajorVersion = value;
    }

    /**
     * Gets the value of the managementMinorVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagementMinorVersion() {
        return managementMinorVersion;
    }

    /**
     * Sets the value of the managementMinorVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagementMinorVersion(String value) {
        this.managementMinorVersion = value;
    }

    /**
     * Gets the value of the managementMicroVersion property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManagementMicroVersion() {
        return managementMicroVersion;
    }

    /**
     * Sets the value of the managementMicroVersion property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManagementMicroVersion(String value) {
        this.managementMicroVersion = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        if (name == null) {
            return "Server Info";
        } else {
            return name;
        }
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

}
