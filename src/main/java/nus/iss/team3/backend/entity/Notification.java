package nus.iss.team3.backend.entity;

import java.time.ZonedDateTime;

public class Notification {
  private Integer id;

  private Integer userId;

  private String title;

  private String content;

  private ENotificationType type;

  private Boolean isRead;

  private ZonedDateTime createDateTime;

  // Constructors, getters, and setters

  public Notification() {}

  public Notification(Integer userId, String title, String content, ENotificationType type) {
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.type = type;
    this.isRead = false;
    this.createDateTime = ZonedDateTime.now();
  }

  // Getters and setters for all fields

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public Integer getUserId() {
    return userId;
  }

  public void setUserId(Integer userId) {
    this.userId = userId;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public ENotificationType getType() {
    return type;
  }

  public void setType(ENotificationType type) {
    this.type = type;
  }

  public Boolean getIsRead() {
    return isRead;
  }

  public void setIsRead(Boolean isRead) {
    this.isRead = isRead;
  }

  public ZonedDateTime getCreateDateTime() {
    return createDateTime;
  }

  public void setCreateDateTime(ZonedDateTime createDateTime) {
    this.createDateTime = createDateTime;
  }
}
