package net.mclegacy.server.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class APISecurityManager
{
    public static String getKeyContent(String holderName) throws IOException
    {
        return new String(Files.readAllBytes(Paths.get(getKeyFile(holderName).getAbsolutePath())));
    }

    public static boolean checkKey(String holderName, String key) throws IOException
    {
        return new String(Files.readAllBytes(Paths.get(getKeyFile(holderName).getAbsolutePath()))).equals(key);
    }

    public static void deleteKey(String holderName)
    {
        getKeyFile(holderName).delete();
    }

    public static String registerNewKey(String holderName) throws IOException
    {
        File keyFile = getKeyFile(holderName);
        if (keyFile.exists()) return getKeyContent(holderName);
        keyFile.getParentFile().mkdirs();
        try(FileOutputStream fos = new FileOutputStream(keyFile); BufferedOutputStream bos = new BufferedOutputStream(fos))
        {
            String data = generateToken();
            byte[] bytes = data.getBytes();
            bos.write(bytes);
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    public static File getKeyFile(String holderName)
    {
        File security = new File("security");
        security.mkdirs();
        File keyFile = new File(security, "keys/api/" + holderName + ".key");
        return keyFile;
    }

    public static String generateToken()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
