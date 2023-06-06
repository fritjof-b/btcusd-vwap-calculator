package timertasks;

import analysis.VolumeCalculator;
import analysis.VolumeWeightedAveragePriceCalculator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.TimerTask;
import model.TradeEntry;

public class CalculateVWAPTask extends TimerTask {
  private final Deque<TradeEntry> tradeHistory;
  private final VolumeWeightedAveragePriceCalculator vwapCalculator;
  private final VolumeCalculator volumeCalculator;

  public CalculateVWAPTask(Deque<TradeEntry> tradeHistory) {
    this.tradeHistory = tradeHistory;
    this.vwapCalculator = new VolumeWeightedAveragePriceCalculator();
    this.volumeCalculator = new VolumeCalculator();
  }

  @Override
  public void run() {
    synchronized (tradeHistory) {
      List<TradeEntry> tradeHistoryArrayList = new ArrayList<>(tradeHistory);
      Instant now = Instant.now();
      BigDecimal vwapForLastTwoMinutes =
          vwapCalculator.calculateVWAPForLastTwoMinutes(tradeHistoryArrayList, now);
      BigDecimal vwapForLastTenMinutes =
          vwapCalculator.calculateVWAPForLastTenMinutes(tradeHistoryArrayList, now);
      BigDecimal volumeForLastTwoMinutes =
          volumeCalculator.calculateVolumeForLastTwoMinutes(tradeHistoryArrayList, now);
      BigDecimal volumeForLastTenMinutes =
          volumeCalculator.calculateVolumeForLastTenMinutes(tradeHistoryArrayList, now);

      printResults(
          now,
          vwapForLastTwoMinutes,
          vwapForLastTenMinutes,
          volumeForLastTwoMinutes,
          volumeForLastTenMinutes);
    }
  }

  private void printResults(
      Instant now,
      BigDecimal vwapForLastTwoMinutes,
      BigDecimal vwapForLastTenMinutes,
      BigDecimal volumeForLastTwoMinutes,
      BigDecimal volumeForLastTenMinutes) {
    System.out.println("\nTime of calculation: " + now);
    System.out.println("VWAP    2 minutes:  " + vwapForLastTwoMinutes);
    System.out.println("VWAP   10 minutes:  " + vwapForLastTenMinutes);
    System.out.println("Volume  2 minutes:  " + volumeForLastTwoMinutes);
    System.out.println("Volume 10 minutes:  " + volumeForLastTenMinutes);

    String priceTrend =
        vwapForLastTwoMinutes.compareTo(vwapForLastTenMinutes) > 0
            ? "Price is going up"
            : vwapForLastTwoMinutes.compareTo(vwapForLastTenMinutes) < 0
                ? "Price is going down"
                : "Price is stable";

    System.out.println("Price trend: " + priceTrend);
  }
}
