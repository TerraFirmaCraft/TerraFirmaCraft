/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.pet;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.Util;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import com.mojang.serialization.Dynamic;
import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.entities.ai.PredicateMoveControl;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.livestock.LivestockAi;
import net.dries007.tfc.common.entities.ai.pet.CatAi;
import net.dries007.tfc.common.entities.livestock.Mammal;
import net.dries007.tfc.common.entities.livestock.TFCAnimal;
import net.dries007.tfc.config.animals.MammalConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import org.jetbrains.annotations.Nullable;

public abstract class TamableMammal extends Mammal implements OwnableEntity
{
    public static AttributeSupplier.Builder createAttributes()
    {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0D).add(Attributes.MOVEMENT_SPEED, 2F).add(Attributes.ATTACK_DAMAGE, 2f);
    }

    public static final EntityDataAccessor<Optional<UUID>> DATA_OWNER = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> DATA_COMMAND = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Byte> DATA_PET_FLAGS = SynchedEntityData.defineId(TamableMammal.class, EntityDataSerializers.BYTE);

    private static final int SLEEPING_FLAG = 1;
    private static final int SITTING_FLAG = 4;
    private static final int UNUSED_FLAG_1 = 8;
    private static final int UNUSED_FLAG_2 = 16;

    public TamableMammal(EntityType<? extends TFCAnimal> animal, Level level, TFCSounds.EntitySound sounds, MammalConfig config)
    {
        super(animal, level, sounds, config);
        moveControl = new PredicateMoveControl<>(this, e -> !e.isSitting() && !e.isSleeping());
    }

    @Override
    protected void defineSynchedData()
    {
        super.defineSynchedData();
        entityData.define(DATA_OWNER, Optional.empty());
        entityData.define(DATA_COMMAND, Command.RELAX.ordinal());
        entityData.define(DATA_PET_FLAGS, (byte) 0);
    }

    @Override
    protected Brain.Provider<? extends TamableMammal> brainProvider()
    {
        return Brain.provider(LivestockAi.MEMORY_TYPES, LivestockAi.SENSOR_TYPES);
    }

    @Override
    protected Brain<?> makeBrain(Dynamic<?> dynamic)
    {
        return CatAi.makeBrain(brainProvider().makeBrain(dynamic));
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
        ((Brain<TamableMammal>) getBrain()).tick((ServerLevel) level, this);
        CatAi.updateActivity(this, tickCount % 20 == 0);
    }

    /**
     * Used to determine if the command can be on the pet screen on the client.
     */
    public boolean willListenTo(Command command)
    {
        return true;
    }

    /**
     * Called on the server to process a command being received.
     */
    public void receiveCommand(ServerPlayer player, Command command)
    {
        if (getOwner() != null && getOwner().equals(player))
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
                }
                case HOME -> {
                    getBrain().setMemory(MemoryModuleType.HOME, GlobalPos.of(level.dimension(), player.blockPosition()));
                    command = Command.RELAX; // 'home' isn't a constant state, it defaults to relax after doing its thing
                }
                case FOLLOW, HUNT -> {
                    setSitting(false);
                    setSleeping(false);
                    getBrain().eraseMemory(TFCBrain.SIT_TIME.get());
                }
            }
            setCommand(command);

        }
        else
        {
            player.displayClientMessage(Helpers.translatable("tfc.pet.not_owner"), true);
        }
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

    private byte setBit(byte oldBit, int offset, boolean value)
    {
        return (byte) (value ? (oldBit | offset) : (oldBit & ~offset));
    }

    public Command getCommand()
    {
        return Command.valueOf(entityData.get(DATA_COMMAND));
    }

    public void setCommand(Command command)
    {
        entityData.set(DATA_COMMAND, command.ordinal());
    }

    @Nullable
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
    public Entity getOwner()
    {
        try
        {
            final UUID uuid = this.getOwnerUUID();
            return uuid == null ? null : this.level.getPlayerByUUID(uuid);
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
        if (held.isEmpty() && player.isShiftKeyDown() && getOwner() != null && getOwner().equals(player))
        {
            if (level.isClientSide)
            {
                ClientHelpers.openPetScreen(this);
            }
            return InteractionResult.SUCCESS;
        }
        if (getFamiliarity() > 0.15f && getOwnerUUID() == null && isFood(held) && isHungry())
        {
            tame(player);
        }
        return super.mobInteract(player, hand);
    }

    public void spawnTamingParticles(ParticleOptions particle)
    {
        for (int i = 0; i < 7; ++i)
        {
            this.level.addParticle(particle, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D), random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D, random.nextGaussian() * 0.02D);
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

    public boolean isOwnedBy(LivingEntity entity)
    {
        return entity == getOwner();
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        if (getOwnerUUID() != null)
        {
            tag.putUUID("Owner", getOwnerUUID());
        }
        tag.putInt("command", getCommand().ordinal());
        tag.putByte("petFlags", entityData.get(DATA_PET_FLAGS));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("Owner"))
        {
            setOwnerUUID(tag.getUUID("Owner"));
        }
        setCommand(Command.valueOf(tag.getInt("command")));
        entityData.set(DATA_PET_FLAGS, tag.getByte("petFlags"));
    }

    @Override
    public void die(DamageSource source)
    {
        final Component deathMessage = getCombatTracker().getDeathMessage();
        super.die(source);
        if (dead && getOwner() instanceof ServerPlayer serverPlayer && level.getGameRules().getBoolean(GameRules.RULE_SHOWDEATHMESSAGES))
        {
            serverPlayer.sendMessage(deathMessage, Util.NIL_UUID);
        }
    }

    public enum Command
    {
        RELAX(TFCBrain.IDLE_AT_HOME), // hang around near home
        HOME(null), // set a new home position
        SIT(TFCBrain.SIT), // sit for a period of time
        FOLLOW(TFCBrain.FOLLOW), // follow but don't participate in combat
        HUNT(TFCBrain.HUNT); // follow and participate in combat

        public static final Command[] VALUES = values();

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
