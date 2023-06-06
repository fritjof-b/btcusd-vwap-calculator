package timertasks;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.TimerTask;
import model.TradeEntry;
import service.GeminiApiService;

public class FetchTradeEntriesTask extends TimerTask {
  private final GeminiApiService geminiApiService;
  private final Deque<TradeEntry> tradeHistory;
  private long lastTid = 0;

  public FetchTradeEntriesTask(GeminiApiService geminiApiService, Deque<TradeEntry> tradeHistory) {
    this.geminiApiService = geminiApiService;
    this.tradeHistory = tradeHistory;
  }

  @Override
  public void run() {
    try {
      List<TradeEntry> tradeEntries;
      synchronized (tradeHistory) {
        lastTid = tradeHistory.getFirst().getTid();
        System.out.println("Fetching all trade entries after " + lastTid);
        tradeEntries = geminiApiService.fetchTradeEntriesAfterTid(lastTid);
        tradeEntries.forEach(
            trade -> {
              if (trade.getTid() > lastTid) {
                tradeHistory.addFirst(trade);
                lastTid = trade.getTid();
              }
            });
      }
      System.out.println("Trade history size: " + tradeHistory.size());
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
