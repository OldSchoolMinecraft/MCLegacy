package net.mclegacy.server.util;

public class SystemConfiguration
{
    public String apiKey;
    public String contentDirectory;
    public JettyNetworkConfiguration jettyNetworkConfiguration;

    public SystemConfiguration()
    {
        apiKey = "CHANGEME :D";
        contentDirectory = "pages/";
        jettyNetworkConfiguration = new JettyNetworkConfiguration();
    }

    public class JettyNetworkConfiguration
    {
        public String bindAddress;
        public int bindPort;

        public JettyNetworkConfiguration()
        {
            bindAddress = "127.0.0.1";
            bindPort = 8080;
        }
    }
}
