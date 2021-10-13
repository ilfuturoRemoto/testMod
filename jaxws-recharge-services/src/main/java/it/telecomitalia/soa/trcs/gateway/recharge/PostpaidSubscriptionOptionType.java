//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.3.0 
// Vedere <a href="https://javaee.github.io/jaxb-v2/">https://javaee.github.io/jaxb-v2/</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2021.10.13 alle 05:08:21 PM CEST 
//


package it.telecomitalia.soa.trcs.gateway.recharge;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Complex type describing the Subscription structure
 * 			
 * 
 * <p>Classe Java per PostpaidSubscriptionOptionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PostpaidSubscriptionOptionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}OptionalSubscriptionType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ActivationDate" type="{http://telecomitalia.it/SOA/TRCS/Gateway}LifeCycleType" minOccurs="0"/&gt;
 *         &lt;element name="DeactivationDate" type="{http://telecomitalia.it/SOA/TRCS/Gateway}LifeCycleType" minOccurs="0"/&gt;
 *         &lt;element name="ExpiryDate" type="{http://telecomitalia.it/SOA/TRCS/Gateway}LifeCycleType" minOccurs="0"/&gt;
 *         &lt;element name="Offer" maxOccurs="unbounded" minOccurs="0"&gt;
 *           &lt;complexType&gt;
 *             &lt;complexContent&gt;
 *               &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}ActOfferType"&gt;
 *                 &lt;sequence minOccurs="0"&gt;
 *                   &lt;element name="ExtraData" minOccurs="0"&gt;
 *                     &lt;complexType&gt;
 *                       &lt;complexContent&gt;
 *                         &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}ExtraDataType"&gt;
 *                         &lt;/extension&gt;
 *                       &lt;/complexContent&gt;
 *                     &lt;/complexType&gt;
 *                   &lt;/element&gt;
 *                 &lt;/sequence&gt;
 *               &lt;/extension&gt;
 *             &lt;/complexContent&gt;
 *           &lt;/complexType&gt;
 *         &lt;/element&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PostpaidSubscriptionOptionType", propOrder = {
    "activationDate",
    "deactivationDate",
    "expiryDate",
    "offer"
})
@XmlSeeAlso({
    PostpaidAnyOfferChangeType.class,
    ActAnyOfferChangeType.class
})
public class PostpaidSubscriptionOptionType
    extends OptionalSubscriptionType
{

    @XmlElement(name = "ActivationDate")
    protected LifeCycleType activationDate;
    @XmlElement(name = "DeactivationDate")
    protected LifeCycleType deactivationDate;
    @XmlElement(name = "ExpiryDate")
    protected LifeCycleType expiryDate;
    @XmlElement(name = "Offer")
    protected List<PostpaidSubscriptionOptionType.Offer> offer;

    /**
     * Recupera il valore della proprietà activationDate.
     * 
     * @return
     *     possible object is
     *     {@link LifeCycleType }
     *     
     */
    public LifeCycleType getActivationDate() {
        return activationDate;
    }

    /**
     * Imposta il valore della proprietà activationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link LifeCycleType }
     *     
     */
    public void setActivationDate(LifeCycleType value) {
        this.activationDate = value;
    }

    /**
     * Recupera il valore della proprietà deactivationDate.
     * 
     * @return
     *     possible object is
     *     {@link LifeCycleType }
     *     
     */
    public LifeCycleType getDeactivationDate() {
        return deactivationDate;
    }

    /**
     * Imposta il valore della proprietà deactivationDate.
     * 
     * @param value
     *     allowed object is
     *     {@link LifeCycleType }
     *     
     */
    public void setDeactivationDate(LifeCycleType value) {
        this.deactivationDate = value;
    }

    /**
     * Recupera il valore della proprietà expiryDate.
     * 
     * @return
     *     possible object is
     *     {@link LifeCycleType }
     *     
     */
    public LifeCycleType getExpiryDate() {
        return expiryDate;
    }

    /**
     * Imposta il valore della proprietà expiryDate.
     * 
     * @param value
     *     allowed object is
     *     {@link LifeCycleType }
     *     
     */
    public void setExpiryDate(LifeCycleType value) {
        this.expiryDate = value;
    }

    /**
     * Gets the value of the offer property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the offer property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOffer().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PostpaidSubscriptionOptionType.Offer }
     * 
     * 
     */
    public List<PostpaidSubscriptionOptionType.Offer> getOffer() {
        if (offer == null) {
            offer = new ArrayList<PostpaidSubscriptionOptionType.Offer>();
        }
        return this.offer;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType&gt;
     *   &lt;complexContent&gt;
     *     &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}ActOfferType"&gt;
     *       &lt;sequence minOccurs="0"&gt;
     *         &lt;element name="ExtraData" minOccurs="0"&gt;
     *           &lt;complexType&gt;
     *             &lt;complexContent&gt;
     *               &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}ExtraDataType"&gt;
     *               &lt;/extension&gt;
     *             &lt;/complexContent&gt;
     *           &lt;/complexType&gt;
     *         &lt;/element&gt;
     *       &lt;/sequence&gt;
     *     &lt;/extension&gt;
     *   &lt;/complexContent&gt;
     * &lt;/complexType&gt;
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "extraData"
    })
    public static class Offer
        extends ActOfferType
    {

        @XmlElement(name = "ExtraData")
        protected PostpaidSubscriptionOptionType.Offer.ExtraData extraData;

        /**
         * Recupera il valore della proprietà extraData.
         * 
         * @return
         *     possible object is
         *     {@link PostpaidSubscriptionOptionType.Offer.ExtraData }
         *     
         */
        public PostpaidSubscriptionOptionType.Offer.ExtraData getExtraData() {
            return extraData;
        }

        /**
         * Imposta il valore della proprietà extraData.
         * 
         * @param value
         *     allowed object is
         *     {@link PostpaidSubscriptionOptionType.Offer.ExtraData }
         *     
         */
        public void setExtraData(PostpaidSubscriptionOptionType.Offer.ExtraData value) {
            this.extraData = value;
        }


        /**
         * <p>Classe Java per anonymous complex type.
         * 
         * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
         * 
         * <pre>
         * &lt;complexType&gt;
         *   &lt;complexContent&gt;
         *     &lt;extension base="{http://telecomitalia.it/SOA/TRCS/Gateway}ExtraDataType"&gt;
         *     &lt;/extension&gt;
         *   &lt;/complexContent&gt;
         * &lt;/complexType&gt;
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ExtraData
            extends ExtraDataType
        {


        }

    }

}
