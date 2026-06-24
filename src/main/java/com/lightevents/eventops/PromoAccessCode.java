package com.lightevents.eventops;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity @Table(name="promo_access_codes", uniqueConstraints=@UniqueConstraint(columnNames={"eventId","code"}))
public class PromoAccessCode {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private String code; private String type="PROMO"; private BigDecimal discountAmount=BigDecimal.ZERO; private Integer discountPercent; private Integer maxRedemptions; private int redeemedCount; private boolean active=true; private LocalDateTime startsAt; private LocalDateTime endsAt;
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getCode(){return code;} public void setCode(String v){code=v;} public String getType(){return type;} public void setType(String v){type=v;} public BigDecimal getDiscountAmount(){return discountAmount;} public void setDiscountAmount(BigDecimal v){discountAmount=v;} public Integer getDiscountPercent(){return discountPercent;} public void setDiscountPercent(Integer v){discountPercent=v;} public Integer getMaxRedemptions(){return maxRedemptions;} public void setMaxRedemptions(Integer v){maxRedemptions=v;} public int getRedeemedCount(){return redeemedCount;} public void setRedeemedCount(int v){redeemedCount=v;} public boolean isActive(){return active;} public void setActive(boolean v){active=v;} public LocalDateTime getStartsAt(){return startsAt;} public void setStartsAt(LocalDateTime v){startsAt=v;} public LocalDateTime getEndsAt(){return endsAt;} public void setEndsAt(LocalDateTime v){endsAt=v;}
}
