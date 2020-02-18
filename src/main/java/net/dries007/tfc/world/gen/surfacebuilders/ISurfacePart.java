package net.dries007.tfc.world.gen.surfacebuilders;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;

import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.TFCBlocks;
import net.dries007.tfc.objects.blocks.soil.SoilBlockType;
import net.dries007.tfc.world.gen.rock.RockData;

public interface ISurfacePart
{
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    static ISurfacePart soil(SoilBlockType soil)
    {
        return (rockData, x, z) -> TFCBlocks.SOIL.get(soil).get(rockData.getSoil(x, z)).get().getDefaultState();
    }

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    static ISurfacePart sand()
    {
        return (rockData, x, z) -> TFCBlocks.SAND.get(rockData.getSand(x, z)).get().getDefaultState();
    }

    @Nonnull
    static ISurfacePart rock(Rock.BlockType type)
    {
        return (rockData, x, z) -> rockData.getTopRock(x, z).getBlock(type).getDefaultState();
    }

    BlockState get(RockData rockData, int x, int z);
}
