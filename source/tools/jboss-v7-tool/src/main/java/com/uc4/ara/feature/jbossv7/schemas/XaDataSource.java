//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.12.04 at 03:31:00 PM ICT
//

package com.uc4.ara.feature.jbossv7.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for XaDataSource complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="XaDataSource">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="jndi-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="driver-name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="xa-datasource-class" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "XaDataSource", propOrder = {
    "jndiName",
    "enabled",
    "driverName",
    "xaDatasourceClass"
})
public class XaDataSource {

    @XmlElement(name = "jndi-name", required = true)
    protected String jndiName;
    @XmlElement(required = true)
    protected String enabled;
    @XmlElement(name = "driver-name", required = true)
    protected String driverName;
    @XmlElement(name = "xa-datasource-class", required = true)
    protected String xaDatasourceClass;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the jndiName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getJndiName() {
        return jndiName;
    }

    /**
     * Sets the value of the jndiName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setJndiName(String value) {
        this.jndiName = value;
    }

    /**
     * Gets the value of the enabled property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setEnabled(String value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the driverName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDriverName() {
        return driverName;
    }

    /**
     * Sets the value of the driverName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDriverName(String value) {
        this.driverName = value;
    }

    /**
     * Gets the value of the xaDatasourceClass property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getXaDatasourceClass() {
        return xaDatasourceClass;
    }

    /**
     * Sets the value of the xaDatasourceClass property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setXaDatasourceClass(String value) {
        this.xaDatasourceClass = value;
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

}
