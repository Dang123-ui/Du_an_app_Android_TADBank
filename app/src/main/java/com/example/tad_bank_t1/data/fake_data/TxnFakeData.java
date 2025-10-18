package com.example.tad_bank_t1.data.fake_data;

import com.example.tad_bank_t1.data.model.Transaction;
import com.example.tad_bank_t1.data.model.enums.TnxStatus;
import com.example.tad_bank_t1.data.model.enums.TxnChannel;
import com.example.tad_bank_t1.data.model.enums.TxnType;
import com.example.tad_bank_t1.util.DateTimeUtil;
import com.example.tad_bank_t1.util.TransactionUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class TxnFakeData {
    // public Transaction(Long transactionId, Long accountId, TxnType type, TnxStatus status, TxnChannel channel, Double amount, String description, Date createAt, String counterpartyAccount, String counterpartyName, String counterpartyBankCode, Double feeAmount) {

    private  static List<Transaction> dataTxns = List.of(
            new Transaction(1L, 1L, TxnType.TRANSFER_INTERNAL, TnxStatus.COMPLETED, TxnChannel.ONLINE, 1000000.0, "Chuyen den thuyen", DateTimeUtil.localDateTimeToDateUTC(LocalDateTime.of(2023, 10, 10, 10, 10)), "TK123", "TRAN MINH THUAN", "TADBANK", 0.0),
            new Transaction(2L, 1L, TxnType.TRANSFER_INTERNAL, TnxStatus.COMPLETED, TxnChannel.ONLINE, 1000000.0, "Chuyen den thuyen", DateTimeUtil.localDateTimeToDateUTC(LocalDateTime.of(2023, 10, 10, 10, 10)), "TK123", "TRAN MINH THUAN", "TADBANK", 0.0),
            new Transaction(3L, 1L, TxnType.RECEIVE_TRANSFER, TnxStatus.COMPLETED, TxnChannel.ONLINE, 100000.0, "Chuyen den thuyen", DateTimeUtil.localDateTimeToDateUTC(LocalDateTime.of(2023, 10, 10, 10, 10)), "TK123", "TRAN MINH THUAN", "TADBANK", 0.0),
            new Transaction(4L, 1L, TxnType.RECEIVE_TRANSFER, TnxStatus.COMPLETED, TxnChannel.ONLINE, 500000.0, "Chuyen den thuyen", DateTimeUtil.localDateTimeToDateUTC(LocalDateTime.of(2023, 10, 10, 10, 10)), "TK123", "TRAN MINH THUAN", "TADBANK", 0.0)
    );

    public static List<Transaction> getDataTxns(){
        return dataTxns;
    }

    public static List<Transaction> filter (boolean isIncoming){
        if (isIncoming){
            return dataTxns.stream().filter(TransactionUtil::isIncoming).
                    collect(Collectors.toList());
        }
        return dataTxns.stream().filter(txn -> !TransactionUtil.isIncoming(txn)).
                collect(Collectors.toList());
    }
}
