package nus.iss.team3.backend.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * Recipe Class
 *
 * @author Mao Weining
 */
public enum ERecipeStatus {
  DRAFT(0),
  PUBLISHED(1),
  ARCHIVED(2);

  private static final Map<Integer, ERecipeStatus> BY_CODE = new HashMap<>();

  static {
    for (ERecipeStatus status : values()) {
      BY_CODE.put(status.code, status);
    }
  }

  public final int code;

  private ERecipeStatus(int code) {
    this.code = code;
  }

  public static ERecipeStatus valueOfCode(int code) {
    return BY_CODE.get(code);
  }
}