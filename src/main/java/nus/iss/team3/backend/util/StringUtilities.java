package nus.iss.team3.backend.util;

/**
 * Utilities class for String related objects
 *
 * @author Desmond Tan Zhi Heng
 */
public class StringUtilities {

  public static boolean isStringNullOrBlank(String input) {
    return input == null || input.isBlank();
  }
}
