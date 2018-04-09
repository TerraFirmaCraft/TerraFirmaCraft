package net.dries007.tfc.objects.blocks;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;

public class BlockLogTFC extends BlockLog
{
    public final Wood wood;

    public BlockLogTFC(Wood wood)
    {
        this.wood = wood;
        this.setDefaultState(this.blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LOG_AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LOG_AXIS, EnumAxis.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LOG_AXIS).ordinal();
    }

    public enum Wood
    {
        ASH(12600, 5970),//Black Ash
        ASPEN(9230, 5220),//Black Poplar
        BIRCH(12300, 5690),//Paper Birch
        CHESTNUT(8600, 5320),//Sweet Chestnut
        DOUGLAS_FIR(12500, 6950),//Douglas Fir
        HICKORY(17100, 9040),//Bitternut Hickory
        MAPLE(13400, 6540),//Red Maple
        OAK(14830, 7370),//White Oak
        PINE(14500, 8470),//Longleaf Pine
        SEQUOIA(8950, 5690),//Redwood
        SPRUCE(8640, 4730),//White Spruce
        SYCAMORE(10000, 5380),//American Sycamore
        WHITE_CEDAR(6500, 3960),//Northern White Cedar
        WILLOW(8150, 3900),//White Willow
        KAPOK(14320, 6690),//Chakte Kok - No data on kapok so went with the brazilian Chakte Kok(Redheart Tree)
        ACACIA(12620, 7060), //Acacia Koa
        ROSEWOOD(19570, 9740),//Brazilian Rosewood
        BLACKWOOD(15020, 7770),//Australian Blackwood
        PALM(12970, 9590);//Red Palm

        public final int bend;
        public final int compression;

        private BlockLogTFC ref;

        Wood(int bend, int compression)
        {
            this.bend = bend;
            this.compression = compression;
        }
    }
}
