package nus.iss.team3.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 * User account status
 *
 * <p>ACTIVE(1): User account is active and can perform all operations INACTIVE(2): User account is
 * temporarily inactive BANNED(0): User account is banned from system
 *
 * @author Desmond Tan Zhi Heng, REN JIARUI
 */
public enum EUserStatus {
  ACTIVE(1),
  INACTIVE(2),
  BANNED(0);

  public final int code;

  private static final Map<Integer, EUserStatus> BY_CODE = new HashMap<>();

  static {
    for (EUserStatus e : values()) {
      BY_CODE.put(e.code, e);
    }
  }

  EUserStatus(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static EUserStatus fromValue(int code) {
    for (EUserStatus status : values()) {
      if (status.code == code) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid status code: " + code);
  }

  public static EUserStatus valueOfCode(int code) {
    EUserStatus status = BY_CODE.get(code);
    if (status == null) {
      throw new IllegalArgumentException("Invalid user account status code: " + code);
    }
    return status;
  }
}
