package net.mclegacy.server.servlets.bans;

import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.BanUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Unban extends ServletBase
{
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            if (!hasAllParams(request, "holder", "username"))
            {
                sendInternalError(response, "Missing parameters", 400);
                return;
            }

            if (!hasValidAPIKey(request, request.getParameter("holder")))
            {
                sendInternalError(response, "Unauthorized", 401);
                return;
            }

            BanUtil.issueUnban(request.getParameter("username"));

            status(response, 200, "text/plain");
            sendStringResource(response, "OK");
        } catch (Exception ex) {
            status(response, 500, "text/plain");
            sendStringResource(response, ex.getMessage());
        }
    }
}
