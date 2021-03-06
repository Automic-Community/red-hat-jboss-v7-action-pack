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
 * <p>Java class for ResourceRefType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="ResourceRefType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resource-refs" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resource-ref" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="res-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="res-auth" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="res-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="resource-env-refs">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resource-env-ref" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="resource-env-ref-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="resource-env-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="env-entries">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="env-entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="env-entry-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                             &lt;element name="env-entry-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="env-entry-name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
@XmlType(name = "ResourceRefType", propOrder = {
    "resourceRefs",
    "resourceEnvRefs",
    "envEntries"
})
public class ResourceRefType {

    @XmlElement(name = "resource-refs")
    protected ResourceRefType.ResourceRefs resourceRefs;
    @XmlElement(name = "resource-env-refs", required = true)
    protected ResourceRefType.ResourceEnvRefs resourceEnvRefs;
    @XmlElement(name = "env-entries", required = true)
    protected ResourceRefType.EnvEntries envEntries;
    @XmlAttribute
    protected String name;

    /**
     * Gets the value of the resourceRefs property.
     *
     * @return
     *     possible object is
     *     {@link ResourceRefType.ResourceRefs }
     *
     */
    public ResourceRefType.ResourceRefs getResourceRefs() {
        return resourceRefs;
    }

    /**
     * Sets the value of the resourceRefs property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceRefType.ResourceRefs }
     *
     */
    public void setResourceRefs(ResourceRefType.ResourceRefs value) {
        this.resourceRefs = value;
    }

    /**
     * Gets the value of the resourceEnvRefs property.
     *
     * @return
     *     possible object is
     *     {@link ResourceRefType.ResourceEnvRefs }
     *
     */
    public ResourceRefType.ResourceEnvRefs getResourceEnvRefs() {
        return resourceEnvRefs;
    }

    /**
     * Sets the value of the resourceEnvRefs property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceRefType.ResourceEnvRefs }
     *
     */
    public void setResourceEnvRefs(ResourceRefType.ResourceEnvRefs value) {
        this.resourceEnvRefs = value;
    }

    /**
     * Gets the value of the envEntries property.
     *
     * @return
     *     possible object is
     *     {@link ResourceRefType.EnvEntries }
     *
     */
    public ResourceRefType.EnvEntries getEnvEntries() {
        return envEntries;
    }

    /**
     * Sets the value of the envEntries property.
     *
     * @param value
     *     allowed object is
     *     {@link ResourceRefType.EnvEntries }
     *
     */
    public void setEnvEntries(ResourceRefType.EnvEntries value) {
        this.envEntries = value;
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
     *         &lt;element name="env-entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="env-entry-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="env-entry-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="env-entry-name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    @XmlType(name = "", propOrder = {
        "envEntry"
    })
    public static class EnvEntries {

        @XmlElement(name = "env-entry")
        protected List<ResourceRefType.EnvEntries.EnvEntry> envEntry;
        @XmlAttribute
        protected String name;

        /**
         * Gets the value of the envEntry property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the envEntry property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEnvEntry().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ResourceRefType.EnvEntries.EnvEntry }
         *
         *
         */
        public List<ResourceRefType.EnvEntries.EnvEntry> getEnvEntry() {
            if (envEntry == null) {
                envEntry = new ArrayList<ResourceRefType.EnvEntries.EnvEntry>();
            }
            return this.envEntry;
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
         *         &lt;element name="env-entry-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="env-entry-value" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *       &lt;attribute name="env-entry-name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "envEntryType",
            "envEntryValue"
        })
        public static class EnvEntry {

            @XmlElement(name = "env-entry-type", required = true)
            protected String envEntryType;
            @XmlElement(name = "env-entry-value", required = true)
            protected String envEntryValue;
            @XmlAttribute(name = "env-entry-name")
            protected String envEntryName;

            /**
             * Gets the value of the envEntryType property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getEnvEntryType() {
                return envEntryType;
            }

            /**
             * Sets the value of the envEntryType property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setEnvEntryType(String value) {
                this.envEntryType = value;
            }

            /**
             * Gets the value of the envEntryValue property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getEnvEntryValue() {
                return envEntryValue;
            }

            /**
             * Sets the value of the envEntryValue property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setEnvEntryValue(String value) {
                this.envEntryValue = value;
            }

            /**
             * Gets the value of the envEntryName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getEnvEntryName() {
                return envEntryName;
            }

            /**
             * Sets the value of the envEntryName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setEnvEntryName(String value) {
                this.envEntryName = value;
            }

        }

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
     *         &lt;element name="resource-env-ref" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="resource-env-ref-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="resource-env-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    @XmlType(name = "", propOrder = {
        "resourceEnvRef"
    })
    public static class ResourceEnvRefs {

        @XmlElement(name = "resource-env-ref")
        protected List<ResourceRefType.ResourceEnvRefs.ResourceEnvRef> resourceEnvRef;
        @XmlAttribute
        protected String name;

        /**
         * Gets the value of the resourceEnvRef property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resourceEnvRef property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResourceEnvRef().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ResourceRefType.ResourceEnvRefs.ResourceEnvRef }
         *
         *
         */
        public List<ResourceRefType.ResourceEnvRefs.ResourceEnvRef> getResourceEnvRef() {
            if (resourceEnvRef == null) {
                resourceEnvRef = new ArrayList<ResourceRefType.ResourceEnvRefs.ResourceEnvRef>();
            }
            return this.resourceEnvRef;
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
         *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="resource-env-ref-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *       &lt;attribute name="resource-env-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "description",
            "resourceEnvRefType"
        })
        public static class ResourceEnvRef {

            @XmlElement(required = true)
            protected String description;
            @XmlElement(name = "resource-env-ref-type", required = true)
            protected String resourceEnvRefType;
            @XmlAttribute(name = "resource-env-ref-name")
            protected String resourceEnvRefName;

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
             * Gets the value of the resourceEnvRefType property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getResourceEnvRefType() {
                return resourceEnvRefType;
            }

            /**
             * Sets the value of the resourceEnvRefType property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setResourceEnvRefType(String value) {
                this.resourceEnvRefType = value;
            }

            /**
             * Gets the value of the resourceEnvRefName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getResourceEnvRefName() {
                return resourceEnvRefName;
            }

            /**
             * Sets the value of the resourceEnvRefName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setResourceEnvRefName(String value) {
                this.resourceEnvRefName = value;
            }

        }

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
     *         &lt;element name="resource-ref" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="res-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="res-auth" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                   &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="res-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
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
    @XmlType(name = "", propOrder = {
        "resourceRef"
    })
    public static class ResourceRefs {

        @XmlElement(name = "resource-ref")
        protected List<ResourceRefType.ResourceRefs.ResourceRef> resourceRef;
        @XmlAttribute
        protected String name;

        /**
         * Gets the value of the resourceRef property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resourceRef property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResourceRef().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ResourceRefType.ResourceRefs.ResourceRef }
         *
         *
         */
        public List<ResourceRefType.ResourceRefs.ResourceRef> getResourceRef() {
            if (resourceRef == null) {
                resourceRef = new ArrayList<ResourceRefType.ResourceRefs.ResourceRef>();
            }
            return this.resourceRef;
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
         *         &lt;element name="res-type" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="res-auth" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/sequence>
         *       &lt;attribute name="res-ref-name" type="{http://www.w3.org/2001/XMLSchema}string" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "resType",
            "resAuth",
            "description"
        })
        public static class ResourceRef {

            @XmlElement(name = "res-type", required = true)
            protected String resType;
            @XmlElement(name = "res-auth", required = true)
            protected String resAuth;
            @XmlElement(required = true)
            protected String description;
            @XmlAttribute(name = "res-ref-name")
            protected String resRefName;

            /**
             * Gets the value of the resType property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getResType() {
                return resType;
            }

            /**
             * Sets the value of the resType property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setResType(String value) {
                this.resType = value;
            }

            /**
             * Gets the value of the resAuth property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getResAuth() {
                return resAuth;
            }

            /**
             * Sets the value of the resAuth property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setResAuth(String value) {
                this.resAuth = value;
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
             * Gets the value of the resRefName property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getResRefName() {
                return resRefName;
            }

            /**
             * Sets the value of the resRefName property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setResRefName(String value) {
                this.resRefName = value;
            }

        }

    }

}
