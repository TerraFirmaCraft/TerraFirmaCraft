package net.dries007.tfc.world.classic.worldgen;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.block.BlockColored;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;

import java.util.Random;

import static net.dries007.tfc.world.classic.ChunkGenTFC.FRESH_WATER;

public class WorldGenWaterPlants extends WorldGenerator
{
    @Override
    public boolean generate(World world, Random rng, final BlockPos pos)
    {
        if (!BlocksTFC.isWater(world.getBlockState(pos.add(0, -1, 0))) || !world.isAirBlock(pos)) return false;

        int depthCounter = 2;
        int maxDepth = world.getBlockState(pos) != FRESH_WATER ? 10 : 4; // todo: add some rng? biome variance? temp variance?
        while (BlocksTFC.isWater(world.getBlockState(pos.add(0, -depthCounter, 0))))
        {
            depthCounter++;
            if (depthCounter > maxDepth) return false;
        }
        // todo: replace with actual plant
        world.setBlockState(pos.add(0, -depthCounter + 1, 0), Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME), 0x02);
        /*
        int meta = world.getBlockMetadata(x, y, z);
        Block oldBlock = world.getBlock(x, y, z);
        if (TFC_Core.isSoilOrGravel(oldBlock) || TFC_Core.isSand(oldBlock))
        {
            world.setBlock(x, y, z, this.plantBlock, meta, 2);
            TileEntity te = world.getTileEntity(x, y, z);
            if(te instanceof TEWaterPlant){
                ((TEWaterPlant)te).setBlock(oldBlock);
            }
        }
        */
        return true;
    }
}
