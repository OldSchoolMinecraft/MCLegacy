package net.mclegacy.server.util;

public class BanHolder
{
    public String server, username, reason, expiration, issued_by, issued_at;

    public BanHolder(String server, String username, String reason, String expiration, String issued_by, String issued_at)
    {
        this.server = server;
        this.username = username;
        this.reason = reason;
        this.expiration = expiration;
        this.issued_by = issued_by;
        this.issued_at = issued_at;
    }
}