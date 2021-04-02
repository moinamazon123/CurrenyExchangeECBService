# CurrenyExchangeECBService

1: Place the history data json file and load init data. ALL THIS DATA ARE FROM EUROPEAN CENTRAL BANK(ECB) SITE.
2: It will insert curreny record in the tables.
3: Access the services:

http://localhost:7070/api/rates/initData : Initialize the data
http://localhost:7070/api/rates/getList : List of Currencies
http://localhost:7070/api/rates/convertCurrency/from/MXN/to/MTL : Convert Currency Rates
http://localhost:7070/api/rates/getConversionHits/USD
http://localhost:7070/api/rates/convertCurrencyUnits/from/GBP/2/to/INR
http://localhost:7070/api/rates/getCurrencyTrend/USD/2021-02-02/2021-04-02
