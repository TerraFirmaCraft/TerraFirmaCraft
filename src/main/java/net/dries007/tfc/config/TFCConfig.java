/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.config;

import java.util.function.Function;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Central point for all configuration options
 * <ul>
 *     <li><strong>Common</strong> is used for options which need to be world-agnostic, or are independent of side</li>
 *     <li><strong>Server</strong> is used for generic mechanics options, stuff which is synchronized but server priority, etc.</li>
 *     <li><strong>Client</strong> is used for purely graphical or client side only options</li>
 * </ul>
 */
public final class TFCConfig
{
    public static final CommonConfig COMMON = register(CommonConfig::new, ConfigBuilder.CommonValue::new, "common");
    public static final ClientConfig CLIENT = register(ClientConfig::new, ConfigBuilder.ClientValue::new, "client");
    public static final ServerConfig SERVER = register(ServerConfig::new, ConfigBuilder.ServerValue::new, "server");

    private static <C extends BaseConfig> C register(Function<ConfigBuilder, C> factory, ConfigBuilder.Factory value, String prefix)
    {
        final Pair<C, ModConfigSpec> pair = new ModConfigSpec.Builder()
            .configure(builder -> factory.apply(new ConfigBuilder(builder, value, prefix)));
        pair.getKey().updateSpec(pair.getValue());
        return pair.getKey();
    }
}