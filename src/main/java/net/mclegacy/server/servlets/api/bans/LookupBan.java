package net.mclegacy.server.servlets.api.bans;

import com.google.gson.Gson;
import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.cache.Page1Bans;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

public class LookupBan extends ServletBase
{
    private static final Logger log = Main.getLogger();
    private static final Gson gson = new Gson();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        try
        {
            if (!hasAllParams(request, "target"))
            {
                sendInternalError(response, "Target username missing from the request.", 400);
                return;
            }

            BanHolder ban = BanUtil.getSpecificBan(request.getParameter("target"));

            if (ban == null)
            {
                sendInternalError(response, "The lookup succeeded, but no matching records could be found.", 200);
                return;
            }

            status(response, 200, "application/json");
            sendStringResource(response, gson.toJson(ban));
        } catch (Exception ex) {
            status(response, 500, "text/plain");
            sendStringResource(response, ex.getMessage());
        }
    }

    private class BanLookupResponse
    {
        public boolean matchesFound;
        public String errorMessage;
        public BanHolder lookupResult;

        public BanLookupResponse() {}

        public BanLookupResponse(BanHolder result)
        {
            matchesFound = result == null;
            errorMessage = matchesFound ? "No issues reported" : "The lookup succeeded, but no matching records could be found";
            lookupResult = result;
        }
    }
}
