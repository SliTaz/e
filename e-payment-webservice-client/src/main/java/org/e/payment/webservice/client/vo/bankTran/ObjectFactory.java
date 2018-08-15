
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.e.payment.webservice.client.vo.bankTran package. 
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
    private final static QName _Reverse_QNAME = new QName("http://201.249.156.12/webservice", "Reverse");
    private final static QName _ReverseResponse_QNAME = new QName("http://201.249.156.12/webservice", "ReverseResponse");
    private final static QName _BankRechargeReqBody_QNAME = new QName("http://201.249.156.12/webservice", "bankRechargeReqBody");
    private final static QName _BankRechargeRespBody_QNAME = new QName("http://201.249.156.12/webservice", "bankRechargeRespBody");
    private final static QName _ReverseReqBody_QNAME = new QName("http://201.249.156.12/webservice", "reverseReqBody");
    private final static QName _ReverseRespBody_QNAME = new QName("http://201.249.156.12/webservice", "reverseRespBody");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.e.payment.webservice.client.vo.bankTran
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
     * Create an instance of {@link Reverse }
     * 
     */
    public Reverse createReverse() {
        return new Reverse();
    }

    /**
     * Create an instance of {@link ReverseResponse }
     * 
     */
    public ReverseResponse createReverseResponse() {
        return new ReverseResponse();
    }

    /**
     * Create an instance of {@link BankRechargeReqBody }
     * 
     */
    public BankRechargeReqBody createBankRechargeReqBody() {
        return new BankRechargeReqBody();
    }

    /**
     * Create an instance of {@link BankRechargeRespBody }
     * 
     */
    public BankRechargeRespBody createBankRechargeRespBody() {
        return new BankRechargeRespBody();
    }

    /**
     * Create an instance of {@link ReverseReqBody }
     * 
     */
    public ReverseReqBody createReverseReqBody() {
        return new ReverseReqBody();
    }

    /**
     * Create an instance of {@link ReverseRespBody }
     * 
     */
    public ReverseRespBody createReverseRespBody() {
        return new ReverseRespBody();
    }

    /**
     * Create an instance of {@link RechargeRequest }
     * 
     */
    public RechargeRequest createRechargeRequest() {
        return new RechargeRequest();
    }

    /**
     * Create an instance of {@link BankPaymentInfo }
     * 
     */
    public BankPaymentInfo createBankPaymentInfo() {
        return new BankPaymentInfo();
    }

    /**
     * Create an instance of {@link RechargeResponse2 }
     * 
     */
    public RechargeResponse2 createRechargeResponse2() {
        return new RechargeResponse2();
    }

    /**
     * Create an instance of {@link ReverseRequest }
     * 
     */
    public ReverseRequest createReverseRequest() {
        return new ReverseRequest();
    }

    /**
     * Create an instance of {@link ReverseResponse2 }
     * 
     */
    public ReverseResponse2 createReverseResponse2() {
        return new ReverseResponse2();
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
     * Create an instance of {@link JAXBElement }{@code <}{@link Reverse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "Reverse")
    public JAXBElement<Reverse> createReverse(Reverse value) {
        return new JAXBElement<Reverse>(_Reverse_QNAME, Reverse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "ReverseResponse")
    public JAXBElement<ReverseResponse> createReverseResponse(ReverseResponse value) {
        return new JAXBElement<ReverseResponse>(_ReverseResponse_QNAME, ReverseResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BankRechargeReqBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "bankRechargeReqBody")
    public JAXBElement<BankRechargeReqBody> createBankRechargeReqBody(BankRechargeReqBody value) {
        return new JAXBElement<BankRechargeReqBody>(_BankRechargeReqBody_QNAME, BankRechargeReqBody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link BankRechargeRespBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "bankRechargeRespBody")
    public JAXBElement<BankRechargeRespBody> createBankRechargeRespBody(BankRechargeRespBody value) {
        return new JAXBElement<BankRechargeRespBody>(_BankRechargeRespBody_QNAME, BankRechargeRespBody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseReqBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "reverseReqBody")
    public JAXBElement<ReverseReqBody> createReverseReqBody(ReverseReqBody value) {
        return new JAXBElement<ReverseReqBody>(_ReverseReqBody_QNAME, ReverseReqBody.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ReverseRespBody }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://201.249.156.12/webservice", name = "reverseRespBody")
    public JAXBElement<ReverseRespBody> createReverseRespBody(ReverseRespBody value) {
        return new JAXBElement<ReverseRespBody>(_ReverseRespBody_QNAME, ReverseRespBody.class, null, value);
    }

}
