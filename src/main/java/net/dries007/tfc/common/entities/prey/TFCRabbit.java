/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforge.neoforged.event.ForgeEventFactory;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.common.entities.livestock.CommonAnimalData;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCRabbit extends Rabbit implements MammalProperties
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 12.0D).add(Attributes.MOVEMENT_SPEED, 0.3F);
    }

    private static final EntityDataAccessor<Boolean> GENDER = SynchedEntityData.defineId(TFCRabbit.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> BIRTHDAY = SynchedEntityData.defineId(TFCRabbit.class, EntityHelpers.LONG_SERIALIZER);
    private static final EntityDataAccessor<Float> FAMILIARITY = SynchedEntityData.defineId(TFCRabbit.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Integer> USES = SynchedEntityData.defineId(TFCRabbit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FERTILIZED = SynchedEntityData.defineId(TFCRabbit.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Long> OLD_DAY = SynchedEntityData.defineId(TFCRabbit.class, EntityHelpers.LONG_SERIALIZER);
    private static final EntityDataAccessor<Integer> GENETIC_SIZE = SynchedEntityData.defineId(TFCRabbit.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Long> LAST_FED = SynchedEntityData.defineId(TFCRabbit.class, EntityHelpers.LONG_SERIALIZER);
    private static final CommonAnimalData ANIMAL_DATA = new CommonAnimalData(GENDER, BIRTHDAY, FAMILIARITY, USES, FERTILIZED, OLD_DAY, GENETIC_SIZE, LAST_FED);
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(TFCRabbit.class, EntityHelpers.LONG_SERIALIZER);

    private long lastFDecay; //Last time(in days) this entity's familiarity had decayed
    private long matingTime; //The last time(in ticks) this male tried fertilizing females
    @Nullable private CompoundTag genes;
    private TFCAnimalProperties.Age lastAge = TFCAnimalProperties.Age.CHILD;
    private final AnimalConfig config;
    private final MammalConfig mammalConfig;
    private int moreCarrotTicks;

    public TFCRabbit(EntityType<? extends Rabbit> type, Level level, MammalConfig config)
    {
        super(type, level);
        this.config = config.inner();
        this.mammalConfig = config;
        this.matingTime = Calendars.get(level).getTicks();
        this.lastFDecay = Calendars.get(level).getTotalDays();
    }

    @Override
    public void customServerAiStep()
    {
        super.customServerAiStep();
        if (moreCarrotTicks > 0)
        {
            moreCarrotTicks -= random.nextInt(3);
            if (moreCarrotTicks < 0)
            {
                moreCarrotTicks = 0;
            }
        }
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 3); // tempt
        EntityHelpers.removeGoalOfPriority(goalSelector, 4); // avoid goals
        EntityHelpers.removeGoalOfPriority(goalSelector, 5); // vanilla raid garden
        goalSelector.addGoal(3, new TemptGoal(this, 1.25f, Ingredient.of(getFoodTag()), false));
        goalSelector.addGoal(4, new TFCAvoidEntityGoal<>(this, PathfinderMob.class, 8.0F, 2.2D, 2.2D, TFCTags.Entities.HUNTS_LAND_PREY));
        goalSelector.addGoal(5, new RaidGardenGoal(this));
    }

    @Override
    protected float getJumpPower()
    {
        return 0.42F * this.getBlockJumpFactor() + this.getJumpBoostPower();
    }

    @Override
    protected void jumpFromGround()
    {
        super.jumpFromGround();
        if (getDeltaMovement().x == 0f || getDeltaMovement().z == 0f)
        {
            final Vec3 wanted = new Vec3(moveControl.getWantedX(), moveControl.getWantedY(), moveControl.getWantedZ());
            final Vec3 delta = wanted.subtract(position()).normalize().scale(0.1);
            setDeltaMovement(getDeltaMovement().add(delta));
        }

    }

    @Override
    @Nullable
    public TFCRabbit getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        final AgeableMob mob = MammalProperties.super.getBreedOffspring(level, other);
        return mob instanceof TFCRabbit rabbit ? rabbit : null;
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties male)
    {
        MammalProperties.super.createGenes(tag, male);
        tag.putString("variant1", getVariant().getSerializedName());
        if (male instanceof TFCRabbit rabbit)
            tag.putString("variant2", rabbit.getVariant().getSerializedName());
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties baby)
    {
        MammalProperties.super.applyGenes(tag, baby);
        if (baby instanceof TFCRabbit rabbit)
        {
            if (tag.contains("variant2", Tag.TAG_INT) && random.nextInt(10) != 0)
            {
                rabbit.setVariant(Variant.byId(random.nextBoolean() ? tag.getInt("variant1") : tag.getInt("variant2")));
            }
            else if (level() instanceof ServerLevelAccessor server)
            {
                rabbit.setVariant(getRandomRabbitType(server, blockPosition()));
            }
        }
    }

    @Override
    public TagKey<Item> getFoodTag()
    {
        return TFCTags.Items.RABBIT_FOOD;
    }

    // BEGIN COPY PASTE FROM TFC ANIMAL

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        TFCRabbit other = (TFCRabbit) otherAnimal;
        return this.getGender() != other.getGender() && this.isReadyToMate() && other.isReadyToMate();
    }

    @Override
    public TFCAnimalProperties.Age getLastAge()
    {
        return lastAge;
    }

    @Override
    public void setLastAge(TFCAnimalProperties.Age lastAge)
    {
        this.lastAge = lastAge;
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType reason, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag tag)
    {
        spawnData = super.finalizeSpawn(level, difficulty, reason, spawnData, tag);
        if (reason != MobSpawnType.BREEDING)
        {
            initCommonAnimalData(level, difficulty, reason);
            this.setVariant(getRandomRabbitType(level, blockPosition()));
        }
        setPregnantTime(-1L);
        return spawnData;
    }

    @Override
    public MammalConfig getMammalConfig()
    {
        return mammalConfig;
    }

    @Override
    public long getPregnantTime()
    {
        return entityData.get(PREGNANT_TIME);
    }

    @Override
    public void setPregnantTime(long day)
    {
        entityData.set(PREGNANT_TIME, day);
    }

    @Override
    public void setGenes(@Nullable CompoundTag tag)
    {
        genes = tag;
    }

    @Override
    @Nullable
    public CompoundTag getGenes()
    {
        return genes;
    }

    @Override
    public AnimalConfig animalConfig()
    {
        return config;
    }

    @Override
    public CommonAnimalData animalData()
    {
        return ANIMAL_DATA;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        registerCommonData(builder);
        builder.define(PREGNANT_TIME, -1L);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        saveCommonAnimalData(nbt);
        nbt.putInt("TFCMoreCarrotTicks", moreCarrotTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        readCommonAnimalData(nbt);
        moreCarrotTicks = nbt.getInt("TFCMoreCarrotTicks");
    }

    @Override
    public boolean isBaby()
    {
        return getAgeType() == TFCAnimalProperties.Age.CHILD;
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0); // no-op vanilla aging
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data)
    {
        super.onSyncedDataUpdated(data);
        if (BIRTHDAY.equals(data))
        {
            refreshDimensions();
        }
    }

    @Override
    public long getLastFamiliarityDecay()
    {
        return lastFDecay;
    }

    @Override
    public void setLastFamiliarityDecay(long days)
    {
        lastFDecay = days;
    }

    @Override
    public void setMated(long ticks)
    {
        matingTime = ticks;
    }

    @Override
    public long getMated()
    {
        return matingTime;
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 20 == 0)
        {
            // legacy breeding behavior
            if (!getEntity().level().isClientSide() && getGender() == Gender.MALE && isReadyToMate())
            {
                EntityHelpers.findFemaleMate((Animal & TFCAnimalProperties) this);
            }
            tickAnimalData();
        }
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return MammalProperties.super.isFood(stack);
    }

    @Override
    public Component getTypeName()
    {
        return getGenderedTypeName();
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    public PathNavigation createNavigation(Level level)
    {
        return new TFCGroundPathNavigation(this, level);
    }

    @Override
    public boolean isInWall()
    {
        return !level().isClientSide && super.isInWall();
    }

    @Override
    protected void pushEntities()
    {
        if (!level().isClientSide) super.pushEntities();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        InteractionResult result = MammalProperties.super.mobInteract(player, hand);
        return result == InteractionResult.PASS ? super.mobInteract(player, hand) : result;
    }

    private Rabbit.Variant getRandomRabbitType(ServerLevelAccessor level, BlockPos pos)
    {
        final int i = random.nextInt(100);
        final ChunkData data = EntityHelpers.getChunkDataForSpawning(level, pos);
        final float rain = data.getRainfall(pos);
        final float temp = data.getAverageTemp(pos);
        if (temp < 0)
        {
            return i < 80 ? Variant.WHITE : Variant.WHITE_SPLOTCHED;
        }
        else if (rain < 125)
        {
            return Variant.GOLD;
        }
        return i < 50 ? Variant.BROWN : (i < 90 ? Variant.SALT : Variant.BLACK);
    }

    public boolean wantsMoreFood()
    {
        return moreCarrotTicks == 0;
    }

    public static class RaidGardenGoal extends MoveToBlockGoal
    {
        private final TFCRabbit rabbit;
        private boolean wantsToRaid;
        private boolean canRaid;

        public RaidGardenGoal(TFCRabbit rabbit)
        {
            super(rabbit, 0.7F, 16);
            this.rabbit = rabbit;
        }

        @Override
        public boolean canUse()
        {
            if (nextStartTick <= 0)
            {
                if (!ForgeEventFactory.getMobGriefingEvent(rabbit.level(), rabbit))
                {
                    return false;
                }
                canRaid = false;
                wantsToRaid = rabbit.wantsMoreFood();
            }

            return super.canUse();
        }

        @Override
        public boolean canContinueToUse()
        {
            return canRaid && super.canContinueToUse();
        }

        @Override
        public void tick()
        {
            super.tick();
            rabbit.getLookControl().setLookAt((double) blockPos.getX() + 0.5D, blockPos.getY() + 1, (double) blockPos.getZ() + 0.5D, 10.0F, (float) rabbit.getMaxHeadXRot());
            if (isReachedTarget())
            {
                Level level = rabbit.level();
                BlockPos abovePos = blockPos.above();
                BlockState aboveState = level.getBlockState(abovePos);
                Block aboveBlock = aboveState.getBlock();
                if (canRaid && Helpers.isBlock(aboveBlock, TFCTags.Blocks.RABBIT_RAIDABLE))
                {
                    level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 2);
                    level.destroyBlock(abovePos, true, rabbit);
                    rabbit.moreCarrotTicks = 40;
                }

                canRaid = false;
                nextStartTick = 10;
            }

        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos)
        {
            BlockState state = level.getBlockState(pos);
            if (Helpers.isBlock(state, TFCTags.Blocks.FARMLAND) && wantsToRaid && !canRaid)
            {
                state = level.getBlockState(pos.above());
                if (Helpers.isBlock(state, TFCTags.Blocks.RABBIT_RAIDABLE))
                {
                    canRaid = true;
                    return true;
                }
            }
            return false;
        }
    }
}
