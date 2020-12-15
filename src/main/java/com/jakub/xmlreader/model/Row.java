package com.jakub.xmlreader.model;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import lombok.Getter;

import java.util.Date;

public class Row {

  @Element(name="row", required=false)
  private String row;

  @Getter
  @Attribute(name="Id", required = false)
  private Long id;

  @Attribute(name="PostTypeId", required = false)
  private Long postTypeId;

  @Getter
  @Attribute(name="AcceptedAnswerId", required = false)
  private Long acceptedAnswerId;

  @Getter
  @Attribute(name="CreationDate", required = false)
  private Date creationDate;

  @Getter
  @Attribute(name="Score", required = false)
  private Long score;

  @Attribute(name="ViewCount", required = false)
  private Long viewCount;

  @Attribute(name="Body", required = false)
  private String body;

  @Attribute(name="OwnerUserId", required = false)
  private Long ownerUserId;

  @Attribute(name="LastEditorUserId", required = false)
  private Long lastEditorUserId;

  @Attribute(name="LastEditDate", required = false)
  private String lastEditDate;

  @Attribute(name="LastActivityDate", required = false)
  private String lastActivityDate;

  @Attribute(name="Title", required = false)
  private String title;

  @Attribute(name="Tags", required = false)
  private String tags;

  @Attribute(name="AnswerCount", required = false)
  private Long answerCount;

  @Attribute(name="CommentCount", required = false)
  private Long commentCount;

  @Attribute(name="ClosedDate", required = false)
  private Date closedDate;

  @Attribute(name="ParentId", required = false)
  private Long parentId;

  @Attribute(name="FavoriteCount", required = false)
  private Long favoriteCount;

  @Attribute(name="CommunityOwnedDate", required = false)
  private Date communityOwnedDate;

  @Attribute(name="OwnerDisplayName", required = false)
  private String ownerDisplayName;

}

