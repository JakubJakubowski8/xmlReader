package com.jakub.xmlreader.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.jakub.xmlreader.model.ResponseBody;
import com.jakub.xmlreader.model.UrlAddress;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

import javax.validation.Valid;

@RestController
@EnableAutoConfiguration
@RequestMapping("/")
public class ReaderController  {

  @PostMapping("analyze")
  public HttpEntity<ResponseBody> getAnalyze(@Valid @RequestBody UrlAddress urlAddress) {

    ResponseBody responseBody = new ResponseBody();
    responseBody.setAnalyseDate(new Date());
    responseBody.add(linkTo(methodOn(ReaderController.class).getAnalyze(urlAddress)).withSelfRel());
    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }
}