//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.12.04 at 03:31:00 PM ICT
//

package com.uc4.ara.feature.jbossv7.schemas;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for ServerGroup complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ServerGroup">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="profile" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="socket-binding-port-offset" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="socket-binding-group" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="deployments">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="deployment" type="{http://www.example.org/JBossV7SnapshotSchema}ServerGroupDeployment" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ServerGroup", propOrder = {
    "profile",
    "socketBindingPortOffset",
    "socketBindingGroup",
    "deployments"
})
public class ServerGroup {

    @XmlElement(required = true)
    protected String profile;
    @XmlElement(name = "socket-binding-port-offset", required = true)
    protected String socketBindingPortOffset;
    @XmlElement(name = "socket-binding-group", required = true)
    protected String socketBindingGroup;
    @XmlElement(required = true)
    protected ServerGroup.Deployments deployments;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the profile property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * Gets the value of the socketBindingPortOffset property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSocketBindingPortOffset() {
        return socketBindingPortOffset;
    }

    /**
     * Sets the value of the socketBindingPortOffset property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSocketBindingPortOffset(String value) {
        this.socketBindingPortOffset = value;
    }

    /**
     * Gets the value of the socketBindingGroup property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSocketBindingGroup() {
        return socketBindingGroup;
    }

    /**
     * Sets the value of the socketBindingGroup property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSocketBindingGroup(String value) {
        this.socketBindingGroup = value;
    }

    /**
     * Gets the value of the deployments property.
     *
     * @return
     *     possible object is
     *     {@link ServerGroup.Deployments }
     *
     */
    public ServerGroup.Deployments getDeployments() {
        return deployments;
    }

    /**
     * Sets the value of the deployments property.
     *
     * @param value
     *     allowed object is
     *     {@link ServerGroup.Deployments }
     *
     */
    public void setDeployments(ServerGroup.Deployments value) {
        this.deployments = value;
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
        return name;
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

    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="deployment" type="{http://www.example.org/JBossV7SnapshotSchema}ServerGroupDeployment" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "deployment"
    })
    public static class Deployments {

        @XmlElement(nillable = true)
        protected List<ServerGroupDeployment> deployment;

        /**
         * Gets the value of the deployment property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the deployment property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDeployment().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ServerGroupDeployment }
         *
         *
         */
        public List<ServerGroupDeployment> getDeployment() {
            if (deployment == null) {
                deployment = new ArrayList<ServerGroupDeployment>();
            }
            return this.deployment;
        }

    }

}
