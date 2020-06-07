/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.carver;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.api.Rock;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.objects.types.RockManager;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCWorldCarvers
{
    public static final DeferredRegister<WorldCarver<?>> CARVERS = new DeferredRegister<>(ForgeRegistries.WORLD_CARVERS, MOD_ID);

    public static final RegistryObject<WorldCarver<ProbabilityConfig>> CAVE = CARVERS.register("cave", () -> new TFCCaveWorldCarver(ProbabilityConfig::deserialize, 256));
    public static final RegistryObject<WorldCarver<ProbabilityConfig>> CANYON = CARVERS.register("canyon", () -> new TFCCanyonWorldCarver(ProbabilityConfig::deserialize));
    public static final RegistryObject<WorldCarver<ProbabilityConfig>> UNDERWATER_CAVE = CARVERS.register("underwater_cave", () -> new TFCUnderwaterCaveWorldCarver(ProbabilityConfig::deserialize));
    public static final RegistryObject<WorldCarver<ProbabilityConfig>> UNDERWATER_CANYON = CARVERS.register("underwater_canyon", () -> new TFCUnderwaterCanyonWorldCarver(ProbabilityConfig::deserialize));

    /**
     * Vanilla carvers have a set of blocks they think are valid for carving
     * We need to add our own to that list.
     */
    public static Set<Block> fixCarvableBlocksList(Set<Block> original)
    {
        Set<Block> carvableBlocks = new HashSet<>(original);
        for (Rock rock : RockManager.INSTANCE.getValues())
        {
            carvableBlocks.add(rock.getBlock(Rock.BlockType.RAW));
        }
        for (SoilBlockType.Variant variant : SoilBlockType.Variant.values())
        {
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(variant).get());
            carvableBlocks.add(TFCBlocks.SOIL.get(SoilBlockType.GRASS).get(variant).get());
        }
        return carvableBlocks;
    }
}
