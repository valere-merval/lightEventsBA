package com.lightevents.profiles;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Entity @Table(name = "profiles")
public class UserProfile {
 @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
 @NotBlank private String fullName; @Email private String email; private String phone; private String avatarUrl;
 private String headline; private String company; private String city; private String country;
 @Column(length=2000) private String bio; private String skills; private String lookingFor; private String offering;
 private String linkedinUrl; private String whatsappNumber; private Instant createdAt = Instant.now();
 public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getAvatarUrl(){return avatarUrl;} public void setAvatarUrl(String v){avatarUrl=v;} public String getHeadline(){return headline;} public void setHeadline(String v){headline=v;} public String getCompany(){return company;} public void setCompany(String v){company=v;} public String getCity(){return city;} public void setCity(String v){city=v;} public String getCountry(){return country;} public void setCountry(String v){country=v;} public String getBio(){return bio;} public void setBio(String v){bio=v;} public String getSkills(){return skills;} public void setSkills(String v){skills=v;} public String getLookingFor(){return lookingFor;} public void setLookingFor(String v){lookingFor=v;} public String getOffering(){return offering;} public void setOffering(String v){offering=v;} public String getLinkedinUrl(){return linkedinUrl;} public void setLinkedinUrl(String v){linkedinUrl=v;} public String getWhatsappNumber(){return whatsappNumber;} public void setWhatsappNumber(String v){whatsappNumber=v;} public Instant getCreatedAt(){return createdAt;}
}
