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
import net.minecraft.block.BlockBed;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.items.ItemAnimalHide;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockThatchBed extends BlockHorizontal
{
    public static final PropertyEnum<BlockBed.EnumPartType> PART = PropertyEnum.create("part", BlockBed.EnumPartType.class);
    public static final PropertyBool OCCUPIED = PropertyBool.create("occupied");

    private static final AxisAlignedBB BED_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.5625D, 1.0D);

    public BlockThatchBed()
    {
        super(Material.PLANTS);
        setSoundType(SoundType.PLANT);
        setHardness(0.6F);
        Blocks.FIRE.setFireInfo(this, 60, 20);
        this.hasTileEntity = false;
    }


    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        EnumFacing enumfacing = EnumFacing.byHorizontalIndex(meta);
        return (meta & 8) > 0 ? this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.HEAD).withProperty(FACING, enumfacing).withProperty(OCCUPIED, Boolean.valueOf((meta & 4) > 0)) : this.getDefaultState().withProperty(PART, BlockBed.EnumPartType.FOOT).withProperty(FACING, enumfacing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int i = 0;
        i = i | (state.getValue(FACING)).getHorizontalIndex();

        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD)
        {
            i |= 8;

            if (state.getValue(OCCUPIED))
            {
                i |= 4;
            }
        }

        return i;
    }


    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT)
        {
            IBlockState iblockstate = worldIn.getBlockState(pos.offset(state.getValue(FACING)));

            if (iblockstate.getBlock() == this)
            {
                state = state.withProperty(OCCUPIED, iblockstate.getValue(OCCUPIED));
            }
        }

        return state;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BED_AABB;
    }

    @SideOnly(Side.CLIENT)
    @Override

    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (state.getValue(PART) == BlockBed.EnumPartType.FOOT)
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
        }
        else if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
        {
            if (!worldIn.isRemote)
            {
                this.dropBlockAsItem(worldIn, pos, state, 0);
            }

            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return Item.getItemFromBlock(BlocksTFC.THATCH);
    }

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        playerIn.setSpawnPoint(pos, false);
        playerIn.sendMessage(new TextComponentTranslation("tfc.thatch_bed.spawnpoint"));
        if (!worldIn.isThundering())
        {
            playerIn.sendStatusMessage(new TextComponentTranslation("tfc.thatch_bed.not_thundering"), true);
            return true;
        }
        if (state.getValue(PART) != BlockBed.EnumPartType.HEAD)
        {
            pos = pos.offset(state.getValue(FACING));
            state = worldIn.getBlockState(pos);

            if (state.getBlock() != this)
            {
                return true;
            }
        }

        net.minecraft.world.WorldProvider.WorldSleepResult sleepResult = worldIn.provider.canSleepAt(playerIn, pos);
        if (sleepResult != net.minecraft.world.WorldProvider.WorldSleepResult.BED_EXPLODES)
        {
            if (sleepResult == net.minecraft.world.WorldProvider.WorldSleepResult.DENY) return true;
            if (state.getValue(OCCUPIED))
            {
                EntityPlayer entityplayer = this.getPlayerInBed(worldIn, pos);

                if (entityplayer != null)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.occupied"), true);
                    return true;
                }

                state = state.withProperty(OCCUPIED, false);
                worldIn.setBlockState(pos, state, 4);
            }

            EntityPlayer.SleepResult entityplayer$sleepresult = playerIn.trySleep(pos);

            if (entityplayer$sleepresult == EntityPlayer.SleepResult.OK)
            {
                state = state.withProperty(OCCUPIED, true);
                worldIn.setBlockState(pos, state, 4);
                return true;
            }
            else
            {
                if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_POSSIBLE_NOW)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.noSleep"), true);
                }
                else if (entityplayer$sleepresult == EntityPlayer.SleepResult.NOT_SAFE)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.notSafe"), true);
                }
                else if (entityplayer$sleepresult == EntityPlayer.SleepResult.TOO_FAR_AWAY)
                {
                    playerIn.sendStatusMessage(new TextComponentTranslation("tile.bed.tooFarAway"), true);
                }

                return true;
            }
        }
        else
        {
            worldIn.setBlockToAir(pos);
            BlockPos blockpos = pos.offset((state.getValue(FACING)));

            if (worldIn.getBlockState(blockpos).getBlock() == this)
            {
                worldIn.setBlockToAir(blockpos);
            }

            worldIn.newExplosion(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, 5.0F, true, true);
            return true;
        }
    }

    @Override
    public EnumPushReaction getPushReaction(IBlockState state)
    {
        return EnumPushReaction.DESTROY;
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance)
    {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance * 0.5F);
    }

    @Override
    public void onLanded(World worldIn, Entity entityIn)
    {
        if (entityIn.isSneaking())
        {
            super.onLanded(worldIn, entityIn);
        }
        else if (entityIn.motionY < 0.0D)
        {
            entityIn.motionY = -entityIn.motionY * 0.6600000262260437D;

            if (!(entityIn instanceof EntityLivingBase))
            {
                entityIn.motionY *= 0.8D;
            }
        }
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        if (player.capabilities.isCreativeMode && state.getValue(PART) == BlockBed.EnumPartType.FOOT)
        {
            BlockPos blockpos = pos.offset(state.getValue(FACING));

            if (worldIn.getBlockState(blockpos).getBlock() == this)
            {
                worldIn.setBlockToAir(blockpos);
            }
        }
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, PART, OCCUPIED);
    }

    @Nullable
    private EntityPlayer getPlayerInBed(World worldIn, BlockPos pos)
    {
        for (EntityPlayer entityplayer : worldIn.playerEntities)
        {
            if (entityplayer.isPlayerSleeping() && entityplayer.bedLocation.equals(pos))
            {
                return entityplayer;
            }
        }

        return null;
    }

    
    @Override
    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state)
    {
        return new ItemStack(BlocksTFC.THATCH);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (state.getValue(PART) == BlockBed.EnumPartType.HEAD)
        {
            spawnAsEntity(worldIn, pos, new ItemStack(ItemAnimalHide.get(ItemAnimalHide.HideType.RAW, ItemAnimalHide.HideSize.LARGE)));
            spawnAsEntity(worldIn, pos, new ItemStack(BlocksTFC.THATCH, 2));
        }
    }

    @Override
    public boolean isBed(IBlockState state, IBlockAccess world, BlockPos pos, @Nullable Entity player)
    {
        return true;
    }
}
