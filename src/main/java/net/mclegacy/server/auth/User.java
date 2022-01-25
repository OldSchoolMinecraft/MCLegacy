package net.mclegacy.server.auth;

import net.mclegacy.server.cosmetics.Cosmetic;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable
{
    public String uuid;
    public String username;
    public String password;
    public String passwordSalt;
    public ArrayList<Cosmetic> cosmetics;
}
