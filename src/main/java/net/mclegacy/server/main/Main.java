package net.mclegacy.server.main;

import net.mclegacy.server.MCLegacy;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Main
{
    private static Logger log = Logger.getLogger("MCLegacy");

    public static Logger getLogger()
    {
        return log;
    }

    public static void main(String[] args)
    {
        try (InputStream stream = Main.class.getResourceAsStream("/logging.properties"))
        {
            LogManager.getLogManager().readConfiguration(stream);
            log = Logger.getLogger(MCLegacy.class.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new MCLegacy().init();

        log.info("Started");
    }
}
