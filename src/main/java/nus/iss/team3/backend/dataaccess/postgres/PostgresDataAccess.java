/* (C)2024 */
package nus.iss.team3.backend.dataaccess.postgres;

import jakarta.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PostgresDataAccess implements IPostgresDataAccess {

  private static final int MAX_RETRY = 5;
  private static final int BUFFER_BETWEEN_TRY = 2;
  private static final Logger logger = LogManager.getLogger(PostgresDataAccess.class);

  @Autowired private NamedParameterJdbcTemplate jdbcTemplate;

  @PostConstruct
  public void postConstruct() {
    try {
      if (jdbcTemplate.getJdbcTemplate().getDataSource() != null) {

        logger.info(
            "Connection details : {}",
            jdbcTemplate.getJdbcTemplate().getDataSource().getConnection().toString());
      }
    } catch (SQLException e) {

      logger.info("Connection details : ERROR, connection to database not enabled.");
      throw new RuntimeException(e);
    }
  }

  public List<Map<String, Object>> queryStatement(String sql, Map<String, ?> inputs) {

    if (inputs == null) {
      inputs = new HashMap<>();
    }
    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        return jdbcTemplate.queryForList(sql, inputs);
      } catch (DataAccessException e) {
        logger.error("[{}/{}] Error when executing query statement: {}", (i + 1), MAX_RETRY, sql);
        logger.error(
            "[{}/{}] Error when executing query statement: {}", (i + 1), MAX_RETRY, e.getMessage());
        try {
          TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
    return null;
  }

  public int upsertStatement(String sql, Map<String, ?> inputs) {

    for (int i = 0; i < MAX_RETRY; i++) {
      try {
        return jdbcTemplate.update(sql, inputs);

      } catch (DataAccessException e) {
        logger.error("[{}/{}]Error when executing update statement: {}", (i + 1), MAX_RETRY, sql);
        logger.error(
            "[{}/{}]Error when executing update statement: {}", (i + 1), MAX_RETRY, e.getMessage());
        try {
          TimeUnit.SECONDS.sleep(BUFFER_BETWEEN_TRY);
        } catch (InterruptedException ex) {
          throw new RuntimeException(ex);
        }
      }
    }
    return -1;
  }
}
