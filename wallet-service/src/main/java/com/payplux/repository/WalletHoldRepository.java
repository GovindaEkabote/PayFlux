package com.payplux.repository;

import com.payplux.model.WalletHold;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletHoldRepository extends JpaRepository<WalletHold, Long> {

    Optional<WalletHold> findByHoldReference(String holdReference);

    List<WalletHold>  findByStatusAndExpiredAtBefore(String status, LocalDateTime now);

}
