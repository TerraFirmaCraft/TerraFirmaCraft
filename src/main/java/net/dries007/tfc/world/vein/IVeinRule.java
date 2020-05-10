package net.dries007.tfc.world.vein;

import java.util.function.BiPredicate;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.json.TypeBasedDeserializer;

/**
 * Rule used for vein placement. Called once before vein generation.
 *
 * To add and register new rules, see {@link Serializer}
 */
public interface IVeinRule extends BiPredicate<IWorld, ChunkPos>
{
    class Serializer extends TypeBasedDeserializer<IVeinRule>
    {
        public static final Serializer INSTANCE = new Serializer();

        private Serializer()
        {
            super("vein rule");

            register(Helpers.identifier("rainfall"), RainfallRule::new);
            register(Helpers.identifier("temperature"), TemperatureRule::new);
        }
    }
}
