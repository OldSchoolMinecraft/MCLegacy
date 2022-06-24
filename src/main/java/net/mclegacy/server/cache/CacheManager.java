package net.mclegacy.server.cache;

import java.util.HashMap;

public class CacheManager
{
    private HashMap<String, ObjectCache> cacheMap = new HashMap<>();

    public void addCache(String alias, ObjectCache cache)
    {
        cacheMap.put(alias, cache);
    }

    public void removeCache(String alias)
    {
        cacheMap.remove(alias);
    }

    public ObjectCache getCache(String alias)
    {
        if (!cacheMap.containsKey(alias)) return null;
        return cacheMap.get(alias);
    }
}
