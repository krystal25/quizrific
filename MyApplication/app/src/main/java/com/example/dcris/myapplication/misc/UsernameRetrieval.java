package com.example.dcris.myapplication.misc;

public class UsernameRetrieval  {

    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        UsernameRetrieval.username = username;
    }
}