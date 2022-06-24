package net.mclegacy.server.servlets.ws;

import com.google.gson.Gson;
import net.mclegacy.server.util.ws.WSClientIntent;
import net.mclegacy.server.util.ws.WSPacket;
import net.mclegacy.server.util.ws.WSPacketHandler;
import net.mclegacy.server.util.ws.WSRequestIntent;
import net.mclegacy.server.util.ws.handlers.WSHandshakeHandler;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

import java.io.IOException;
import java.util.HashMap;

public class ChatSocket
{
    private static final Gson gson = new Gson();
    private static final HashMap<String, String> sessionTracker = new HashMap<>();
    private static final HashMap<Integer, Class<? extends WSPacketHandler>> packetHandlers = new HashMap<>();

    static
    {
        packetHandlers.put(0, WSHandshakeHandler.class);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) throws IOException
    {
        System.out.println("WS connection from: " + session.getRemoteAddress().getAddress().getHostAddress());

        // ask for client intent (session token & channel)
        session.getRemote().sendString(gson.toJson(new WSRequestIntent(), WSRequestIntent.class));
    }

    @OnWebSocketMessage
    public void onMessage(String message)
    {
        WSPacket packet = gson.fromJson(message, WSPacket.class);
    }
}
