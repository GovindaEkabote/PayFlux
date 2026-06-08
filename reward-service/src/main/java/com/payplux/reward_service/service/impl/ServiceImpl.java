package com.payplux.reward_service.service.impl;


import com.payplux.reward_service.model.Reward;
import com.payplux.reward_service.repository.RewardRepository;
import com.payplux.reward_service.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServiceImpl implements RewardService {

    @Autowired
    private RewardRepository rewardRepository;

    @Override
    public Reward save(Reward reward) {
        reward.setSendAt(LocalDateTime.now());
        return rewardRepository.save(reward);
    }

    @Override
    public List<Reward> getRewardsByUserId(Long userId) {
        return rewardRepository.findByUserId(userId);
    }
}
