//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.3.0 
// Vedere <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2021.08.02 alle 11:23:32 AM CEST 
//


package it.telecomitalia.soa.trcs.gateway;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Complex type describing the Transaction header
 * 				information
 * 			
 * 
 * <p>Classe Java per TransactionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="TransactionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="Subsystem" use="required"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="12"/&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="TID" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="GovernanceTid"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="48"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="IDServiceNotify"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="12"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Service"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="12"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="RetCode"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="10"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="AppDep_1"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="11"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="AppDep_2"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="11"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="Routing"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="10"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="IDOperationNotify" type="{http://www.w3.org/2001/XMLSchema}integer" /&gt;
 *       &lt;attribute name="responseTarget" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="IDSystem"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *             &lt;maxLength value="12"/&gt;
 *             &lt;minLength value="1"/&gt;
 *             &lt;whiteSpace value="preserve"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="RequestDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionType")
@XmlSeeAlso({
    it.telecomitalia.soa.trcs.gateway.PersonalCreditSwitchIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.MigrateSubscriberXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.InstallSubscriberXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.DeleteSubscriberXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.RestoreSubscriberXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.SaleOperationXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.SetSubscriberStatusXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.ChangeTOCIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.PersonalDataManagerXIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.ChangeNumberIbData.Transaction.class,
    it.telecomitalia.soa.trcs.gateway.NetworkReconfigureIbData.Transaction.class
})
public class TransactionType {

    @XmlAttribute(name = "Subsystem", required = true)
    protected String subsystem;
    @XmlAttribute(name = "TID")
    protected String tid;
    @XmlAttribute(name = "GovernanceTid")
    protected String governanceTid;
    @XmlAttribute(name = "IDServiceNotify")
    protected String idServiceNotify;
    @XmlAttribute(name = "Service")
    protected String service;
    @XmlAttribute(name = "RetCode")
    protected String retCode;
    @XmlAttribute(name = "AppDep_1")
    protected String appDep1;
    @XmlAttribute(name = "AppDep_2")
    protected String appDep2;
    @XmlAttribute(name = "Routing")
    protected String routing;
    @XmlAttribute(name = "IDOperationNotify")
    protected BigInteger idOperationNotify;
    @XmlAttribute(name = "responseTarget")
    protected String responseTarget;
    @XmlAttribute(name = "IDSystem")
    protected String idSystem;
    @XmlAttribute(name = "RequestDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDate;

    /**
     * Recupera il valore della proprietà subsystem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubsystem() {
        return subsystem;
    }

    /**
     * Imposta il valore della proprietà subsystem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubsystem(String value) {
        this.subsystem = value;
    }

    /**
     * Recupera il valore della proprietà tid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTID() {
        return tid;
    }

    /**
     * Imposta il valore della proprietà tid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTID(String value) {
        this.tid = value;
    }

    /**
     * Recupera il valore della proprietà governanceTid.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGovernanceTid() {
        return governanceTid;
    }

    /**
     * Imposta il valore della proprietà governanceTid.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGovernanceTid(String value) {
        this.governanceTid = value;
    }

    /**
     * Recupera il valore della proprietà idServiceNotify.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDServiceNotify() {
        return idServiceNotify;
    }

    /**
     * Imposta il valore della proprietà idServiceNotify.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDServiceNotify(String value) {
        this.idServiceNotify = value;
    }

    /**
     * Recupera il valore della proprietà service.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getService() {
        return service;
    }

    /**
     * Imposta il valore della proprietà service.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setService(String value) {
        this.service = value;
    }

    /**
     * Recupera il valore della proprietà retCode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRetCode() {
        return retCode;
    }

    /**
     * Imposta il valore della proprietà retCode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRetCode(String value) {
        this.retCode = value;
    }

    /**
     * Recupera il valore della proprietà appDep1.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppDep1() {
        return appDep1;
    }

    /**
     * Imposta il valore della proprietà appDep1.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppDep1(String value) {
        this.appDep1 = value;
    }

    /**
     * Recupera il valore della proprietà appDep2.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAppDep2() {
        return appDep2;
    }

    /**
     * Imposta il valore della proprietà appDep2.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAppDep2(String value) {
        this.appDep2 = value;
    }

    /**
     * Recupera il valore della proprietà routing.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRouting() {
        return routing;
    }

    /**
     * Imposta il valore della proprietà routing.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRouting(String value) {
        this.routing = value;
    }

    /**
     * Recupera il valore della proprietà idOperationNotify.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getIDOperationNotify() {
        return idOperationNotify;
    }

    /**
     * Imposta il valore della proprietà idOperationNotify.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setIDOperationNotify(BigInteger value) {
        this.idOperationNotify = value;
    }

    /**
     * Recupera il valore della proprietà responseTarget.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResponseTarget() {
        return responseTarget;
    }

    /**
     * Imposta il valore della proprietà responseTarget.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResponseTarget(String value) {
        this.responseTarget = value;
    }

    /**
     * Recupera il valore della proprietà idSystem.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIDSystem() {
        return idSystem;
    }

    /**
     * Imposta il valore della proprietà idSystem.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIDSystem(String value) {
        this.idSystem = value;
    }

    /**
     * Recupera il valore della proprietà requestDate.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Imposta il valore della proprietà requestDate.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

}