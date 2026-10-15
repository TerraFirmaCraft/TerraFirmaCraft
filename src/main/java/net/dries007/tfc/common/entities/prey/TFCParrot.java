/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomFlyingGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.pathfinder.FlyNodeEvaluator;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.level.pathfinder.PathFinder;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.PathfindingContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCParrot extends Parrot
{
    private static final float LEAVE_TEMPERATURE = 10f;

    public static List<Variant> getClimateVariants(LevelReader level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);
        final float temperature = EnvironmentHelpers.adjustAvgTempForElev(pos.getY(), data.getAverageSeaLevelTemp(pos));
        final float groundwater = data.getAverageGroundwater(pos);
        final int forest = data.getForestType().getDensity();

        final List<Variant> variants = new ArrayList<>();
        if (temperature >= 21 && groundwater >= 250 && forest >= 2) variants.add(Variant.RED_BLUE);
        if (temperature >= 20 && groundwater >= 150 && groundwater <= 300 && forest <= 2) variants.add(Variant.BLUE);
        if (temperature >= 18 && groundwater >= 350 && forest >= 3) variants.add(Variant.GREEN);
        if (temperature >= 21 && groundwater >= 350) variants.add(Variant.YELLOW_BLUE);
        if (temperature >= 12 && temperature <= 30 && groundwater >= 60 && groundwater <= 200 && forest <= 1) variants.add(Variant.GRAY);
        return variants;
    }

    private static boolean isTooCold(ClimateModel model, LevelReader level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        final long calendarTicks = calendar.getCalendarTicks();
        return model.getTimeAverageTemperature(level, pos, calendarTicks - ICalendar.CALENDAR_TICKS_IN_DAY, calendarTicks, calendar.getCalendarDaysInMonth()) < LEAVE_TEMPERATURE;
    }

    private long nextLeaveCheckTick = Long.MIN_VALUE;

    public TFCParrot(EntityType<? extends Parrot> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.getAvailableGoals().removeIf(wrapped -> wrapped.getGoal() instanceof WaterAvoidingRandomFlyingGoal); // vanilla wander goal, which only perches on vanilla leaves
        goalSelector.addGoal(2, new TFCParrotWanderGoal(this, 1.0));
    }

    @Override
    protected PathNavigation createNavigation(Level level)
    {
        final TFCParrotNavigation navigation = new TFCParrotNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        navigation.setCanPassDoors(true);
        return navigation;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        final List<Variant> variants = getClimateVariants(level, blockPosition());
        if (!variants.isEmpty())
        {
            setVariant(variants.get(random.nextInt(variants.size())));
        }
        return spawnData;
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType)
    {
        final BlockPos pos = blockPosition();
        final int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        return pos.getY() >= surfaceY
            && level.getFluidState(pos.atY(surfaceY - 1)).isEmpty()
            && !getClimateVariants(level, pos).isEmpty()
            && !isTooCold(Climate.get(level()), level, pos)
            && super.checkSpawnRules(level, spawnType);
    }

    @Override
    protected void customServerAiStep()
    {
        super.customServerAiStep();
        if (getInBlockState().getBlock() instanceof ILeavesBlock)
        {
            // Stuck inside leaves, which it can't path through, so fly straight up and out of the canopy
            final Vec3 motion = getDeltaMovement();
            setDeltaMovement(motion.x, 0.2, motion.z);
        }
        if (isTame() || isPersistenceRequired()) return;

        final ICalendar calendar = Calendars.get(level());
        final long ticks = calendar.getTicks();
        if (ticks < nextLeaveCheckTick) return;
        nextLeaveCheckTick = ticks + calendar.getTicksInHour() * ICalendar.HOURS_IN_DAY;

        // Wild parrots leave for the winter, and normal spawning brings them back once it warms up again
        if (isTooCold(Climate.get(level()), level(), blockPosition()) && level().getNearestPlayer(this, 32) == null)
        {
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putLong("nextLeaveCheck", nextLeaveCheckTick);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        nextLeaveCheckTick = EntityHelpers.getLongOrDefault(tag, "nextLeaveCheck", Long.MIN_VALUE);
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        if (!isTame() && Helpers.isItem(player.getItemInHand(hand), ItemTags.PARROT_FOOD))
        {
            return InteractionResult.PASS; // wild parrots cannot be tamed
        }
        return super.mobInteract(player, hand);
    }

    static class TFCParrotWanderGoal extends WaterAvoidingRandomFlyingGoal
    {
        private static final int MAX_TREE_PATH_ATTEMPTS = 3;
        private static final int MAX_PARROTS_PER_PERCH = 2;

        private static int countParrotsAt(List<TFCParrot> parrots, BlockPos pos)
        {
            int count = 0;
            for (TFCParrot parrot : parrots)
            {
                if (pos.equals(parrot.blockPosition()) || pos.equals(parrot.getNavigation().getTargetPos()))
                {
                    count++;
                }
            }
            return count;
        }

        @Nullable private Path treePath;

        TFCParrotWanderGoal(PathfinderMob mob, double speedModifier)
        {
            super(mob, speedModifier);
        }

        @Override
        public void start()
        {
            if (treePath != null)
            {
                mob.getNavigation().moveTo(treePath, speedModifier);
                treePath = null;
            }
            else
            {
                super.start();
            }
        }

        @Nullable
        @Override
        protected Vec3 getPosition()
        {
            treePath = null;
            Vec3 pos = mob.isInWater() ? LandRandomPos.getPos(mob, 15, 15) : null;
            if (mob.getRandom().nextFloat() >= probability)
            {
                pos = getTreePos();
            }
            return pos == null ? super.getPosition() : pos;
        }

        @Nullable
        private Vec3 getTreePos()
        {
            // Reaches higher than vanilla, as TFC trees are much taller
            final BlockPos origin = mob.blockPosition();
            final BlockPos min = origin.offset(-3, -6, -3);
            final BlockPos max = origin.offset(3, 20, 3);
            final List<BlockPos> perches = new ArrayList<>();
            for (BlockPos pos : BlockPos.betweenClosed(min, max))
            {
                final BlockState below = mob.level().getBlockState(pos.relative(Direction.DOWN));
                if (!pos.equals(origin) && (below.getBlock() instanceof ILeavesBlock || Helpers.isBlock(below, BlockTags.LOGS)) && mob.level().isEmptyBlock(pos) && mob.level().isEmptyBlock(pos.above()))
                {
                    perches.add(pos.immutable());
                }
            }
            if (perches.isEmpty()) return null;

            // Spread out over the canopy in random order, instead of every parrot picking the same nearest perch, and skip perches that are already taken
            final List<TFCParrot> others = mob.level().getEntitiesOfClass(TFCParrot.class, AABB.encapsulatingFullBlocks(min, max).inflate(1), parrot -> parrot != mob);
            Util.shuffle(perches, mob.getRandom());
            int attempts = 0;
            for (BlockPos pos : perches)
            {
                if (countParrotsAt(others, pos) >= MAX_PARROTS_PER_PERCH) continue;

                // Leaves block parrots, so a perch is only usable if the path reaches it, not a partial path ending below the canopy
                final Path path = mob.getNavigation().createPath(pos, 0);
                if (path != null && path.canReach())
                {
                    treePath = path;
                    return Vec3.atBottomCenterOf(pos);
                }
                if (++attempts >= MAX_TREE_PATH_ATTEMPTS)
                {
                    break;
                }
            }
            return null;
        }
    }

    static class TFCParrotNavigation extends FlyingPathNavigation
    {
        TFCParrotNavigation(TFCParrot parrot, Level level)
        {
            super(parrot, level);
        }

        @Override
        protected PathFinder createPathFinder(int maxVisitedNodes)
        {
            this.nodeEvaluator = new TFCParrotNodeEvaluator();
            this.nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(this.nodeEvaluator, maxVisitedNodes);
        }
    }

    static class TFCParrotNodeEvaluator extends FlyNodeEvaluator
    {
        @Override
        public PathType getPathType(PathfindingContext context, int x, int y, int z)
        {
            // Leaves are solid for parrots, so avoid them like vanilla parrots avoid vanilla leaves
            return context.getBlockState(new BlockPos(x, y, z)).getBlock() instanceof ILeavesBlock ? PathType.LEAVES : super.getPathType(context, x, y, z);
        }
    }
}
