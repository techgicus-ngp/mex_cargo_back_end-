package com.mexcorgo.event;


import com.mexcorgo.datamodel.Quatation;
import com.mexcorgo.datamodel.User;
import com.mexcorgo.dto.response.NeedEmailDetails;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@Getter @AllArgsConstructor
public class QuotationAssignedEvent {
    private final Set<User> executives;
    private final Quatation quotation;
    private final NeedEmailDetails need;
}
