package com.jakub.xmlreader.service;

import com.jakub.xmlreader.DateFormatTransformer;
import com.jakub.xmlreader.model.Details;
import com.jakub.xmlreader.model.Row;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.transform.RegistryMatcher;

import lombok.SneakyThrows;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Flow;

public class RowFinder implements Flow.Subscriber<String> {

  private final CompletableFuture<Details> found;
  private Flow.Subscription subscription;
  private Details details = new Details();
  private Long numberOfPosts = 0L;
  private Long numberOfAccepted = 0L;
  private Long sumScore = 0L;
  private Date firstPost;
  private Date lastPost;
  private final DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS",
      Locale.ENGLISH);

  RegistryMatcher registryMatcher = new RegistryMatcher();
  Serializer serializer = new Persister((registryMatcher));

  RowFinder() {
    this.found = new CompletableFuture<>();
    registryMatcher.bind(Date.class, new DateFormatTransformer(format));
  }

  @Override
  public void onSubscribe(Flow.Subscription subscription) {
    this.subscription = subscription;
    this.subscription.request(1);
  }

  @SneakyThrows
  @Override
  public void onNext(String line) {
    if (line.contains("<row ")) {
        Row row = serializer.read(Row.class, line);
        handleResult(row);
    }

    subscription.request(1);
  }

  @Override
  public void onError(Throwable ex) {
    found.completeExceptionally(ex);
  }

  @Override
  public void onComplete() {
    details.setTotalPosts(numberOfPosts);
    details.setTotalAcceptedPosts(numberOfAccepted);
    details.setAvgScore(((double) sumScore/numberOfPosts));
    details.setFirtPost(firstPost);
    details.setLastPost(lastPost);
    found.complete(details);
  }

  public CompletableFuture<Details> found() {
    return found;
  }

  private void handleResult(Row row) {
    countPosts(row);
    countAccepted(row);
    calcAvgScore(row);
    getFirstAndLastPost(row);
  }

  private void countPosts(Row row) {
    if (row.getId() != null) {
      numberOfPosts++;
    }
  }

  private void countAccepted(Row row) {
    if (row.getAcceptedAnswerId() != null) {
      numberOfAccepted++;
    }
  }

  private void calcAvgScore(Row row) {
      sumScore += row.getScore();
  }

  private void getFirstAndLastPost(Row row) {
    if(firstPost == null){
      firstPost = row.getCreationDate();
      lastPost = row.getCreationDate();
    }
    if (row.getCreationDate().before(firstPost)){
      firstPost = row.getCreationDate();
    }
    if (row.getCreationDate().after(lastPost)){
      lastPost = row.getCreationDate();
    }
  }

}