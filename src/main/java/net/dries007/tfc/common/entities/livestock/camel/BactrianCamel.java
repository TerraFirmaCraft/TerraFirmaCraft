/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.camel;

import java.util.List;
import java.util.function.Supplier;
import com.mojang.serialization.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.IShearable;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.CommonAnimalData;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.entities.livestock.horse.HorseProperties;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.config.animals.ProducingMammalConfig;
import net.dries007.tfc.mixin.accessor.CamelAccessor;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.events.AnimalProductEvent;

public class BactrianCamel extends AbstractCamel implements HorseProperties, IShearable
{
    private static final CommonAnimalData ANIMAL_DATA = CommonAnimalData.create(BactrianCamel.class);
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(BactrianCamel.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<Long> DATA_PRODUCED = SynchedEntityData.defineId(BactrianCamel.class, EntityDataSerializers.LONG);

    protected final Supplier<Integer> produceTicks;
    protected final Supplier<Double> produceFamiliarity;

    @Nullable private CompoundTag genes;
    private final AnimalConfig config;
    private final MammalConfig mammalConfig;
    private final ProducingMammalConfig producingMammalConfig;

    public BactrianCamel(EntityType<? extends Camel> type, Level level, ProducingMammalConfig config)
    {
        super(type, level);
        this.config = config.inner().inner();
        this.mammalConfig = config.inner();
        this.producingMammalConfig = config;
        this.produceTicks = config.produceTicks();
        this.produceFamiliarity = config.produceFamiliarity();
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TFCCamelAi.makeBrain(TFCCamelAi.brainProvider().makeBrain(dynamic));
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties maleProperties)
    {
        super.createGenes(tag, maleProperties);
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties babyProperties)
    {
        super.applyGenes(tag, babyProperties);
    }

    @Override
    public boolean isReadyForAnimalProduct()
    {
        return (getFamiliarity() > produceFamiliarity.get() && hasProduct()) && getAgeType() == Age.ADULT;
    }

    @Override
    public void setProductsCooldown()
    {
        setProducedTick(Calendars.get(level()).getTicks());
    }

    @Override
    public long getProductsCooldown()
    {
        return Math.max(0, produceTicks.get() + getProducedTick() - Calendars.get(level()).getTicks());
    }

    public long getProducedTick()
    {
        return entityData.get(DATA_PRODUCED);
    }

    public void setProducedTick(long producedTick)
    {
        entityData.set(DATA_PRODUCED, producedTick);
    }

    @Override
    public boolean isShearable(@Nullable Player player, ItemStack item, Level level, BlockPos pos)
    {
        return isReadyForAnimalProduct();
    }

    @Override
    public List<ItemStack> onSheared(@Nullable Player player, ItemStack item, Level level, BlockPos pos)
    {
        setProductsCooldown();
        playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);

        AnimalProductEvent event = new AnimalProductEvent(level, pos, player, this, getWoolItem(), item, 1);
        if (!NeoForge.EVENT_BUS.post(event).isCanceled())
        {
            addUses(event.getUses());
        }
        return List.of(event.getProduct());
    }

    @Override
    public boolean hasProduct()
    {
        return getProducedTick() <= 0 || getProductsCooldown() <= 0;
    }

    public ItemStack getWoolItem()
    {
        final int amount = getFamiliarity() > 0.99f ? 2 : 1;
        return new ItemStack(TFCItems.WOOL.get(), amount);
    }

    @Override
    public MutableComponent getProductReadyName()
    {
        return Component.translatable("tfc.jade.product.wool");
    }

    @Override
    public void setInLove(@Nullable Player player) {}

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        if (otherAnimal.getClass() != this.getClass()) return false;
        BactrianCamel other = (BactrianCamel) otherAnimal;
        return this.getGender() != other.getGender()
            && this.isReadyToMate() && other.isReadyToMate()
            && checkExtraBreedConditions(other);
    }

    @Override
    public boolean checkExtraBreedConditions(TFCAnimalProperties otherAnimal)
    {
        if (otherAnimal instanceof BactrianCamel otherCamel)
        {
            return vanillaParentingCheck(this) && vanillaParentingCheck(otherCamel);
        }
        return false;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        InteractionResult result = super.mobInteract(player, hand);
        if (result == InteractionResult.PASS)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (!this.isBaby())
            {
                if (this.isTamed() && player.isSecondaryUseActive())
                {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                if (this.isVehicle())
                {
                    return InteractionResult.PASS;
                }
            }

            if (!stack.isEmpty())
            {
                InteractionResult res = stack.interactLivingEntity(player, this, hand);
                if (res.consumesAction())
                {
                    return res;
                }

                if (!this.isTamed())
                {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                final boolean canBeSaddled = !this.isBaby() && !this.isSaddled() && stack.is(Items.SADDLE);
                if (this.isBodyArmorItem(stack) || canBeSaddled)
                {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }

            if (this.isBaby())
            {
                return InteractionResult.PASS;
            }
            else
            {
                if (isTamed() && getOwnerUUID() == null)
                {
                    tameWithName(player);
                }
                if (canAddPassenger(player))
                {
                    this.doPlayerRide(player);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return result;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger)
    {
        return this.getPassengers().size() <= 1;
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick)
    {
        float f = 0.0F;
        float f1 = (float) (this.isRemoved() ? 0.01F : ((CamelAccessor) this).invoke$getBodyAnchorAnimationYOffset(true, 0.0F, dimensions, partialTick));
        return new Vec3(0.0, (double) f1, (double) (f * partialTick)).yRot(-this.getYRot() * (float) (Math.PI / 180.0));
    }

    @Override
    public boolean isTamed()
    {
        return getFamiliarity() > produceFamiliarity.get();
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType spawnType, @Nullable SpawnGroupData spawnData)
    {
        spawnData = super.finalizeSpawn(level, difficulty, spawnType, spawnData);
        if (spawnType != MobSpawnType.BREEDING)
        {
            initCommonAnimalData(level, difficulty, spawnType);
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
    public @Nullable CompoundTag getGenes()
    {
        return genes;
    }

    @Override
    public void setGenes(@Nullable CompoundTag tag)
    {
        genes = tag;
    }

    @Override
    public CommonAnimalData animalData()
    {
        return ANIMAL_DATA;
    }

    @Override
    public AnimalConfig animalConfig()
    {
        return config;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        animalData().define(builder);
        builder.define(PREGNANT_TIME, -1L);
        builder.define(DATA_PRODUCED, 0L);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        super.addAdditionalSaveData(nbt);
        saveCommonAnimalData(nbt);
        nbt.putLong("produced", getProducedTick());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        super.readAdditionalSaveData(nbt);
        readCommonAnimalData(nbt);
        setProducedTick(nbt.getLong("produced"));
    }

    @Override
    public boolean isBaby()
    {
        return getAgeType() == Age.CHILD;
    }

    @Override
    public void setAge(int age)
    {
        super.setAge(0);
    }

    @Override
    public int getAge()
    {
        return isBaby() ? -24000 : 0;
    }

    @Override
    public @Nullable BactrianCamel getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        final AgeableMob mob = super.getBreedOffspring(level, other);
        return mob instanceof BactrianCamel camel ? camel : null;
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data)
    {
        super.onSyncedDataUpdated(data);
        if (ANIMAL_DATA.birthTick().equals(data))
        {
            refreshDimensions();
        }
    }

    @Override
    protected void customServerAiStep()
    {
        ((Brain<BactrianCamel>) getBrain()).tick((ServerLevel) level(), this);
        TFCCamelAi.updateActivity(this);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 20 == 0)
        {
            tickAnimalData();
        }
    }

    // A sprinting Bactrian Camel is a bit faster than a sprinting player (0.13 vs 0.15)
    @Override
    protected float getRiddenSpeed(Player player)
    {
        float sprintSpeedBonus = 0.0875F;
        float f = player.isSprinting() && this.getJumpCooldown() == 0 ? sprintSpeedBonus : 0.0F;
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) + f;
    }

    @Override
    public float getWalkTargetValue(BlockPos pos, LevelReader level)
    {
        return level.getBlockState(pos.below()).is(TFCTags.Blocks.BUSH_PLANTABLE_ON) ? 10.0F : level.getPathfindingCostFromLightLevels(pos);
    }

    @Override
    protected PathNavigation createNavigation(Level level)
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
}
