package nus.iss.team3.backend.service.util;

import java.util.Map;

/**
 * Utilities class for Sql related objects
 *
 * @author Pinardy
 */
public class SqlUtilities {

  public static Long getLongValue(Map<String, Object> row, String column) {
    return row.get(column) != null ? ((Number) row.get(column)).longValue() : null;
  }

  public static String getStringValue(Map<String, Object> row, String column) {
    return row.get(column) != null ? (String) row.get(column) : null;
  }

  public static Double getDoubleValue(Map<String, Object> row, String column) {
    return row.get(column) != null ? ((Number) row.get(column)).doubleValue() : null;
  }
}
