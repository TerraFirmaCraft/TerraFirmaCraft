package net.dries007.tfc.objects.blocks.property;

import net.minecraft.block.state.IBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class FlowerProperty implements IUnlistedProperty<IBlockState>
{
    @Override
    public String getName()
    {
        return "flower";
    }

    @Override
    public boolean isValid(IBlockState state)
    {
        return true;
    }

    @Override
    public Class<IBlockState> getType()
    {
        return IBlockState.class;
    }

    @Override
    public String valueToString(IBlockState state)
    {
        return state.toString();
    }
}
