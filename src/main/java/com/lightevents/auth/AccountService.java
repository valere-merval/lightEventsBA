package com.lightevents.auth;

import com.lightevents.shared.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.security.SecureRandom;

@Service
public class AccountService {
    private final AccountRepository accounts;
    private final SecureRandom random = new SecureRandom();
    public AccountService(AccountRepository accounts){this.accounts=accounts;}

    @Transactional
    public Account register(AuthDtos.RegisterRequest r){
        if((r.email()==null || r.email().isBlank()) && (r.phone()==null || r.phone().isBlank())) throw new ApiException(HttpStatus.BAD_REQUEST, "Email or phone is required");
        Account a = r.email()==null?null:accounts.findByEmail(r.email()).orElse(null);
        if(a==null) a = new Account();
        a.setFullName(r.fullName()); a.setEmail(r.email()); a.setPhone(r.phone()); a.setWhatsappNumber(r.whatsappNumber());
        a.setRole(r.role()==null||r.role()==AccountRole.ADMIN?AccountRole.PARTICIPANT:r.role());
        a.setPayoutMethod(r.payoutMethod()==null?PayoutMethod.BANK_TRANSFER:r.payoutMethod()); a.setPayoutCountry(r.payoutCountry()); a.setPayoutAccountName(r.payoutAccountName()); a.setPayoutAccountRef(r.payoutAccountRef());
        a.setEmailVerificationCode(code()); a.setPhoneVerificationCode(code());
        return accounts.save(a);
    }
    @Transactional
    public Account verify(AuthDtos.VerifyRequest r){
        Account a = r.channel().equalsIgnoreCase("phone") || r.channel().equalsIgnoreCase("whatsapp") ? accounts.findAll().stream().filter(x -> r.destination().equals(x.getPhone()) || r.destination().equals(x.getWhatsappNumber())).findFirst().orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Account not found")) : accounts.findByEmail(r.destination()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Account not found"));
        if(r.channel().equalsIgnoreCase("email")){ if(!r.code().equals(a.getEmailVerificationCode())) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid email code"); a.setEmailVerified(true); }
        else { if(!r.code().equals(a.getPhoneVerificationCode())) throw new ApiException(HttpStatus.BAD_REQUEST,"Invalid phone code"); a.setPhoneVerified(true); }
        return accounts.save(a);
    }
    public Account login(AuthDtos.LoginRequest r){ return accounts.findByEmail(r.email()).orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,"Account not found")); }
    public Account requireAdmin(String token){ Account a=accountFromToken(token); if(a.getRole()!=AccountRole.ADMIN) throw new ApiException(HttpStatus.FORBIDDEN,"Admin only"); return a; }
    private Account accountFromToken(String token){
        if(token==null || token.isBlank()) throw new ApiException(HttpStatus.UNAUTHORIZED, "Login required");
        return accounts.findByApiToken(token.replace("Bearer ","")).orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED,"Invalid token"));
    }
    public Account requireVerified(String token){
        Account a = accountFromToken(token);
        if(!a.isVerified()) throw new ApiException(HttpStatus.FORBIDDEN,"Account must be verified by email, SMS or WhatsApp before publishing");
        return a;
    }
    public Account registerAdmin(AuthDtos.RegisterRequest r){ Account a=accounts.findByEmail(r.email()).orElse(new Account()); a.setFullName(r.fullName()); a.setEmail(r.email()); a.setPhone(r.phone()); a.setWhatsappNumber(r.whatsappNumber()); a.setRole(AccountRole.ADMIN); a.setEmailVerified(true); a.setPhoneVerified(true); a.setEmailVerificationCode(code()); a.setPhoneVerificationCode(code()); return accounts.save(a); }
    public AuthDtos.AccountResponse response(Account a){return new AuthDtos.AccountResponse(a.getId(),a.getFullName(),a.getEmail(),a.getPhone(),a.getRole(),a.isVerified(),a.getApiToken(),a.getEmailVerificationCode(),a.getPhoneVerificationCode());}
    private String code(){return String.valueOf(100000 + random.nextInt(900000));}
}
