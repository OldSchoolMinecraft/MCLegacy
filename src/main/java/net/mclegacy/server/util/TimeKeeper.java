package net.mclegacy.server.util;

public class TimeKeeper
{
    public long start;
    public long end;

    public void start()
    {
        start = System.currentTimeMillis();
    }

    public long calc()
    {
        end = System.currentTimeMillis();
        return (end - start);
    }
}
