package net.mclegacy.server.cache;

import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;

import java.util.logging.Logger;

public class PlayerIssuedBans extends ObjectCache<BanHolder>
{
    private static final Logger log = Main.getLogger();

    private String username;

    public PlayerIssuedBans(String username, int minutesToLive)
    {
        super("issuedBans-" + username, minutesToLive);
        this.username = username;
    }

    @Override
    public void refresh()
    {
        try
        {
            if (!timeToDie()) return;
            if (username == null) return;
            clear();
            addAll(BanUtil.getIssuedBans(username));
            super.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
