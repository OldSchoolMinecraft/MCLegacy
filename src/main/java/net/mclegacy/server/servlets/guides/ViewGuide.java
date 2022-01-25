package net.mclegacy.server.servlets.guides;

import net.mclegacy.server.servlets.ServletBase;

public class ViewGuide extends ServletBase
{
    public ViewGuide()
    {
        super("guides/view_guide.html", "shared/template.html");
    }
}
