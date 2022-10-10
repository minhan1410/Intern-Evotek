package com.example.fraud;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudCheckService {
    private final FraudCheckHistoryRepository repository;

    public boolean isFraudulentCustomer(Integer customerId){
        repository.save(FraudCheckHistory.builder().customerId(customerId)
                .isFraudster(false).createAt(LocalDateTime.now()).build());
        return false;
    }
}
