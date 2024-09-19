/* (C)2024 */
package nus.iss.team3.backend.dataaccess.postgres;

import java.util.List;
import java.util.Map;

public interface IPostgresDataAccess {

  List<Map<String, Object>> queryStatement(String sql, Map<String, ?> inputs);

  int upsertStatement(String sql, Map<String, ?> inputs);
}
