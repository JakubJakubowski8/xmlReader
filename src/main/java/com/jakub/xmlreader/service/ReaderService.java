package com.jakub.xmlreader.service;

import com.jakub.xmlreader.model.Details;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

@Service
public class ReaderService {

  private static final HttpClient CLIENT = HttpClient.newBuilder().build();

  public Details getData(URI url) throws ExecutionException, InterruptedException {
    return reactiveSearch(CLIENT, url);
  }

  private static Details reactiveSearch(HttpClient client, URI url) throws ExecutionException, InterruptedException {
    HttpRequest request = HttpRequest.newBuilder()
        .GET()
        .uri(url)
        .build();
    RowFinder finder = new RowFinder();
    client.sendAsync(request, BodyHandlers.fromLineSubscriber(finder))
        .exceptionally(ex -> {
          finder.onError(ex);
          return null;
        });
    return finder
        .found()
        .get();
  }
}