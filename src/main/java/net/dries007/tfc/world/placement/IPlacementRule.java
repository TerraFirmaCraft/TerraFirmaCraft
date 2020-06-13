/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.placement;

import java.util.function.BiPredicate;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.json.TypeBasedDeserializer;

/**
 * Generic JSON based rule class for world generation
 *
 * To add and register new rules, see {@link Serializer}
 */
public interface IPlacementRule extends BiPredicate<IWorld, BlockPos>
{
    class Serializer extends TypeBasedDeserializer<IPlacementRule>
    {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer()
        {
            super("placement rule");

            register(Helpers.identifier("rainfall"), RainfallRule::new);
            register(Helpers.identifier("temperature"), TemperatureRule::new);
            register(Helpers.identifier("biomes"), BiomeRule::new);
        }
    }
}
