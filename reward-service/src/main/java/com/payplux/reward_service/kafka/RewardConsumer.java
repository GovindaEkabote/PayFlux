package com.payplux.reward_service.kafka;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payplux.reward_service.model.Reward;
import com.payplux.reward_service.model.Transaction;
import com.payplux.reward_service.repository.RewardRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class RewardConsumer {

    private final RewardRepository rewardRepository;
    private final ObjectMapper objectMapper;

    public RewardConsumer(RewardRepository rewardRepository, ObjectMapper objectMapper) {
        this.rewardRepository = rewardRepository;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "txn-initiated", groupId = "reward-group")
    public  void  consumertransaction(Transaction transaction) {
        try {
            if (rewardRepository.existsByTransactionId(transaction.getId())){
                System.out.println("Transaction already processed");
                return;
            }
            Reward reward = new Reward();
            reward.setUserId(transaction.getSenderId());
            reward.setPoints(transaction.getAmount().multiply(new BigDecimal(100)).doubleValue());
            reward.setSendAt(LocalDateTime.now());
            reward.setTransactionId(transaction.getId());
            rewardRepository.save(reward);
            System.out.println("Reward Saved: "+ reward);
        }catch (Exception e){
            System.out.println("Faild to process"+ transaction.getId() + ": "+ e.getMessage());
            throw new RuntimeException(e);
        }
    }



}
