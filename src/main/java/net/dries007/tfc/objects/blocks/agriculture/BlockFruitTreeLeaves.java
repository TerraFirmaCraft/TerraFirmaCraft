/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.agriculture;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockFruitTreeLeaves extends BlockLeaves
{
    public static final PropertyEnum<EnumLeafState> LEAF_STATE = PropertyEnum.create("state", BlockFruitTreeLeaves.EnumLeafState.class);
    public static final PropertyBool HARVESTABLE = PropertyBool.create("harvestable");
    private static final Map<IFruitTree, BlockFruitTreeLeaves> MAP = new HashMap<>();

    public static BlockFruitTreeLeaves get(IFruitTree tree)
    {
        return MAP.get(tree);
    }

    private final IFruitTree tree;

    public BlockFruitTreeLeaves(IFruitTree tree)
    {
        this.tree = tree;
        if (MAP.put(tree, this) != null) throw new IllegalStateException("There can only be one.");
        setDefaultState(blockState.getBaseState().withProperty(DECAYABLE, false).withProperty(LEAF_STATE, EnumLeafState.NORMAL).withProperty(HARVESTABLE, false));
        leavesFancy = true; // Fast / Fancy graphics works correctly
        OreDictionaryHelper.register(this, "tree", "leaves");
        OreDictionaryHelper.register(this, "tree", "leaves", tree.getName());
        Blocks.FIRE.setFireInfo(this, 30, 60);
        setTickRandomly(true);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(HARVESTABLE, meta > 3).withProperty(LEAF_STATE, EnumLeafState.valueOf(meta & 0b11));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(LEAF_STATE).ordinal() + (state.getValue(HARVESTABLE) ? 4 : 0);
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (!world.isRemote)
        {
            if (state.getValue(HARVESTABLE) && tree.isHarvestMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
            {
                TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
                if (te != null)
                {
                    long hours = te.getTicksSinceUpdate() / ICalendar.TICKS_IN_HOUR;
                    if (hours > (tree.getGrowthTime() * ConfigTFC.General.FOOD.fruitTreeGrowthTimeModifier))
                    {
                        world.setBlockState(pos, world.getBlockState(pos).withProperty(LEAF_STATE, EnumLeafState.FRUIT));
                        te.resetCounter();
                    }
                }
            }
            else if (tree.isFlowerMonth(CalendarTFC.CALENDAR_TIME.getMonthOfYear()))
            {
                if (world.getBlockState(pos).getValue(LEAF_STATE) != EnumLeafState.FLOWERING)
                {
                    world.setBlockState(pos, world.getBlockState(pos).withProperty(LEAF_STATE, EnumLeafState.FLOWERING));
                }
            }
            else
            {
                if (world.getBlockState(pos).getValue(LEAF_STATE) != EnumLeafState.NORMAL)
                {
                    world.setBlockState(pos, world.getBlockState(pos).withProperty(LEAF_STATE, EnumLeafState.NORMAL));
                }
            }
            doLeafDecay(world, pos, state);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, @Nullable Block blockIn, @Nullable BlockPos fromPos)
    {
        doLeafDecay(world, pos, state);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state)
    {
        TETickCounter tile = Helpers.getTE(worldIn, pos, TETickCounter.class);
        if (tile != null)
        {
            tile.resetCounter();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.getBlockState(pos).getValue(LEAF_STATE) == EnumLeafState.FRUIT)
        {
            if (!worldIn.isRemote)
            {
                ItemHandlerHelper.giveItemToPlayer(playerIn, tree.getFoodDrop());
                worldIn.setBlockState(pos, worldIn.getBlockState(pos).withProperty(LEAF_STATE, EnumLeafState.NORMAL));
                TETickCounter te = Helpers.getTE(worldIn, pos, TETickCounter.class);
                if (te != null)
                {
                    te.resetCounter();
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        // Duplicated from BlockLeavesTFC#onEntityCollision
        if ((!(entityIn instanceof EntityPlayer) || !((EntityPlayer) entityIn).isCreative()))
        {
            // Player will take damage when falling through leaves if fall is over 9 blocks, fall damage is then set to 0.
            entityIn.fall((entityIn.fallDistance - 6), 1.0F);
            entityIn.fallDistance = 0;
            // Entity motion is reduced by leaves.
            entityIn.motionX *= ConfigTFC.General.MISC.leafMovementModifier;
            if (entityIn.motionY < 0)
            {
                entityIn.motionY *= ConfigTFC.General.MISC.leafMovementModifier;
            }
            entityIn.motionZ *= ConfigTFC.General.MISC.leafMovementModifier;
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, DECAYABLE, LEAF_STATE, HARVESTABLE);
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
        return new TETickCounter();
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        doLeafDecay(worldIn, pos, state);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        /*
         * This is a way to make sure the leave settings are updated.
         * The result of this call is cached somewhere, so it's not that important, but:
         * The alternative would be to use `Minecraft.getMinecraft().gameSettings.fancyGraphics` directly in the 2 relevant methods.
         * It's better to do that than to refer to Blocks.LEAVES, for performance reasons.
         */
        leavesFancy = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        return super.getRenderLayer();
    }

    @Override
    public BlockPlanks.EnumType getWoodType(int meta)
    {
        return BlockPlanks.EnumType.OAK;
    }

    @Override
    public void beginLeavesDecay(IBlockState state, World world, BlockPos pos)
    {
        // Don't do vanilla decay
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        // Stops dropping oak saplings inherited from BlockLeaves
        drops.clear();
    }

    @SuppressWarnings("deprecation")
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side)
    {
        /*
         * See comment on getRenderLayer()
         */
        leavesFancy = Minecraft.getMinecraft().gameSettings.fancyGraphics;
        return true;// super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    @Nonnull
    public IFruitTree getTree()
    {
        return tree;
    }

    @Override
    public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune)
    {
        return ImmutableList.of(new ItemStack(this));
    }

    private void doLeafDecay(World world, BlockPos pos, IBlockState state)
    {
        // TFC Leaf Decay, modified for fruit trees
        if (world.isRemote)
            return;


        Set<BlockPos> paths = new HashSet<>();
        Set<BlockPos> evaluated = new HashSet<>(); // Leaves that everything was evaluated so no need to do it again
        List<BlockPos> pathsToAdd; // New Leaves that needs evaluation
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos(pos);
        IBlockState state1;
        paths.add(pos); // Center block

        for (int i = 0; i < 2; i++)
        {
            pathsToAdd = new ArrayList<>();
            for (BlockPos p1 : paths)
            {
                for (EnumFacing face : EnumFacing.values())
                {
                    pos1.setPos(p1).move(face);
                    if (evaluated.contains(pos1) || !world.isBlockLoaded(pos1))
                        continue;
                    state1 = world.getBlockState(pos1);
                    if (state1.getBlock() == BlockFruitTreeTrunk.get(tree) || state1.getBlock() == BlockFruitTreeBranch.get(tree))
                        return;
                    if (state1.getBlock() == this)
                        pathsToAdd.add(pos1.toImmutable());
                }
                evaluated.add(p1); // Evaluated
            }
            paths.addAll(pathsToAdd);
            paths.removeAll(evaluated);
        }

        world.setBlockToAir(pos);
    }

    /**
     * Enum state for blockstate
     * Used to render the correct texture of this leaf block
     */
    public enum EnumLeafState implements IStringSerializable
    {
        NORMAL, FLOWERING, FRUIT;

        private static final EnumLeafState[] VALUES = values();

        @Nonnull
        public static EnumLeafState valueOf(int index)
        {
            return index < 0 || index > VALUES.length ? NORMAL : VALUES[index];
        }

        @Override
        public String getName()
        {
            return this.name().toLowerCase();
        }
    }
}
