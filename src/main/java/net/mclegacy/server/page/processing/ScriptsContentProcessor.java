package net.mclegacy.server.page.processing;

import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.ResourceLoader;

import java.util.logging.Logger;

public class ScriptsContentProcessor extends ContentProcessor
{
    private static final Logger log = Main.getLogger();

    public ScriptsContentProcessor(ResourceLoader resourceLoader)
    {
        super(resourceLoader);
    }

    public void addScript(String source)
    {
        log.info(String.format("Script added to content source: %s, %s", super.toString(), source));
        appendContent(String.format("<script src=\"%s\"></script>", source));
    }

    public void addScript(String source, String... attributes)
    {
        log.info(String.format("Script added to content source: %s, %s", super.toString(), source));
        StringBuilder sb = new StringBuilder();
        if (attributes.length > 0) for (String attr : attributes) sb.append(attr + " ");
        appendContent(String.format("<script src=\"%s\" %s></script>", source, sb.toString().trim()));
    }

    public void addScriptRaw(String code)
    {
        appendContent(String.format("<script>%s</script>", code));
    }
}
