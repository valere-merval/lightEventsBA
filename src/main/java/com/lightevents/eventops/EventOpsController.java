package com.lightevents.eventops;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController @RequestMapping("/api")
public class EventOpsController {
    private final EventOpsService service;
    public EventOpsController(EventOpsService service){this.service=service;}

    @GetMapping("/organizer/apps") public List<OrganizerApplication> organizerApps(@RequestParam(required=false) Long organizerAccountId){ return service.organizerApplications(organizerAccountId); }
    @PostMapping("/organizer/apps") public OrganizerApplication createOrganizerApp(@Valid @RequestBody EventOpsDtos.OrganizerApplicationRequest r){ return service.createOrganizerApplication(r); }
    @PostMapping("/organizer/apps/{id}/enabled") public OrganizerApplication setOrganizerAppEnabled(@PathVariable Long id, @RequestParam boolean enabled){ return service.setOrganizerApplicationEnabled(id, enabled); }
    @PostMapping("/organizer/events/{eventId}/box-office-link") public Map<String,Object> createBoxOfficeLink(@PathVariable Long eventId, @RequestBody EventOpsDtos.BoxOfficeLinkRequest r){ return service.createBoxOfficeLink(eventId, r); }

    @GetMapping("/box-office/sales") public List<BoxOfficeSale> boxOfficeSales(@RequestParam(required=false) Long eventId){ return service.boxOfficeSales(eventId); }
    @PostMapping("/box-office/sales") public BoxOfficeSale recordBoxOfficeSale(@Valid @RequestBody EventOpsDtos.BoxOfficeSaleRequest r){ return service.recordBoxOfficeSale(r); }
    @PostMapping("/organizer/events/{eventId}/door-sales") public BoxOfficeSale recordDoorSale(@PathVariable Long eventId, @Valid @RequestBody EventOpsDtos.DoorSaleRequest r){ return service.recordDoorSale(eventId, r); }

    @GetMapping("/events/{eventId}/seat-maps") public List<SeatMap> seatMaps(@PathVariable Long eventId){ return service.seatMaps(eventId); }
    @GetMapping("/seat-maps/{seatMapId}") public EventOpsDtos.SeatMapView seatMap(@PathVariable Long seatMapId){ return service.seatMapView(seatMapId); }
    @PostMapping("/seat-maps") public SeatMap createSeatMap(@Valid @RequestBody EventOpsDtos.SeatMapRequest r){ return service.createSeatMap(r); }
    @PostMapping("/seat-maps/seats") public Seat addSeat(@Valid @RequestBody EventOpsDtos.SeatRequest r){ return service.addSeat(r); }
    @PostMapping("/seats/{seatId}/reserve") public Seat reserveSeat(@PathVariable Long seatId, @RequestBody EventOpsDtos.SeatReservationRequest r){ return service.reserveSeat(seatId, r); }
    @PostMapping("/seats/{seatId}/release") public Seat releaseSeat(@PathVariable Long seatId){ return service.releaseSeat(seatId); }

    @GetMapping("/events/{eventId}/promo-codes") public List<PromoAccessCode> promoCodes(@PathVariable Long eventId){ return service.promoCodes(eventId); }
    @PostMapping("/promo-codes") public PromoAccessCode createPromoCode(@Valid @RequestBody EventOpsDtos.PromoAccessCodeRequest r){ return service.createPromoCode(r); }
    @PostMapping("/promo-codes/redeem") public PromoAccessCode redeemPromoCode(@Valid @RequestBody EventOpsDtos.RedeemCodeRequest r){ return service.redeemPromoCode(r); }

    @GetMapping("/events/{eventId}/waitlist") public List<WaitlistEntry> waitlist(@PathVariable Long eventId){ return service.waitlist(eventId); }
    @PostMapping("/waitlist") public WaitlistEntry joinWaitlist(@Valid @RequestBody EventOpsDtos.WaitlistEntryRequest r){ return service.joinWaitlist(r); }
    @PostMapping("/events/{eventId}/waitlist/notify-next") public WaitlistEntry notifyNextWaitlist(@PathVariable Long eventId){ return service.notifyNextWaitlist(eventId); }

    @GetMapping("/refunds") public List<RefundRequest> refunds(@RequestParam(required=false) Long eventId){ return service.refunds(eventId); }
    @PostMapping("/refunds") public RefundRequest requestRefund(@Valid @RequestBody EventOpsDtos.RefundRequestCreate r){ return service.requestRefund(r); }
    @PostMapping("/refunds/{id}/status") public RefundRequest setRefundStatus(@PathVariable Long id, @Valid @RequestBody EventOpsDtos.RefundStatusRequest r){ return service.setRefundStatus(id, r.status()); }

    @GetMapping("/events/{eventId}/team") public List<TeamMember> team(@PathVariable Long eventId){ return service.team(eventId); }
    @PostMapping("/team-members") public TeamMember addTeamMember(@Valid @RequestBody EventOpsDtos.TeamMemberRequest r){ return service.addTeamMember(r); }

    @GetMapping("/events/{eventId}/attendee-questions") public List<AttendeeQuestion> questions(@PathVariable Long eventId){ return service.questions(eventId); }
    @PostMapping("/attendee-questions") public AttendeeQuestion addQuestion(@Valid @RequestBody EventOpsDtos.AttendeeQuestionRequest r){ return service.addQuestion(r); }
    @PostMapping("/attendee-answers") public AttendeeAnswer answerQuestion(@Valid @RequestBody EventOpsDtos.AttendeeAnswerRequest r){ return service.answerQuestion(r); }
    @GetMapping("/attendees/{attendeeId}/answers") public List<AttendeeAnswer> answersForAttendee(@PathVariable Long attendeeId){ return service.answersForAttendee(attendeeId); }

    @GetMapping("/events/{eventId}/marketing-campaigns") public List<MarketingCampaign> campaigns(@PathVariable Long eventId){ return service.campaigns(eventId); }
    @PostMapping("/marketing-campaigns") public MarketingCampaign createCampaign(@Valid @RequestBody EventOpsDtos.MarketingCampaignRequest r){ return service.createCampaign(r); }
    @PostMapping("/tracking-links") public TrackingLink createTrackingLink(@Valid @RequestBody EventOpsDtos.TrackingLinkRequest r){ return service.createTrackingLink(r); }
    @PostMapping("/tracking-links/{slug}/click") public TrackingLink clickTrackingLink(@PathVariable String slug){ return service.clickTrackingLink(slug); }

    @GetMapping("/developer/webhooks") public List<DeveloperWebhook> webhooks(@RequestParam(required=false) Long eventId){ return service.webhooks(eventId); }
    @PostMapping("/developer/webhooks") public DeveloperWebhook createWebhook(@Valid @RequestBody EventOpsDtos.DeveloperWebhookRequest r){ return service.createWebhook(r); }
    @PostMapping("/developer/webhooks/{webhookId}/test") public WebhookDelivery testWebhook(@PathVariable Long webhookId, @Valid @RequestBody EventOpsDtos.WebhookTestRequest r){ return service.testWebhook(webhookId, r); }
    @GetMapping("/developer/webhooks/{webhookId}/deliveries") public List<WebhookDelivery> webhookDeliveries(@PathVariable Long webhookId){ return service.webhookDeliveries(webhookId); }
}
