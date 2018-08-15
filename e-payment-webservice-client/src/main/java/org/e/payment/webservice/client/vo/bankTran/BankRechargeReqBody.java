
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>bankRechargeReqBody complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="bankRechargeReqBody"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="bankPaymentInfo" type="{http://201.249.156.12/webservice}bankPaymentInfo" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bankRechargeReqBody", propOrder = {
    "bankPaymentInfo"
})
public class BankRechargeReqBody {

    protected BankPaymentInfo bankPaymentInfo;

    /**
     * 获取bankPaymentInfo属性的值。
     * 
     * @return
     *     possible object is
     *     {@link BankPaymentInfo }
     *     
     */
    public BankPaymentInfo getBankPaymentInfo() {
        return bankPaymentInfo;
    }

    /**
     * 设置bankPaymentInfo属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link BankPaymentInfo }
     *     
     */
    public void setBankPaymentInfo(BankPaymentInfo value) {
        this.bankPaymentInfo = value;
    }

}
