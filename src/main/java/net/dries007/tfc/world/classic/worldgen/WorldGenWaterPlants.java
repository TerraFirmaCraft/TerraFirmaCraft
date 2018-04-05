package net.dries007.tfc.world.classic.worldgen;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
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
        final IBlockState block = world.getBlockState(pos);
        if (!BlocksTFC.isWater(block) || !world.isAirBlock(pos.add(0, 1, 0))) return false;

        //todo: this can be done better
        //How far underwater are we going
        int depthCounter = 1;
        //Effectively makes sea grass grow less frequently as depth increases beyond 6 m.
        boolean isTooDeep = false;
        int maxDepth = block != FRESH_WATER ? 10 : 4;

        //travel down until a solid surface is reached
        int y = 0;
        while (BlocksTFC.isWater(world.getBlockState(pos.add(0, --y, 0))) && !isTooDeep)
        {
            depthCounter++;
            if(depthCounter > maxDepth)
            {
                //If depthCounter reaches 11, automatically prevents plants from growing
                isTooDeep = true;
            }
        }

        if (isTooDeep || depthCounter <= 0) return false;

        // todo: debug

        world.setBlockState(pos, Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.LIME), 0x02);
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
