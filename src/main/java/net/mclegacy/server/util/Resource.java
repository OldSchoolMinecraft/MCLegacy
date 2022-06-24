package net.mclegacy.server.util;

public class Resource
{
    private String content;

    public Resource(ResourceLoader resourceLoader)
    {
        this.content = resourceLoader.read();
    }

    public Resource(String content)
    {
        this.content = content;
    }

    public String getContent()
    {
        return content;
    }
}
