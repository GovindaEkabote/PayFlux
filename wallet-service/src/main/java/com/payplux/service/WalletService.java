package com.payplux.service;

import com.payplux.dto.*;

public interface WalletService {


    HoldResponse releaseHold(String ref);

    WalletResponse createWallet(CreateWalletRequest request);

    WalletResponse getWallet(String userId);
    WalletResponse credit(CreditRequest request);

    WalletResponse debit(DebitRequest request);

    HoldResponse placeHold(HoldRequest request);

    WalletResponse captureHold(CaptureRequest request);



}
