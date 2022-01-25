package net.mclegacy.server.util;

import net.mclegacy.server.MCLegacy;
import net.mclegacy.server.page.processing.ContentProcessor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * Basically does the same stuff as MFile but much cleaner.
 *
 * @author moderator_man
 * @since 1.0
 */
public class ResourceLoader
{
    private String resourceLocation;
    private String basePath;
    private int lastReadBytesTotal = 0;
    private boolean exists;
    private BasicFileAttributes attr;

    public ResourceLoader(String resourceLocation)
    {
        this.resourceLocation = resourceLocation;
        this.basePath = MCLegacy.getInstance().getConfig().contentDirectory;
        this.exists = new File(basePath + resourceLocation).exists();
        if (!exists) return;
        try
        {
            this.attr = Files.readAttributes(Paths.get(new File(basePath + resourceLocation).getAbsolutePath()), BasicFileAttributes.class);
        } catch (IOException e) { e.printStackTrace(); }
    }

    public ResourceLoader(String resourceLocation, String basePath)
    {
        this(resourceLocation);
        this.basePath = basePath;
    }

    public BasicFileAttributes getAttributes()
    {
        return attr;
    }

    public boolean isGood()
    {
        return  exists &&
                attr != null &&
                resourceLocation != null &&
                basePath != null &&
                !resourceLocation.isEmpty() &&
                !basePath.isEmpty();
    }

    public InputStream getInputStream()
    {
        return getClass().getResourceAsStream(basePath + resourceLocation);
    }

    public FileInputStream getFileInputStream()
    {
        try
        {
            return new FileInputStream(basePath + resourceLocation);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public String read()
    {
        return readAsString(getFileInputStream());
    }

    private String readAsString(InputStream is)
    {
        lastReadBytesTotal = 0;
        StringBuilder sb = new StringBuilder();
        if (is == null) return "buildString failed: is == null"; // sanity check
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
        {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) > 0)
            {
                String readData = String.valueOf(buf, 0, numRead);
                sb.append(readData);
                lastReadBytesTotal += numRead;
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    public boolean directTo(InputStream is, Writer writer)
    {
        lastReadBytesTotal = 0;
        if (is == null) return false; // sanity check
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is)))
        {
            char[] buf = new char[1024];
            int numRead = 0;
            while ((numRead = reader.read(buf)) > 0)
            {
                String readData = String.valueOf(buf, 0, numRead);
                writer.append(readData);
                lastReadBytesTotal += numRead;
            }
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getLastReadBytesTotal()
    {
        return lastReadBytesTotal;
    }

    @Override
    public String toString()
    {
        return resourceLocation;
    }
}
