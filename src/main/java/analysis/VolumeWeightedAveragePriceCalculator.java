package analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.NonNull;
import model.TradeEntry;

public class VolumeWeightedAveragePriceCalculator {

  public BigDecimal calculateVWAPForLastTwoMinutes(List<TradeEntry> tradeEntries, Instant now) {
    return calculateVWAPForLastNMinutes(tradeEntries, Duration.ofMinutes(2), now);
  }

  public BigDecimal calculateVWAPForLastTenMinutes(List<TradeEntry> tradeEntries, Instant now) {
    return calculateVWAPForLastNMinutes(tradeEntries, Duration.ofMinutes(10), now);
  }

  BigDecimal calculateVWAPForLastNMinutes(
      @NonNull List<TradeEntry> tradeEntries, @NonNull Duration duration, @NonNull Instant now) {

    BigDecimal totalAmount = BigDecimal.ZERO;
    BigDecimal totalAmountTimesPrice = BigDecimal.ZERO;

    Instant start = now.minus(duration);

    for (TradeEntry tradeEntry : tradeEntries) {
      Instant timestampMs = Instant.ofEpochMilli(tradeEntry.getTimestampms());
      if (!timestampMs.isBefore(start) && !timestampMs.isAfter(now)) {
        totalAmount = totalAmount.add(tradeEntry.getAmount());
        totalAmountTimesPrice =
            totalAmountTimesPrice.add(tradeEntry.getAmount().multiply(tradeEntry.getPrice()));
      }
    }

    if (totalAmount.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    } else {
      return totalAmountTimesPrice.divide(totalAmount, 2, RoundingMode.HALF_UP);
    }
  }
}
