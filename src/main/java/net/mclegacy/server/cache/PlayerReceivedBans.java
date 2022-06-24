package net.mclegacy.server.cache;

import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;

import java.util.logging.Logger;

public class PlayerReceivedBans extends ObjectCache<BanHolder>
{
    private static final Logger log = Main.getLogger();

    private String username;

    public PlayerReceivedBans(String username, int minutesToLive)
    {
        super("receivedBans-" + username, minutesToLive);
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
            addAll(BanUtil.getReceivedBans(username));
            super.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
