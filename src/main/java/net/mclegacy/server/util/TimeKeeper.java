package net.mclegacy.server.util;

public class TimeKeeper
{
    public long start;
    public long end;

    public int calc()
    {
        end = System.currentTimeMillis();
        return (int) (end - start);
    }
}
