package com.mexcorgo.config;


import com.mexcorgo.component.LeadEvent;
import com.mexcorgo.component.LeadStatus;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;


@Configuration
@EnableStateMachineFactory
public class LeadStateMachineConfig extends StateMachineConfigurerAdapter<LeadStatus, LeadEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<LeadStatus, LeadEvent> states)
            throws Exception {

        states
                .withStates()
                .initial(LeadStatus.CREATED)
                .states(EnumSet.allOf(LeadStatus.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<LeadStatus, LeadEvent> t)
            throws Exception {


        t.withExternal()
                .source(LeadStatus.CREATED)
                .target(LeadStatus.SENT_TO_PURCHASE)
                .event(LeadEvent.SEND_TO_PURCHASE)

                .and().withExternal()
                .source(LeadStatus.SENT_TO_PURCHASE)
                .target(LeadStatus.SENT_TO_MASTER)
                .event(LeadEvent.SENT_TO_MASTER)

                .and().withExternal()
                .source(LeadStatus.SENT_TO_MASTER)
                .target(LeadStatus.MASTER_RESPONSE_RECEIVED)
                .event(LeadEvent.COLLECT_RESPONSE)

                // using this we can add multiple master response
                .and().withExternal()
                .source(LeadStatus.MASTER_RESPONSE_RECEIVED)
                .target(LeadStatus.MASTER_RESPONSE_RECEIVED) // self transition
                .event(LeadEvent.COLLECT_RESPONSE)

                // even after collect master response we can send quotation to the new
                // master.
                .and().withExternal()
                .source(LeadStatus.MASTER_RESPONSE_RECEIVED)
                .target(LeadStatus.SENT_TO_MASTER)
                .event(LeadEvent.SENT_TO_MASTER)


                .and().withExternal()
                .source(LeadStatus.SENT_TO_MASTER)
                .target(LeadStatus.SENT_TO_PRICING)
                .event(LeadEvent.SEND_TO_PRICING)

                .and().withExternal()
                .source(LeadStatus.SENT_TO_PRICING)
                .target(LeadStatus.SENT_TO_MASTER)
                .event(LeadEvent.SEND_TO_ANOTHER_MASTER)

                .and().withExternal()
                .source(LeadStatus.SENT_TO_PRICING)
                .target(LeadStatus.MASTER_RESPONSE_RECEIVED)
                .event(LeadEvent.COLLECT_RESPONSE)

                // one quotation we can send the pricing still the pricing team not
                // start their worked.
                .and().withExternal()
                .source(LeadStatus.SENT_TO_PRICING)
                .target(LeadStatus.SENT_TO_PRICING)
                .event(LeadEvent.SEND_TO_PRICING)

                .and().withExternal()
                .source(LeadStatus.MASTER_RESPONSE_RECEIVED)
                .target(LeadStatus.SENT_TO_PRICING)
                .event(LeadEvent.SEND_TO_PRICING)


                .and().withExternal()
                .source(LeadStatus.SENT_TO_PRICING)
                .target(LeadStatus.ANALYZED_PRICING)
                .event(LeadEvent.ANALYZED_PRICING)

                .and().withExternal()
                .source(LeadStatus.ANALYZED_PRICING)
                .target(LeadStatus.ANALYZED_PRICING)
                .event(LeadEvent.ANALYZED_PRICING)

                .and().withExternal()
                .source(LeadStatus.ANALYZED_PRICING)
                .target(LeadStatus.SEND_TO_LEAD_CREATOR)
                .event(LeadEvent.SEND_TO_LEAD_CREATOR)

                .and().withExternal()
                .source(LeadStatus.SEND_TO_LEAD_CREATOR)
                .target(LeadStatus.ANALYZED_PRICING)
                .event(LeadEvent.ANALYZED_PRICING)

                .and().withExternal()
                .source(LeadStatus.SEND_TO_LEAD_CREATOR)
                .target(LeadStatus.SEND_TO_LEAD_CREATOR)
                .event(LeadEvent.SEND_TO_LEAD_CREATOR)

                .and().withExternal()
                .source(LeadStatus.SEND_TO_LEAD_CREATOR)
                .target(LeadStatus.QUOTATION_PRICE_FINALIZED)
                .event(LeadEvent.PRICING_DONE)

                .and().withExternal()
                .source(LeadStatus.SEND_TO_LEAD_CREATOR)
                .target(LeadStatus.QUOTATION_PRICE_FINALIZED)
                .event(LeadEvent.PRICING_DONE)

                .and().withExternal()
                .source(LeadStatus.QUOTATION_PRICE_FINALIZED)
                .target(LeadStatus.SENT_TO_CUSTOMER)
                .event(LeadEvent.SEND_TO_CUSTOMER)

                .and().withExternal()
                .source(LeadStatus.SENT_TO_CUSTOMER)
                .target(LeadStatus.SENT_TO_CUSTOMER)
                .event(LeadEvent.SEND_TO_CUSTOMER);
//
//                .and().withExternal()
//                .source(LeadStatus.SENT_TO_LEAD_CREATOR)
//                .target(LeadStatus.PRICING_COMPLETE)
//                .event(LeadEvent.PRICING_DONE)
//
//                 .and().withExternal()
//                .source(LeadStatus.PRICING_COMPLETE)
//                .target(LeadStatus.SENT_TO_CUSTOMER)
//                .event(LeadEvent.SEND_TO_CUSTOMER)
//
//                .and().withExternal()
//                .source(LeadStatus.SENT_TO_CUSTOMER)
//                .target(LeadStatus.LEAD_CONFIRMED)
//                .event(LeadEvent.LEAD_CONFIRMED)
//
//        // OPTIONAL ACTIONS (SELF TRANSITIONS)
//                .and().withExternal()
//                .source(LeadStatus.LEAD_CONFIRMED)
//                .target(LeadStatus.LEAD_CONFIRMED)
//                .event(LeadEvent.SENT_TO_ACCOUNT)
//
//                .and().withExternal()
//                .source(LeadStatus.LEAD_CONFIRMED)
//                .target(LeadStatus.LEAD_CONFIRMED)
//                .event(LeadEvent.SENT_TO_PLANNING);

    }
}
