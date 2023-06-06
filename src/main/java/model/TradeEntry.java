package model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TradeEntry {
  private final long timestamp;
  private final long timestampms;
  private final long tid;
  private final BigDecimal price;
  private final BigDecimal amount;
  private final String exchange;
  private final String type;
}
