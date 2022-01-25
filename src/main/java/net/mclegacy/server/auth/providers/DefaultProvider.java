package net.mclegacy.server.auth.providers;

import net.mclegacy.server.auth.TrustedProvider;

public class DefaultProvider extends TrustedProvider
{
    private String email;
    private String password;

    public DefaultProvider(String email, String password)
    {
        super("MCLegacy (Default)");
        this.email = email;
        this.password = password;
    }

    public boolean authenticateUser()
    {
        return false;
    }
}
