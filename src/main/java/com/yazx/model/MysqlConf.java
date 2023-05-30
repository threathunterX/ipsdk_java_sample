package com.yazx.model;

public final class MysqlConf {
   
   private String databaseName;
   
   private String url;
   
   private String username;
   
   private String password;

   public MysqlConf(String databaseName, String url, String username, String password) {
      this.databaseName = databaseName;
      this.url = url;
      this.username = username;
      this.password = password;
   }

   public String getDatabaseName() {
      return databaseName;
   }

   public void setDatabaseName(String databaseName) {
      this.databaseName = databaseName;
   }

   public String getUrl() {
      return url;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getPassword() {
      return password;
   }

   public void setPassword(String password) {
      this.password = password;
   }
}