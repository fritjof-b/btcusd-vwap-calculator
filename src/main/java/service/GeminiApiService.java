package service;

import com.google.gson.Gson;
import exception.RateLimitExceededException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.TradeEntry;

public class GeminiApiService {
  private static final String BASE_URL = "https://api.gemini.com/v1";
  private static final String ENDPOINT = "/trades/btcusd";
  private static final String QUERY = "?limit_trades=%s&since_tid=%d";
  private static final String TIMESTAMP_QUERY = "?limit_trades=%s&timestamp=%d";
  private static final String LIMIT = "500";
  private final Gson gson;
  private final HttpClient httpClient;

  public GeminiApiService(Gson gson, HttpClient httpClient) {
    this.gson = gson;
    this.httpClient = httpClient;
  }

  public List<TradeEntry> fetchTradeEntriesAfterTid(Long tid)
      throws IOException, InterruptedException {
    String query = String.format(QUERY, LIMIT, tid);
    String url = BASE_URL + ENDPOINT + query;
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 429) {
      throw new RateLimitExceededException("Rate limit exceeded");
    }
    return Arrays.asList(gson.fromJson(response.body(), TradeEntry[].class));
  }

  public List<TradeEntry> fetchAllTradeEntriesAfterTimestamp(Long timestamp)
      throws InterruptedException, IOException {
    System.out.println("Fetching initial trade entries after timestamp " + timestamp);
    List<TradeEntry> allTradeEntries = new ArrayList<>();
    Long tid = null;

    List<TradeEntry> fetchedTradeEntries = fetchInitialTradeEntries(timestamp);
    if (!fetchedTradeEntries.isEmpty()) {
      allTradeEntries.addAll(fetchedTradeEntries);
      tid = fetchedTradeEntries.get(0).getTid();
    }

    while (tid != null) {
      fetchedTradeEntries = fetchTradeEntriesAfterTid(tid);
      if (fetchedTradeEntries.isEmpty() || fetchedTradeEntries.get(0).getTid() == tid) {
        break;
      }
      allTradeEntries.addAll(fetchedTradeEntries);
      tid = fetchedTradeEntries.get(fetchedTradeEntries.size() - 1).getTid();
      Thread.sleep(1000);
    }
    return allTradeEntries;
  }

  public List<TradeEntry> fetchInitialTradeEntries(Long timestamp)
      throws IOException, InterruptedException {
    String query = String.format(TIMESTAMP_QUERY, LIMIT, timestamp);
    String url = BASE_URL + ENDPOINT + query;
    HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

    if (response.statusCode() == 429) {
      throw new RateLimitExceededException("Rate limit exceeded");
    }
    return Arrays.asList(gson.fromJson(response.body(), TradeEntry[].class));
  }
}
