package nus.iss.team3.backend.service.util;

import org.mindrot.jbcrypt.BCrypt;

public class EncryptionUtilities {

  public static String hashPassword(String plainTextPassword) {
    return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
  }
}
