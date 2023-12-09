/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import net.dries007.tfc.util.Helpers;

/**
 * Central point for all configuration options
 * - Common is used for options which need to be world-agnostic, or are independent of side
 * - Server is used for generic mechanics options, stuff which is synchronized but server priority, etc.
 * - Client is used for purely graphical or client side only options
 */
public final class TFCConfig
{
    public static final CommonConfig COMMON = register(ModConfig.Type.COMMON, CommonConfig::new, "common").getKey();
    public static final ClientConfig CLIENT = register(ModConfig.Type.CLIENT, ClientConfig::new, "client").getKey();
    public static final ServerConfig SERVER;

    private static final ForgeConfigSpec SERVER_SPEC;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> pair = register(ModConfig.Type.SERVER, ServerConfig::new, "server");

        SERVER = pair.getKey();
        SERVER_SPEC = pair.getRight();
    }

    public static void init() {}

    public static boolean isServerConfigLoaded()
    {
        return SERVER_SPEC.isLoaded();
    }

    private static <C> Pair<C, ForgeConfigSpec> register(ModConfig.Type type, Function<ConfigBuilder, C> factory, String prefix)
    {
        final Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> factory.apply(new ConfigBuilder(builder, prefix)));
        if (!Helpers.BOOTSTRAP_ENVIRONMENT) ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair;
    }
}