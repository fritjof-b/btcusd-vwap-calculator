package analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import model.TradeEntry;
import org.junit.jupiter.api.Test;

public class VolumeWeightedAveragePriceCalculatorTest {

  @Test
  public void calculateVWAPForLastNMinutes_ShouldGiveCorrectCalculations() {
    Instant now = Instant.now();
    List<TradeEntry> tradeEntries =
        List.of(
            TradeEntry.builder()
                .timestampms(now.minusSeconds(60).toEpochMilli())
                .amount(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(5000))
                .build(),
            TradeEntry.builder()
                .timestampms(now.minusSeconds(120).toEpochMilli())
                .amount(BigDecimal.valueOf(3))
                .price(BigDecimal.valueOf(6000))
                .build(),
            TradeEntry.builder()
                .timestampms(now.minusSeconds(180).toEpochMilli())
                .amount(BigDecimal.valueOf(4))
                .price(BigDecimal.valueOf(7000))
                .build());

    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();

    BigDecimal vwap2Min = vwap.calculateVWAPForLastTwoMinutes(tradeEntries, now);
    BigDecimal vwap10Min = vwap.calculateVWAPForLastTenMinutes(tradeEntries, now);

    assertEquals(0, vwap2Min.compareTo(BigDecimal.valueOf(5000)));
    assertEquals(0, vwap10Min.compareTo(BigDecimal.valueOf(6222.22)));
  }

  @Test
  public void calculateVWAPForLastNMinutes_ShouldGiveZero_WhenNoTradeEntries() {
    List<TradeEntry> tradeEntries = List.of();

    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();

    BigDecimal vwap2Min = vwap.calculateVWAPForLastTwoMinutes(tradeEntries, Instant.now());
    BigDecimal vwap10Min = vwap.calculateVWAPForLastTenMinutes(tradeEntries, Instant.now());

    assertEquals(0, vwap2Min.compareTo(BigDecimal.ZERO));
    assertEquals(0, vwap10Min.compareTo(BigDecimal.ZERO));
  }

  @Test
  public void calculateVWAPForLastTwoMinutes_ShouldGiveZero_WhenNoTradeEntriesInLastTwoMinutes() {
    Instant now = Instant.now();
    List<TradeEntry> tradeEntries =
        List.of(
            TradeEntry.builder()
                .timestampms(now.minusSeconds(120).toEpochMilli())
                .amount(BigDecimal.valueOf(3))
                .price(BigDecimal.valueOf(6000))
                .build(),
            TradeEntry.builder()
                .timestampms(now.minusSeconds(180).toEpochMilli())
                .amount(BigDecimal.valueOf(4))
                .price(BigDecimal.valueOf(7000))
                .build());

    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();

    BigDecimal vwap2Min = vwap.calculateVWAPForLastTwoMinutes(tradeEntries, now);

    assertEquals(0, vwap2Min.compareTo(BigDecimal.ZERO));
  }

  @Test
  public void
      calculateVWAPForLastNMinutes_ShouldThrowNullPointerException_WhenTradeEntriesIsNull() {
    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();
    assertThrows(
        NullPointerException.class,
        () -> vwap.calculateVWAPForLastNMinutes(null, Duration.ofMinutes(2), Instant.now()));
  }

  @Test
  public void calculateVWAPForLastNMinutes_ShouldThrowNullPointerException_WhenDurationIsNull() {
    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();
    assertThrows(
        NullPointerException.class,
        () -> vwap.calculateVWAPForLastNMinutes(List.of(), null, Instant.now()));
  }

  @Test
  public void calculateVWAPForLastTwoMinutes_ShouldIncludeTradeExactlyAtBoundary() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    List<TradeEntry> tradeEntries =
        List.of(
            TradeEntry.builder()
                .timestampms(now.minus(2, ChronoUnit.MINUTES).plusMillis(1).toEpochMilli())
                .amount(BigDecimal.valueOf(2))
                .price(BigDecimal.valueOf(5000))
                .build(),
            TradeEntry.builder()
                .timestampms(now.minus(2, ChronoUnit.MINUTES).toEpochMilli())
                .amount(BigDecimal.valueOf(3))
                .price(BigDecimal.valueOf(6000))
                .build());

    VolumeWeightedAveragePriceCalculator vwap = new VolumeWeightedAveragePriceCalculator();

    BigDecimal vwap2Min = vwap.calculateVWAPForLastTwoMinutes(tradeEntries, now);

    assertEquals(0, vwap2Min.compareTo(BigDecimal.valueOf(5600)));
  }
}
