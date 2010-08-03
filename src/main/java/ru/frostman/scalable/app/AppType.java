package ru.frostman.scalable.app;

import java.util.HashMap;

/**
 * @author Sergey "Frostman" Lukjanov
 *         (me@frostman.ru)
 */
public enum AppType {
    CLIENT,
    NETTY_CLIENT,
    FLOOD_CLIENT,
    TPC_SERVER,
    REACTOR_SERVER;

    private static final HashMap<String, AppType> abbrevs;

    public static AppType fromString(String str) {
        return abbrevs.get(str.toLowerCase());
    }

    static {
        abbrevs = new HashMap<String, AppType>();
        abbrevs.put("client", AppType.CLIENT);
        abbrevs.put("tpc_client", AppType.CLIENT);
        abbrevs.put("netty_client", AppType.NETTY_CLIENT);
        abbrevs.put("flood_client", AppType.FLOOD_CLIENT);
        abbrevs.put("tpc_server", AppType.TPC_SERVER);
        abbrevs.put("reactor_server", AppType.REACTOR_SERVER);
    }
}
