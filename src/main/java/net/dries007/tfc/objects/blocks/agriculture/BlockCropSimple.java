/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillTier;
import net.dries007.tfc.util.skills.SkillType;

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
    public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack)
    {
        // todo: in 1.14 change to use the apply skill loot table
        super.harvestBlock(worldIn, player, pos, state, te, stack);

        ItemStack seedStack = new ItemStack(ItemSeedsTFC.get(crop));
        ItemStack foodStack = crop.getFoodDrop(state.getValue(getStageProperty()));
        SimpleSkill skill = CapabilityPlayerData.getSkill(player, SkillType.AGRICULTURE);
        if (skill != null)
        {
            foodStack.setCount(2 + (int) (skill.getTotalLevel()));
            if (skill.getTier() == SkillTier.EXPERT && RANDOM.nextInt(4) == 0)
            {
                seedStack.setCount(2);
            }
            skill.add(0.04f);
        }
        if (!seedStack.isEmpty())
        {
            InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, seedStack);
        }
        if (!foodStack.isEmpty())
        {
            InventoryHelper.spawnItemStack(worldIn, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, foodStack);
        }
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, getStageProperty(), WILD);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        drops.clear();
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return CROPS_AABB[state.getValue(getStageProperty())];
    }
}
