
package org.e.payment.webservice.client.vo.V2.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>rechargeRequestV2 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="rechargeRequestV2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="bankRechargeReqBody" type="{http://201.249.156.12/webservice}bankRechargeReqBodyV2" minOccurs="0"/&gt;
 *         &lt;element name="interfaceVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="sigMsg" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "rechargeRequestV2", propOrder = {
    "bankId",
    "bankRechargeReqBody",
    "interfaceVersion",
    "refNo",
    "sigMsg"
})
public class RechargeRequestV2 {

    protected String bankId;
    protected BankRechargeReqBodyV2 bankRechargeReqBody;
    protected String interfaceVersion;
    protected String refNo;
    protected String sigMsg;

    /**
     * 获取bankId属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBankId() {
        return bankId;
    }

    /**
     * 设置bankId属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBankId(String value) {
        this.bankId = value;
    }

    /**
     * 获取bankRechargeReqBody属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BankRechargeReqBodyV2 }
     *     
     */
    public BankRechargeReqBodyV2 getBankRechargeReqBody() {
        return bankRechargeReqBody;
    }

    /**
     * 设置bankRechargeReqBody属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BankRechargeReqBodyV2 }
     *     
     */
    public void setBankRechargeReqBody(BankRechargeReqBodyV2 value) {
        this.bankRechargeReqBody = value;
    }

    /**
     * 获取interfaceVersion属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInterfaceVersion() {
        return interfaceVersion;
    }

    /**
     * 设置interfaceVersion属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInterfaceVersion(String value) {
        this.interfaceVersion = value;
    }

    /**
     * 获取refNo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRefNo() {
        return refNo;
    }

    /**
     * 设置refNo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRefNo(String value) {
        this.refNo = value;
    }

    /**
     * 获取sigMsg属性的值。
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSigMsg() {
        return sigMsg;
    }

    /**
     * 设置sigMsg属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSigMsg(String value) {
        this.sigMsg = value;
    }

}
