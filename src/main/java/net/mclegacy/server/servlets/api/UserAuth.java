package net.mclegacy.server.servlets.api;

import net.mclegacy.server.auth.providers.DefaultProvider;
import net.mclegacy.server.servlets.ServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserAuth extends ServletBase
{
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String pageToken = request.getParameter("pageToken");
        boolean result = new DefaultProvider(email, password).authenticateUser();
    }
}
