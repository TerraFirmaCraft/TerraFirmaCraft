/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.livestock.pet.TFCCat;
import net.dries007.tfc.mixin.accessor.OcelotAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class TFCOcelot extends Ocelot
{
    public static final EntityDataAccessor<Float> DATA_FAMILIARITY = SynchedEntityData.defineId(TFCOcelot.class, EntityDataSerializers.FLOAT);

    private long nextFeedTime = Long.MIN_VALUE;

    public TFCOcelot(EntityType<? extends Ocelot> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 3); // tempt goal
        EntityHelpers.removeGoalOfPriority(goalSelector, 9); // breed goal
        EntityHelpers.removeGoalOfPriority(targetSelector, 1); // avoid / attack goals

        goalSelector.addGoal(3, new OcelotTemptGoal(this, 0.6, Ingredient.of(TFCTags.Items.CAT_FOOD), true));
        targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, LivingEntity.class, true, e -> !(e instanceof Player) && Helpers.isEntity(e, TFCTags.Entities.HUNTED_BY_CATS)));
    }

    @Override
    public boolean removeWhenFarAway(double distance)
    {
        return false;
    }

    public float getFamiliarity()
    {
        return entityData.get(DATA_FAMILIARITY);
    }

    public void setFamiliarity(float amount)
    {
        entityData.set(DATA_FAMILIARITY, Mth.clamp(amount, 0f, 1f));
    }

    public void addFamiliarity(float amount)
    {
        setFamiliarity(getFamiliarity() + amount);
    }

    @Override
    public void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_FAMILIARITY, 0f);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putFloat("familiarity", getFamiliarity());
        tag.putLong("nextFeed", nextFeedTime);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        setFamiliarity(EntityHelpers.getFloatOrDefault(tag, "familiarity", 0f));
        nextFeedTime = EntityHelpers.getLongOrDefault(tag, "nextFeed", Long.MIN_VALUE);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (isFood(held))
        {
            if (!level().isClientSide)
            {
                final long ticks = Calendars.SERVER.getTicks();
                if (ticks > nextFeedTime)
                {
                    addFamiliarity(0.1f);
                    ((OcelotAccessor) this).invoke$setTrusting(true);
                    nextFeedTime = ticks + ICalendar.TICKS_IN_DAY;
                    if (!player.isCreative()) held.shrink(1);
                    playSound(getEatingSound(held), getSoundVolume(), getVoicePitch());
                    if (getFamiliarity() > 0.99f)
                    {
                        final boolean wasBaby = isBaby();
                        final TFCCat cat = convertTo(TFCEntities.CAT.get(), false);
                        if (cat != null && level() instanceof ServerLevelAccessor server)
                        {
                            cat.finalizeSpawn(server, level().getCurrentDifficultyAt(blockPosition()), MobSpawnType.CONVERSION, null);
                            if (!wasBaby)
                            {
                                cat.setBirthDay(Calendars.get(level()).getTotalDays() - 120);
                            }
                        }
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return !FoodCapability.isRotten(stack)
            && Helpers.isItem(stack, TFCTags.Items.CAT_FOOD);
    }

    @Override
    public boolean checkSpawnObstruction(LevelReader level)
    {
        return level.isUnobstructed(this) && !level.containsAnyLiquid(this.getBoundingBox());
    }

    @Override
    public void setInLove(@Nullable Player player) {} // nobody could love an ocelot

    public static class OcelotTemptGoal extends TemptGoal
    {
        private final Ocelot ocelot;

        public OcelotTemptGoal(Ocelot ocelot, double speedMod, Ingredient ingredient, boolean canScare)
        {
            super(ocelot, speedMod, ingredient, canScare);
            this.ocelot = ocelot;
        }

        @Override
        protected boolean canScare()
        {
            return super.canScare() && !((OcelotAccessor) ocelot).invoke$isTrusting();
        }
    }
}
