package com.lightevents.auth;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "accounts")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @NotBlank private String fullName;
    @Email @Column(unique = true) private String email;
    @Column(unique = true) private String phone;
    private String whatsappNumber;
    @Enumerated(EnumType.STRING) private AccountRole role = AccountRole.PARTICIPANT;
    private boolean emailVerified;
    private boolean phoneVerified;
    private String emailVerificationCode;
    private String phoneVerificationCode;
    @Column(unique = true, nullable = false) private String apiToken = UUID.randomUUID().toString();
    @Enumerated(EnumType.STRING) private PayoutMethod payoutMethod = PayoutMethod.BANK_TRANSFER;
    private String payoutCountry;
    private String payoutAccountName;
    private String payoutAccountRef;
    private Instant createdAt = Instant.now();

    public Long getId(){return id;} public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;} public String getEmail(){return email;} public void setEmail(String v){email=v;} public String getPhone(){return phone;} public void setPhone(String v){phone=v;} public String getWhatsappNumber(){return whatsappNumber;} public void setWhatsappNumber(String v){whatsappNumber=v;} public AccountRole getRole(){return role;} public void setRole(AccountRole v){role=v;} public boolean isEmailVerified(){return emailVerified;} public void setEmailVerified(boolean v){emailVerified=v;} public boolean isPhoneVerified(){return phoneVerified;} public void setPhoneVerified(boolean v){phoneVerified=v;} public String getEmailVerificationCode(){return emailVerificationCode;} public void setEmailVerificationCode(String v){emailVerificationCode=v;} public String getPhoneVerificationCode(){return phoneVerificationCode;} public void setPhoneVerificationCode(String v){phoneVerificationCode=v;} public String getApiToken(){return apiToken;} public PayoutMethod getPayoutMethod(){return payoutMethod;} public void setPayoutMethod(PayoutMethod v){payoutMethod=v;} public String getPayoutCountry(){return payoutCountry;} public void setPayoutCountry(String v){payoutCountry=v;} public String getPayoutAccountName(){return payoutAccountName;} public void setPayoutAccountName(String v){payoutAccountName=v;} public String getPayoutAccountRef(){return payoutAccountRef;} public void setPayoutAccountRef(String v){payoutAccountRef=v;} public Instant getCreatedAt(){return createdAt;}
    public boolean isVerified(){ return emailVerified || phoneVerified; }
}
