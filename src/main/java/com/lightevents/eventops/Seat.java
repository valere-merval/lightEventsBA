package com.lightevents.eventops;

import jakarta.persistence.*;

@Entity @Table(name="seats", uniqueConstraints=@UniqueConstraint(columnNames={"seatMapId","seatLabel"}))
public class Seat {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private Long seatMapId; private String sectionName; private String rowLabel; private Integer seatNumber; private String seatLabel;
    private String status="AVAILABLE"; private Long attendeeId; private String reservationReference;
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getSeatMapId(){return seatMapId;} public void setSeatMapId(Long v){seatMapId=v;} public String getSectionName(){return sectionName;} public void setSectionName(String v){sectionName=v;} public String getRowLabel(){return rowLabel;} public void setRowLabel(String v){rowLabel=v;} public Integer getSeatNumber(){return seatNumber;} public void setSeatNumber(Integer v){seatNumber=v;} public String getSeatLabel(){return seatLabel;} public void setSeatLabel(String v){seatLabel=v;} public String getStatus(){return status;} public void setStatus(String v){status=v;} public Long getAttendeeId(){return attendeeId;} public void setAttendeeId(Long v){attendeeId=v;} public String getReservationReference(){return reservationReference;} public void setReservationReference(String v){reservationReference=v;}
}
