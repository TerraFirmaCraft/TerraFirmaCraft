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
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.devices.BlockCharcoalForge;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.objects.te.TECharcoalForge;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockCharcoalPile extends Block implements ILightableBlock
{
    public static final Material CHARCOAL_MATERIAL = new Material(MapColor.BROWN);

    public static final PropertyInteger LAYERS = PropertyInteger.create("type", 1, 8);

    private static final AxisAlignedBB[] PILE_AABB = new AxisAlignedBB[] {
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.125D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.375D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.625D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.75D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.875D, 1.0D),
        new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)};

    public BlockCharcoalPile()
    {
        super(CHARCOAL_MATERIAL);

        setSoundType(TFCSounds.CHARCOAL_PILE);
        setHarvestLevel("shovel", 0);
        setHardness(1.0F);
        this.setDefaultState(this.blockState.getBaseState().withProperty(LAYERS, 1));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        return state.getValue(LAYERS) == 8;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(LAYERS, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LAYERS) - 1;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return state.getValue(LAYERS) == 8;
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return PILE_AABB[state.getValue(LAYERS)];
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN || state.getValue(LAYERS) == 8 ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
    }

    @Override
    @Nullable
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        return PILE_AABB[state.getValue(LAYERS)];
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return state.getValue(LAYERS) == 8;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            // Try to drop the rock down
            IBlockState stateUnder = worldIn.getBlockState(pos.down());
            if (stateUnder.getBlock() instanceof BlockCharcoalPile)
            {
                int layersAt = state.getValue(LAYERS);
                int layersUnder = stateUnder.getValue(LAYERS);
                if (layersUnder < 8)
                {
                    if (layersUnder + layersAt <= 8)
                    {
                        worldIn.setBlockState(pos.down(), stateUnder.withProperty(LAYERS, layersAt + layersUnder));
                        worldIn.setBlockToAir(pos);
                    }
                    else
                    {
                        worldIn.setBlockState(pos.down(), stateUnder.withProperty(LAYERS, 8));
                        worldIn.setBlockState(pos, state.withProperty(LAYERS, layersAt + layersUnder - 8));
                    }
                }
                return;
            }
            if (!stateUnder.isSideSolid(worldIn, pos, EnumFacing.UP))
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    @Override
    @Nonnull
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Items.COAL;
    }

    @Override
    public int damageDropped(IBlockState state)
    {
        return 1;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (state.getValue(LAYERS) >= 7 && BlockCharcoalForge.isValid(world, pos) && ItemFireStarter.onIgnition(stack))
        {
            if (!world.isRemote)
            {
                TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                world.setBlockState(pos, BlocksTFC.CHARCOAL_FORGE.getDefaultState().withProperty(LIT, true));
                TECharcoalForge te = Helpers.getTE(world, pos, TECharcoalForge.class);
                if (te != null)
                {
                    te.onCreate();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LAYERS);
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        this.onBlockHarvested(world, pos, state, player);

        if (player.isCreative())
        {
            return super.removedByPlayer(state, world, pos, player, willHarvest);
        }

        if (!world.isRemote)
        {
            int layers = state.getValue(LAYERS);
            if (layers == 1)
            {
                world.setBlockToAir(pos);
            }
            else
            {
                world.setBlockState(pos, state.withProperty(LAYERS, layers - 1));
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return new ItemStack(Items.COAL, 1, 1);
    }
}
