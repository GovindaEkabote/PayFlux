package com.payplux.scheduler;

import com.payplux.model.Status;
import com.payplux.model.WalletHold;
import com.payplux.repository.WalletHoldRepository;
import com.payplux.service.WalletService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class HoldExpiryScheduler {

    private final WalletHoldRepository walletHoldRepository;
    private final WalletService walletService;

    public HoldExpiryScheduler(
            WalletHoldRepository walletHoldRepository,
            WalletService walletService) {
        this.walletHoldRepository = walletHoldRepository;
        this.walletService = walletService;
    }

    @Scheduled(fixedRate = 60000) // Runs every 1 minute
    public void expireHolds() {

        LocalDateTime now = LocalDateTime.now();

        List<WalletHold> expiredHolds =
                walletHoldRepository.findByStatusAndExpiredAtBefore(
                        Status.ACTIVE.name(),
                        now
                );

        for (WalletHold hold : expiredHolds) {
            String ref = hold.getHoldReference();

            try {
                walletService.releaseHold(ref);
                System.out.println("Released hold: " + ref);
            } catch (Exception e) {
                System.err.println("Failed to release hold: " + ref);
                e.printStackTrace();
            }
        }
    }
}