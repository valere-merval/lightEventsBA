package com.lightevents.eventops;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="attendee_answers")
public class AttendeeAnswer {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    private Long eventId; private Long attendeeId; private Long questionId; @Column(length=4000) private String answerText; private Instant answeredAt=Instant.now();
    public Long getId(){return id;} public Long getEventId(){return eventId;} public void setEventId(Long v){eventId=v;} public Long getAttendeeId(){return attendeeId;} public void setAttendeeId(Long v){attendeeId=v;} public Long getQuestionId(){return questionId;} public void setQuestionId(Long v){questionId=v;} public String getAnswerText(){return answerText;} public void setAnswerText(String v){answerText=v;} public Instant getAnsweredAt(){return answeredAt;}
}
