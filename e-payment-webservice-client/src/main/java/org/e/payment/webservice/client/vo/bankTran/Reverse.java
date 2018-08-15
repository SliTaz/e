
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Reverse complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="Reverse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReverseRequest" type="{http://201.249.156.12/webservice}reverseRequest" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Reverse", propOrder = {
    "reverseRequest"
})
public class Reverse {

    @XmlElement(name = "ReverseRequest")
    protected ReverseRequest reverseRequest;

    /**
     * 获取reverseRequest属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ReverseRequest }
     *     
     */
    public ReverseRequest getReverseRequest() {
        return reverseRequest;
    }

    /**
     * 设置reverseRequest属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ReverseRequest }
     *     
     */
    public void setReverseRequest(ReverseRequest value) {
        this.reverseRequest = value;
    }

}
