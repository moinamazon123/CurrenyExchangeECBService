package com.sample.exchange.repository;

import com.sample.exchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRate, Integer> {
    @Query(nativeQuery = true, value = "SELECT DISTINCT(currency) FROM exchange_rate where currency is not null")
    public List<String>   getList();
    @Query(nativeQuery = true ,value="select * from exchange.exchange_rate where date between CAST(:fromDate as date) and CAST(:toDate as date) and currency=:currency order by date DESC")
    public List<ExchangeRate> getTrend(@Param("currency") String currency,@Param("fromDate") String fromDate ,@Param("toDate") String toDate);
}