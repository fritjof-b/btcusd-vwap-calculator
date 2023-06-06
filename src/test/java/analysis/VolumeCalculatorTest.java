package analysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import model.TradeEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VolumeCalculatorTest {
  private VolumeCalculator volumeCalculator;

  @BeforeEach
  void setUp() {
    volumeCalculator = new VolumeCalculator();
  }

  @Test
  void calculateVolumeForLastTwoMinutes() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    TradeEntry tradeEntry1 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(60).toEpochMilli())
            .amount(new BigDecimal("40"))
            .build();

    TradeEntry tradeEntry2 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(120).toEpochMilli())
            .amount(new BigDecimal("2"))
            .build();

    TradeEntry tradeEntry3 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(150).toEpochMilli())
            .amount(new BigDecimal("1337"))
            .build();

    List<TradeEntry> tradeEntries = Arrays.asList(tradeEntry1, tradeEntry2, tradeEntry3);

    BigDecimal volume = volumeCalculator.calculateVolumeForLastTwoMinutes(tradeEntries, now);

    assertEquals(new BigDecimal("42"), volume);
  }

  @Test
  void calculateVolumeForLastTenMinutes() {
    Instant now = Instant.now().truncatedTo(ChronoUnit.MILLIS);
    TradeEntry tradeEntry1 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(300).toEpochMilli())
            .amount(new BigDecimal("5"))
            .build();

    TradeEntry tradeEntry2 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(600).toEpochMilli())
            .amount(new BigDecimal("10"))
            .build();

    TradeEntry tradeEntry3 =
        TradeEntry.builder()
            .timestampms(now.minusSeconds(700).toEpochMilli())
            .amount(new BigDecimal("15"))
            .build();

    List<TradeEntry> tradeEntries = Arrays.asList(tradeEntry1, tradeEntry2, tradeEntry3);

    BigDecimal volume = volumeCalculator.calculateVolumeForLastTenMinutes(tradeEntries, now);

    assertEquals(new BigDecimal("15"), volume);
  }
}
