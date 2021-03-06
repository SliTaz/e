
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>reverseRequest complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="reverseRequest"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bankId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="interfaceVersion" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="refNo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="reverseReqBody" type="{http://201.249.156.12/webservice}reverseReqBody" minOccurs="0"/&gt;
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
@XmlType(name = "reverseRequest", propOrder = {
    "bankId",
    "interfaceVersion",
    "refNo",
    "reverseReqBody",
    "sigMsg"
})
public class ReverseRequest {

    protected String bankId;
    protected String interfaceVersion;
    protected String refNo;
    protected ReverseReqBody reverseReqBody;
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
     * 获取reverseReqBody属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ReverseReqBody }
     *     
     */
    public ReverseReqBody getReverseReqBody() {
        return reverseReqBody;
    }

    /**
     * 设置reverseReqBody属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ReverseReqBody }
     *     
     */
    public void setReverseReqBody(ReverseReqBody value) {
        this.reverseReqBody = value;
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
