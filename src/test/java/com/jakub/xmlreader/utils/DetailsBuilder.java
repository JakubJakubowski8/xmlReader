package com.jakub.xmlreader.utils;

import com.jakub.xmlreader.model.Details;

import java.util.Date;

public class DetailsBuilder  {

  private final Date firtPost;
  private final Date lastPost;
  private final Long totalPosts;
  private final Long totalAcceptedPosts;
  private final Double avgScore;

  public DetailsBuilder(){
    firtPost = new Date();
    lastPost = new Date();
    totalPosts = 10L;
    totalAcceptedPosts = 1L;
    avgScore = 2.1;
  }

  public Details build() {
    return new Details(firtPost, lastPost, totalPosts, totalAcceptedPosts, avgScore);
  }
}
