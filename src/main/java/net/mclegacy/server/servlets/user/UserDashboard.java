package net.mclegacy.server.servlets.user;

import net.mclegacy.server.servlets.ServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserDashboard extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        resource = "user/dashboard.html";
        basicDynamicStack();
        super.doGet(request, response);
    }
}
