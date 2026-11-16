package com.lumi.service.payment;

import com.lumi.domain.ParentStudentLink;
import com.lumi.domain.User;
import com.lumi.service.dto.ParentPurchaseAccessDTO;
import java.util.List;

public interface ParentPurchaseAccessService {

    ParentPurchaseAccessDTO validateForCheckout(Long studentId);

    ParentPurchaseAccessDTO validateAndLockForOrder(Long studentId);

    List<ParentStudentLink> getPurchasableChildren();

    User getCurrentActiveParent();
}
