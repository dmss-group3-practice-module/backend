/* (C)2024 */
package nus.iss.team3.backend.entity;

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

  public int getCode() {
    return code;
  }

  /**
   * Get the enum value based on the status code
   *
   * @param code The integer code of the status
   * @return The corresponding EUserAccountStatus, or null if not found
   */
  public static EUserAccountStatus valueOfCode(int code) {
    EUserAccountStatus status = BY_CODE.get(code);
    if (status == null) {
      throw new IllegalArgumentException("Invalid user account status code: " + code);
    }
    return status;
  }
}
