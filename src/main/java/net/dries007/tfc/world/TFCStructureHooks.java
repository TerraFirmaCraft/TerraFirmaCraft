/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import net.dries007.tfc.TerraFirmaCraft;

public class TFCStructureHooks
{
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENT_TYPES = DeferredRegister.create(Registry.STRUCTURE_PLACEMENT_TYPE_REGISTRY, TerraFirmaCraft.MOD_ID);

    public static final RegistryObject<StructurePlacementType<TFCStructurePlacement>> TFC_STRUCTURE_PLACEMENT = registerPlacement("chunk_data", TFCStructurePlacement.CODEC);

    private static <P extends StructurePlacement> RegistryObject<StructurePlacementType<P>> registerPlacement(String name, Codec<P> codec)
    {
        return STRUCTURE_PLACEMENT_TYPES.register(name, () -> () -> codec);
    }

}
