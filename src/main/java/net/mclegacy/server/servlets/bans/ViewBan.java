package net.mclegacy.server.servlets.bans;

import net.mclegacy.server.servlets.ServletBase;

public class ViewBan extends ServletBase
{
    public ViewBan()
    {
        super("bans/view_ban.html", "shared/template.html");
    }
}
