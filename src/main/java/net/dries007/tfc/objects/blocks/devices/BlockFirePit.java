/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.util.IBellowsConsumerBlock;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TEBellows;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
public class BlockFirePit extends Block implements IBellowsConsumerBlock, ILightableBlock
{
    public static final PropertyEnum<FirePitAttachment> ATTACHMENT = PropertyEnum.create("attachment", FirePitAttachment.class);

    private static final AxisAlignedBB DEFAULT_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.125, 1);
    private static final AxisAlignedBB FIREPIT_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.125, 1);
    private static final AxisAlignedBB FIREPIT_WITH_SHIT_AABB = new AxisAlignedBB(0, 0, 0, 1, 0.7, 1);
    private static final AxisAlignedBB ATTACHMENT_AABB = new AxisAlignedBB(0.1875, 0.125, 0.1875, 0.8125, 0.6875, 0.8125);

    public BlockFirePit()
    {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(LIT, false).withProperty(ATTACHMENT, FirePitAttachment.NONE));
        disableStats();
        setTickRandomly(true);
        setLightLevel(1F);
        setHardness(0.3F);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LIT, (meta & 1) > 0).withProperty(ATTACHMENT, FirePitAttachment.valueOf(meta >> 1));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return (state.getValue(LIT) ? 1 : 0) + (state.getValue(ATTACHMENT).ordinal() << 1);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (state.getValue(ATTACHMENT) != FirePitAttachment.NONE)
        {
            return FIREPIT_WITH_SHIT_AABB;
        }
        return DEFAULT_AABB;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, FIREPIT_AABB);
        if (state.getValue(ATTACHMENT) != FirePitAttachment.NONE)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, ATTACHMENT_AABB);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (worldIn.isRainingAt(pos))
        {
            worldIn.setBlockState(pos, state.withProperty(LIT, false), 2);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rng)
    {
        if (!state.getValue(LIT)) return;

        if (rng.nextInt(24) == 0)
        {
            world.playSound(pos.getX() + 0.5F, pos.getY() + 0.5F, pos.getZ() + 0.5F, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rng.nextFloat(), rng.nextFloat() * 0.7F + 0.3F, false);
        }
        double x = pos.getX() + 0.5;
        double y = pos.getY() + 0.1;
        double z = pos.getZ() + 0.5;

        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rng.nextFloat() - 0.5, y, z + rng.nextFloat() - 0.5, 0.0D, 0.2D, 0.0D);
        if (rng.nextFloat() > 0.75)
            world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, x + rng.nextFloat() - 0.5, y, z + rng.nextFloat() - 0.5, 0.0D, 0.1D, 0.0D);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!canBePlacedOn(worldIn, pos.add(0, -1, 0)))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEFirePit tile = Helpers.getTE(worldIn, pos, TEFirePit.class);
        if (tile != null)
        {
            tile.onBreakBlock(worldIn, pos, state);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.AIR;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return super.canPlaceBlockAt(worldIn, pos) && canBePlacedOn(worldIn, pos.add(0, -1, 0));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack held = player.getHeldItem(hand);

            // Try to light the fire pit
            if (!state.getValue(LIT))
            {
                if (ItemFireStarter.onIgnition(held))
                {
                    TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                    worldIn.setBlockState(pos, state.withProperty(LIT, true));
                    return true;
                }
            }

            // Try to attach an item
            FirePitAttachment attachment = state.getValue(ATTACHMENT);
            TEFirePit tile = Helpers.getTE(worldIn, pos, TEFirePit.class);
            if (tile != null)
            {
                if (attachment == FirePitAttachment.NONE)
                {
                    if (OreDictionaryHelper.doesStackMatchOre(held, "cookingPot"))
                    {
                        worldIn.setBlockState(pos, state.withProperty(ATTACHMENT, FirePitAttachment.COOKING_POT));
                        tile.onConvertToCookingPot(player, held);
                        return true;
                    }
                    else if (OreDictionaryHelper.doesStackMatchOre(held, "grill"))
                    {
                        worldIn.setBlockState(pos, state.withProperty(ATTACHMENT, FirePitAttachment.GRILL));
                        tile.onConvertToGrill(player, held);
                        return true;
                    }
                }
                else if (attachment == FirePitAttachment.COOKING_POT)
                {
                    // Interact with the cooking pot
                    if (tile.getCookingPotStage() == TEFirePit.CookingPotStage.EMPTY)
                    {
                        FluidStack fluidStack = FluidUtil.getFluidContained(held);
                        if (fluidStack != null && fluidStack.amount >= 1000 && fluidStack.getFluid() == FluidsTFC.FRESH_WATER.get())
                        {
                            // Add water
                            tile.addWaterToCookingPot();
                            IFluidHandler fluidHandler = FluidUtil.getFluidHandler(held);
                            if (fluidHandler != null)
                            {
                                fluidHandler.drain(1000, true);
                            }
                            worldIn.playSound(null, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 0.5f, 1.0f);
                            return true;
                        }
                    }
                    else if (tile.getCookingPotStage() == TEFirePit.CookingPotStage.FINISHED)
                    {
                        if (OreDictionaryHelper.doesStackMatchOre(held, "bowl"))
                        {
                            tile.onUseBowlOnCookingPot(player, held);
                            return true;
                        }
                    }
                }
            }

            if (!player.isSneaking())
            {
                TFCGuiHandler.openGui(worldIn, pos, player, TFCGuiHandler.Type.FIRE_PIT);
            }

        }
        return true;
    }

    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn)
    {
        IBlockState state = worldIn.getBlockState(pos);
        if (state.getValue(LIT) && !entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase)
        {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if (state.getValue(LIT) && !entityIn.isImmuneToFire() && entityIn instanceof EntityLivingBase)
        {
            entityIn.attackEntityFrom(DamageSource.IN_FIRE, 1.0F);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LIT, ATTACHMENT);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? super.getLightValue(state, world, pos) : 0;
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
        return new TEFirePit();
    }

    @Override
    public boolean canIntakeFrom(TEBellows te, Vec3i offset, EnumFacing facing)
    {
        return offset.equals(TEBellows.OFFSET_LEVEL);
    }

    @Override
    public void onAirIntake(TEBellows te, World world, BlockPos pos, int airAmount)
    {
        TEFirePit teFirePit = Helpers.getTE(world, pos, TEFirePit.class);
        if (teFirePit != null)
        {
            teFirePit.onAirIntake(airAmount);
        }
    }

    private boolean canBePlacedOn(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).isSideSolid(worldIn, pos, EnumFacing.UP);
    }

    public enum FirePitAttachment implements IStringSerializable
    {
        NONE, GRILL, COOKING_POT;

        private static final FirePitAttachment[] VALUES = values();

        public static FirePitAttachment valueOf(int i)
        {
            return i < 0 || i >= VALUES.length ? NONE : VALUES[i];
        }

        @Nonnull
        @Override
        public String getName()
        {
            return name().toLowerCase();
        }
    }
}
