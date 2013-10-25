//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.10.16 at 03:07:54 PM CEST 
//


package org.parallelj.internal.conf.pojos;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element ref="{http://parallelj.org}procedures"/>
 *         &lt;element ref="{http://parallelj.org}servers"/>
 *         &lt;element ref="{http://parallelj.org}executor-services"/>
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
    "procedures",
    "servers",
    "executorServices"
})
@XmlRootElement(name = "parallelj")
public class ParalleljConfiguration {

    @XmlElement(required = true)
    protected CProcedures procedures;
    @XmlElement(required = true)
    protected CServers servers;
    @XmlElement(name = "executor-services", required = true)
    protected CExecutors executorServices;

    /**
     * Gets the value of the procedures property.
     * 
     * @return
     *     possible object is
     *     {@link CProcedures }
     *     
     */
    public CProcedures getProcedures() {
        return procedures;
    }

    /**
     * Sets the value of the procedures property.
     * 
     * @param value
     *     allowed object is
     *     {@link CProcedures }
     *     
     */
    public void setProcedures(CProcedures value) {
        this.procedures = value;
    }

    /**
     * Gets the value of the servers property.
     * 
     * @return
     *     possible object is
     *     {@link CServers }
     *     
     */
    public CServers getServers() {
        return servers;
    }

    /**
     * Sets the value of the servers property.
     * 
     * @param value
     *     allowed object is
     *     {@link CServers }
     *     
     */
    public void setServers(CServers value) {
        this.servers = value;
    }

    /**
     * Gets the value of the executorServices property.
     * 
     * @return
     *     possible object is
     *     {@link CExecutors }
     *     
     */
    public CExecutors getExecutorServices() {
        return executorServices;
    }

    /**
     * Sets the value of the executorServices property.
     * 
     * @param value
     *     allowed object is
     *     {@link CExecutors }
     *     
     */
    public void setExecutorServices(CExecutors value) {
        this.executorServices = value;
    }

}
