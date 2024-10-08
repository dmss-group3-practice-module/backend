package nus.iss.team3.backend.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgresConnectionTest {
  public static void main(String[] args) {
    // 数据库URL格式: jdbc:postgresql://主机:端口/数据库名
    String url = "jdbc:postgresql://localhost:5432/test";
    String user = "postgres";
    String password = "my789668";

    try (Connection conn = DriverManager.getConnection(url, user, password)) {
      if (conn != null) {
        System.out.println("成功连接到Postgres数据库！");
      } else {
        System.out.println("连接失败。");
      }
    } catch (SQLException e) {
      System.out.println("数据库连接出错：");
      e.printStackTrace();
    }
  }
}
