package net.mclegacy.server.servlets.user;

import net.mclegacy.server.servlets.ServletBase;

public class UserDashboard extends ServletBase
{
    public UserDashboard()
    {
        super("user/dashboard.html", "shared/template.html");
    }
}
