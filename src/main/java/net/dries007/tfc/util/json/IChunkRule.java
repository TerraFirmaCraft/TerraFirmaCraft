/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.json;

import java.util.function.BiPredicate;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.Helpers;

/**
 * Rules used for generation. Common usages are rainfall and temperature
 *
 * To add and register new rules, see {@link Serializer}
 */
public interface IChunkRule extends BiPredicate<IWorld, ChunkPos>
{
    class Serializer extends TypeBasedDeserializer<IChunkRule>
    {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer()
        {
            super("chunk rule");

            register(Helpers.identifier("rainfall"), RainfallRule::new);
            register(Helpers.identifier("temperature"), TemperatureRule::new);
            register(Helpers.identifier("biomes"), BiomeRule::new);
        }
    }
}
