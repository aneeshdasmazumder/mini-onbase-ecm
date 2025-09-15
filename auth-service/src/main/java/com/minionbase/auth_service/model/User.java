package com.minionbase.auth_service.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

@Entity
@Table(name="users")
public class User {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true)
  private String username;

  @Column(nullable=false)
  private String passwordHash;

  @Column
  private String roles; // CSV: ROLE_USER,ROLE_ADMIN

  public User() {}
  public User(String username, String passwordHash, List<String> rolesList){
    this.username=username; this.passwordHash=passwordHash; setRolesList(rolesList);
  }
  // getters/setters...
  public Long getId(){return id;} public void setId(Long id){this.id=id;}
  public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String p){this.passwordHash=p;}
  public String getRoles(){return roles;} public void setRoles(String r){this.roles=r;}
  public List<String> getRolesList(){ if(roles==null||roles.isBlank()) return List.of(); return Arrays.stream(roles.split(",")).map(String::trim).collect(Collectors.toList());}
  public void setRolesList(List<String> list){ this.roles = (list==null||list.isEmpty())? "": String.join(",", list); }
}
