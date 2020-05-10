package net.dries007.tfc.config;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Wrapper for all TFC config endpoints. Individual configuration options are in each specific config class
 *
 * @see CommonConfig
 * @see ClientConfig
 * @see ServerConfig
 */
public final class TFCConfig
{
    public static final CommonConfig COMMON;
    public static final ClientConfig CLIENT;
    public static final ServerConfig SERVER;

    private static final ForgeConfigSpec COMMON_SPEC;
    private static final ForgeConfigSpec CLIENT_SPEC;
    private static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        Pair<CommonConfig, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
        Pair<ClientConfig, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
        Pair<ServerConfig, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(ServerConfig::new);

        COMMON_SPEC = common.getRight();
        CLIENT_SPEC = client.getRight();
        SERVER_SPEC = server.getRight();

        COMMON = common.getLeft();
        CLIENT = client.getLeft();
        SERVER = server.getLeft();
    }

    public static void init()
    {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, TFCConfig.COMMON_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, TFCConfig.CLIENT_SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, TFCConfig.SERVER_SPEC);
    }
}
