/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.placement;

import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCPlacements
{
    public static final DeferredRegister<Placement<?>> PLACEMENTS = new DeferredRegister<>(ForgeRegistries.DECORATORS, MOD_ID);

    public static final RegistryObject<AtFlatSurfaceWithChance> FLAT_SURFACE_WITH_CHANCE = PLACEMENTS.register("at_flat_surface_with_chance", AtFlatSurfaceWithChance::new);
}
