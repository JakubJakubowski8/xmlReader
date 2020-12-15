package com.jakub.xmlreader.model;

import org.springframework.hateoas.RepresentationModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBody extends RepresentationModel<ResponseBody> {
  private Date analyseDate;
  private Details details;
}
