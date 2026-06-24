package com.lightevents.eventops;

import com.lightevents.events.*;
import com.lightevents.shared.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventOpsService {
    private final EventRepository events; private final TicketTypeRepository tickets; private final AttendeeRepository attendees;
    private final OrganizerApplicationRepository apps; private final BoxOfficeSaleRepository boxOffice; private final SeatMapRepository seatMaps; private final SeatRepository seats; private final PromoAccessCodeRepository codes; private final WaitlistEntryRepository waitlist; private final RefundRequestRepository refunds; private final TeamMemberRepository team; private final AttendeeQuestionRepository questions; private final AttendeeAnswerRepository answers; private final MarketingCampaignRepository campaigns; private final TrackingLinkRepository links; private final DeveloperWebhookRepository webhooks; private final WebhookDeliveryRepository deliveries;
    public EventOpsService(EventRepository events, TicketTypeRepository tickets, AttendeeRepository attendees, OrganizerApplicationRepository apps, BoxOfficeSaleRepository boxOffice, SeatMapRepository seatMaps, SeatRepository seats, PromoAccessCodeRepository codes, WaitlistEntryRepository waitlist, RefundRequestRepository refunds, TeamMemberRepository team, AttendeeQuestionRepository questions, AttendeeAnswerRepository answers, MarketingCampaignRepository campaigns, TrackingLinkRepository links, DeveloperWebhookRepository webhooks, WebhookDeliveryRepository deliveries){this.events=events;this.tickets=tickets;this.attendees=attendees;this.apps=apps;this.boxOffice=boxOffice;this.seatMaps=seatMaps;this.seats=seats;this.codes=codes;this.waitlist=waitlist;this.refunds=refunds;this.team=team;this.questions=questions;this.answers=answers;this.campaigns=campaigns;this.links=links;this.webhooks=webhooks;this.deliveries=deliveries;}

    public List<OrganizerApplication> organizerApplications(Long organizerAccountId){ return organizerAccountId==null?apps.findAll():apps.findByOrganizerAccountId(organizerAccountId); }
    public OrganizerApplication createOrganizerApplication(EventOpsDtos.OrganizerApplicationRequest r){ OrganizerApplication a=new OrganizerApplication(); a.setOrganizerAccountId(r.organizerAccountId()); a.setBusinessName(r.businessName()); a.setContactEmail(r.contactEmail()); a.setWebsiteUrl(r.websiteUrl()); a.setDescription(r.description()); return apps.save(a); }
    @Transactional public OrganizerApplication setOrganizerApplicationEnabled(Long id, boolean enabled){ OrganizerApplication a=apps.findById(id).orElseThrow(()->notFound("Organizer application not found")); a.setEnabled(enabled); return apps.save(a); }
    public Map<String,Object> createBoxOfficeLink(Long eventId, EventOpsDtos.BoxOfficeLinkRequest r){ Event event=requireEvent(eventId); OrganizerApplication a=new OrganizerApplication(); a.setBusinessName(blank(r.deviceName())?"Box-office " + event.getTitle():r.deviceName()); a.setContactEmail(event.getOrganizerEmail()); a.setWebsiteUrl("/organizer?eventId="+eventId); a.setDescription("Lien mobile box-office/check-in pour " + event.getTitle() + ". Expire dans " + (r.expiresInHours()==null?24:r.expiresInHours()) + "h."); a=apps.save(a); return Map.of("eventId", eventId, "deviceName", a.getBusinessName(), "apiKey", a.getApiKey(), "mobilePath", "/organizer?eventId="+eventId+"&boxOfficeKey="+a.getApiKey(), "expiresInHours", r.expiresInHours()==null?24:r.expiresInHours()); }

    public List<BoxOfficeSale> boxOfficeSales(Long eventId){ return eventId==null?boxOffice.findAll():boxOffice.findByEventId(eventId); }
    @Transactional public BoxOfficeSale recordBoxOfficeSale(EventOpsDtos.BoxOfficeSaleRequest r){ Event event=requireEvent(r.eventId()); TicketType ticket=tickets.findById(r.ticketTypeId()).orElseThrow(()->notFound("Ticket type not found")); if(ticket.getEvent()!=null&&!ticket.getEvent().getId().equals(event.getId())) throw new ApiException(HttpStatus.BAD_REQUEST,"Ticket does not belong to event"); int qty=Math.max(1,r.quantity()); if(ticket.getSold()+qty>ticket.getQuantity()) throw new ApiException(HttpStatus.CONFLICT,"Not enough tickets available"); BigDecimal unit=r.unitPrice()!=null?r.unitPrice():safe(ticket.getPrice()); BoxOfficeSale sale=new BoxOfficeSale(); sale.setEventId(event.getId()); sale.setTicketTypeId(ticket.getId()); sale.setCashierAccountId(r.cashierAccountId()); sale.setBuyerName(r.buyerName()); sale.setBuyerEmail(r.buyerEmail()); sale.setBuyerPhone(r.buyerPhone()); sale.setQuantity(qty); sale.setUnitPrice(unit); sale.setTotalAmount(unit.multiply(BigDecimal.valueOf(qty))); sale.setCurrency(blank(r.currency())?ticket.getCurrency():r.currency()); sale.setPaymentMethod(blank(r.paymentMethod())?"CASH":r.paymentMethod()); ticket.setSold(ticket.getSold()+qty); tickets.save(ticket); return boxOffice.save(sale); }
    @Transactional public BoxOfficeSale recordDoorSale(Long eventId, EventOpsDtos.DoorSaleRequest r){ return recordBoxOfficeSale(new EventOpsDtos.BoxOfficeSaleRequest(eventId, r.ticketTypeId(), null, r.buyerName(), r.buyerEmail(), r.buyerPhone(), r.quantity()==null?1:r.quantity(), null, null, r.paymentMethod())); }

    public List<SeatMap> seatMaps(Long eventId){ return seatMaps.findByEventId(eventId); }
    public EventOpsDtos.SeatMapView seatMapView(Long seatMapId){ SeatMap m=seatMaps.findById(seatMapId).orElseThrow(()->notFound("Seat map not found")); return new EventOpsDtos.SeatMapView(m,seats.findBySeatMapId(seatMapId)); }
    public SeatMap createSeatMap(EventOpsDtos.SeatMapRequest r){ requireEvent(r.eventId()); SeatMap m=new SeatMap(); m.setEventId(r.eventId()); m.setName(r.name()); m.setVenueName(r.venueName()); m.setLayoutJson(r.layoutJson()); if(r.active()!=null)m.setActive(r.active()); return seatMaps.save(m); }
    public Seat addSeat(EventOpsDtos.SeatRequest r){ requireEvent(r.eventId()); seatMaps.findById(r.seatMapId()).orElseThrow(()->notFound("Seat map not found")); Seat s=new Seat(); s.setEventId(r.eventId()); s.setSeatMapId(r.seatMapId()); s.setSectionName(r.sectionName()); s.setRowLabel(r.rowLabel()); s.setSeatNumber(r.seatNumber()); s.setSeatLabel(r.seatLabel()); return seats.save(s); }
    @Transactional public Seat reserveSeat(Long seatId, EventOpsDtos.SeatReservationRequest r){ Seat s=seats.findById(seatId).orElseThrow(()->notFound("Seat not found")); if(!"AVAILABLE".equalsIgnoreCase(s.getStatus())) throw new ApiException(HttpStatus.CONFLICT,"Seat is not available"); s.setStatus("RESERVED"); s.setAttendeeId(r.attendeeId()); s.setReservationReference(r.reservationReference()); return seats.save(s); }
    @Transactional public Seat releaseSeat(Long seatId){ Seat s=seats.findById(seatId).orElseThrow(()->notFound("Seat not found")); s.setStatus("AVAILABLE"); s.setAttendeeId(null); s.setReservationReference(null); return seats.save(s); }

    public List<PromoAccessCode> promoCodes(Long eventId){ return codes.findByEventId(eventId); }
    public PromoAccessCode createPromoCode(EventOpsDtos.PromoAccessCodeRequest r){ requireEvent(r.eventId()); PromoAccessCode c=new PromoAccessCode(); c.setEventId(r.eventId()); c.setCode(r.code()); c.setType(blank(r.type())?"PROMO":r.type()); c.setDiscountAmount(r.discountAmount()==null?BigDecimal.ZERO:r.discountAmount()); c.setDiscountPercent(r.discountPercent()); c.setMaxRedemptions(r.maxRedemptions()); if(r.active()!=null)c.setActive(r.active()); c.setStartsAt(r.startsAt()); c.setEndsAt(r.endsAt()); return codes.save(c); }
    @Transactional public PromoAccessCode redeemPromoCode(EventOpsDtos.RedeemCodeRequest r){ PromoAccessCode c=codes.findByEventIdAndCodeIgnoreCase(r.eventId(),r.code()).orElseThrow(()->notFound("Promo/access code not found")); if(!c.isActive()) throw new ApiException(HttpStatus.BAD_REQUEST,"Code is inactive"); LocalDateTime now=LocalDateTime.now(); if(c.getStartsAt()!=null&&c.getStartsAt().isAfter(now)) throw new ApiException(HttpStatus.BAD_REQUEST,"Code is not active yet"); if(c.getEndsAt()!=null&&c.getEndsAt().isBefore(now)) throw new ApiException(HttpStatus.BAD_REQUEST,"Code has expired"); if(c.getMaxRedemptions()!=null&&c.getRedeemedCount()>=c.getMaxRedemptions()) throw new ApiException(HttpStatus.CONFLICT,"Code redemption limit reached"); c.setRedeemedCount(c.getRedeemedCount()+1); return codes.save(c); }

    public List<WaitlistEntry> waitlist(Long eventId){ return waitlist.findByEventId(eventId); }
    public WaitlistEntry joinWaitlist(EventOpsDtos.WaitlistEntryRequest r){ requireEvent(r.eventId()); WaitlistEntry w=new WaitlistEntry(); w.setEventId(r.eventId()); w.setTicketTypeId(r.ticketTypeId()); w.setFullName(r.fullName()); w.setEmail(r.email()); w.setPhone(r.phone()); w.setQuantity(r.quantity()==null?1:r.quantity()); return waitlist.save(w); }
    @Transactional public WaitlistEntry notifyNextWaitlist(Long eventId){ WaitlistEntry w=waitlist.findByEventIdAndStatusOrderByCreatedAtAsc(eventId,"WAITING").stream().findFirst().orElseThrow(()->notFound("No waiting entry")); w.setStatus("NOTIFIED"); w.setNotifiedAt(Instant.now()); return waitlist.save(w); }

    public List<RefundRequest> refunds(Long eventId){ return eventId==null?refunds.findAll():refunds.findByEventId(eventId); }
    public RefundRequest requestRefund(EventOpsDtos.RefundRequestCreate r){ RefundRequest x=new RefundRequest(); x.setReservationId(r.reservationId()); x.setEventId(r.eventId()); x.setBuyerEmail(r.buyerEmail()); x.setAmount(r.amount()==null?BigDecimal.ZERO:r.amount()); x.setReason(r.reason()); return refunds.save(x); }
    @Transactional public RefundRequest setRefundStatus(Long id, String status){ RefundRequest x=refunds.findById(id).orElseThrow(()->notFound("Refund request not found")); x.setStatus(status); if("APPROVED".equalsIgnoreCase(status)||"REJECTED".equalsIgnoreCase(status)||"CANCELLED".equalsIgnoreCase(status)) x.setResolvedAt(Instant.now()); return refunds.save(x); }

    public List<TeamMember> team(Long eventId){ return team.findByEventId(eventId); }
    public TeamMember addTeamMember(EventOpsDtos.TeamMemberRequest r){ requireEvent(r.eventId()); TeamMember m=new TeamMember(); m.setEventId(r.eventId()); m.setAccountId(r.accountId()); m.setEmail(r.email()); m.setFullName(r.fullName()); m.setRole(blank(r.role())?"STAFF":r.role()); m.setPermissions(join(r.permissions())); if(r.active()!=null)m.setActive(r.active()); return team.save(m); }

    public List<AttendeeQuestion> questions(Long eventId){ return questions.findByEventIdOrderBySortOrderAsc(eventId); }
    public AttendeeQuestion addQuestion(EventOpsDtos.AttendeeQuestionRequest r){ requireEvent(r.eventId()); AttendeeQuestion q=new AttendeeQuestion(); q.setEventId(r.eventId()); q.setLabel(r.label()); q.setType(blank(r.type())?"TEXT":r.type()); q.setRequired(Boolean.TRUE.equals(r.required())); q.setOptionsText(join(r.options())); q.setSortOrder(r.sortOrder()==null?0:r.sortOrder()); return questions.save(q); }
    public AttendeeAnswer answerQuestion(EventOpsDtos.AttendeeAnswerRequest r){ requireEvent(r.eventId()); attendees.findById(r.attendeeId()).orElseThrow(()->notFound("Attendee not found")); questions.findById(r.questionId()).orElseThrow(()->notFound("Question not found")); AttendeeAnswer a=new AttendeeAnswer(); a.setEventId(r.eventId()); a.setAttendeeId(r.attendeeId()); a.setQuestionId(r.questionId()); a.setAnswerText(r.answerText()); return answers.save(a); }
    public List<AttendeeAnswer> answersForAttendee(Long attendeeId){ return answers.findByAttendeeId(attendeeId); }

    public List<MarketingCampaign> campaigns(Long eventId){ return campaigns.findByEventId(eventId); }
    public MarketingCampaign createCampaign(EventOpsDtos.MarketingCampaignRequest r){ requireEvent(r.eventId()); MarketingCampaign c=new MarketingCampaign(); c.setEventId(r.eventId()); c.setName(r.name()); c.setChannel(blank(r.channel())?"EMAIL":r.channel()); c.setBudget(r.budget()==null?BigDecimal.ZERO:r.budget()); c.setStartsAt(r.startsAt()); c.setEndsAt(r.endsAt()); c.setStatus(blank(r.status())?"DRAFT":r.status()); return campaigns.save(c); }
    public TrackingLink createTrackingLink(EventOpsDtos.TrackingLinkRequest r){ requireEvent(r.eventId()); TrackingLink l=new TrackingLink(); l.setEventId(r.eventId()); l.setCampaignId(r.campaignId()); l.setSlug(r.slug()); l.setUrl(r.url()); l.setUtmSource(r.utmSource()); l.setUtmMedium(r.utmMedium()); l.setUtmCampaign(r.utmCampaign()); return links.save(l); }
    @Transactional public TrackingLink clickTrackingLink(String slug){ TrackingLink l=links.findBySlug(slug).orElseThrow(()->notFound("Tracking link not found")); l.setClicks(l.getClicks()+1); return links.save(l); }

    public List<DeveloperWebhook> webhooks(Long eventId){ return eventId==null?webhooks.findAll():webhooks.findByEventId(eventId); }
    public DeveloperWebhook createWebhook(EventOpsDtos.DeveloperWebhookRequest r){ if(r.eventId()!=null) requireEvent(r.eventId()); DeveloperWebhook w=new DeveloperWebhook(); w.setEventId(r.eventId()); w.setTargetUrl(r.targetUrl()); w.setEventsCsv(join(r.events())); if(r.active()!=null)w.setActive(r.active()); return webhooks.save(w); }
    public WebhookDelivery testWebhook(Long webhookId, EventOpsDtos.WebhookTestRequest r){ DeveloperWebhook w=webhooks.findById(webhookId).orElseThrow(()->notFound("Webhook not found")); WebhookDelivery d=new WebhookDelivery(); d.setWebhookId(w.getId()); d.setEventType(r.eventType()); d.setPayload(blank(r.payload())?"{}":r.payload()); d.setStatusCode(202); d.setSuccess(true); return deliveries.save(d); }
    public List<WebhookDelivery> webhookDeliveries(Long webhookId){ return deliveries.findByWebhookId(webhookId); }

    private Event requireEvent(Long eventId){ return events.findById(eventId).orElseThrow(()->notFound("Event not found")); }
    private static ApiException notFound(String message){ return new ApiException(HttpStatus.NOT_FOUND,message); }
    private static boolean blank(String v){ return v==null||v.isBlank(); }
    private static BigDecimal safe(BigDecimal v){ return v==null?BigDecimal.ZERO:v; }
    private static String join(List<String> values){ return values==null?null:values.stream().filter(v->v!=null&&!v.isBlank()).map(v->v.trim().toLowerCase(Locale.ROOT)).collect(Collectors.joining(",")); }
}
