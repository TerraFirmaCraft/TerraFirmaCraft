/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.plants;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TECropsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.CalendarTFC;

@ParametersAreNonnullByDefault
public abstract class BlockCropTFC extends BlockBush implements IGrowable
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

    private static final Map<ICrop, BlockCropTFC> MAP = new HashMap<>();

    public static BlockCropTFC get(ICrop crop)
    {
        return MAP.get(crop);
    }

    public final ICrop crop;

    public BlockCropTFC(ICrop crop)
    {
        super(Material.PLANTS);
        if (MAP.put(crop, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
        this.crop = crop;

        setDefaultState(getBlockState().getBaseState().withProperty(getStageProperty(), 0));
    }

    @Override
    public boolean canGrow(World worldIn, BlockPos pos, IBlockState state, boolean isClient)
    {
        // todo: advanced check involving light, nutrients, etc.
        return true;
    }

    @Override
    public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state)
    {
        // todo: remove
        return state.getValue(getStageProperty()) != crop.getMaxStage();
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
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(getStageProperty(), meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(getStageProperty());
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TECropsTFC te = Helpers.getTE(worldIn, pos, TECropsTFC.class);
        if (te != null)
        {
            te.onPlaced();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (crop.isPickable())
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
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getStageProperty());
    }

    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, net.minecraft.world.IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TECropsTFC();
    }

    public abstract PropertyInteger getStageProperty();

    @Nonnull
    public ICrop getCrop()
    {
        return crop;
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        super.updateTick(world, pos, state, random);
        if (!world.isRemote)
        {
            TECropsTFC te = Helpers.getTE(world, pos, TECropsTFC.class);
            if (te != null)
            {
                // todo: reevaluate this
                long hours = te.getHoursSincePlaced();
                if (hours > (((state.getValue(getStageProperty()) * crop.getGrowthTime()) + crop.getGrowthTime()) * CalendarTFC.HOURS_IN_DAY))
                {
                    grow(world, random, pos, state);
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

    @Nonnull
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos)
    {
        return EnumPlantType.Crop;
    }

    /**
     * todo: there must be a better way
     * The problem: The stage property needs to be different on each crop block, to account for the growth stages
     * This property is used before the constructor is finished (via createBlockState)
     * Thus, it can't be driven by anything in the BlockCropTFC constructor
     */
    public static class Simple5 extends BlockCropTFC
    {
        private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 4);

        public Simple5(ICrop crop)
        {
            super(crop);
        }

        @Override
        public PropertyInteger getStageProperty()
        {
            return STAGE;
        }
    }

    public static class Simple6 extends BlockCropTFC
    {
        private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 5);

        public Simple6(ICrop crop)
        {
            super(crop);
        }

        @Override
        public PropertyInteger getStageProperty()
        {
            return STAGE;
        }
    }

    public static class Simple7 extends BlockCropTFC
    {
        private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 6);

        public Simple7(ICrop crop)
        {
            super(crop);
        }

        @Override
        public PropertyInteger getStageProperty()
        {
            return STAGE;
        }
    }

    public static class Simple8 extends BlockCropTFC
    {
        private static final PropertyInteger STAGE = PropertyInteger.create("stage", 0, 7);

        public Simple8(ICrop crop)
        {
            super(crop);
        }

        @Override
        public PropertyInteger getStageProperty()
        {
            return STAGE;
        }
    }
}
