/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.EntityEvent;
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
import net.minecraft.world.item.ItemStack;
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
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.wood.ILeavesBlock;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.ClimateModel;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCParrot extends Parrot
{
    private static final EntityDataAccessor<Integer> DATA_SPECIES = SynchedEntityData.defineId(TFCParrot.class, EntityDataSerializers.INT);
    private static final byte IDLE_ANIMATION_EVENT = 70; // One event per IdleAnimation, above the vanilla ids
    private static final int MAX_IDLE_ANIMATION_TICKS = 100;

    /**
     * @return The species which live in the climate at {@code pos}, and for which it is not currently too cold
     */
    private static List<Species> getClimateSpecies(ClimateModel model, LevelReader level, BlockPos pos)
    {
        final ChunkData data = ChunkData.get(level, pos);
        final float temperature = EnvironmentHelpers.adjustAvgTempForElev(pos.getY(), data.getAverageSeaLevelTemp(pos));
        final float groundwater = data.getAverageGroundwater(pos);
        final int forest = data.getForestType().getDensity();
        final float currentTemperature = getDailyTemperature(model, level, pos);

        final List<Species> species = new ArrayList<>();
        for (Species value : Species.VALUES)
        {
            if (value.livesIn(temperature, groundwater, forest) && currentTemperature >= value.leaveTemperature)
            {
                species.add(value);
            }
        }
        return species;
    }

    /**
     * Vanilla only renders and plays sounds for shoulder entities with the exact vanilla parrot id
     */
    public static Optional<EntityType<?>> asVanillaParrot(Optional<EntityType<?>> type)
    {
        return type.map(t -> t == TFCEntities.PARROT.get() ? EntityType.PARROT : t);
    }

    private static float getDailyTemperature(ClimateModel model, LevelReader level, BlockPos pos)
    {
        final ICalendar calendar = Calendars.get(level);
        final long calendarTicks = calendar.getCalendarTicks();
        return model.getTimeAverageTemperature(level, pos, calendarTicks - ICalendar.CALENDAR_TICKS_IN_DAY, calendarTicks, calendar.getCalendarDaysInMonth());
    }

    private static float getFoodHealing(ItemStack stack)
    {
        if (Helpers.isItem(stack, Tags.Items.SEEDS)) return 1f;
        if (Helpers.isItem(stack, TFCTags.Items.GRAINS)) return 2f;
        return 3f; // berries
    }

    public final AnimationState idleAnimationState = new AnimationState();

    private long nextLeaveCheckTick = Long.MIN_VALUE;
    private IdleAnimation idleAnimation = IdleAnimation.PREEN;
    private int idleAnimationTicks, idleAnimationCooldown;
    private float idleAnimationWeight, prevIdleAnimationWeight;

    public TFCParrot(EntityType<? extends Parrot> type, Level level)
    {
        super(type, level);
    }

    public Species getSpecies()
    {
        return Species.VALUES[entityData.get(DATA_SPECIES)];
    }

    public void setSpecies(Species species)
    {
        entityData.set(DATA_SPECIES, species.ordinal());
    }

    public IdleAnimation getIdleAnimation()
    {
        return idleAnimation;
    }

    /**
     * Idle animations fade out while the parrot is moving, and back in when it stops, so they never snap
     */
    public float getIdleAnimationWeight(float partialTick)
    {
        return Mth.lerp(partialTick, prevIdleAnimationWeight, idleAnimationWeight);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().isClientSide)
        {
            final boolean still = (onGround() || isInSittingPose()) && !isPartyParrot() && distanceToSqr(xo, getY(), zo) < 1.0E-4;
            prevIdleAnimationWeight = idleAnimationWeight;
            idleAnimationWeight = Mth.approach(idleAnimationWeight, still ? 1f : 0f, 0.2f);
            if (idleAnimationState.isStarted() && ++idleAnimationTicks > MAX_IDLE_ANIMATION_TICKS)
            {
                idleAnimationState.stop();
            }
        }
    }

    @Override
    public void handleEntityEvent(byte id)
    {
        if (id >= IDLE_ANIMATION_EVENT && id < IDLE_ANIMATION_EVENT + IdleAnimation.VALUES.length)
        {
            idleAnimation = IdleAnimation.VALUES[id - IDLE_ANIMATION_EVENT];
            idleAnimationState.start(tickCount);
            idleAnimationTicks = 0;
        }
        else
        {
            super.handleEntityEvent(id);
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_SPECIES, 0);
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
        // A spawned group is a flock of a single species
        if (spawnData instanceof SpeciesGroupData group)
        {
            setSpecies(group.species);
        }
        else
        {
            final List<Species> species = getClimateSpecies(Climate.get(level()), level, blockPosition());
            setSpecies(species.isEmpty() ? Util.getRandom(Species.VALUES, random) : species.get(random.nextInt(species.size())));
            if (spawnData == null)
            {
                spawnData = new SpeciesGroupData(getSpecies());
            }
        }
        return super.finalizeSpawn(level, difficulty, spawnType, spawnData);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType)
    {
        // Natural spawning picks a random y, which may be underground
        final BlockPos pos = blockPosition();
        final int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
        return pos.getY() >= surfaceY
            && level.getFluidState(pos.atY(surfaceY - 1)).isEmpty()
            && !getClimateSpecies(Climate.get(level()), level, pos).isEmpty()
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
        if (idleAnimationCooldown > 0)
        {
            idleAnimationCooldown--;
        }
        else if ((onGround() || isInSittingPose()) && getNavigation().isDone() && !isPartyParrot())
        {
            // Picked on the server, so every player sees the same idle at the same time
            idleAnimationCooldown = 100 + random.nextInt(200);
            level().broadcastEntityEvent(this, (byte) (IDLE_ANIMATION_EVENT + random.nextInt(IdleAnimation.VALUES.length)));
        }
        if (isTame() || isPersistenceRequired() || isLeashed()) return;

        final ICalendar calendar = Calendars.get(level());
        final long ticks = calendar.getTicks();
        if (ticks < nextLeaveCheckTick) return;
        nextLeaveCheckTick = ticks + calendar.getTicksInHour() * ICalendar.HOURS_IN_DAY;

        // Wild parrots leave for the winter, and normal spawning brings them back once it warms up again
        if (getDailyTemperature(Climate.get(level()), level(), blockPosition()) < getSpecies().leaveTemperature && level().getNearestPlayer(this, 32) == null)
        {
            discard();
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putLong("nextLeaveCheck", nextLeaveCheckTick);
        tag.putString("species", getSpecies().getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        nextLeaveCheckTick = EntityHelpers.getLongOrDefault(tag, "nextLeaveCheck", Long.MIN_VALUE);
        setSpecies(Species.byName(tag.getString("species")));
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack stack = player.getItemInHand(hand);
        if (Helpers.isItem(stack, TFCTags.Items.PARROT_FOOD) && !FoodCapability.isRotten(stack) && (!isTame() || getHealth() < getMaxHealth()))
        {
            // Same as vanilla taming, but with TFC food, which also heals
            if (!level().isClientSide)
            {
                heal(getFoodHealing(stack));
            }
            stack.consume(1, player);
            if (!isSilent())
            {
                level().playSound(null, getX(), getY(), getZ(), SoundEvents.PARROT_EAT, getSoundSource(), 1f, 1f + (random.nextFloat() - random.nextFloat()) * 0.2f);
            }
            if (!level().isClientSide && !isTame())
            {
                if (random.nextInt(10) == 0 && !EventHooks.onAnimalTame(this, player))
                {
                    tame(player);
                    level().broadcastEntityEvent(this, EntityEvent.TAMING_SUCCEEDED);
                }
                else
                {
                    level().broadcastEntityEvent(this, EntityEvent.TAMING_FAILED);
                }
            }
            return InteractionResult.sidedSuccess(level().isClientSide);
        }
        if (!isTame() && Helpers.isItem(stack, ItemTags.PARROT_FOOD))
        {
            return InteractionResult.PASS; // only TFC parrot food tames, not the vanilla tag
        }
        return super.mobInteract(player, hand);
    }

    public enum Species implements StringRepresentable
    {
        // Vanilla colours
        SCARLET_MACAW(21, 40, 250, 500, 2, 4, 10),
        HYACINTH_MACAW(20, 40, 150, 300, 0, 2, 10),
        MILITARY_MACAW(16, 26, 150, 350, 1, 4, 10),
        BLUE_AND_YELLOW_MACAW(21, 40, 350, 500, 0, 4, 10),
        COCKATIEL(12, 30, 60, 200, 0, 1, 10),
        // Arid
        BUDGERIGAR(20, 40, 0, 150, 0, 1, 10),
        GALAH(15, 40, 50, 250, 0, 1, 5),
        ROSY_FACED_LOVEBIRD(20, 40, 30, 150, 0, 1, 10),
        // Tropical
        SUN_CONURE(22, 40, 150, 350, 1, 4, 10),
        RAINBOW_LORIKEET(18, 40, 250, 500, 1, 4, 10),
        AFRICAN_GREY(22, 40, 350, 500, 2, 4, 10),
        // Temperate
        MONK_PARAKEET(6, 24, 80, 300, 0, 2, -5),
        SULPHUR_CRESTED_COCKATOO(8, 28, 200, 450, 1, 4, 0);

        public static final Species[] VALUES = values();
        public static final StringRepresentable.EnumCodec<Species> CODEC = StringRepresentable.fromEnum(Species::values);

        public static Species byName(String name)
        {
            return CODEC.byName(name, SCARLET_MACAW);
        }

        private final String serializedName;
        private final float minTemperature, maxTemperature, minGroundwater, maxGroundwater;
        private final int minForest, maxForest;
        private final float leaveTemperature; // wild parrots leave, and don't spawn, when the past day's average temperature is below this

        Species(float minTemperature, float maxTemperature, float minGroundwater, float maxGroundwater, int minForest, int maxForest, float leaveTemperature)
        {
            this.serializedName = name().toLowerCase(Locale.ROOT);
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.minGroundwater = minGroundwater;
            this.maxGroundwater = maxGroundwater;
            this.minForest = minForest;
            this.maxForest = maxForest;
            this.leaveTemperature = leaveTemperature;
        }

        public boolean livesIn(float temperature, float groundwater, int forest)
        {
            return temperature >= minTemperature && temperature <= maxTemperature
                && groundwater >= minGroundwater && groundwater <= maxGroundwater
                && forest >= minForest && forest <= maxForest;
        }

        @Override
        public String getSerializedName()
        {
            return serializedName;
        }
    }

    public enum IdleAnimation
    {
        PREEN, HEAD_TILT, WING_STRETCH, TAIL_FLICK, FLUFF;

        public static final IdleAnimation[] VALUES = values();
    }

    static class SpeciesGroupData extends AgeableMob.AgeableMobGroupData
    {
        private final Species species;

        SpeciesGroupData(Species species)
        {
            super(false);
            this.species = species;
        }
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
            nodeEvaluator = new TFCParrotNodeEvaluator();
            nodeEvaluator.setCanPassDoors(true);
            return new PathFinder(nodeEvaluator, maxVisitedNodes);
        }
    }

    static class TFCParrotNodeEvaluator extends FlyNodeEvaluator
    {
        private final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        @Override
        public PathType getPathType(PathfindingContext context, int x, int y, int z)
        {
            // Leaves are solid for parrots, so avoid them like vanilla parrots avoid vanilla leaves
            return context.getBlockState(cursor.set(x, y, z)).getBlock() instanceof ILeavesBlock ? PathType.LEAVES : super.getPathType(context, x, y, z);
        }
    }
}