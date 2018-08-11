/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.*;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockLogTFC extends BlockLog
{
    public static final PropertyBool PLACED = PropertyBool.create("placed");
    public static final PropertyBool SMALL = PropertyBool.create("small");
    public static final AxisAlignedBB SMALL_AABB_Y = new AxisAlignedBB(0.25, 0, 0.25, 0.75, 1, 0.75);
    public static final AxisAlignedBB SMALL_AABB_X = new AxisAlignedBB(0, 0.25, 0.25, 1, 0.75, 0.75);
    public static final AxisAlignedBB SMALL_AABB_Z = new AxisAlignedBB(0.25, 0.25, 0, 0.75, 0.75, 1);
    private static final Map<Tree, BlockLogTFC> MAP = new HashMap<>();

    public static BlockLogTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

    public BlockLogTFC(Tree wood)
    {
        this.wood = wood;
        if (MAP.put(wood, this) != null) throw new IllegalStateException("There can only be one.");
        setDefaultState(blockState.getBaseState().withProperty(LOG_AXIS, BlockLog.EnumAxis.Y).withProperty(PLACED, true).withProperty(SMALL, false));
        setHarvestLevel("axe", 0);
        setHardness(15.0F);
        setResistance(5.0F);
        OreDictionaryHelper.register(this, "log", "wood");
        OreDictionaryHelper.register(this, "log", "wood", wood.name());
        Blocks.FIRE.setFireInfo(this, 5, 5);
        setTickRandomly(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return !state.getValue(SMALL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getBlockHardness(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return (blockState.getValue(PLACED) ? 1.0f : 2.5f) * super.getBlockHardness(blockState, worldIn, pos);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (!state.getValue(SMALL)) return FULL_BLOCK_AABB;
        switch (state.getValue(LOG_AXIS))
        {
            case X:
                return SMALL_AABB_X;
            case Y:
                return SMALL_AABB_Y;
            case Z:
                return SMALL_AABB_Z;
        }
        return FULL_BLOCK_AABB;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return !state.getValue(SMALL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(LOG_AXIS, EnumAxis.values()[meta & 0b11]).withProperty(PLACED, (meta & 0b100) == 0b100).withProperty(SMALL, (meta & 0b1000) == 0b1000);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LOG_AXIS).ordinal() | (state.getValue(PLACED) ? 0b100 : 0) | (state.getValue(SMALL) ? 0b1000 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, LOG_AXIS, PLACED, SMALL);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(PLACED, true).withProperty(SMALL, placer.isSneaking());
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
    {
        // For floating tree things, just make them gently disappear over time
        for (int x = -1; x <= 1; x++)
            for (int y = -1; y <= 1; y++)
                for (int z = -1; z <= 1; z++)
                    if (state.getValue(PLACED) || (world.getBlockState(pos.add(x, y, z)).getBlock() == this && (z != 0 || y != 0 || x != 0)))
                        return;
        world.setBlockToAir(pos);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        // The itemstack is a cheeky hack
        if (!worldIn.isRemote)
            removeTree(worldIn, pos, null, ItemStack.EMPTY, 1);
    }
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (state.getValue(PLACED) || world.isRemote)
            return super.removedByPlayer(state, world, pos, player, willHarvest);

        // Check if player has a valid tool
        ItemStack stack = player.getHeldItemMainhand();
        int flags = 0;
        if (Helpers.doesStackMatchOrePrefix(stack, "axe"))
            flags += 1; // axe
        if (Helpers.doesStackMatchOrePrefix(stack, "hammer"))
            flags += 2; // hammer
        if (Helpers.doesStackMatchOre(stack, "axeStone") || Helpers.doesStackMatchOre(stack, "hammerStone"))
            flags += 4; // stone

        if ((flags & 1) != 0) // bit 1 = is axe, bit 2 = is hammer, bit 3 is stone tool
        {
            // cut down the tree
            return removeTree(world, pos, player, stack, flags);
        }
        else if ((flags & 2) != 0)
        {
            // Break log and spawn some sticks
            world.setBlockToAir(pos);
            Helpers.spawnItemStack(world, pos.add(0.5D, 0.5D, 0.5D), new ItemStack(Items.STICK, 1 + (int) (Math.random() * 3)));
            return true;
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        // Don't do vanilla leaf decay
    }

    private boolean removeTree(World world, BlockPos pos, @Nullable EntityPlayer player, ItemStack stack, int flags)
    {
        TerraFirmaCraft.getLog().info("Natural log harvesting");
        int maxLogs = Integer.MAX_VALUE;
        if (!stack.isEmpty())
            maxLogs = 1 + stack.getMaxDamage() - stack.getItemDamage();

        // find all logs and add them to a list
        List<BlockPos> logs = new ArrayList<>();
        BlockPos pos1, pos2;
        logs.add(pos);
        for (int i = 0; i < logs.size(); i++)
        {
            pos1 = logs.get(i);
            // check for nearby logs
            for (int x = -1; x <= 1; x++)
            {
                for (int y = -1; y <= 1; y++)
                {
                    for (int z = -1; z <= 1; z++)
                    {
                        pos2 = pos1.add(x, y, z);
                        IBlockState state = world.getBlockState(pos2);
                        if (state.getBlock() == this && !state.getValue(PLACED) && !logs.contains(pos2))
                            logs.add(pos2);
                    }
                }
            }
        }
        TerraFirmaCraft.getLog().info("Logs to break + " + maxLogs + " in: " + logs);
        // sort the list in terms of max distance to the original tree
        logs.sort(Comparator.comparing(x -> x.distanceSq(pos)));
        Collections.reverse(logs);
        // start removing logs*/
        for (int i = 0; i < Math.min(logs.size(), maxLogs); i++)
        {
            // Remove the top log of the list
            pos1 = logs.get(i);
            if (!stack.isEmpty() && player != null)
            {
                if ((flags & 4) == 0 || Math.random() < 0.6) // Stone tools are 60% efficient
                    harvestBlock(world, player, pos1, world.getBlockState(pos1), null, stack);
                stack.damageItem(1, player);
            }
            else
            {
                if (Math.random() < 0.3) // Explosions are 30% Efficient: no TNT powered tree farms.
                    Helpers.spawnItemStack(world, pos.add(0.5d, 0.5d, 0.5d), new ItemStack(Item.getItemFromBlock(this)));
            }
            world.setBlockToAir(pos1);
        }
        return maxLogs >= logs.size();
    }

}
