//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.12.04 at 03:31:00 PM ICT
//

package com.uc4.ara.feature.jbossv7.schemas;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for StandaloneServerInfo complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="StandaloneServerInfo">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.example.org/JBossV7SnapshotSchema}ServerInfo">
 *       &lt;sequence>
 *         &lt;element name="server-state" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StandaloneServerInfo", propOrder = {
    "serverState"
})
public class StandaloneServerInfo
    extends ServerInfo
{

    @XmlElement(name = "server-state", required = true)
    protected String serverState;

    /**
     * Gets the value of the serverState property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getServerState() {
        return serverState;
    }

    /**
     * Sets the value of the serverState property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setServerState(String value) {
        this.serverState = value;
    }

}
