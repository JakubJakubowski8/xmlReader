package com.jakub.xmlreader.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import com.jakub.xmlreader.model.Details;
import com.jakub.xmlreader.model.ResponseBody;
import com.jakub.xmlreader.model.UrlAddress;
import com.jakub.xmlreader.service.ReaderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.validation.Valid;

@RestController
@EnableAutoConfiguration
@RequestMapping(path = "/",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
public class ReaderController  {

  private final ReaderService readerService;

  @Autowired
  ReaderController(ReaderService readerService) {
    this.readerService = readerService;
  }

  @PostMapping("analyze")
  public HttpEntity<ResponseBody> getAnalyze(@Valid @RequestBody UrlAddress urlAddress) throws ExecutionException, InterruptedException {

    Details details = readerService.getData(URI.create(urlAddress.getUrl()));

    ResponseBody responseBody = new ResponseBody();
    responseBody.setDetails(details);
    responseBody.add(linkTo(methodOn(ReaderController.class).getAnalyze(urlAddress)).withSelfRel());

    return new ResponseEntity<>(responseBody, HttpStatus.OK);
  }
}