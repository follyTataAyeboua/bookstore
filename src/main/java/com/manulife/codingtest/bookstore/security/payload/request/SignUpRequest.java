package com.manulife.codingtest.bookstore.security.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Set;

public class SignUpRequest {
  @NotBlank
  @Size(min = 3, max = 20)
  private String username;

  @NotBlank
  @Size(min = 3, max = 20)
  private String firstname;

  @NotBlank
  @Size(min = 3, max = 20)
  private String lastname;

  @NotBlank
  @Size(max = 50)
//  @Email(regexp = "/[a-zA-Z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,8}(.[a-z{2,8}])?/g")
  private String email;

  private Set<String> role;

  @NotBlank
  @Size(min = 6, max = 40)
  /*@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*])(?=.{8,})",
          message = "must contain at least 1 lowercase, 1 uppercase, 1 digit and 1 special character, alphabetical character!")*/
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFirstname() {
    return firstname;
  }

  public void setFirstname(String firstname) {
    this.firstname = firstname;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<String> getRole() {
    return this.role;
  }

  public void setRole(Set<String> role) {
    this.role = role;
  }
}
