import com.google.gson.Gson;
import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import model.TradeEntry;
import service.GeminiApiService;
import timertasks.CalculateVWAPTask;
import timertasks.FetchTradeEntriesTask;
import timertasks.RemoveOldTradesTask;

public class Main {
  public static void main(String[] args) throws IOException, InterruptedException {
    Gson gson = new Gson();
    GeminiApiService geminiApiService = new GeminiApiService(gson, HttpClient.newHttpClient());
    long tenMinutesAgo = Instant.now().getEpochSecond() - 600;
    Deque<TradeEntry> tradeHistory =
        new LinkedList<>(geminiApiService.fetchAllTradeEntriesAfterTimestamp(tenMinutesAgo));

    System.out.println("Fetched initial trade history of size " + tradeHistory.size());
    System.out.println("For timestamp " + tenMinutesAgo);

    FetchTradeEntriesTask fetchTradeEntriesTask =
        new FetchTradeEntriesTask(geminiApiService, tradeHistory);
    CalculateVWAPTask calculateVWAPTask = new CalculateVWAPTask(tradeHistory);
    RemoveOldTradesTask cleaningTask = new RemoveOldTradesTask(tradeHistory, 60 * 11);

    ScheduledExecutorService executorService =
        java.util.concurrent.Executors.newScheduledThreadPool(3);

    executorService.scheduleAtFixedRate(fetchTradeEntriesTask, 0, 10, TimeUnit.SECONDS);
    executorService.scheduleAtFixedRate(calculateVWAPTask, 10, 2 * 60, TimeUnit.SECONDS);
    executorService.scheduleAtFixedRate(cleaningTask, 15, 3 * 60, TimeUnit.SECONDS);
  }
}
