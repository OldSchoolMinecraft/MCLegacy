package net.mclegacy.server.auth;

public abstract class TrustedProvider
{
    private String name;

    public TrustedProvider(String name)
    {
        this.name = name;
    }

    public abstract boolean authenticateUser();

    public String getName()
    {
        return name;
    }
}
