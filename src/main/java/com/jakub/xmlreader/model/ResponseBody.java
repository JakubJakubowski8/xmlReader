package com.jakub.xmlreader.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@AllArgsConstructor
public class ResponseBody extends RepresentationModel<ResponseBody> {

  private final Date analyseDate;
  @Setter
  private Details details;

  public ResponseBody() {
    this.analyseDate = new Date();
  }
}
