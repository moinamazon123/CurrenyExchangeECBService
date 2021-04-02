package com.sample.exchange.repository;

import com.sample.exchange.model.CurrencyHits;
import com.sample.exchange.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyHitsRepository extends JpaRepository<CurrencyHits, Integer> {

    @Query(nativeQuery = true, value = "SELECT COALESCE(SUM(hits),0) FROM exchange.currency_hits where source_currency =:sourceCurr")
    int getCurrencyHits(@Param("sourceCurr") String sourceCurr);


}