
package org.e.payment.webservice.client.vo.V2.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>rechargeResponseV2 complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="rechargeResponseV2"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bankRechargeRespBody" type="{http://201.249.156.12/webservice}bankRechargeRespBodyV2" minOccurs="0"/&gt;
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
@XmlType(name = "rechargeResponseV2", propOrder = {
    "bankRechargeRespBody",
    "interfaceVersion",
    "refNo",
    "sigMsg"
})
public class RechargeResponseV2 {

    protected BankRechargeRespBodyV2 bankRechargeRespBody;
    protected String interfaceVersion;
    protected String refNo;
    protected String sigMsg;

    /**
     * 获取bankRechargeRespBody属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BankRechargeRespBodyV2 }
     *     
     */
    public BankRechargeRespBodyV2 getBankRechargeRespBody() {
        return bankRechargeRespBody;
    }

    /**
     * 设置bankRechargeRespBody属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BankRechargeRespBodyV2 }
     *     
     */
    public void setBankRechargeRespBody(BankRechargeRespBodyV2 value) {
        this.bankRechargeRespBody = value;
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
