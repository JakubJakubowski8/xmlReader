package com.jakub.xmlreader.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Date;

@Getter
@NoArgsConstructor
public class Details {
  private Date firtPost;
  private Date lastPost;
  private Long totalPosts;
  private Long totalAcceptedPosts;
  private Short avgScore;
}