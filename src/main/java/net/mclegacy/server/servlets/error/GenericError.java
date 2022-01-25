package net.mclegacy.server.servlets.error;

import net.mclegacy.server.servlets.ServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GenericError extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        response.getWriter().println(String.format("<html><h1>%s</h1></html>", response.getStatus()));
    }
}
