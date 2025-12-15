package com.example.tad_bank_t1.util.email;

import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.util.email.templates.DefaultEmailTemplate;
import com.example.tad_bank_t1.util.email.templates.MobileTopupEmailTemplate;
import com.example.tad_bank_t1.util.email.templates.TransferExternalEmailTemplate;
import com.example.tad_bank_t1.util.email.templates.TransferInternalEmailTemplate;

public class TransactionEmailTemplateFactory {
    public static TransactionEmailTemplate getEmailTemplate(TxnType type){
        if (type == null) return new DefaultEmailTemplate();

        switch (type){
            case TRANSFER_INTERNAL:
                return new TransferInternalEmailTemplate();
            case TRANSFER_EXTERNAL:
                return new TransferExternalEmailTemplate();
            case MOBILE_TOPUP:
                return new MobileTopupEmailTemplate();
//            case WITHDRAWAL:
//                return new WithdrawalEmailTemplate();
            default:
                return new DefaultEmailTemplate();
        }
    }
}
