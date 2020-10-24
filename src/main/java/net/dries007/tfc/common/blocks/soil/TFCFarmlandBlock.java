package net.dries007.tfc.common.blocks.soil;

import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;
import net.dries007.tfc.common.blocks.IForgeBlockProperties;
import net.dries007.tfc.common.tileentity.FarmlandTileEntity;
import net.dries007.tfc.util.Helpers;

public class TFCFarmlandBlock extends FarmlandBlock implements ISoilBlock, IForgeBlockProperties
{
    public static final IntegerProperty MOISTURE = BlockStateProperties.MOISTURE;
    private final ForgeBlockProperties properties;

    public TFCFarmlandBlock(ForgeBlockProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    public BlockState getDirt(IWorld world, BlockPos pos, BlockState state)
    {
        return Helpers.getTileEntityOrThrow(world, pos, FarmlandTileEntity.class).getDirt();
    }

    @Override
    public ForgeBlockProperties getForgeProperties()
    {
        return properties;
    }
}
