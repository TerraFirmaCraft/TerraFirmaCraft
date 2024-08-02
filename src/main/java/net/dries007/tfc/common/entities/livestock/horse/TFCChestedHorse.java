/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Container;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.common.entities.ai.TFCGroundPathNavigation;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.CommonAnimalData;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.config.animals.AnimalConfig;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.util.Helpers;

public abstract class TFCChestedHorse extends AbstractChestedHorse implements HorseProperties
{
    public static boolean vanillaParentingCheck(AbstractHorse horse)
    {
        return !horse.isVehicle() && !horse.isPassenger() && horse.isTamed() && !horse.isBaby();
    }

    private static final CommonAnimalData ANIMAL_DATA = CommonAnimalData.create(TFCChestedHorse.class);
    private static final EntityDataAccessor<Long> PREGNANT_TIME = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.LONG);
    private static final EntityDataAccessor<ItemStack> CHEST_ITEM = SynchedEntityData.defineId(TFCChestedHorse.class, EntityDataSerializers.ITEM_STACK);

    private boolean overburdened = false;
    @Nullable private CompoundTag genes;
    private final Supplier<? extends SoundEvent> ambient;
    private final Supplier<? extends SoundEvent> hurt;
    private final Supplier<? extends SoundEvent> death;
    private final Supplier<? extends SoundEvent> step;
    private final Supplier<? extends SoundEvent> eat;
    private final Supplier<? extends SoundEvent> angry;
    private final AnimalConfig config;
    private final MammalConfig mammalConfig;

    public TFCChestedHorse(EntityType<? extends TFCChestedHorse> type, Level level, TFCSounds.EntityId sounds, Supplier<? extends SoundEvent> eatSound, Supplier<? extends SoundEvent> angrySound, MammalConfig config)
    {
        super(type, level);
        this.ambient = sounds.ambient();
        this.hurt = sounds.hurt();
        this.death = sounds.death();
        this.step = sounds.step();
        this.eat = eatSound;
        this.angry = angrySound;
        this.config = config.inner();
        this.mammalConfig = config;
    }

    // HORSE SPECIFIC STUFF

    public ItemStack getChestItem()
    {
        return entityData.get(CHEST_ITEM);
    }

    public void setChestItem(ItemStack stack)
    {
        entityData.set(CHEST_ITEM, stack);
    }

    @Override
    public boolean hasChest()
    {
        return !getChestItem().isEmpty() && Helpers.isItem(getChestItem(), Tags.Items.CHESTS_WOODEN);
    }

    @Override
    public void containerChanged(Container container)
    {
        super.containerChanged(container);
        overburdened = Helpers.countOverburdened(container) == 1;
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 3);
        goalSelector.addGoal(3, new TemptGoal(this, 1.25f, Ingredient.of(getFoodTag()), false));
        goalSelector.addGoal(5, new TFCAvoidEntityGoal<>(this, PathfinderMob.class, 8f, 1.6f, 1.4f, TFCTags.Entities.HUNTS_LAND_PREY));
    }

    @Override
    protected void dropEquipment()
    {
        if (!getChestItem().isEmpty())
        {
            if (!level().isClientSide)
            {
                spawnAtLocation(getChestItem());
            }
            setChestItem(ItemStack.EMPTY);
        }
        super.dropEquipment();
    }

    @Override
    public boolean canMate(Animal otherAnimal)
    {
        return otherAnimal instanceof TFCAnimalProperties other && this.getGender() != other.getGender() && this.isReadyToMate() && other.isReadyToMate();
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        // triggers feeding actions
        InteractionResult result = HorseProperties.super.mobInteract(player, hand);
        if (result == InteractionResult.PASS)
        {
            ItemStack stack = player.getItemInHand(hand);
            if (!this.isBaby())
            {
                if (this.isTamed() && player.isSecondaryUseActive())
                {
                    // interacting specifically with barrels
                    if (!hasChest() && !getChestItem().isEmpty())
                    {
                        // removing the barrel
                        if (stack.isEmpty())
                        {
                            ItemHandlerHelper.giveItemToPlayer(player, getChestItem().copy());
                            setChestItem(ItemStack.EMPTY);
                            return InteractionResult.sidedSuccess(this.level().isClientSide);
                        }
                        else
                        {
                            final IFluidHandlerItem destFluidItemHandler = stack.getCapability(Capabilities.FluidHandler.ITEM);
                            final IFluidHandlerItem sourceFluidItemHandler = getChestItem().getCapability(Capabilities.FluidHandler.ITEM);
                            if (destFluidItemHandler != null && sourceFluidItemHandler != null)
                            {
                                if (FluidHelpers.transferBetweenItemAndOther(getChestItem(), destFluidItemHandler, sourceFluidItemHandler, destFluidItemHandler, FluidHelpers.Transfer.FILL, level(), blockPosition(), FluidHelpers.with(player, hand)))
                                {
                                    return InteractionResult.sidedSuccess(level().isClientSide);
                                }
                            }

                        }
                    }
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                if (this.isVehicle())
                {
                    return InteractionResult.PASS; // calls super in vanilla, we don't need to
                }
            }

            if (!stack.isEmpty())
            {
                // food eating in vanilla is here we handled it in interface super

                if (!this.isTamed())
                {
                    this.makeMad();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                if (this.getChestItem().isEmpty() && Helpers.isItem(stack, TFCTags.Items.CARRIED_BY_HORSE))
                {
                    this.setChestItem(stack.copy()); // set an explicit chest item
                    this.playChestEquipsSound();
                    if (!player.getAbilities().instabuild)
                    {
                        stack.shrink(1);
                    }

                    this.createInventory();
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }

                if (!this.isBaby() && !this.isSaddled() && stack.is(Items.SADDLE))
                {
                    this.openCustomInventoryScreen(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
            }

            if (this.isBaby())
            {
                return InteractionResult.PASS; // calls super in vanilla but we don't need to
            }
            else
            {
                if (isTamed() && getOwnerUUID() == null) // tfc: add an owner
                {
                    tameWithName(player);
                }
                this.doPlayerRide(player);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
        }
        return result;
    }

    @Override
    public boolean isTamed()
    {
        return getFamiliarity() > TAMED_FAMILIARITY;
    }

    @Override
    public SlotAccess getSlot(int slot)
    {
        return slot == AbstractHorse.CHEST_SLOT_OFFSET ? new SlotAccess()
        {
            @Override
            public ItemStack get()
            {
                return getChestItem();
            }

            @Override
            public boolean set(ItemStack stack)
            {
                TFCChestedHorse horse = TFCChestedHorse.this;
                if (stack.isEmpty())
                {
                    if (horse.hasChest())
                    {
                        horse.setChestItem(ItemStack.EMPTY);
                        horse.createInventory();
                    }
                    return true;
                }
                else if (Helpers.isItem(stack, TFCTags.Items.CARRIED_BY_HORSE))
                {
                    if (!horse.hasChest())
                    {
                        horse.setChestItem(stack);
                        horse.createInventory();
                    }
                    return true;
                }
                else
                {
                    return false;
                }
            }
        } : super.getSlot(slot);
    }

    @Override
    protected SoundEvent getEatingSound()
    {
        super.getEatingSound();
        return eat.get();
    }

    @Override
    protected SoundEvent getAngrySound()
    {
        super.getAngrySound();
        return angry.get();
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        return Helpers.isBlock(level().getBlockState(blockPosition()), TFCTags.Blocks.PLANTS) ? 1.0F : super.getBlockSpeedFactor();
    }

    @Override
    public boolean canBeLeashed()
    {
        return super.canBeLeashed() && !overburdened;
    }

    @Nullable
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
        animalData().define(builder);
        builder.define(PREGNANT_TIME, -1L);
        builder.define(CHEST_ITEM, ItemStack.EMPTY);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt)
    {
        nbt.put("chestItem", getChestItem().saveOptional(registryAccess()));
        super.addAdditionalSaveData(nbt);
        saveCommonAnimalData(nbt);
        nbt.putBoolean("overburdened", overburdened);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt)
    {
        setChestItem(ItemStack.parseOptional(registryAccess(), nbt.getCompound("chestItem")));
        super.readAdditionalSaveData(nbt);
        readCommonAnimalData(nbt);
        overburdened = nbt.getBoolean("overburdened");
    }

    @Override
    public boolean isBaby()
    {
        return getAgeType() == Age.CHILD;
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

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        return HorseProperties.super.getBreedOffspring(level, other);
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
    public void tick()
    {
        super.tick();
        if (level().getGameTime() % 20 == 0)
        {
            tickAnimalData();
            if (overburdened)
            {
                addEffect(Helpers.getOverburdened(true));
            }
        }
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return HorseProperties.super.isFood(stack);
    }

    @Override
    public Component getTypeName()
    {
        return getGenderedTypeName();
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        super.getAmbientSound();
        return ambient.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource src)
    {
        super.getHurtSound(src);
        return hurt.get();
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        super.getDeathSound();
        return death.get();
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState block)
    {
        super.playStepSound(pos, block);
        this.playSound(step.get(), 0.15F, 1.0F);
    }

    @Override
    @SuppressWarnings("deprecation")
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
}
