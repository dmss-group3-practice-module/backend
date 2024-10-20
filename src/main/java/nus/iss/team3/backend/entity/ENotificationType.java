package nus.iss.team3.backend.entity;

public enum ENotificationType {
  INFO("info"),
  WARNING("warning"),
  ERROR("error");

  private final String value;

  ENotificationType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static ENotificationType fromValue(String value) {
    for (ENotificationType type : values()) {
      if (type.value.equalsIgnoreCase(value)) {
        return type;
      }
    }
    throw new IllegalArgumentException("Unknown ENotificationType value: " + value);
  }
}
