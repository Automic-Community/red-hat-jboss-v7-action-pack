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
 * <p>Java class for RarDDType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="RarDDType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="display-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="vendor-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resourceadapter-version" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="resource-adapters">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resource-adapter" type="{http://www.example.org/JBossV7SnapshotSchema}ResourceAdapterType" maxOccurs="unbounded" minOccurs="0"/>
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
@XmlType(name = "RarDDType", propOrder = {
    "displayName",
    "description",
    "vendorName",
    "resourceadapterVersion",
    "resourceAdapters"
})
public class RarDDType {

    @XmlElement(name = "display-name", required = true)
    protected String displayName;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "vendor-name", required = true)
    protected String vendorName;
    @XmlElement(name = "resourceadapter-version", required = true)
    protected String resourceadapterVersion;
    @XmlElement(name = "resource-adapters", required = true)
    protected RarDDType.ResourceAdapters resourceAdapters;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the displayName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the value of the displayName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDisplayName(String value) {
        this.displayName = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the vendorName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the value of the vendorName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVendorName(String value) {
        this.vendorName = value;
    }

    /**
     * Gets the value of the resourceadapterVersion property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getResourceadapterVersion() {
        return resourceadapterVersion;
    }

    /**
     * Sets the value of the resourceadapterVersion property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setResourceadapterVersion(String value) {
        this.resourceadapterVersion = value;
    }

    /**
     * Gets the value of the resourceAdapters property.
     *
     * @return
     *     possible object is
     *     {@link RarDDType.ResourceAdapters }
     *
     */
    public RarDDType.ResourceAdapters getResourceAdapters() {
        return resourceAdapters;
    }

    /**
     * Sets the value of the resourceAdapters property.
     *
     * @param value
     *     allowed object is
     *     {@link RarDDType.ResourceAdapters }
     *
     */
    public void setResourceAdapters(RarDDType.ResourceAdapters value) {
        this.resourceAdapters = value;
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
     *         &lt;element name="resource-adapter" type="{http://www.example.org/JBossV7SnapshotSchema}ResourceAdapterType" maxOccurs="unbounded" minOccurs="0"/>
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
        "resourceAdapter"
    })
    public static class ResourceAdapters {

        @XmlElement(name = "resource-adapter", nillable = true)
        protected List<ResourceAdapterType> resourceAdapter;

        /**
         * Gets the value of the resourceAdapter property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resourceAdapter property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResourceAdapter().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ResourceAdapterType }
         *
         *
         */
        public List<ResourceAdapterType> getResourceAdapter() {
            if (resourceAdapter == null) {
                resourceAdapter = new ArrayList<ResourceAdapterType>();
            }
            return this.resourceAdapter;
        }

    }

}
