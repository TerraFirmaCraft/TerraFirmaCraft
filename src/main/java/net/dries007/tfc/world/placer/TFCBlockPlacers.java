/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placer;

import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacer;
import net.minecraft.world.level.levelgen.feature.blockplacers.BlockPlacerType;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCBlockPlacers
{
    public static final DeferredRegister<BlockPlacerType<?>> BLOCK_PLACERS = DeferredRegister.create(ForgeRegistries.BLOCK_PLACER_TYPES, MOD_ID);

    public static final RegistryObject<BlockPlacerType<TallPlantPlacer>> TALL_PLANT = register("tall_plant", TallPlantPlacer.CODEC);
    public static final RegistryObject<BlockPlacerType<EmergentPlantPlacer>> EMERGENT_PLANT = register("emergent_plant", EmergentPlantPlacer.CODEC);
    public static final RegistryObject<BlockPlacerType<KelpTreePlacer>> KELP_TREE = register("kelp_tree", KelpTreePlacer.CODEC);

    public static final RegistryObject<BlockPlacerType<RandomPropertyPlacer>> RANDOM_PROPERTY = register("random_property", RandomPropertyPlacer.CODEC);

    private static <P extends BlockPlacer> RegistryObject<BlockPlacerType<P>> register(String name, Codec<P> codec)
    {
        return BLOCK_PLACERS.register(name, () -> new BlockPlacerType<>(codec));
    }
}
