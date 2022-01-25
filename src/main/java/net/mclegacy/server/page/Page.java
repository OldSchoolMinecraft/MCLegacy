package net.mclegacy.server.page;

import net.mclegacy.server.servlets.ServletBase;

public class Page
{
    protected Class<? extends ServletBase> servletClass;
    protected String pathSpec;

    public Page(Class<? extends ServletBase> servletClass, String pathSpec)
    {
        this.servletClass = servletClass;
        this.pathSpec = pathSpec;
    }

    public Class<? extends ServletBase> getServletClass()
    {
        return servletClass;
    }

    public String getPathSpec()
    {
        return pathSpec;
    }
}
