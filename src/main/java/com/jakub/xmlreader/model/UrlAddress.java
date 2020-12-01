package com.jakub.xmlreader.model;

import org.hibernate.validator.constraints.URL;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UrlAddress {
  @URL
  private String url;
}
