package net.mclegacy.server.servlets.hosting;

import net.mclegacy.server.servlets.ServletBase;

public class HostingIndex extends ServletBase
{
    private int stock = 1;

    public HostingIndex()
    {
        super("hosting/index.html", "shared/template.html");

        scriptsProcessor.addScript("/js/hosting/index/orderSetup.js");
        contentProcessor.addPreProcessorHook((pageContent -> pageContent.replace("${STOCK}", String.valueOf(stock))));
    }
}
