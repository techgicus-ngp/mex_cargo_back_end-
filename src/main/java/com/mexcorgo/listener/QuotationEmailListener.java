package com.mexcorgo.listener;


import com.mexcorgo.event.QuotationAssignedEvent;
import com.mexcorgo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class QuotationEmailListener {

    private final NotificationService notificationService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleQuotationAssigned(QuotationAssignedEvent event) {

        notificationService.sendQuotationAssignmentEmailsToPurchaseExcutive(
                event.getExecutives(),
                event.getQuotation(),
                event.getNeed()
        );
    }
}
