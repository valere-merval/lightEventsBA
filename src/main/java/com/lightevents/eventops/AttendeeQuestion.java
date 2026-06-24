package com.lightevents.eventops;

import jakarta.persistence.*;

@Entity @Table(name="attendee_questions")
public class AttendeeQuestion {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private String label; private String type="TEXT"; private boolean required; @Column(length=2000) private String optionsText; private int sortOrder;
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public String getLabel(){return label;} public void setLabel(String v){label=v;} public String getType(){return type;} public void setType(String v){type=v;} public boolean isRequired(){return required;} public void setRequired(boolean v){required=v;} public String getOptionsText(){return optionsText;} public void setOptionsText(String v){optionsText=v;} public int getSortOrder(){return sortOrder;} public void setSortOrder(int v){sortOrder=v;}
}
