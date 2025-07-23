package com.example.demo.controller;

import com.example.demo.model.UserInputException;
import com.example.demo.service.SymbolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.CandlestickService;

@RestController
public class CandlestickController {
    @Autowired
    private SymbolService symbolService;

    @Autowired
    private CandlestickService candlestickService;

    @GetMapping("/abc")
    public void load(@RequestParam(name = "symbol") String symbol,
                     @RequestParam(name = "st") Long startTime,
                     @RequestParam(name = "et") Long endTime){
        if (symbol == null || symbol.trim().isEmpty()) {
            throw new UserInputException("Symbol must not be null or empty.");
        }

        if (!symbolService.isValid(symbol)) {
            throw new UserInputException("Symbol is not a valid crypto symbol.");
        }

        if (startTime == null || endTime == null) {
            throw new UserInputException("Start time and end time must not be null.");
        }

        if (startTime >= endTime) {
            throw new UserInputException("Start time must be less than end time.");
        }

        candlestickService.load(symbol, startTime, endTime);
        return;
    }

}
