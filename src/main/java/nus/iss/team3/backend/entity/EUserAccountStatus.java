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

  private EUserAccountStatus(int code) {
    this.code = code;
  }

  public static EUserAccountStatus valueOfCode(int code) {
    return BY_CODE.get(code);
  }
}
