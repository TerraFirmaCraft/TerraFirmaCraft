/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.structure;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.util.registry.RegistryHolder;

public final class TFCStructureHooks
{
    public static final DeferredRegister<StructurePlacementType<?>> STRUCTURE_PLACEMENTS = DeferredRegister.create(Registries.STRUCTURE_PLACEMENT, TerraFirmaCraft.MOD_ID);

    public static final Id<ClimateStructurePlacement> CLIMATE = register("climate", ClimateStructurePlacement.PLACEMENT_CODEC);

    private static <T extends StructurePlacement> Id<T> register(String name, MapCodec<T> codec)
    {
        return new Id<>(STRUCTURE_PLACEMENTS.register(name, () -> () -> codec));
    }

    public record Id<T extends StructurePlacement>(DeferredHolder<StructurePlacementType<?>, StructurePlacementType<T>> holder)
        implements RegistryHolder<StructurePlacementType<?>, StructurePlacementType<T>> {}
}
