package com.mexcorgo.component;


// this shows the position of lead
public enum LeadStatus {
    CREATED(1),
    SENT_TO_PURCHASE(2),
    SENT_TO_MASTER(3),
    MASTER_RESPONSE_RECEIVED(4),
//    QUOTATION_RECEIVED,
    SENT_TO_PRICING(5),
    ANALYZED_PRICING(6),
    SEND_TO_LEAD_CREATOR(7),
//    PENDING_PRICING,
   QUOTATION_PRICE_FINALIZED(8),
//    SENT_TO_LEAD_CREATOR(7),
//    PRICING_COMPLETE(8),
    SENT_TO_CUSTOMER(9);
//    // FOLLOW_UP,
//    LEAD_CONFIRMED(10),
//     SENT_TO_ACCOUNT(11),
//    SENT_TO_PLANNING(12),
//    PAYMENT_PENDING(13),
//    SERVICE_PLANNING(14),
//    PROJECT_CREATED(15),
//    CLOSED(16);

    private final int order;

    LeadStatus(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
