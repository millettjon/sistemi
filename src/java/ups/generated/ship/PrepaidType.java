//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.02.18 at 09:59:43 PM CST 
//


package ups.generated.ship;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for PrepaidType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PrepaidType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BillShipper" type="{http://www.ups.com/XMLSchema/XOLTWS/Ship/v1.0}BillShipperType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrepaidType", propOrder = {
    "billShipper"
})
public class PrepaidType {

    @XmlElement(name = "BillShipper", required = true)
    protected BillShipperType billShipper;

    /**
     * Gets the value of the billShipper property.
     * 
     * @return
     *     possible object is
     *     {@link BillShipperType }
     *     
     */
    public BillShipperType getBillShipper() {
        return billShipper;
    }

    /**
     * Sets the value of the billShipper property.
     * 
     * @param value
     *     allowed object is
     *     {@link BillShipperType }
     *     
     */
    public void setBillShipper(BillShipperType value) {
        this.billShipper = value;
    }

}
