/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.util;

import java.util.*;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.worldtracker.CapabilityWorldTracker;
import net.dries007.tfc.api.capability.worldtracker.CollapseData;
import net.dries007.tfc.api.capability.worldtracker.WorldTracker;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockSupport;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;

public class FallingBlockManager
{

    private static final Set<Material> SOFT_MATERIALS = new ObjectOpenHashSet<>(new Material[] { Material.GROUND, Material.SAND, Material.GRASS, Material.CLAY });
    private static final Set<Material> HARD_MATERIALS = new ObjectOpenHashSet<>(new Material[] { Material.IRON, BlockCharcoalPile.CHARCOAL_MATERIAL });

    private static final Map<IBlockState, Specification> FALLABLES = new Object2ObjectOpenHashMap<>();

    private static final Set<IBlockState> SIDE_SUPPORTS = new ObjectOpenHashSet<>(0);

    public static void registerSoftMaterial(Material material)
    {
        SOFT_MATERIALS.add(material);
    }

    public static void registerHardMaterial(Material material)
    {
        HARD_MATERIALS.add(material);
    }

    public static void registerFallable(IBlockState state, Specification specification)
    {
        FALLABLES.put(state, specification);
    }

    public static void registerFallable(Block block, Specification specification)
    {
        for (IBlockState state : block.getBlockState().getValidStates())
        {
            FALLABLES.put(state, specification);
        }
    }

    public static void registerSideSupports(IBlockState state)
    {
        SIDE_SUPPORTS.add(state);
    }

    public static void registerSideSupports(Block block)
    {
        SIDE_SUPPORTS.addAll(block.getBlockState().getValidStates());
    }

    public static void removeSoftMaterial(Material material)
    {
        SOFT_MATERIALS.remove(material);
    }

    public static void removeHardMaterial(Material material)
    {
        HARD_MATERIALS.remove(material);
    }

    public static void removeFallable(IBlockState state)
    {
        FALLABLES.remove(state);
    }

    public static void removeFallable(Block block)
    {
        block.getBlockState().getValidStates().forEach(FALLABLES::remove);
    }

    public static void removeSideSupport(IBlockState state)
    {
        SIDE_SUPPORTS.remove(state);
    }

    public static void removeSideSupport(Block block)
    {
        block.getBlockState().getValidStates().forEach(SIDE_SUPPORTS::remove);
    }

    @Nullable
    public static Specification getSpecification(IBlockState state)
    {
        return FALLABLES.get(state);
    }

    public static boolean canFallThrough(World world, BlockPos pos, Material fallingBlockMaterial)
    {
        return canFallThrough(world, pos, fallingBlockMaterial, world.getBlockState(pos));
    }

    public static boolean canFallThrough(World world, BlockPos pos, Material fallableMaterial, IBlockState targetState)
    {
        if (BlockFalling.canFallThrough(targetState))
        {
            return true;
        }
        if ((SOFT_MATERIALS.contains(fallableMaterial) && HARD_MATERIALS.contains(targetState.getMaterial())) || targetState.getBlockHardness(world, pos) == -1.0F)
        {
            return false;
        }
        if (!world.isSideSolid(pos, EnumFacing.UP))
        {
            return true;
        }
        return !targetState.isFullBlock();
    }

    public static boolean hasSupportingSideBlock(IBlockState state)
    {
        return state.isNormalCube() || SIDE_SUPPORTS.contains(state) || state.getBlock() instanceof BlockRockVariant && (((BlockRockVariant) state.getBlock()).getType() == Rock.Type.FARMLAND || ((BlockRockVariant) state.getBlock()).getType() == Rock.Type.PATH);
    }

    public static boolean shouldFall(World world, BlockPos posToFallFrom, BlockPos originalPos, IBlockState originalState, boolean ignoreSupportChecks)
    {
        return ConfigTFC.General.FALLABLE.enable && canFallThrough(world, posToFallFrom.down(), originalState.getMaterial()) && (ignoreSupportChecks || !BlockSupport.isBeingSupported(world, originalPos));
    }

    public static boolean canCollapse(World world, BlockPos pos)
    {
        return canCollapseAt(world, pos.down());
    }

    public static boolean canCollapseAt(World world, BlockPos pos)
    {
        return canCollapseAt(world.getBlockState(pos));
    }

    public static boolean canCollapseAt(IBlockState state)
    {
        return state.getMaterial().isReplaceable();
    }

    @Nullable
    public static BlockPos getFallablePos(World world, BlockPos pos, IBlockState state, boolean ignoreSupportChecks)
    {
        Specification specification = FALLABLES.get(state);

        if (specification == null)
        {
            return null;
        }

        if (shouldFall(world, pos, pos, state, ignoreSupportChecks))
        {
            return checkAreaClear(world, state, pos);
        }

        if (specification.canFallHorizontally)
        {
            // Check if supported by at least two horizontals, or one on top
            if (hasSupportingSideBlock(world.getBlockState(pos.up())))
            {
                return null;
            }

            final List<BlockPos> candidates = new ObjectArrayList<>(4); // Max 4 elements
            boolean hasFoundSideSupport = false;

            for (EnumFacing horizontalFace : EnumFacing.HORIZONTALS)
            {
                BlockPos offsetPos = pos.offset(horizontalFace);
                IBlockState offsetState = world.getBlockState(offsetPos);
                if (hasSupportingSideBlock(offsetState))
                {
                    if (hasFoundSideSupport)
                    {
                        return null;
                    }
                    hasFoundSideSupport = true;
                }
                if (shouldFall(world, offsetPos, pos, state, ignoreSupportChecks) && canFallThrough(world, offsetPos, state.getMaterial(), offsetState))
                {
                    candidates.add(offsetPos);
                }
            }

            return candidates.isEmpty() ? null : checkAreaClear(world, state, candidates.get(Constants.RNG.nextInt(candidates.size())));
        }

        return null;
    }

    public static boolean checkFalling(World world, BlockPos pos, IBlockState state)
    {
        return checkFalling(world, pos, state, false);
    }

    /**
     * Check if this block gonna fall.
     *
     * @param world the world
     * @param pos     the position of the original block
     * @param state   the state of the original block
     * @return true if this block has fallen, false otherwise
     */
    public static boolean checkFalling(World world, BlockPos pos, IBlockState state, boolean ignoreSupportChecks)
    {
        // Check for loaded area to fix stack overflow crash from endless falling / liquid block updates
        if (BlockFalling.fallInstantly)
        {
            if (!world.isAreaLoaded(pos.add(-2, -2, -2), pos.add(2, 2, 2)))
            {
                return false;
            }

            BlockPos fallablePos = getFallablePos(world, pos, state, ignoreSupportChecks);

            if (fallablePos == null)
            {
                return false;
            }

            world.setBlockToAir(pos);

            BlockPos.MutableBlockPos fallingPos = new BlockPos.MutableBlockPos(fallablePos);
            fallingPos.setY(fallingPos.getY() - 1);
            while (canFallThrough(world, fallingPos, state.getMaterial()) && fallingPos.getY() > 0)
            {
                fallingPos.setY(fallingPos.getY() - 1);
            }
            if (fallablePos.getY() > 0)
            {
                fallingPos.setY(fallingPos.getY() + 1);
                world.setBlockState(fallingPos.toImmutable(), state); // Includes Forge's fix for data loss.
            }
            return false;
        }
        else if (world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
        {
            BlockPos fallablePos = getFallablePos(world, pos, state, ignoreSupportChecks);

            if (fallablePos != null)
            {
                if (!fallablePos.equals(pos))
                {
                    world.getGameRules().setOrCreateGameRule("doTileDrops", Boolean.toString(false));
                    world.setBlockToAir(pos);
                    world.setBlockState(fallablePos, state);
                    world.getGameRules().setOrCreateGameRule("doTileDrops", Boolean.toString(true));
                }
                world.spawnEntity(new EntityFallingBlockTFC(world, fallablePos, state));
                return true;
            }
        }
        return false;
    }

    /**
     * Check an area of blocks for collapsing mechanics
     *
     * @param world the worldObj this block is in
     * @param pos     the BlockPos this block has been mined from
     * @return true if a collapse did occur, false otherwise
     */
    public static boolean checkCollapsingArea(World world, BlockPos pos)
    {
        if (world.isRemote || !world.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
        {
            return false; // First, let's check if this area is loaded and is on server
        }
        if (Constants.RNG.nextDouble() < ConfigTFC.General.FALLABLE.collapseChance) // Then, we check rng if a collapse should trigger
        {
            //Rng the radius
            int radX = (Constants.RNG.nextInt(5) + 4) / 2;
            int radY = (Constants.RNG.nextInt(3) + 2) / 2;
            int radZ = (Constants.RNG.nextInt(5) + 4) / 2;
            for (BlockPos checking : BlockSupport.getAllUnsupportedBlocksIn(world, pos.add(-radX, -radY, -radZ), pos.add(radX, radY, radZ))) //9x5x9 max
            {
                // Check the area for a block collapse!
                IBlockState state = world.getBlockState(checking);
                Specification spec = getSpecification(state);
                if (spec != null && spec.collapsable)
                {
                    if (spec.collapseChecker.canCollapse(world, checking)) // Still needs this to check if this can collapse without support (ie: no blocks below)
                    {
                        collapseArea(world, checking);
                        world.playSound(null, pos, TFCSounds.ROCK_SLIDE_LONG, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        return true; // Don't need to check other blocks
                    }
                }
            }
        }
        return false;
    }

    /**
     * Collapse an area!
     *
     * @param world       the worldObj
     * @param centerPoint the center of this area
     */
    public static void collapseArea(World world, BlockPos centerPoint)
    {
        int radius = (world.rand.nextInt(31) + 5) / 2;
        int radiusSquared = radius * radius;
        List<BlockPos> secondaryPositions = new ArrayList<>();
        // Initially only scan on the bottom layer, and advance upwards
        for (BlockPos pos : BlockPos.getAllInBoxMutable(centerPoint.add(-radius, -4, -radius), centerPoint.add(radius, -4, radius)))
        {
            boolean foundEmpty = false; // If we've found a space to collapse into
            for (int y = 0; y <= 8; y++)
            {
                BlockPos posAt = pos.up(y);
                IBlockState stateAt = world.getBlockState(posAt);
                Specification specAt;
                if (foundEmpty && (specAt = getSpecification(stateAt)) != null && specAt.collapsable && specAt.collapseChecker.canCollapse(world, posAt) && !BlockSupport.isBeingSupported(world, posAt))
                {
                    // Check for a possible collapse
                    if (posAt.distanceSq(centerPoint) < radiusSquared && world.rand.nextFloat() < ConfigTFC.General.FALLABLE.propagateCollapseChance)
                    {
                        // This column has started to collapse. Mark the next block above as unstable for the "follow up"
                        IBlockState resultState = specAt.getResultingState(stateAt);
                        world.setBlockState(posAt, resultState);
                        checkFalling(world, posAt, resultState, true);
                        secondaryPositions.add(posAt.up());
                        break;
                    }
                }
                if (canFallThrough(world, posAt, stateAt.getMaterial(), stateAt))
                {
                    foundEmpty = true;
                }
            }
        }

        if (!secondaryPositions.isEmpty())
        {
            WorldTracker tracker = world.getCapability(CapabilityWorldTracker.CAPABILITY, null);
            if (tracker != null)
            {
                tracker.addCollapseData(new CollapseData(centerPoint, secondaryPositions, radiusSquared));
            }
        }
    }

    @Nullable
    public static BlockPos checkAreaClear(World world, IBlockState state, BlockPos pos)
    {
        // Check that there are no entities in the area, otherwise it would collide with them
        if (!world.getEntitiesWithinAABB(EntityFallingBlock.class, new AxisAlignedBB(pos, pos.add(1, 1, 1))).isEmpty())
        {
            // If we can't fall due to a collision, wait for the block to move out of the way and try again later
            world.scheduleUpdate(pos, state.getBlock(), 20);
            return null;
        }
        return pos;
    }

    public static class Specification
    {

        public static final IFallDropsProvider DEFAULT_DROPS_PROVIDER = (world, pos, state, teData, fallTime, fallDistance) -> Collections.singletonList(new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)));
        public static final ICollapseChecker DEFAULT_COLLAPSE_CHECKER = (world, collapsePos) -> world.getBlockState(collapsePos.down()).getMaterial().isReplaceable();

        public static final Specification VERTICAL_AND_HORIZONTAL = new Specification(true, () -> TFCSounds.DIRT_SLIDE_SHORT);
        public static final Specification VERTICAL_ONLY = new Specification(false, () -> TFCSounds.DIRT_SLIDE_SHORT);
        public static final Specification COLLAPSABLE = new Specification(false, true, () -> TFCSounds.ROCK_SLIDE_LONG);

        private final boolean canFallHorizontally;
        private final Supplier<SoundEvent> soundEventDelegate;
        private final IFallDropsProvider fallDropsProvider;

        private final boolean collapsable;
        private ICollapseChecker collapseChecker;

        @Nullable private IBlockState resultingState; // Defaults to base IBlockState, null here as a reference as states can be pretty big in memory
        @Nullable private IBeginFallCallback beginFallCallback;
        @Nullable private IEndFallCallback endFallCallback;

        public Specification(Specification specification)
        {
            this.canFallHorizontally = specification.canFallHorizontally;
            this.collapsable = specification.collapsable;
            this.collapseChecker = specification.collapseChecker;
            this.soundEventDelegate = specification.soundEventDelegate;
            this.fallDropsProvider = specification.fallDropsProvider;
            this.resultingState = specification.resultingState;
            this.beginFallCallback = specification.beginFallCallback;
            this.endFallCallback = specification.endFallCallback;
        }

        public Specification(boolean canFallHorizontally, Supplier<SoundEvent> soundEventDelegate)
        {
            this(canFallHorizontally, false, soundEventDelegate, DEFAULT_DROPS_PROVIDER);
        }

        public Specification(boolean canFallHorizontally, boolean collapsable, Supplier<SoundEvent> soundEventDelegate)
        {
            this(canFallHorizontally, collapsable, soundEventDelegate, DEFAULT_DROPS_PROVIDER);
        }

        public Specification(boolean canFallHorizontally, boolean collapsable, Supplier<SoundEvent> soundEventDelegate, IFallDropsProvider fallDropsProvider)
        {
            this.canFallHorizontally = canFallHorizontally;
            this.collapsable = collapsable;
            if (this.collapsable)
            {
                this.collapseChecker = DEFAULT_COLLAPSE_CHECKER;
            }
            this.soundEventDelegate = soundEventDelegate;
            this.fallDropsProvider = fallDropsProvider;
        }

        public void setResultingState(IBlockState state)
        {
            this.resultingState = state;
        }

        public void setBeginFallCallback(IBeginFallCallback callback)
        {
            this.beginFallCallback = callback;
        }

        public void setEndFallCallback(IEndFallCallback callback)
        {
            this.endFallCallback = callback;
        }

        public void setCollapseCondition(ICollapseChecker collapseChecker)
        {
            this.collapseChecker = collapseChecker;
        }

        public boolean canFallHorizontally()
        {
            return canFallHorizontally;
        }

        public boolean isCollapsable()
        {
            return collapsable;
        }

        public SoundEvent getSoundEvent()
        {
            return soundEventDelegate.get();
        }

        @Nullable
        public IBlockState getResultingState()
        {
            return resultingState;
        }

        @Nonnull
        public IBlockState getResultingState(IBlockState originalState)
        {
            return resultingState == null ? originalState : resultingState;
        }

        public Iterable<ItemStack> getDrops(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance)
        {
            return fallDropsProvider.getDropsFromFall(world, pos, state, teData, fallTime, fallDistance);
        }

        public boolean canCollapse(World world, BlockPos pos)
        {
            return this.collapseChecker.canCollapse(world, pos);
        }

        public void beginFall(World world, BlockPos pos)
        {
            if (beginFallCallback != null)
            {
                beginFallCallback.beginFall(world, pos);
            }
        }

        public void endFall(World world, BlockPos pos)
        {
            if (endFallCallback != null)
            {
                endFallCallback.endFall(world, pos);
            }
        }

        @FunctionalInterface
        public interface IFallDropsProvider
        {
            Iterable<ItemStack> getDropsFromFall(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance);
        }

        @FunctionalInterface
        public interface IBeginFallCallback
        {
            void beginFall(World world, BlockPos startPos);
        }

        @FunctionalInterface
        public interface IEndFallCallback
        {
            void endFall(World world, BlockPos endPos);
        }

        @FunctionalInterface
        public interface ICollapseChecker
        {
            boolean canCollapse(World world, BlockPos collapsePos);
        }
    }

    private FallingBlockManager() { }

}
