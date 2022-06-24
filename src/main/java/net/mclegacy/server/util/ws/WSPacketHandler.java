package net.mclegacy.server.util.ws;

import com.google.gson.Gson;

public abstract class WSPacketHandler
{
    protected static final Gson gson = new Gson();

    public abstract void onReceived(String rawContent);
}
