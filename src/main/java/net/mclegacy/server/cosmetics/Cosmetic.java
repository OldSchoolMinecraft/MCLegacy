package net.mclegacy.server.cosmetics;

import java.io.Serializable;

public abstract class Cosmetic implements Serializable
{
    private String name;
    private String type;
    private boolean enabled = false;

    public Cosmetic(String name, String type)
    {
        this.name = name;
        this.type = type;
    }

    public String getName()
    {
        return name;
    }

    public String getType()
    {
        return type;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean flag)
    {
        this.enabled = flag;
    }
}
