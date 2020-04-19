package net.dries007.tfc.objects.blocks.rock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;

import net.dries007.tfc.api.Ore;

public class GradedOreBlock extends TFCOreBlock
{
    public static final EnumProperty<Ore.Grade> GRADE = EnumProperty.create("grade", Ore.Grade.class);

    public GradedOreBlock()
    {
        setDefaultState(stateContainer.getBaseState().with(GRADE, Ore.Grade.NORMAL));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(GRADE);
    }
}
