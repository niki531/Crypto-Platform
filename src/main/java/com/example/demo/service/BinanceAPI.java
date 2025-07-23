package com.example.demo.service;

import com.example.demo.model.Candlestick;
import com.example.demo.repo.CandlestickRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BinanceAPI {

    @Value("${url_template}")
    private String urlTemplate;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private CandlestickRepo candlestickRepo;

    public Candlestick fetchOneAggregatedCandlestick(String symbol, long startTime, long endTime) {
        List<Candlestick> rawList = candlestickRepo.findInRange(symbol, startTime, endTime);
        if (rawList.size() == 0) {
            return null; //throw exception？
        }
        return aggregateGroup(rawList);
    }


    private Candlestick aggregateGroup(List<Candlestick> group) {
        Candlestick first = group.get(0);
        Candlestick last = group.get(group.size() - 1);

        double high = Double.NEGATIVE_INFINITY;
        double low = Double.POSITIVE_INFINITY;
        double volume = 0;
        double quoteAssetVolume = 0;
        int numberOfTrades = 0;
        double takerBuyBaseVolume = 0;
        double takerBuyQuoteVolume = 0;

        //会出现race condition
        for (Candlestick c : group) {
            high = Math.max(high, c.getHighPrice());
            low = Math.min(low, c.getLowPrice());
            volume += c.getVolume();
            quoteAssetVolume += c.getQuoteAssetVolume();
            numberOfTrades += c.getNumberOfTrades();
            takerBuyBaseVolume += c.getTakerBuyBaseVolume();
            takerBuyQuoteVolume += c.getTakerBuyQuoteVolume();
        }

        if (group.isEmpty()) {
            high = 0;
            low = 0;
        }

        return new Candlestick(
                first.getSymbol(),
                first.getOpenTime(),
                first.getOpenPrice(),
                high,
                low,
                last.getClosePrice(),
                volume,
                last.getCloseTime(),
                quoteAssetVolume,
                numberOfTrades,
                takerBuyBaseVolume,
                takerBuyQuoteVolume,
                "aggregated"
        );
    }

    public List<Candlestick> fetchCandlesticks(String symbol, Long startTime, Long endTime) {
        String url = String.format(urlTemplate, symbol, startTime, endTime);
        ResponseEntity<String[][]> response = restTemplate.getForEntity(url, String[][].class);
        String[][] rawData = response.getBody();
        if (rawData == null || rawData.length == 0) {
            System.out.println("No data coming in");//throw excpetion
        }
        List<Candlestick> candlestickList = Arrays.stream(rawData)
                .parallel()
                .map(candle -> {
                    Candlestick c = new Candlestick();
                    c.setSymbol(symbol);
                    c.setOpenTime(Long.parseLong(candle[0]));
                    c.setOpenPrice(Double.parseDouble(candle[1]));
                    c.setHighPrice(Double.parseDouble(candle[2]));
                    c.setLowPrice(Double.parseDouble(candle[3]));
                    c.setClosePrice(Double.parseDouble(candle[4]));
                    c.setVolume(Double.parseDouble(candle[5]));
                    c.setCloseTime(Long.parseLong(candle[6]));
                    c.setQuoteAssetVolume(Double.parseDouble(candle[7]));
                    c.setNumberOfTrades(Integer.parseInt(candle[8]));
                    c.setTakerBuyBaseVolume(Double.parseDouble(candle[9]));
                    c.setTakerBuyQuoteVolume(Double.parseDouble(candle[10]));
                    c.setIgnoreField(candle[11]);
                    return c;
                })
                .collect(Collectors.toList());
        return candlestickList;
    }
}
