package timertasks;

import java.time.Instant;
import java.util.Deque;
import java.util.TimerTask;
import model.TradeEntry;

public class RemoveOldTradesTask extends TimerTask {
  private final Deque<TradeEntry> tradeHistory;
  private final long timeframe;

  public RemoveOldTradesTask(Deque<TradeEntry> tradeHistory, long timeframe) {
    this.tradeHistory = tradeHistory;
    this.timeframe = timeframe;
  }

  @Override
  public void run() {
    long oldestAllowedTimestamp = Instant.now().minusSeconds(timeframe).getEpochSecond();
    synchronized (tradeHistory) {
      TradeEntry entry;
      while ((entry = tradeHistory.peekLast()) != null
          && entry.getTimestamp() < oldestAllowedTimestamp) {
        tradeHistory.removeLast();
      }
    }
  }
}
