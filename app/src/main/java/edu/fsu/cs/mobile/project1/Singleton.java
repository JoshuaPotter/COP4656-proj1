package edu.fsu.cs.mobile.project1;
import java.util.ArrayList;

public class Singleton {
    private static Singleton instance = null;
    public ArrayList<String> favoritedPostList = new ArrayList<>();

    protected Singleton() {

    }

    public static Singleton getInstance() {
        if(instance == null) {
            instance = new Singleton();
        }
        return instance;
    }
}