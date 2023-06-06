package service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import com.google.gson.Gson;
import exception.RateLimitExceededException;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import model.TradeEntry;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class GeminiApiServiceTest {
  private AutoCloseable closeable;

  @Mock private Gson gson;

  @Mock private HttpClient httpClient;
  @Mock private HttpResponse<String> httpResponse;

  private GeminiApiService geminiApiService;

  @BeforeEach
  void setUp() {
    closeable = MockitoAnnotations.openMocks(this);
    geminiApiService = new GeminiApiService(gson, httpClient);
  }

  @Test
  public void fetchTradeEntriesAfterTid_ShouldThrowIOException_WhenHttpClientThrowsIOException()
      throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("IO Exception"));
    assertThrows(IOException.class, () -> geminiApiService.fetchTradeEntriesAfterTid(0L));
  }

  @Test
  public void fetchInitialTradeEntries_ShouldThrowIOException_WhenHttpClientThrowsIOException()
      throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenThrow(new IOException("IO Exception"));
    assertThrows(IOException.class, () -> geminiApiService.fetchInitialTradeEntries(0L));
  }

  @Test
  public void
      fetchTradeEntriesAfterTid_ShouldThrowJsonSyntaxException_WhenGsonThrowsJsonSyntaxException()
          throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.body()).thenReturn("invalid json");
    when(gson.fromJson(anyString(), eq(TradeEntry[].class)))
        .thenThrow(new com.google.gson.JsonSyntaxException("invalid json"));
    assertThrows(
        com.google.gson.JsonSyntaxException.class,
        () -> geminiApiService.fetchTradeEntriesAfterTid(0L));
  }

  @Test
  public void
      fetchInitialTradeEntries_ShouldThrowJsonSyntaxException_WhenGsonThrowsJsonSyntaxException()
          throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.body()).thenReturn("invalid json");
    when(gson.fromJson(anyString(), eq(TradeEntry[].class)))
        .thenThrow(new com.google.gson.JsonSyntaxException("invalid json"));
    assertThrows(
        com.google.gson.JsonSyntaxException.class,
        () -> geminiApiService.fetchInitialTradeEntries(0L));
  }

  @Test
  public void fetchTradeEntries_ShouldThrowRateLimitExceededException_WhenHttpClientReturns429()
      throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.statusCode()).thenReturn(429);
    assertThrows(
        RateLimitExceededException.class, () -> geminiApiService.fetchTradeEntriesAfterTid(0L));
  }

  @Test
  public void
      fetchInitialTradeEntries_ShouldThrowRateLimitExceededException_WhenHttpClientReturns429()
          throws IOException, InterruptedException {
    when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
        .thenReturn(httpResponse);
    when(httpResponse.statusCode()).thenReturn(429);
    assertThrows(
        RateLimitExceededException.class, () -> geminiApiService.fetchInitialTradeEntries(0L));
  }

  @AfterEach
  void tearDown() throws Exception {
    closeable.close();
  }
}
