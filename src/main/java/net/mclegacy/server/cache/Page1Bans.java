package net.mclegacy.server.cache;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.main.Main;
import net.mclegacy.server.util.BanHolder;
import net.mclegacy.server.util.BanUtil;

import java.util.logging.Logger;

public class Page1Bans extends ObjectCache<BanHolder>
{
    private static Logger log = Main.getLogger();

    public Page1Bans(int cachePurgeInterval)
    {
        super("p1bans", cachePurgeInterval);
    }

    public void refresh(boolean force)
    {
        if (force) hardRefresh();
        super.refresh();
    }

    @Override
    public void refresh()
    {
        try
        {
            if (!timeToDie()) return;
            hardRefresh();
            super.refresh();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void hardRefresh()
    {
        try
        {
            clear();
            addAll(BanUtil.getBanRange(0, MCLegacy.getInstance().getConfig().bans.entriesPerPage));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
