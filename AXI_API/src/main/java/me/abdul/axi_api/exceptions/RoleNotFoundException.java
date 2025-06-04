package me.abdul.axi_api.exceptions;

public class RoleNotFoundException extends RuntimeException {
  public RoleNotFoundException(String message) {
    super(message);
  }
}
