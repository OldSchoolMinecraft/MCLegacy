package net.mclegacy.server.media;

import net.mclegacy.server.auth.User;

import java.io.Serializable;
import java.util.ArrayList;

public class Video implements Serializable
{
    public String uuid;
    public String resourceLocation;
    public Visibility visibility;
    public String author;
    public int views;
    public ArrayList<Comment> comments;

    public class Comment implements Serializable
    {
        public User author;
        public boolean anonymous;
        public String message;
    }

    public enum Visibility
    {
        Private, Public, Unlisted, WhitelistedLink, WhitelistedKey
    }
}
