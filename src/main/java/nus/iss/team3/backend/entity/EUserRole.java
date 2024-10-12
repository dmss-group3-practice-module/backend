package nus.iss.team3.backend.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

/**
 * User role
 *
 * @author RENJIARUI
 */
public enum EUserRole {
  ADMIN(1),
  USER(2);

  public final int code;

  private static final Map<Integer, EUserRole> BY_CODE = new HashMap<>();

  static {
    for (EUserRole e : values()) {
      BY_CODE.put(e.code, e);
    }
  }

  EUserRole(int code) {
    this.code = code;
  }

  @JsonValue
  public int getCode() {
    return code;
  }

  @JsonCreator
  public static EUserRole fromValue(int code) {
    for (EUserRole role : values()) {
      if (role.code == code) {
        return role;
      }
    }
    throw new IllegalArgumentException("Invalid role code: " + code);
  }

  public static EUserRole valueOfCode(int code) {
    EUserRole role = BY_CODE.get(code);
    if (role == null) {
      throw new IllegalArgumentException("Invalid user role code: " + code);
    }
    return role;
  }
}
