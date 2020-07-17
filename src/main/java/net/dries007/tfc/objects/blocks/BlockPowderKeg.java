/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.te.TEPowderKeg;
import net.dries007.tfc.util.Helpers;

/**
 * Powderkeg is an inventory that preserves the contents when sealed
 * It can be picked up and keeps it's inventory
 * Sealed state is stored in a block state property, and cached in the TE (for gui purposes)
 */
@ParametersAreNonnullByDefault
public class BlockPowderKeg extends Block implements IItemSize, ILightableBlock
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");

    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    /**
     * Used to update the keg seal state and the TE, in the correct order
     */
    public static void togglePowderKegSeal(World world, BlockPos pos)
    {
        TEPowderKeg tile = Helpers.getTE(world, pos, TEPowderKeg.class);
        if (tile != null)
        {
            IBlockState state = world.getBlockState(pos);
            boolean previousSealed = state.getValue(SEALED);
            world.setBlockState(pos, state.withProperty(SEALED, !previousSealed));
            tile.setSealed(!previousSealed);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public BlockPowderKeg()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHardness(2F);
        setTickRandomly(true);

        setDefaultState(blockState.getBaseState().withProperty(LIT, false).withProperty(SEALED, false));
    }

    public void trigger(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase igniter)
    {
        if (!worldIn.isRemote)
        {
            TEPowderKeg te = Helpers.getTE(worldIn, pos, TEPowderKeg.class);
            if (te != null && state.getValue(SEALED) && state.getValue(LIT) && te.getStrength() > 0) //lit state set before called
            {
                worldIn.setBlockState(pos, state);
                te.setLit(true);
                te.setIgniter(igniter);
            }
        }
    }

    @Override
    @Nonnull
    public Size getSize(ItemStack stack)
    {
        return stack.getTagCompound() == null ? Size.VERY_LARGE : Size.HUGE; // Causes overburden if sealed
    }

    @Override
    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    // Cannot use onExplosionDestroy like TNT because all state has already been lost at that point.

    @Override
    public boolean canStack(@Nonnull ItemStack stack)
    {
        return stack.getTagCompound() == null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(SEALED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(SEALED) ? 1 : 0;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rng)
    {
        if (!state.getValue(LIT))
            return;

        TEPowderKeg te = Helpers.getTE(world, pos, TEPowderKeg.class);
        if (te != null)
        {
            int fuse = te.getFuse();
            if (rng.nextInt(6) == 0 && fuse > 20)
            {
                world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, rng.nextFloat() * 1.3F + 0.3F / fuse, false);
            }
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + 0.625, pos.up().getY() + 0.125, pos.getZ() + 0.375, 0.0D, 1.0D + 1.0D / fuse, 0.0D);
        }
    }

    /**
     * Called after a player destroys this Block - the position pos may no longer hold the state indicated.
     */
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        trigger(worldIn, pos, state, null);
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (world.isBlockPowered(pos) || world.getBlockState(fromPos).getMaterial() == Material.FIRE)
        {
            onPlayerDestroy(world, pos, state.withProperty(LIT, true));
        }
        else if (state.getValue(LIT) && pos.up().equals(fromPos) && world.getBlockState(fromPos).getMaterial() == Material.WATER)
        {
            TEPowderKeg tile = Helpers.getTE(world, pos, TEPowderKeg.class);
            if (tile != null)
            {
                world.setBlockState(pos, state.withProperty(LIT, false));
                tile.setLit(false);
            }
        } // do not care otherwise, as canStay may be violated by an explosion, which we want to trigger off of
    }

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        super.onBlockAdded(worldIn, pos, state);
        if (worldIn.isBlockPowered(pos))
        {
            onPlayerDestroy(worldIn, pos, state.withProperty(LIT, true));
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEPowderKeg tile = Helpers.getTE(worldIn, pos, TEPowderKeg.class);
        if (tile != null && !tile.isLit())
        {
            tile.onBreakBlock(worldIn, pos, state);
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        return canStay(world, pos);
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            TEPowderKeg te = Helpers.getTE(worldIn, pos, TEPowderKeg.class);
            if (te != null)
            {
                if (heldItem.isEmpty() && state.getValue(LIT))
                {
                    worldIn.setBlockState(pos, state.withProperty(LIT, false));
                    te.setLit(false);
                }
                else if (heldItem.isEmpty() && playerIn.isSneaking())
                {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 0.85F);
                    togglePowderKegSeal(worldIn, pos);
                }
                else if (state.getValue(SEALED) && BlockTorchTFC.canLight(heldItem))
                {
                    trigger(worldIn, pos, state.withProperty(LIT, true), playerIn);

                    if (heldItem.getItem().isDamageable())
                    {
                        heldItem.damageItem(1, playerIn);
                    }
                    else if (!playerIn.capabilities.isCreativeMode)
                    {
                        heldItem.shrink(1);
                    }
                }
                else
                {
                    TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.POWDERKEG);
                }
            }
        }
        return true;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (!worldIn.isRemote && entityIn instanceof EntityArrow)
        {
            EntityArrow entityarrow = (EntityArrow) entityIn;

            if (entityarrow.isBurning())
            {
                trigger(worldIn, pos, worldIn.getBlockState(pos).withProperty(LIT, true), entityarrow.shootingEntity instanceof EntityLivingBase ? (EntityLivingBase) entityarrow.shootingEntity : null);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        // If the keg was sealed, then copy the contents from the item
        if (!worldIn.isRemote)
        {
            NBTTagCompound nbt = stack.getTagCompound();
            if (nbt != null)
            {
                TEPowderKeg te = Helpers.getTE(worldIn, pos, TEPowderKeg.class);
                if (te != null)
                {
                    worldIn.setBlockState(pos, state.withProperty(SEALED, true));
                    te.readFromItemTag(nbt);
                }
            }
        }
    }

    /**
     * Return whether this block can drop from an explosion. STATELESS! :(
     * Would drop from explosion if unsealed, but can't tell.
     */
    @Override
    public boolean canDropFromExplosion(Explosion explosionIn)
    {
        return false;
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SEALED, LIT);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 14 : 0;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return false;
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
        return new TEPowderKeg();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        // Only drop the keg if it's not sealed, since the keg with contents will be already dropped by the TE
        if (!state.getValue(SEALED))
        {
            super.getDrops(drops, world, pos, state, fortune);
        }
    }

    /**
     * Called when this Block is destroyed by an Explosion
     */
    @Override
    public void onBlockExploded(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        TEPowderKeg te = Helpers.getTE(worldIn, pos, TEPowderKeg.class);
        if (!worldIn.isRemote && te != null && te.getStrength() > 0) // explode even if not sealed cause gunpowder
        {
            trigger(worldIn, pos, worldIn.getBlockState(pos).withProperty(SEALED, true).withProperty(LIT, true), null);
        }
        else
        {
            super.onBlockExploded(worldIn, pos, explosionIn);
        }
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world,
                                  BlockPos pos, EntityPlayer player)
    {
        TEPowderKeg tile = Helpers.getTE(world, pos, TEPowderKeg.class);
        if (tile != null)
        {
            return tile.getItemStack(state);
        }
        return new ItemStack(state.getBlock());
    }

    private boolean canStay(IBlockAccess world, BlockPos pos)
    {
        boolean solid = world.getBlockState(pos.down()).getBlockFaceShape(world, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID;
        return solid || world.getBlockState(pos.down()).getBlock() instanceof BlockPowderKeg;
    }
}
