package analysis;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.NonNull;
import model.TradeEntry;

public class VolumeCalculator {
  public BigDecimal calculateVolumeForLastTwoMinutes(List<TradeEntry> tradeEntries, Instant now) {
    return calculateVolumeForLastNMinutes(tradeEntries, Duration.ofMinutes(2), now);
  }

  public BigDecimal calculateVolumeForLastTenMinutes(List<TradeEntry> tradeEntries, Instant now) {
    return calculateVolumeForLastNMinutes(tradeEntries, Duration.ofMinutes(10), now);
  }

  BigDecimal calculateVolumeForLastNMinutes(
      @NonNull List<TradeEntry> tradeEntries, @NonNull Duration duration, @NonNull Instant now) {

    BigDecimal totalVolume = BigDecimal.ZERO;
    Instant start = now.minus(duration);

    for (TradeEntry tradeEntry : tradeEntries) {
      Instant timestampMs = Instant.ofEpochMilli(tradeEntry.getTimestampms());
      if (!timestampMs.isBefore(start) && !timestampMs.isAfter(now) || timestampMs.equals(now)) {
        totalVolume = totalVolume.add(tradeEntry.getAmount());
      }
    }

    if (totalVolume.compareTo(BigDecimal.ZERO) == 0) {
      return BigDecimal.ZERO;
    } else {
      return totalVolume;
    }
  }
}
