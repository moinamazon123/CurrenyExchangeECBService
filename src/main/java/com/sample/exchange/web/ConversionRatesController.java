package com.sample.exchange.web;

import com.fasterxml.jackson.core.type.TypeReference;
        import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.exchange.model.CurrencyHits;
import com.sample.exchange.model.ExchangeRate;
import com.sample.exchange.repository.CurrencyHitsRepository;
import com.sample.exchange.repository.ExchangeRateRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


        import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/rates")
@Slf4j
@AllArgsConstructor(onConstructor = @__(@Autowired))
@CrossOrigin(origins = "*", maxAge = 3600)
public class ConversionRatesController {

    //private final ConversionRatesService conversionRatesService;

    @Autowired
    ExchangeRateRepository exchangeRateRepository;
    @Autowired
    CurrencyHitsRepository currencyHitsRepository;

    @GetMapping("/initData")
    public String getConversionRate() throws IOException, ParseException {
        Object obj = new JSONParser().parse(new FileReader("/Users/Downloads/eurofxref-hist.json"));

        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;


        HashMap<String, Object> result = new ObjectMapper()
                .readValue(((JSONObject) obj).toJSONString(), new TypeReference<Map<String, Object>>() {
        });

        System.out.println(result.entrySet());
        ExchangeRate exchangeRate = null;//new ExchangeRate();
        List<ExchangeRate> exchangeRateList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : result.entrySet()) {


            List<String> baseRateList = Arrays.asList(entry.getValue().toString().split(","));
                for(String baseRate: baseRateList) {
                    exchangeRate = new ExchangeRate();
                    exchangeRate.setDate(entry.getKey());
                    if(baseRate.split("=").length>=2) {
                        exchangeRate.setCurrency(baseRate.split("=")[0].replace("{","").trim());
                        System.out.println("Currency , Rate " + baseRate.split("=")[0]+" ="+ baseRate.split("=")[1]);

                         exchangeRate.setRate(new BigDecimal(baseRate.split("=")[1].replace("{","")!=null
                                 && !baseRate.split("=")[1].replace("}","").equals("N/A")?baseRate.split("=")[1].replace("}",""):"0"));
                    }
                    exchangeRateList.add(exchangeRate);

                }

        }
            exchangeRateRepository.save(exchangeRateList);
        return "Success";
    }

    @GetMapping("/getList")
    public List<String> getList() {

        List<String> currStringList = exchangeRateRepository.getList();
            return currStringList;
    }


        @GetMapping("/convertCurrency/from/{from}/to/{to}")
    public BigDecimal convertCurrency(@PathVariable("from") String from , @PathVariable("to") String to) {
            BigDecimal result = new BigDecimal(0.0);

            if(from.equals("EUR")){
                List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream()
                        .filter(x -> x.getDate().equals("2021-03-31")
                                && (x.getCurrency() != null && x.getCurrency().equals(to))).collect(Collectors.toList());
                if (srcExchangeRates.size() > 0 ) {
                    result = srcExchangeRates.get(0).getRate();
                }
            }else if (to.equals("EUR")){

                List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream()
                        .filter(x -> x.getDate().equals("2021-03-31")
                                && (x.getCurrency() != null && x.getCurrency().equals(from))).collect(Collectors.toList());
                if (srcExchangeRates.size() > 0 ) {
                    result = new BigDecimal(1).divide(srcExchangeRates.get(0).getRate(), 3, RoundingMode.HALF_UP);
                }

            } else {


                List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream().filter(x -> x.getDate().equals("2021-03-31") && (x.getCurrency() != null && x.getCurrency().equals(from))).collect(Collectors.toList());
                // 1 EURO = srcExchangeRates 1 euro =1.18 usd

                List<ExchangeRate> trgtExchangeRates = exchangeRateRepository.findAll().stream().filter(x -> x.getDate().equals("2021-03-31") && (x.getCurrency() != null && x.getCurrency().equals(to))).collect(Collectors.toList());
                // 1 EURO = trgtExchangeRates 1 euro = 86.28 INR => 1 usd = 1/1.18 * 86.28

                if (trgtExchangeRates.size() > 0 && srcExchangeRates.size() > 0) {
                    result = trgtExchangeRates.get(0).getRate().divide(srcExchangeRates.get(0).getRate(), 3, RoundingMode.HALF_UP);
                } else {
                    return result;
                }
            }
            List<CurrencyHits> currencyHits = currencyHitsRepository.findAll();
            if (currencyHits.size() > 0) {
                currencyHits = currencyHits.stream().filter(x -> x.getSourceCurrency().equals(from)).collect(Collectors.toList());
            }
            int hits = currencyHits.size() > 0 ? currencyHits.get(0).getHits() : 1;
            if (hits > 1) {
                currencyHitsRepository.delete(currencyHits.get(0).getId());
            }


            CurrencyHits currencyHit = new CurrencyHits();
            currencyHit.setSourceCurrency(from);
            currencyHit.setTargetCurrency(to);
            currencyHit.setHits(hits);
            currencyHitsRepository.save(currencyHit);






return result;
    }

    @GetMapping("getConversionHits/{sourceCurr}")

    public int getConversionHits(@PathVariable("sourceCurr") String sourceCurr){

        int hits = currencyHitsRepository.getCurrencyHits(sourceCurr)!=0? currencyHitsRepository.getCurrencyHits(sourceCurr):0;
        return hits;
    }

    @GetMapping("/convertCurrencyUnits/from/{from}/{unit}/to/{to}")
    public BigDecimal convertCurrency(@PathVariable("from") String from ,@PathVariable("unit") int unit , @PathVariable("to") String to) {
        BigDecimal result =new BigDecimal(0.0);

        if(from.equals("EUR")){
            List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream()
                    .filter(x -> x.getDate().equals("2021-03-31")
                            && (x.getCurrency() != null && x.getCurrency().equals(to))).collect(Collectors.toList());
            if (srcExchangeRates.size() > 0 ) {
                result = srcExchangeRates.get(0).getRate();
            }
        }else if (to.equals("EUR")){

            List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream()
                    .filter(x -> x.getDate().equals("2021-03-31")
                            && (x.getCurrency() != null && x.getCurrency().equals(from))).collect(Collectors.toList());
            if (srcExchangeRates.size() > 0 ) {
                result = new BigDecimal(1).divide(srcExchangeRates.get(0).getRate(), 3, RoundingMode.HALF_UP);
            }

        } else {


            List<ExchangeRate> srcExchangeRates = exchangeRateRepository.findAll().stream().filter(x -> x.getDate().equals("2021-03-31") && (x.getCurrency() != null && x.getCurrency().equals(from))).collect(Collectors.toList());
            // 1 EURO = srcExchangeRates 1 euro =1.18 usd

            List<ExchangeRate> trgtExchangeRates = exchangeRateRepository.findAll().stream().filter(x -> x.getDate().equals("2021-03-31") && (x.getCurrency() != null && x.getCurrency().equals(to))).collect(Collectors.toList());
            // 1 EURO = trgtExchangeRates 1 euro = 86.28 INR => 1 usd = 1/1.18 * 86.28

            if (trgtExchangeRates.size() > 0 && srcExchangeRates.size() > 0) {
                result = trgtExchangeRates.get(0).getRate().divide(srcExchangeRates.get(0).getRate(), 3, RoundingMode.HALF_UP);
            } else {
                return result;
            }

        }


        return result.multiply(new BigDecimal(unit));
    }

    @GetMapping("getCurrencyTrend/{currency}/{fromDate}/{toDate}")

    public List<ExchangeRate> getCurrencyTrend(@PathVariable("currency") String currency ,@PathVariable("fromDate") String fromDate,@PathVariable("toDate") String toDate ){

        List<ExchangeRate> trendList = exchangeRateRepository.getTrend(currency,fromDate,toDate);

        return trendList;

    }

}
