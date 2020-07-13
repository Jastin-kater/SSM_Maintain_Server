package com.kkenterprise.domain.beans;

public class TAResultbean {
        private String token;
        private  boolean authentication;
        private int id;


        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public boolean isAuthentication() {
            return authentication;
        }

        public void setAuthentication(boolean authentication) {
            this.authentication = authentication;
        }



}
