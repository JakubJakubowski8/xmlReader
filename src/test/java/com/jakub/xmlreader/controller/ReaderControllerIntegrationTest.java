package com.jakub.xmlreader.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.web.reactive.function.BodyInserters.*;


import com.jakub.xmlreader.model.Details;
import com.jakub.xmlreader.model.ResponseBody;
import com.jakub.xmlreader.model.UrlAddress;
import com.jakub.xmlreader.service.ReaderService;
import com.jakub.xmlreader.utils.DetailsBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;


import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.concurrent.ExecutionException;

@WebFluxTest(ReaderController.class)
public class ReaderControllerIntegrationTest {

  private final UrlAddress url = new UrlAddress("https://test.com");
  private static final Details details = new DetailsBuilder().build();
  private static final ResponseBody responseBody = new ResponseBody();

  @Autowired
  private WebTestClient webClient;

  @MockBean
  private ReaderService readerService;


  @BeforeEach
  public void setUp() throws ExecutionException, InterruptedException {
    responseBody.setDetails(details);

    given(this.readerService.getData(URI.create(url.getUrl())))
        .willReturn(details);
  }

  @Test
  void shouldReturnValidResponse() {
     this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(Mono.just(url), UrlAddress.class))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody(ResponseBody.class)
        .value(responseBody1 -> responseBody1.getDetails().getTotalPosts(),
            equalTo(responseBody.getDetails().getTotalPosts()));
  }

  @Test
  void shouldReturnUnsupportedMediaType() {
    this.webClient
        .post()
        .uri("/analyze")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
  }

  @Test
  void shouldReturnBadRequestWhenNoBody() {
    this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldReturnBadRequestWhenArgumentNotValid() {
    UrlAddress url2 = new UrlAddress("wrong_url");
    this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(Mono.just(url2), UrlAddress.class))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldReturnBadRequestWhenJsonMalformed() {
    UrlAddress url2 = new UrlAddress("wrong_url");
    this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromValue("""
            { 
            malformed ; json 
            ]
            """))
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldReturnMethodNotAllowed() {
    this.webClient
        .get()
        .uri("/analyze")
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
  }

  @Test
  void shouldReturnBadRequestWhenExecutionException() throws ExecutionException,
      InterruptedException {

    given(this.readerService.getData(URI.create(url.getUrl())))
        .willThrow(ExecutionException.class);

    this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(Mono.just(url), UrlAddress.class))
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldReturnBadRequestWhenInterruptedException() throws ExecutionException,
      InterruptedException {

    given(this.readerService.getData(URI.create(url.getUrl())))
        .willThrow(InterruptedException.class);

    this.webClient
        .post()
        .uri("/analyze")
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromPublisher(Mono.just(url), UrlAddress.class))
        .exchange()
        .expectStatus().isBadRequest();
  }
}
