package net.mclegacy.server.util.ws;

public class WSPacket
{
    public int packetID;
    public String packetContent;

    public WSPacket() {}

    public WSPacket(int packetID, String packetContent)
    {
        this.packetID = packetID;
        this.packetContent = packetContent;
    }
}
