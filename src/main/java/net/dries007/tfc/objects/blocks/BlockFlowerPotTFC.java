package net.dries007.tfc.objects.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.objects.blocks.property.FlowerProperty;
import net.dries007.tfc.objects.te.TEFlowerPotTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockFlowerPotTFC extends Block
{
    public static final FlowerProperty FLOWER = new FlowerProperty();

    protected static final AxisAlignedBB FLOWER_POT_AABB = new AxisAlignedBB(0.3125D, 0.0D, 0.3125D, 0.6875D, 0.375D, 0.6875D);

    public BlockFlowerPotTFC()
    {
        super(Material.CIRCUITS);
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return FLOWER_POT_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return face == EnumFacing.DOWN ? super.getBlockFaceShape(worldIn, state, pos, face) : BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            TEFlowerPotTFC te = Helpers.getTE(world, pos, TEFlowerPotTFC.class);
            if (te != null && te.isEmpty())
            {
                ItemStack held = player.getHeldItem(hand);
                IBlockState putState = Block.getBlockFromItem(held.getItem()).getDefaultState();
                // allow addons to skip the plant check optionally. None of our plants use this name so this statement will always return true in vanilla tfc
                if (!OreDictionaryHelper.doesStackMatchOre(held, "canBePotted"))
                {
                    putState = tryMakePlantState(putState, true);
                }
                te.setState(putState);
                if (!player.isCreative() && putState.getBlock() != Blocks.AIR)
                    held.shrink(1);
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        IBlockState downState = worldIn.getBlockState(pos.down());
        return super.canPlaceBlockAt(worldIn, pos) && (downState.isTopSolid() || downState.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP) == BlockFaceShape.SOLID);
    }

    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        TEFlowerPotTFC te = Helpers.getTE(world, pos, TEFlowerPotTFC.class);
        if (te != null && !player.isCreative())
        {
            te.dump();
        }
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TEFlowerPotTFC te = Helpers.getTE(world, pos, TEFlowerPotTFC.class);
        if (te != null)
        {
            return new ItemStack(Item.getItemFromBlock(te.state.getBlock()));
        }
        return super.getPickBlock(state, target, world, pos, player);
    }

    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        IProperty[] listedProperties = new IProperty[] {};
        IUnlistedProperty[] unlistedProperties = new IUnlistedProperty[] {FLOWER};
        return new ExtendedBlockState(this, listedProperties, unlistedProperties);
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
        return new TEFlowerPotTFC();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        state = tryMakePlantState(state, false); // will return itself if something's wrong or missing
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extension = (IExtendedBlockState) state;
            TEFlowerPotTFC te = Helpers.getTE(world, pos, TEFlowerPotTFC.class);
            if (te != null)
            {
                extension = extension.withProperty(FLOWER, te.state);
            }
            return extension;
        }
        return state;
    }

    private IBlockState tryMakePlantState(IBlockState state, boolean returnAir)
    {
        BlockPlantTFC plantBlock = null;
        if (state.getBlock() instanceof BlockPlantTFC)
        {
            plantBlock = (BlockPlantTFC) state.getBlock(); // so we can use age and stage
        }
        if (plantBlock == null || !plantBlock.getPlant().canBePotted())
        {
            return returnAir ? Blocks.AIR.getDefaultState() : state; // keeps us from potting things we shouldn't
        }
        return state.withProperty(BlockPlantTFC.DAYPERIOD, 2).withProperty(BlockPlantTFC.AGE, 3).withProperty(plantBlock.growthStageProperty, plantBlock.getPlant().getStageForMonth());
    }
}
