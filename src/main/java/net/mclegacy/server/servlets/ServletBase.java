package net.mclegacy.server.servlets;

import com.google.gson.Gson;
import net.mclegacy.server.page.processing.CSSContentProcessor;
import net.mclegacy.server.page.processing.ContentProcessor;
import net.mclegacy.server.page.PageMetaTags;
import net.mclegacy.server.page.processing.ScriptsContentProcessor;
import net.mclegacy.server.util.TimeKeeper;
import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.ResourceLoader;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public abstract class ServletBase extends HttpServlet
{
    protected static final Logger log = Main.getLogger();
    protected static final Gson gson = new Gson();

    protected final ResourceLoader cssLoader = new ResourceLoader("shared/template/css.html");
    protected final ResourceLoader scriptsLoader = new ResourceLoader("shared/template/scripts.html");
    protected final ResourceLoader metaLoader = new ResourceLoader("shared/template/meta.html");
    protected final ResourceLoader bodyTopLoader = new ResourceLoader("shared/template/body_top.html");
    protected final ResourceLoader bodyBottomLoader = new ResourceLoader("shared/template/body_bottom.html");
    protected final ResourceLoader headerLoader = new ResourceLoader("shared/header.html");
    protected final ResourceLoader footerLoader = new ResourceLoader("shared/footer.html");

    protected final CSSContentProcessor cssProcessor = new CSSContentProcessor(cssLoader);
    protected final ScriptsContentProcessor scriptsProcessor = new ScriptsContentProcessor(scriptsLoader);
    protected final ContentProcessor metaProcessor = new ContentProcessor(metaLoader);
    protected final ContentProcessor bodyTopProcessor = new ContentProcessor(bodyTopLoader);
    protected final ContentProcessor bodyBottomProcessor = new ContentProcessor(bodyBottomLoader);
    protected final ContentProcessor headerProcessor = new ContentProcessor(headerLoader);
    protected final ContentProcessor footerProcessor = new ContentProcessor(footerLoader);

    protected boolean enableKeyCheck = false;
    protected int statusCode = 200;
    protected ResourceLoader contentLoader;
    protected ContentProcessor contentProcessor;
    protected ResourceLoader templateLoader;
    protected ContentProcessor templateProcessor;
    protected String contentType = "text/html";
    protected PageMetaTags metaTags = new PageMetaTags();
    private TimeKeeper timeKeeper = new TimeKeeper();

    public ServletBase() {}

    public ServletBase(String sourceFile)
    {
        this.contentLoader = new ResourceLoader(sourceFile);
        this.contentProcessor = new ContentProcessor(contentLoader);
        this.contentType = "text/html";
    }

    public ServletBase(String sourceFile, String templateFile)
    {
        this(sourceFile);
        this.templateLoader = new ResourceLoader(templateFile);
        this.templateProcessor = new ContentProcessor(templateLoader);
        this.templateProcessor.addPreProcessorHook((pageContent) ->
        {
            String currentContent = pageContent;
            currentContent = currentContent.replace("${TITLE}", metaTags.title);
            currentContent = currentContent.replace("${META}", metaProcessor.getCurrentContent());
            currentContent = currentContent.replace("${CSS}", cssProcessor.getCurrentContent());
            currentContent = currentContent.replace("${SCRIPTS}", scriptsProcessor.getCurrentContent());
            currentContent = currentContent.replace("${BODY_TOP}", bodyTopProcessor.getCurrentContent());
            currentContent = currentContent.replace("${HEADER}", headerProcessor.getCurrentContent());
            currentContent = currentContent.replace("${CONTENT}", contentProcessor.getCurrentContent());
            currentContent = currentContent.replace("${FOOTER}", footerProcessor.getCurrentContent());
            currentContent = currentContent.replace("${BODY_BOTTOM}", bodyBottomProcessor.getCurrentContent());
            currentContent = currentContent.replace("${GEN_TIME}", "" + timeKeeper.calc());

            currentContent = currentContent.replace("${OG_TYPE}", metaTags.type);
            currentContent = currentContent.replace("${OG_TITLE}", metaTags.title);
            currentContent = currentContent.replace("${OG_DESCRIPTION}", metaTags.description);
            currentContent = currentContent.replace("${OG_URL}", metaTags.url);
            currentContent = currentContent.replace("${OG_IMAGE}", metaTags.image);

            return currentContent;
        });

        log.info("Servlet created with template: " + sourceFile + ", " + templateFile);
    }

    public ServletBase(String sourceFile, String templateFile, String title)
    {
        this(sourceFile, templateFile);
        this.metaTags.title = title;
    }

    public ServletBase(String sourceFile, boolean enableKeyCheck)
    {
        this(sourceFile);
        this.enableKeyCheck = enableKeyCheck;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        timeKeeper.start = System.currentTimeMillis();
        response.setStatus(statusCode);
        response.setContentType(contentType);
        response.setHeader("Server", "MCLegacy v" + MCLegacy.VERSION + " -- Christ is King of Kings");

        if (enableKeyCheck)
        {
            List<NameValuePair> params = URLEncodedUtils.parse("http://dummy/?" + request.getQueryString(), StandardCharsets.UTF_8);
            for (NameValuePair pair : params)
                if (pair.getName().equalsIgnoreCase("apiKey") && !MCLegacy.getInstance().getConfig().apiKey.equals(pair.getValue()))
                    response.getWriter().println(gson.toJson(new StandardResponse(false, "Invalid API key")));
        }

        if (contentLoader == null || templateLoader == null) return;
        String returnContent = templateProcessor.getCurrentContent();
        response.getWriter().println(returnContent);
    }

    public static class StandardResponse
    {
        public boolean success;
        public String message;

        public StandardResponse() {}

        public StandardResponse(boolean success, String message)
        {
            this.success = success;
            this.message = message;
        }
    }
}
