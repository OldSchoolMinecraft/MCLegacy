package net.mclegacy.server.servlets.user;

import net.mclegacy.server.auth.providers.DefaultProvider;
import net.mclegacy.server.servlets.ServletBase;
import net.mclegacy.server.util.ResourceLoader;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserLogin extends ServletBase
{
    private String email, password;

    public UserLogin()
    {
        super("user/login.html", "shared/template.html");

        cssProcessor.addStylesheet("/css/user/login.css");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        if (request.getMethod().equalsIgnoreCase("post"))
        {
            List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), StandardCharsets.UTF_8);
            for (NameValuePair pair : params)
            {
                if (pair.getName().equalsIgnoreCase("email"))
                    email = pair.getValue();
                if (pair.getName().equalsIgnoreCase("password"))
                    password = pair.getValue();
            }

            if (email != null && password != null)
            {
                if (new DefaultProvider(email, password).authenticateUser())
                {
                    //TODO: success
                }
            }
        }

        super.doGet(request, response);
    }
}
