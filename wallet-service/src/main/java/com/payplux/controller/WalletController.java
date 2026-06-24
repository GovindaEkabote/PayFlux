package com.payplux.controller;

import com.payplux.dto.*;
import com.payplux.exception.NotFoundException;
import com.payplux.exception.InsufficientFundsException;
import com.payplux.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/wallets")
public class WalletController {

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping
    public ResponseEntity<WalletResponse> createWallet(@Validated @RequestBody CreateWalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable Long userId) {
        WalletResponse response = walletService.getWallet(String.valueOf(userId));
        return ResponseEntity.ok(response);
    }


    @PostMapping("/credit")
    public ResponseEntity<WalletResponse> credit(@Validated @RequestBody CreditRequest request) {
        WalletResponse response = walletService.credit(request);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/debit")
    public ResponseEntity<WalletResponse> debit(@Validated @RequestBody DebitRequest request) {
        WalletResponse response = walletService.debit(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Place a hold on wallet amount
     */
    @PostMapping("/holds")
    public ResponseEntity<HoldResponse> placeHold(@Validated @RequestBody HoldRequest request) {
        HoldResponse response = walletService.placeHold(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Capture a hold (complete the transaction)
     */
    @PostMapping("/holds/capture")
    public ResponseEntity<WalletResponse> captureHold(@Validated @RequestBody CaptureRequest request) {
        WalletResponse response = walletService.captureHold(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Release a hold (cancel the hold)
     */
    @DeleteMapping("/holds/{holdReference}")
    public ResponseEntity<HoldResponse> releaseHold(@PathVariable String holdReference) {
        HoldResponse response = walletService.releaseHold(holdReference);
        return ResponseEntity.ok(response);
    }
}