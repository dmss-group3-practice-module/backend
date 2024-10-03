package nus.iss.team3.backend.dataaccess;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class RemotePostgresDataAccess {
  private static final Logger logger = LogManager.getLogger(RemotePostgresDataAccess.class);
  private NamedParameterJdbcTemplate jdbcTemplate;

  public RemotePostgresDataAccess() {
    Properties props = loadProperties();
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.postgresql.Driver");
    dataSource.setUrl(props.getProperty("digitalocean.datasource.url"));
    dataSource.setUsername(props.getProperty("digitalocean.datasource.user"));
    dataSource.setPassword(props.getProperty("digitalocean.datasource.password"));
    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
  }

  private Properties loadProperties() {
    Properties props = new Properties();
    try (InputStream input =
        getClass().getClassLoader().getResourceAsStream("application.properties")) {
      if (input == null) {
        logger.error("Unable to find application.properties");
        return props;
      }
      props.load(input);
    } catch (IOException ex) {
      logger.error("Error loading application.properties", ex);
    }
    return props;
  }

  public List<Map<String, Object>> queryTestTable() {
    String sql = "SELECT * FROM test_table";
    try {
      return jdbcTemplate.queryForList(sql, Map.of());
    } catch (Exception e) {
      logger.error("Error querying test_table: ", e);
      return null;
    }
  }

  public void insertTestData(String name) {
    String sql = "INSERT INTO test_table (name) VALUES (:name)";
    try {
      int result = jdbcTemplate.update(sql, Map.of("name", name));
      System.out.println("Inserted " + result + " row(s)");
    } catch (Exception e) {
      logger.error("Error inserting test data: ", e);
    }
  }

  public static void main(String[] args) {
    RemotePostgresDataAccess dataAccess = new RemotePostgresDataAccess();

    // 测试插入数据
    dataAccess.insertTestData("Test Item from Java");

    // 测试查询数据
    List<Map<String, Object>> results = dataAccess.queryTestTable();
    if (results != null) {
      System.out.println("Query results from test_table:");
      results.forEach(System.out::println);
    } else {
      System.out.println("Error querying remote database. Check logs for details.");
    }
  }
}
