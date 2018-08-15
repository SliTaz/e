
package org.e.payment.webservice.client.vo.V2.bankTran;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.e.payment.webservice.client.vo.V2.bankTran package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Recharge_QNAME = new QName("http://201.249.156.12/webservice", "Recharge");
    private final static QName _RechargeResponse_QNAME = new QName("http://201.249.156.12/webservice", "RechargeResponse");
    private final static QName _BankRechargeReqBodyV2_QNAME = new QName("http://201.249.156.12/webservice", "bankRechargeReqBodyV2");
    private final static QName _BankRechargeRespBodyV2_QNAME = new QName("http://201.249.156.12/webservice", "bankRechargeRespBodyV2");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.e.payment.webservice.client.vo.V2.bankTran
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Recharge }
     * 
     */
    public Recharge createRecharge() {
        return new Recharge();
    }

    /**
     * Create an instance of {@link RechargeResponse }
     * 
     */
    public RechargeResponse createRechargeResponse() {
        return new RechargeResponse();
    }

    /**
     * Create an instance of {@link BankRechargeReqBodyV2 }
     * 
     */
    public BankRechargeReqBodyV2 createBankRechargeReqBodyV2() {
        return new BankRechargeReqBodyV2();
    }

    /**
     * Create an instance of {@link BankRechargeRespBodyV2 }
     * 
     */
    public BankRechargeRespBodyV2 createBankRechargeRespBodyV2() {
        return new BankRechargeRespBodyV2();
    }

    /**
     * Create an instance of {@link RechargeRequestV2 }
     * 
     */
    public RechargeRequestV2 createRechargeRequestV2() {
        return new RechargeRequestV2();
    }

    /**
     * Create an instance of {@link BankPaymentInfoV2 }
     * 
     */
    public BankPaymentInfoV2 createBankPaymentInfoV2() {
        return new BankPaymentInfoV2();
    }

    /**
     * Create an instance of {@link RechargeResponseV2 }
     * 
     */
    public RechargeResponseV2 createRechargeResponseV2() {
        return new RechargeResponseV2();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Recharge }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "Recharge")
    public JAXBElement<Recharge> createRecharge(Recharge value) {
        return new JAXBElement<Recharge>(_Recharge_QNAME, Recharge.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RechargeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "RechargeResponse")
    public JAXBElement<RechargeResponse> createRechargeResponse(RechargeResponse value) {
        return new JAXBElement<RechargeResponse>(_RechargeResponse_QNAME, RechargeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BankRechargeReqBodyV2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "bankRechargeReqBodyV2")
    public JAXBElement<BankRechargeReqBodyV2> createBankRechargeReqBodyV2(BankRechargeReqBodyV2 value) {
        return new JAXBElement<BankRechargeReqBodyV2>(_BankRechargeReqBodyV2_QNAME, BankRechargeReqBodyV2 .class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BankRechargeRespBodyV2 }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "bankRechargeRespBodyV2")
    public JAXBElement<BankRechargeRespBodyV2> createBankRechargeRespBodyV2(BankRechargeRespBodyV2 value) {
        return new JAXBElement<BankRechargeRespBodyV2>(_BankRechargeRespBodyV2_QNAME, BankRechargeRespBodyV2 .class, null, value);
    }

}
