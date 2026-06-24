package com.lightevents.eventops;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="box_office_sales")
public class BoxOfficeSale {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true, nullable=false) private String reference = "POS-" + UUID.randomUUID();
    private Long eventId; private Long ticketTypeId; private Long cashierAccountId;
    private String buyerName; @Email private String buyerEmail; private String buyerPhone;
    private int quantity; private BigDecimal unitPrice=BigDecimal.ZERO; private BigDecimal totalAmount=BigDecimal.ZERO;
    private String currency="XOF"; private String paymentMethod="CASH"; private String status="PAID";
    private Instant soldAt=Instant.now();
    public Long getId(){return id;} public String getReference(){return reference;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getTicketTypeId(){return ticketTypeId;} public void setTicketTypeId(Long v){ticketTypeId=v;} public Long getCashierAccountId(){return cashierAccountId;} public void setCashierAccountId(Long v){cashierAccountId=v;} public String getBuyerName(){return buyerName;} public void setBuyerName(String v){buyerName=v;} public String getBuyerEmail(){return buyerEmail;} public void setBuyerEmail(String v){buyerEmail=v;} public String getBuyerPhone(){return buyerPhone;} public void setBuyerPhone(String v){buyerPhone=v;} public int getQuantity(){return quantity;} public void setQuantity(int v){quantity=v;} public BigDecimal getUnitPrice(){return unitPrice;} public void setUnitPrice(BigDecimal v){unitPrice=v;} public BigDecimal getTotalAmount(){return totalAmount;} public void setTotalAmount(BigDecimal v){totalAmount=v;} public String getCurrency(){return currency;} public void setCurrency(String v){currency=v;} public String getPaymentMethod(){return paymentMethod;} public void setPaymentMethod(String v){paymentMethod=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Instant getSoldAt(){return soldAt;}
}
