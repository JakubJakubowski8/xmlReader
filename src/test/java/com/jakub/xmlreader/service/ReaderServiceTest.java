package com.jakub.xmlreader.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.BinaryBody.binary;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.HttpStatusCode.OK_200;
import static org.springframework.http.HttpHeaders.CACHE_CONTROL;
import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

import com.jakub.xmlreader.model.Details;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockserver.client.MockServerClient;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.model.Header;
import org.mockserver.model.MediaType;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.ExecutionException;

@SpringBootTest
@ExtendWith(MockServerExtension.class)
public class ReaderServiceTest {

  @InjectMocks
  private ReaderService readerService;

  private static ClientAndServer mockServer;

  private static final HttpClient CLIENT = HttpClient.newBuilder().build();

  private final byte[] xmlBytes;

  private final URI uri = URI.create("http://127.0.0.1:1080/data/testdata.xml");

  public ReaderServiceTest() throws IOException {
    this.xmlBytes = IOUtils.toByteArray(new ClassPathResource(
        "testdata.xml").getInputStream());
  }

  @BeforeEach
  public void beforeEach() {
    mockServer = startClientAndServer(1080);

    new MockServerClient("127.0.0.1", 1080)
        .when(
            request()
                .withMethod("GET")
                .withPath("/data/testdata.xml")
        )
        .respond(
            response()
                .withStatusCode(OK_200.code())
                .withReasonPhrase(OK_200.reasonPhrase())
                .withHeaders(
                    new Header(CONTENT_TYPE, MediaType.XML_UTF_8.toString()),
                    new Header(CONTENT_DISPOSITION, "form-data; name=\"testdata.xml\"; " +
                        "filename=\"testdata.xml\""),
                    new Header(CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0")
                )
                .withBody(binary(xmlBytes))
        );
  }

  @AfterEach
  public void stopServer() {

    mockServer.stop();
  }

  @Test
  void shouldGetDetailsWhenFromServer() throws ExecutionException, InterruptedException {

    Details response = readerService.getData(uri);
    assertThat(response.getTotalPosts()).isEqualTo(4);
    assertThat(response.getAvgScore()).isEqualTo(1.25);

  }

  @Test
  void hitTheServer() throws ExecutionException, InterruptedException {

    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(uri)
        .build();
    RowFinder finder = new RowFinder();
    CLIENT.sendAsync(request, HttpResponse.BodyHandlers.fromLineSubscriber(finder)).exceptionally(ex -> {
      finder.onError(ex);
      return null;
    });

    finder.found().get();
    assertThat(finder.found().get().getAvgScore()).isEqualTo(1.25);

  }

}