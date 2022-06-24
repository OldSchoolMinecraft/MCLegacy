package net.mclegacy.server.cache;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.main.Main;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

public abstract class ObjectCache<T>
{
    private static final Logger log = Main.getLogger();

    private ArrayList<T> cache = new ArrayList<>();
    private Instant lastRefreshTime;
    private final int minutesToLive;
    private final String cacheAlias;

    public ObjectCache(String cacheAlias, int minutesToLive)
    {
        this.cacheAlias = cacheAlias;
        this.minutesToLive = minutesToLive;
        MCLegacy.getInstance().getCacheManager().addCache(cacheAlias, this);
        refresh();
    }

    public boolean timeToDie()
    {
        if (lastRefreshTime == null) lastRefreshTime = Instant.now().minus(MCLegacy.getInstance().getConfig().bans.cachePurgeInterval + 5, ChronoUnit.MINUTES);
        return Duration.between(lastRefreshTime, Instant.now()).toMinutes() > minutesToLive;
    }

    public void refresh()
    {
        lastRefreshTime = Instant.now();
        if (System.getenv().containsKey("MCL_DEBUG"))
            log.warning("Cache purged: " + cacheAlias);
    }

    public final void clear()
    {
        cache.clear();
    }

    public final void add(T object)
    {
        cache.add(object);
    }

    public final void addAll(Collection<T> objects)
    {
        cache.addAll(objects);
    }

    public final void remove(T object)
    {
        cache.remove(object);
    }

    public final ArrayList<T> getAll()
    {
        return cache;
    }
}
