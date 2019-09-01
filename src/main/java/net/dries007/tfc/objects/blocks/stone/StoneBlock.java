/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.stone;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Rock;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class StoneBlock extends Block
{
    protected final Rock rock;

    public StoneBlock(Rock rock, Properties properties)
    {
        super(properties);

        this.rock = rock;
        //switch (type)
        //{
        //    case BRICKS:
        //    case RAW:
        //        setSoundType(SoundType.STONE);
        //        setHardness(2.0F).setResistance(10.0F);
        //        setHarvestLevel("pickaxe", 0);
        //        break;
        //    case COBBLE:
        //    case SMOOTH:
        //        setSoundType(SoundType.STONE);
        //        setHardness(1.5F).setResistance(10.0F);
        //        setHarvestLevel("pickaxe", 0);
        //        break;
        //    case SAND:
        //        setSoundType(SoundType.SAND);
        //        setHardness(0.7F);
        //        setHarvestLevel("shovel", 0);
        //        break;
        //    case DIRT:
        //    case PATH:
        //    case FARMLAND:
        //        setSoundType(SoundType.GROUND);
        //        setHardness(1.0F);
        //        setHarvestLevel("shovel", 0);
        //        break;
        //    case GRAVEL:
        //    case CLAY:
        //        setSoundType(SoundType.GROUND);
        //        setHardness(0.8F);
        //        setHarvestLevel("shovel", 0);
        //        break;
        //    case CLAY_GRASS:
        //    case GRASS:
        //    case DRY_GRASS:
        //        setSoundType(SoundType.PLANT);
        //        setHardness(1.1F);
        //        setHarvestLevel("shovel", 0);
        //        break;
        //}
        //OreDictionaryHelper.registerRockType(this, type, rock);
    }

    public Rock getRock()
    {
        return rock;
    }

    //protected void onRockSlide(World world, BlockPos pos)
    //{
    //    switch (type)
    //    {
    //        case GRAVEL:
    //            //world.playSound(null, pos, TFCSounds.DIRT_SLIDE_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
    //            break;
    //        case COBBLE:
    //            //world.playSound(null, pos, TFCSounds.ROCK_SLIDE_SHORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
    //    }
    //}
}
