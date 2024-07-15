/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.pet;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import com.mojang.serialization.Dynamic;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.ai.PredicateMoveControl;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.pet.TamableAi;
import net.dries007.tfc.common.entities.livestock.Mammal;
import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.common.entities.livestock.TFCAnimalProperties;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.util.calendar.Calendars;

public abstract class TamableMammal extends Mammal implements OwnableEntity
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 0.3F).add(Attributes.ATTACK_DAMAGE, 2f);
    }

    public static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Byte> DATA_PET_FLAGS = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.BYTE);
    public static final EntityDataAccessor<Integer> DATA_COLLAR_COLOR = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.INT);

    public final AnimationState sleepingAnimation = new AnimationState();
    public final AnimationState sittingAnimation = new AnimationState();

    private static final int SLEEPING_FLAG = 1;
    private static final int SITTING_FLAG = 4;
    private static final int INTERESTED_FLAG = 8;
    private static final int UNUSED_FLAG_2 = 16;

    private final Supplier<SoundEvent> sleeping;

    private Command command = Command.RELAX; // used to restore the last activity when reloading
    private boolean needsCommandUpdate = false;

    public TamableMammal(EntityType<? extends TFCAnimal> animal, Level level, TFCSounds.EntityId sounds, MammalConfig config)
    {
        super(animal, level, sounds, config);
        sleeping = sounds.sleep().orElseThrow();
        moveControl = new PredicateMoveControl<>(this, e -> !e.isSitting() && !e.isSleeping());
    }

    @Override
    protected Brain.Provider<? extends TamableMammal> brainProvider()
    {
        return Brain.provider(TamableAi.MEMORY_TYPES, TamableAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return TamableAi.makeBrain(brainProvider().makeBrain(dynamic));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Brain<? extends TamableMammal> getBrain()
    {
        return (Brain<TamableMammal>) super.getBrain();
    }

    @SuppressWarnings("unchecked")
    public void tickBrain()
    {
        ((Brain<TamableMammal>) getBrain()).tick((ServerLevel) level(), this);
        TamableAi.updateActivity(this, tickCount % 20 == 0);
    }

    @Override
    public void createGenes(CompoundTag tag, TFCAnimalProperties male)
    {
        super.createGenes(tag, male);
        if (getOwnerUUID() != null)
        {
            tag.putUUID("owner", getOwnerUUID());
        }
    }

    @Override
    public void applyGenes(CompoundTag tag, MammalProperties baby)
    {
        super.applyGenes(tag, baby);
        if (baby instanceof TamableMammal tamable)
        {
            if (tag.hasUUID("owner"))
            {
                tamable.setOwnerUUID(tag.getUUID("owner"));
            }
        }
    }

    @Override
    public void tick()
    {
        if (level().isClientSide)
        {
            EntityHelpers.startOrStop(sittingAnimation, isSitting(), tickCount);
            EntityHelpers.startOrStop(sleepingAnimation, isSleeping(), tickCount);
            if (isSleeping() && getRandom().nextInt(10) == 0)
            {
                level().addParticle(TFCParticles.SLEEP.get(), getX(), getY() + getEyeHeight(), getZ(), 0.01, 0.05, 0.01);
            }
        }
        super.tick();
        if (needsCommandUpdate && command.activity != null)
        {
            getBrain().setActiveActivityIfPossible(command.activity.get());
            needsCommandUpdate = false;
        }
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNER, Optional.empty());
        builder.define(DATA_PET_FLAGS, (byte) 0);
        builder.define(DATA_COLLAR_COLOR, DyeColor.WHITE.getId());
    }

    public DyeColor getCollarColor()
    {
        return DyeColor.byId(this.entityData.get(DATA_COLLAR_COLOR));
    }

    public void setCollarColor(DyeColor color)
    {
        this.entityData.set(DATA_COLLAR_COLOR, color.getId());
    }

    /**
     * Used to determine if the command can be on the pet screen on the client, and check permission for commands on the server
     */
    public boolean willListenTo(Command command, boolean isClientSide)
    {
        return command != Command.SIT || getHealth() >= 5f;
    }

    /**
     * Called on the server to process a command being received.
     */
    public void receiveCommand(ServerPlayer player, Command command)
    {
        if (isOwnedBy(player))
        {
            switch (command)
            {
                case RELAX -> {
                    setSitting(false);
                    setSleeping(false);
                }
                case SIT -> {
                    setSitting(true);
                    setSleeping(false);
                    getBrain().setMemory(TFCBrain.SIT_TIME.get(), Calendars.SERVER.getTicks());
                    getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
                    getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
                }
                case HOME -> {
                    getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level().dimension(), player.blockPosition()));
                    command = Command.RELAX; // 'home' isn't a constant state, it defaults to relax after doing its thing
                }
                case FOLLOW, HUNT -> {
                    setSitting(false);
                    setSleeping(false);
                    getBrain().eraseMemory(TFCBrain.SIT_TIME.get());
                }
            }
            this.command = command;
            Activity commandedActivity = Command.getActivity(command);
            if (commandedActivity != null)
            {
                getBrain().setActiveActivityIfPossible(commandedActivity);
            }
        }
        else
        {
            player.displayClientMessage(Component.translatable("tfc.pet.not_owner"), true);
        }
    }

    public void setCommand(Command command)
    {
        this.command = command;
    }

    public void refreshCommandOnNextTick()
    {
        needsCommandUpdate = true;
    }

    @Override
    public boolean isSleeping()
    {
        return (entityData.get(DATA_PET_FLAGS) & SLEEPING_FLAG) != 0;
    }

    public void setSleeping(boolean sleep)
    {
        entityData.set(DATA_PET_FLAGS, setBit(entityData.get(DATA_PET_FLAGS), SLEEPING_FLAG, sleep));
    }

    public boolean isSitting()
    {
        return (entityData.get(DATA_PET_FLAGS) & SITTING_FLAG) != 0;
    }

    public void setSitting(boolean sitting)
    {
        entityData.set(DATA_PET_FLAGS, setBit(entityData.get(DATA_PET_FLAGS), SITTING_FLAG, sitting));
    }

    public boolean isInterested()
    {
        return (entityData.get(DATA_PET_FLAGS) & INTERESTED_FLAG) != 0;
    }

    public void setInterested(boolean interested)
    {
        entityData.set(DATA_PET_FLAGS, setBit(entityData.get(DATA_PET_FLAGS), INTERESTED_FLAG, interested));
    }

    private byte setBit(byte oldBit, int offset, boolean value)
    {
        return (byte) (value ? (oldBit | offset) : (oldBit & ~offset));
    }

    @Nullable
    @Override
    public UUID getOwnerUUID()
    {
        return entityData.get(DATA_OWNER).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID id)
    {
        entityData.set(DATA_OWNER, Optional.ofNullable(id));
    }

    // vanilla uses a try catch here. do we need to?
    @Nullable
    @Override
    public LivingEntity getOwner()
    {
        try
        {
            final UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level().getPlayerByUUID(uuid);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand)
    {
        final ItemStack held = player.getItemInHand(hand);
        if (held.getItem() instanceof DyeItem dye)
        {
            final DyeColor color = dye.getDyeColor();
            if (color != getCollarColor())
            {
                setCollarColor(color);
                if (!player.getAbilities().instabuild)
                {
                    held.shrink(1);
                }
                return InteractionResult.SUCCESS;
            }
        }
        if (held.isEmpty() && player.isShiftKeyDown() && getOwner() != null && isOwnedBy(player) && !isOnFire())
        {
            if (level().isClientSide)
            {
                ClientHelpers.openPetScreen(this);
            }
            return InteractionResult.SUCCESS;
        }
        if (getFamiliarity() + 0.06f > 0.15f && getOwnerUUID() == null && isFood(held) && isHungry())
        {
            tame(player);
        }
        return super.mobInteract(player, hand);
    }

    public void spawnTamingParticles(ParticleOptions particle)
    {
        if (level() instanceof ServerLevel serverLevel)
        {
            serverLevel.sendParticles(particle, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), 7, random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D, 1);
        }
    }

    public void tame(Player player)
    {
        this.setOwnerUUID(player.getUUID());
        spawnTamingParticles(ParticleTypes.HEART);
        if (player instanceof ServerPlayer serverPlayer)
        {
            CriteriaTriggers.TAME_ANIMAL.trigger(serverPlayer, this);
        }
    }

    @Override
    public boolean canAttack(LivingEntity target)
    {
        return !isOwnedBy(target) && super.canAttack(target);
    }

    public boolean isOwnedBy(@Nullable Entity entity)
    {
        return entity != null && entity.equals(getOwner());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (getOwnerUUID() != null)
        {
            tag.putUUID("Owner", getOwnerUUID());
        }
        tag.putInt("command", command.ordinal());
        tag.putByte("petFlags", entityData.get(DATA_PET_FLAGS));
        tag.putInt("CollarColor", getCollarColor().getId());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner"))
        {
            setOwnerUUID(tag.getUUID("Owner"));
        }
        command = Command.valueOf(tag.getInt("command"));
        entityData.set(DATA_PET_FLAGS, tag.getByte("petFlags"));
        setCollarColor(DyeColor.byId(tag.getInt("CollarColor")));
        refreshCommandOnNextTick();
    }

    @Override
    public void die(DamageSource source)
    {
        final Component deathMessage = getCombatTracker().getDeathMessage();
        super.die(source);
        if (dead && getOwner() instanceof ServerPlayer serverPlayer && level().getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES))
        {
            serverPlayer.sendSystemMessage(deathMessage);
        }
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return isSleeping() ? sleeping.get() : super.getAmbientSound();
    }

    public enum Command
    {
        RELAX(TFCBrain.IDLE_AT_HOME), // hang around near home
        HOME(null), // set a new home position
        SIT(TFCBrain.SIT), // sit for a period of time
        FOLLOW(TFCBrain.FOLLOW), // follow but don't participate in combat
        HUNT(TFCBrain.HUNT); // follow and participate in combat

        public static final Command[] VALUES = values();
        public static final StreamCodec<ByteBuf, Command> STREAM = ByteBufCodecs.BYTE.map(Command::valueOf, c -> (byte) c.ordinal());

        public static Command valueOf(int id)
        {
            return VALUES[id];
        }

        @Nullable
        public static Activity getActivity(Command command)
        {
            return command.activity == null ? null : command.activity.get();
        }

        @Nullable
        private final Supplier<Activity> activity;

        Command(@Nullable Supplier<Activity> activity)
        {
            this.activity = activity;
        }
    }
}
