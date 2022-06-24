package net.mclegacy.server.util.ws.handlers;

import net.mclegacy.server.util.ws.WSPacketHandler;
import net.mclegacy.server.util.ws.packets.WSHandshakePacket;

public class WSHandshakeHandler extends WSPacketHandler
{
    @Override
    public void onReceived(String rawContent)
    {
        WSHandshakePacket packet = gson.fromJson(rawContent, WSHandshakePacket.class);

        //TODO: validate username & userToken with database
    }
}
