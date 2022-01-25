package net.mclegacy.server.page.processing;

import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.ResourceLoader;

import java.util.logging.Logger;

public class CSSContentProcessor extends ContentProcessor
{
    private static Logger log = Main.getLogger();

    public CSSContentProcessor(ResourceLoader resourceLoader)
    {
        super(resourceLoader);
    }

    public void addStylesheet(String href)
    {
        log.info(String.format("Stylesheet link added to content source: %s, %s", super.toString(), href));
        super.appendContent(String.format("<link rel=\"stylesheet\" href=\"%s\">", href));
    }

    public void addStylesheet(String href, String... attributes)
    {
        log.info(String.format("Stylesheet link added to content source: %s, %s", super.toString(), href));
        StringBuilder sb = new StringBuilder();
        if (attributes.length > 0) for (String attr : attributes) sb.append(attr + " ");
        super.appendContent(String.format("<link rel=\"stylesheet\" href=\"%s\" %s>", href, sb.toString().trim()));
    }
}
