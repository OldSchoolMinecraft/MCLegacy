package net.mclegacy.server.page.processing;

import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.ResourceLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ContentProcessor
{
    private static Logger log = Main.getLogger();

    private ResourceLoader resourceLoader;
    private StringBuilder sb = new StringBuilder();
    private final HashMap<String, String> tokenMap = new HashMap<>();
    private ArrayList<ProcessorHook> preProcessorHooks = new ArrayList<>();

    public ContentProcessor(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
        this.sb.append(resourceLoader.read());
    }

    public void mapToken(String key, String value)
    {
        tokenMap.put(key, value);
    }

    public void addPreProcessorHook(ProcessorHook hook)
    {
        preProcessorHooks.add(hook);
    }

    public void appendContent(String content)
    {
        sb.append("\n\n" + content);
    }

    public void appendResource(ResourceLoader resourceLoader)
    {
        appendContent(resourceLoader.read());
    }

    public String getCurrentContent()
    {
        //if (System.getenv().containsKey("MCL_DEBUG")) sb = new StringBuilder(resourceLoader.read());
        process();
        String content = sb.toString();
        return content;
    }

    private void process()
    {
        try
        {
            String currentContent = sb.toString();
            for (String key : tokenMap.keySet())
                if (currentContent.contains(String.format("${%s}", key)))
                    currentContent = currentContent.replace(String.format("${%s}", key), tokenMap.getOrDefault(key, ""));
            for (ProcessorHook hook : preProcessorHooks) currentContent = hook.process(currentContent);
            sb = new StringBuilder(currentContent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString()
    {
        return resourceLoader.toString();
    }
}
