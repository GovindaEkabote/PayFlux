package com.payplux.reward_service.service;

import com.payplux.reward_service.model.Reward;

import java.util.List;

public interface RewardService {

    public Reward save(Reward reward);

    public List<Reward> getRewardsByUserId(Long userId);
}
