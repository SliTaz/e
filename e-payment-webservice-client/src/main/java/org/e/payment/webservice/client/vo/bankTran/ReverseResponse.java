
package org.e.payment.webservice.client.vo.bankTran;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>ReverseResponse complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType name="ReverseResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://201.249.156.12/webservice}reverseResponse" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReverseResponse", propOrder = {
    "_return"
})
public class ReverseResponse {

    @XmlElement(name = "return")
    protected ReverseResponse2 _return;

    /**
     * 获取return属性的值。
     * 
     * @return
     *     possible object is
     *     {@link ReverseResponse2 }
     *     
     */
    public ReverseResponse2 getReturn() {
        return _return;
    }

    /**
     * 设置return属性的值。
     * 
     * @param value
     *     allowed object is
     *     {@link ReverseResponse2 }
     *     
     */
    public void setReturn(ReverseResponse2 value) {
        this._return = value;
    }

}
