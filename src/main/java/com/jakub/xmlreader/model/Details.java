package com.jakub.xmlreader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Details {
  private Date firtPost;
  private Date lastPost;
  private Long totalPosts;
  private Long totalAcceptedPosts;
  private Double avgScore;
}