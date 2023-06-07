# BTC-USD VWAP Calculator

This application calculates the [Volume Weighted Average Price (VWAP)](https://en.wikipedia.org/wiki/Volume-weighted_average_price) for BTC-USD.
It utilizes the [Gemini API](https://docs.gemini.com/rest-api/) to fetch trade entries and calculate VWAP.

## Prerequisites

Ensure that you have the following installed:

* `Java 17` or higher

The project is formatted with [Spotless](https://github.com/diffplug/spotless) according to Google Java Style.


## Building the Project

To build the project, including external dependencies, 
open your terminal/command line, navigate to the project directory
and run the following command:

`./gradlew clean shadowJar`

## Running the Application

Once you've built the project, you can run the application using the following command:

`java -jar build/libs/btcusd-1.0.0-release.jar`

When the application starts, it will fetch trade entries from the `Gemini API` and store them in an in-memory trade history, represented by a Deque.
The application then starts three tasks:

* [FetchTradeEntriesTask.java](https://github.com/fritjof-b/btcusd-vwap-calculator/blob/main/src/main/java/timertasks/FetchTradeEntriesTask.java) fetches new trade entries from the Gemini API every 10 seconds and adds them to the trade history.
* [CalculateVWAPTask.java](https://github.com/fritjof-b/btcusd-vwap-calculator/blob/main/src/main/java/timertasks/CalculateVWAPTask.java) calculates the VWAP for the last two and ten minutes every two minutes and prints the results.
* [RemoveOldTradesTask.java](https://github.com/fritjof-b/btcusd-vwap-calculator/blob/main/src/main/java/timertasks/RemoveOldTradesTask.java) removes trade entries older than 11 minutes from the trade history every three minutes.

## Useful Info

The initial call to the Gemini API fetches all trade entries
from the last ten minutes. We can get a hunch if this number is correct
by running the following command (assuming you have [jq](https://stedolan.github.io/jq/) installed),
assuming there are 500 trades or less:

```bash
curl https://api.gemini.com/v1/trades/btcusd\?limit_trades\=500\&timestamp\=<TIMESTAMP> | jq length
```

Where `<TIMESTAMP>` is the timestamp displayed as the application starts. 
