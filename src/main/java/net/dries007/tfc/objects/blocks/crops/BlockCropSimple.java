/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.crops;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TEPlacedItem;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;
import net.dries007.tfc.world.classic.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public abstract class BlockCropSimple extends BlockCropTFC
{
    private static final AxisAlignedBB[] CROPS_AABB = new AxisAlignedBB[] {
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.125D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.25D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.375D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.5D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.625D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.75D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 0.875D, 0.875D),
        new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D)
    };

    private final boolean isPickable;

    protected BlockCropSimple(ICrop crop, boolean isPickable)
    {
        super(crop);
        this.isPickable = isPickable;

        setDefaultState(getBlockState().getBaseState().withProperty(getStageProperty(), 0).withProperty(WILD, false));
    }

    @Override
    public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(getStageProperty()) < crop.getMaxStage())
            {
                worldIn.setBlockState(pos, state.withProperty(getStageProperty(), state.getValue(getStageProperty()) + 1), 2);
            }
            TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
            if (tile != null)
            {
                tile.resetCounter();
            }
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(getStageProperty(), meta & 7).withProperty(WILD, meta > 7);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(getStageProperty()) + (state.getValue(WILD) ? 8 : 0);
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

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.clear();
        drops.add(new ItemStack(ItemSeedsTFC.get(crop)));

        ItemStack foodDrop = crop.getFoodDrop(state.getValue(getStageProperty()));
        if (!foodDrop.isEmpty())
        {
            drops.add(foodDrop);
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getStageProperty(), WILD);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);
        if (!world.isRemote)
        {
            float temp = ClimateTFC.getTemp(world, pos);
            float rainfall = ChunkDataTFC.getRainfall(world, pos);
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                long hours = te.getTicksSinceUpdate() / CalendarTFC.TICKS_IN_HOUR;
                if (hours > crop.getGrowthTime() && crop.isValidForGrowth(temp, rainfall))
                {
                    grow(world, random, pos, state);
                    te.resetCounter();
                }
            }

            if (!crop.isValidConditions(temp, rainfall))
            {
                world.setBlockState(pos, BlocksTFC.PLACED_ITEM_FLAT.getDefaultState());
                TEPlacedItem tilePlaced = Helpers.getTE(world, pos, TEPlacedItem.class);
                if (tilePlaced != null)
                {
                    IItemHandler cap = tilePlaced.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (cap != null)
                    {
                        cap.insertItem(0, new ItemStack(ItemSeedsTFC.get(crop)), false);
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPS_AABB[state.getValue(getStageProperty())];
    }


    protected abstract PropertyInteger getStageProperty();
}
