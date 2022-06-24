package net.mclegacy.server.servlets.ws;

import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

public class ChatSystem extends WebSocketServlet
{
    public void configure(WebSocketServletFactory factory)
    {
        factory.getPolicy().setIdleTimeout(10000);
        factory.register(ChatSocket.class);
    }
}
