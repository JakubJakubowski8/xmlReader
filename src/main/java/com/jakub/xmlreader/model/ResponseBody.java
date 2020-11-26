package com.jakub.xmlreader.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@NoArgsConstructor
public class ResponseBody extends RepresentationModel<ResponseBody> {

  @Setter
  private Date analyseDate;
  private Details details;
}
