package com.example.demo.repo;

import com.example.demo.model.Candlestick;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CandlestickRepo {

    @Select("SELECT * FROM candlestick")
    List<Candlestick> findAll();

//    @Select("SELECT * FROM candlestick WHERE symbol = #{symbol} AND open_time = #{openTime}")
//    Candlestick findById(@Param("symbol") String symbol, @Param("openTime") long openTime);

    @Select("SELECT * FROM candlestick WHERE symbol = #{symbol} AND open_time BETWEEN #{startTime} AND #{endTime} ORDER BY open_time ASC")
    List<Candlestick> findInRange(@Param("symbol") String symbol,
                                  @Param("startTime") long startTime,
                                  @Param("endTime") long endTime);

    @Delete("DELETE FROM candlestick WHERE symbol = #{symbol} AND open_time = #{openTime}")
    int deleteById(@Param("symbol") String symbol, @Param("openTime") long openTime);
//
//    @Insert("""
//        INSERT INTO candlestick (
//            symbol, open_time, open_price, high_price, low_price, close_price,
//            volume, close_time, quote_asset_volume, number_of_trades,
//            taker_buy_base_volume, taker_buy_quote_volume, ignore_field
//        ) VALUES (
//
//        )
//    """)
//    int insert(Candlestick candlestick);

    @Insert({
            "<script>",
            "insert into candlestick (symbol, open_time, open_price, high_price, low_price, close_price,\n" +
                    "volume, close_time, quote_asset_volume, number_of_trades,\n" +
                    "taker_buy_base_volume, taker_buy_quote_volume, ignore_field)",
            "values ",
            "<foreach  collection='candlestickList' item='candlestick' separator=','>",
            "(#{candlestick.symbol}, #{candlestick.openTime}, #{candlestick.openPrice}, #{candlestick.highPrice}, #{candlestick.lowPrice}, #{candlestick.closePrice},\n" +
                    " #{candlestick.volume}, #{candlestick.closeTime}, #{candlestick.quoteAssetVolume}, #{candlestick.numberOfTrades},\n" +
                    " #{candlestick.takerBuyBaseVolume}, #{candlestick.takerBuyQuoteVolume}, #{candlestick.ignoreField})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("candlestickList") List<Candlestick> candlestickList);

    @Update("""
        UPDATE candlestick SET
            open_price = #{openPrice},
            high_price = #{highPrice},
            low_price = #{lowPrice},
            close_price = #{closePrice},
            volume = #{volume},
            close_time = #{closeTime},
            quote_asset_volume = #{quoteAssetVolume},
            number_of_trades = #{numberOfTrades},
            taker_buy_base_volume = #{takerBuyBaseVolume},
            taker_buy_quote_volume = #{takerBuyQuoteVolume},
            ignore_field = #{ignoreField}
        WHERE symbol = #{symbol} AND open_time = #{openTime}
    """)
    int update(Candlestick candlestick);

}