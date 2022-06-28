package net.mclegacy.server.servlets.bans;

import com.google.gson.Gson;
import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Issue extends ServletBase
{
    private static final Gson gson = new Gson();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            if (hasAllParams(request, "holder") && !hasValidAPIKey(request, request.getParameter("holder")))
            {
                sendInternalError(response, "Unauthorized", 401);
                return;
            }

            BanHolder banHolder = gson.fromJson(request.getReader(), BanHolder.class);
            if (banHolder == null)
            {
                sendInternalError(response, "Invalid request data", 400);
                return;
            }

            BanUtil.issueBan(new BanHolder(request.getParameter("holder"),
                    banHolder.username,
                    banHolder.reason,
                    banHolder.expiration,
                    banHolder.issued_by,
                    banHolder.issued_at));

            status(response, 200, "text/plain");
            sendStringResource(response, "OK");
        } catch (Exception ex) {
            status(response, 500, "text/plain");
            sendStringResource(response, ex.getMessage());
        }
    }
}
