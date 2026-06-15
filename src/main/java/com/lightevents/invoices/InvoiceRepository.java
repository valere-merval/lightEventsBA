package com.lightevents.invoices;
import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface InvoiceRepository extends JpaRepository<Invoice,Long>{ Optional<Invoice> findByReservationReference(String reservationReference); List<Invoice> findByBuyerEmailIgnoreCaseOrderByCreatedAtDesc(String email); List<Invoice> findByBuyerPhoneOrderByCreatedAtDesc(String phone); List<Invoice> findByBuyerWhatsappOrderByCreatedAtDesc(String whatsapp); }
