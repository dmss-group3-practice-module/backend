/* (C)2024 */
package nus.iss.team3.backend.dataaccess.postgres;

import jakarta.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresDataAccess implements IPostgresDataAccess {

  private static final Logger logger = LogManager.getLogger(PostgresDataAccess.class);

  @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

  @PostConstruct
  public void postConstruct() {
    try {
      logger.info(
          "Connection details : {}",
          jdbcTemplate.getJdbcTemplate().getDataSource().getConnection().toString());
    } catch (SQLException e) {

      logger.info("Connection details : ERROR, connection to database not enabled.");
      throw new RuntimeException(e);
    }
  }

  public List<Map<String, Object>> queryStatement(String sql, Map<String, ?> inputs) {

    if (inputs == null) {
      inputs = new HashMap<>();
    }
    try {
      return jdbcTemplate.queryForList(sql, inputs);

    } catch (DataAccessException e) {
      logger.error("Error when executing query statement: {}", sql);
      return null;
    }
  }

  public int upsertStatement(String sql, Map<String, ?> inputs) {

    try {
      return jdbcTemplate.update(sql, inputs);

    } catch (DataAccessException e) {
      logger.error("Error when executing update statement: {}", sql);
      logger.error("Error when executing update statement: {}", e.getMessage());
      return -1;
    }
  }
}
