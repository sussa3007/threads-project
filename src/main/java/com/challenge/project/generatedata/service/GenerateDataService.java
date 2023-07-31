package com.challenge.project.generatedata.service;


import com.challenge.project.generatedata.entity.Counting;
import com.challenge.project.generatedata.entity.GenerateData;
import com.challenge.project.generatedata.repository.CountingRepository;
import com.challenge.project.generatedata.repository.GenerateDataRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor

public class GenerateDataService {

    private final GenerateDataRepository generateDataRepository;

    private final CountingRepository countingRepository;

    public GenerateData createGenerateData(String username, String info) {
        return generateDataRepository.save(GenerateData.builder()
                .username(username)
                .userRankingString(info)
                .build());
    }

    public Long getCountingData() {
        Optional<Counting> byId = countingRepository.findById(1L);
        return byId
                .orElseGet(() -> countingRepository.save(Counting.builder().counting(0L).build()))
                .getCounting();
    }

    public void setCountPlusOne() {
        Counting findCounting = countingRepository.findById(1L)
                .orElseGet(() -> countingRepository.save(Counting.builder().counting(0L).build()));
        findCounting.setCounting(findCounting.getCounting() + 2);
        countingRepository.save(findCounting);
    }
}
