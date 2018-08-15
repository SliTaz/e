
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Recharge complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Recharge"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="RechargeRequest" type="{http://201.249.156.12/webservice}rechargeRequest" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Recharge", propOrder = {
    "rechargeRequest"
})
public class Recharge {

    @XmlElement(name = "RechargeRequest")
    protected RechargeRequest rechargeRequest;

    /**
     * 获取rechargeRequest属性的值。
     * 
     * @return
     *     possible object is
     *     {@link RechargeRequest }
     *     
     */
    public RechargeRequest getRechargeRequest() {
        return rechargeRequest;
    }

    /**
     * 设置rechargeRequest属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link RechargeRequest }
     *     
     */
    public void setRechargeRequest(RechargeRequest value) {
        this.rechargeRequest = value;
    }

}
