/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public abstract class BlockCropSimple extends BlockCropTFC
{
    public static BlockCropSimple create(ICrop crop, boolean isPickable)
    {
        PropertyInteger property = getStagePropertyForCrop(crop);

        if (property == null)
            throw new IllegalStateException("Invalid growthstage property " + (crop.getMaxStage() + 1) + " for crop");

        return new BlockCropSimple(crop, isPickable)
        {
            @Override
            public PropertyInteger getStageProperty()
            {
                return property;
            }
        };
    }

    private final boolean isPickable;

    protected BlockCropSimple(ICrop crop, boolean isPickable)
    {
        super(crop);
        this.isPickable = isPickable;

        setDefaultState(getBlockState().getBaseState().withProperty(getStageProperty(), 0).withProperty(WILD, false));
    }

    @Override
    public void grow(World worldIn, BlockPos pos, IBlockState state, Random random)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(getStageProperty()) < crop.getMaxStage())
            {
                worldIn.setBlockState(pos, state.withProperty(getStageProperty(), state.getValue(getStageProperty()) + 1), 2);
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (isPickable)
        {
            ItemStack foodDrop = crop.getFoodDrop(state.getValue(getStageProperty()));
            if (!foodDrop.isEmpty())
            {
                if (!worldIn.isRemote)
                {
                    worldIn.setBlockState(pos, this.getDefaultState().withProperty(getStageProperty(), state.getValue(getStageProperty()) - 2));
                    Helpers.spawnItemStack(worldIn, pos, crop.getFoodDrop(crop.getMaxStage()));
                }
                return true;
            }
        }
        return false;
    }
}
