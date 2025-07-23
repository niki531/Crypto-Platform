package com.example.demo.service;

import com.example.demo.model.Candlestick;
import com.example.demo.repo.CandlestickRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.LongStream;

@Component
public class CandlestickService {
    @Value("${intervalMs}")
    private Long intervalMs;

    @Value("${limit}")
    private Long limit;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private BinanceAPI binanceApi;

    @Autowired
    private CandlestickRepo candlestickRepo;

    public void load(String symbol, Long startTime, Long endTime) {

        Long batchSizeMs = intervalMs * limit;

        LongStream.iterate(startTime, t -> t + batchSizeMs)
                .takeWhile(t -> t < endTime)
                .parallel()
                .forEach( batchStart->{
                    long batchEnd = Math.min(batchStart + batchSizeMs, endTime);
                    //binance api load
                    List<Candlestick> candlestickList =  binanceApi.fetchCandlesticks(symbol, batchStart, batchEnd);
                    if (candlestickList.isEmpty()) {
                        System.out.println("It's empty");
                    }
                    candlestickRepo.insertBatch(candlestickList);
                        }
                );
    }
}
