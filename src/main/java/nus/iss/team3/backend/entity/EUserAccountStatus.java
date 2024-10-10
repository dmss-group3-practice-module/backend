package nus.iss.team3.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 * User account status
 *
 * @author Desmond Tan Zhi Heng
 */
public enum EUserAccountStatus {
  ACTIVE(1),
  INACTIVE(2),
  BANNED(0);

  public final int code;

  private static final Map<Integer, EUserAccountStatus> BY_CODE = new HashMap<>();

  static {
    for (EUserAccountStatus e : values()) {
      BY_CODE.put(e.code, e);
    }
  }

  EUserAccountStatus(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static EUserAccountStatus fromValue(int code) {
    for (EUserAccountStatus status : values()) {
      if (status.code == code) {
        return status;
      }
    }
    throw new IllegalArgumentException("Invalid status code: " + code);
  }

  public static EUserAccountStatus valueOfCode(int code) {
    EUserAccountStatus status = BY_CODE.get(code);
    if (status == null) {
      throw new IllegalArgumentException("Invalid user account status code: " + code);
    }
    return status;
  }
}
