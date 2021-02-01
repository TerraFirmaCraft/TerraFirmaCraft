package net.dries007.tfc.api.util;

import java.util.*;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockSupport;
import net.dries007.tfc.objects.entity.EntityFallingBlockTFC;
import net.dries007.tfc.util.IFallingBlock;

public class FallingBlockManager
{

    private static final Set<Material> SOFT_MATERIALS = new ObjectOpenHashSet<>(new Material[] { Material.GROUND, Material.SAND, Material.GRASS, Material.CLAY });
    private static final Set<Material> HARD_MATERIALS = new ObjectOpenHashSet<>(new Material[] { Material.IRON, BlockCharcoalPile.CHARCOAL_MATERIAL });
    private static final Map<IBlockState, Specification> FALLABLES = new Object2ObjectOpenHashMap<>();

    private static final Map<IBlockState, SupportBeamImitator> SUPPORT_BEAMS = new Object2ObjectOpenHashMap<>(0);
    private static final Set<IBlockState> SIDE_SUPPORTS = new ObjectOpenHashSet<>(0);

    static {
        SIDE_SUPPORTS.add(Blocks.REDSTONE_WIRE.getDefaultState());
        SUPPORT_BEAMS.put(Blocks.OBSIDIAN.getDefaultState(), (world, pos) -> true);
        FALLABLES.put(Blocks.RED_SHULKER_BOX.getDefaultState(), new Specification(true, () -> SoundEvents.BLOCK_SHULKER_BOX_OPEN));
    }

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

    public static void registerSideSupports(IBlockState state)
    {
        SIDE_SUPPORTS.add(state);
    }

    public static void registerSupportBeams(IBlockState state, SupportBeamImitator imitator)
    {
        SUPPORT_BEAMS.put(state, imitator);
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

            final List<BlockPos> candidatePositions = new ObjectArrayList<>(4);
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
                if (shouldFall(world, offsetPos, pos, state, ignoreSupportChecks) && IFallingBlock.canFallThrough(world, offsetPos, state.getMaterial(), offsetState))
                {
                    candidatePositions.add(offsetPos);
                }
            }

            return candidatePositions.isEmpty() ? null : checkAreaClear(world, state, candidatePositions.get(Constants.RNG.nextInt(candidatePositions.size())));
        }

        return null;
    }

    public static boolean checkFalling(World worldIn, BlockPos pos, IBlockState state)
    {
        return checkFalling(worldIn, pos, state, false);
    }

    /**
     * Check if this block gonna fall.
     *
     * @param worldIn the world
     * @param pos     the position of the original block
     * @param state   the state of the original block
     * @return true if this block has fallen, false otherwise
     */
    public static boolean checkFalling(World worldIn, BlockPos pos, IBlockState state, boolean ignoreSupportChecks)
    {
        // Check for loaded area to fix stack overflow crash from endless falling / liquid block updates
        if (BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-2, -2, -2), pos.add(2, 2, 2)))
        {
            BlockPos fallablePos = getFallablePos(worldIn, pos, state, ignoreSupportChecks);

            if (fallablePos == null)
            {
                return false;
            }

            worldIn.setBlockToAir(pos);

            BlockPos.MutableBlockPos fallingPos = new BlockPos.MutableBlockPos(fallablePos);
            fallingPos.setY(fallingPos.getY() - 1);
            while (canFallThrough(worldIn, fallingPos, state.getMaterial()) && fallingPos.getY() > 0)
            {
                fallingPos.setY(fallingPos.getY() - 1);
            }
            if (fallablePos.getY() > 0)
            {
                fallingPos.setY(fallablePos.getY() + 1);
                worldIn.setBlockState(fallingPos, state); // Includes Forge's fix for data loss.
            }
            return false;
        }
        else if (worldIn.isAreaLoaded(pos.add(-32, -32, -32), pos.add(32, 32, 32)))
        {
            BlockPos fallablePos = getFallablePos(worldIn, pos, state, ignoreSupportChecks);

            if (fallablePos != null)
            {
                if (!fallablePos.equals(pos))
                {
                    worldIn.setBlockToAir(pos); // TODO check if this is needed
                    worldIn.setBlockState(fallablePos, state);
                }
                worldIn.spawnEntity(new EntityFallingBlockTFC(worldIn, fallablePos, state));
                return true;
            }
        }
        return false;
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

        public static final FallDropsProvider DEFAULT_DROPS_PROVIDER =
            (world, pos, state, teData, fallTime, fallDistance) -> Collections.singletonList(new ItemStack(state.getBlock(), 1, state.getBlock().damageDropped(state)));

        private final boolean canFallHorizontally;
        private final Supplier<SoundEvent> soundEventDelegate;
        private final FallDropsProvider fallDropsProvider;

        @Nullable private IBlockState resultingState; // Defaults to base IBlockState, null here as a reference is pretty memory hungry
        @Nullable private BeginFallCallback beginFallCallback;
        @Nullable private EndFallCallback endFallCallback;

        public Specification(Specification specification)
        {
            this.canFallHorizontally = specification.canFallHorizontally;
            this.soundEventDelegate = specification.soundEventDelegate;
            this.fallDropsProvider = specification.fallDropsProvider;
            this.resultingState = specification.resultingState;
            this.beginFallCallback = specification.beginFallCallback;
            this.endFallCallback = specification.endFallCallback;
        }

        public Specification(boolean canFallHorizontally, Supplier<SoundEvent> soundEventDelegate)
        {
            this(canFallHorizontally, soundEventDelegate, DEFAULT_DROPS_PROVIDER);
        }

        public Specification(boolean canFallHorizontally, Supplier<SoundEvent> soundEventDelegate, FallDropsProvider fallDropsProvider)
        {
            this.canFallHorizontally = canFallHorizontally;
            this.soundEventDelegate = soundEventDelegate;
            this.fallDropsProvider = fallDropsProvider;
        }

        public void setResultingState(IBlockState state)
        {
            this.resultingState = state;
        }

        public void setBeginFallCallback(BeginFallCallback callback)
        {
            this.beginFallCallback = callback;
        }

        public void setEndFallCallback(EndFallCallback callback)
        {
            this.endFallCallback = callback;
        }

        public boolean canFallHorizontally()
        {
            return canFallHorizontally;
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
        public interface FallDropsProvider
        {
            Iterable<ItemStack> getDropsFromFall(World world, BlockPos pos, IBlockState state, @Nullable NBTTagCompound teData, int fallTime, float fallDistance);
        }

        @FunctionalInterface
        public interface BeginFallCallback
        {
            void beginFall(World world, BlockPos startPos);
        }

        @FunctionalInterface
        public interface EndFallCallback
        {
            void endFall(World world, BlockPos endPos);
        }

    }

    public interface SupportBeamImitator
    {
        boolean canSupportBlocks(IBlockAccess world, BlockPos pos);
    }

    private FallingBlockManager() { }

}
