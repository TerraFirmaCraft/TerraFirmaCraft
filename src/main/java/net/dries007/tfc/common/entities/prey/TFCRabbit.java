/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ForgeEventFactory;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCRabbit extends Rabbit
{
    private int moreCarrotTicks;

    public TFCRabbit(EntityType<? extends Rabbit> type, Level level)
    {
        super(type, level);
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
    public boolean isFood(ItemStack stack)
    {
        return false;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag)
    {
        super.addAdditionalSaveData(tag);
        tag.putInt("TFCMoreCarrotTicks", moreCarrotTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag)
    {
        super.readAdditionalSaveData(tag);
        moreCarrotTicks = tag.getInt("TFCMoreCarrotTicks");
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 2); // breed
        EntityHelpers.removeGoalOfPriority(goalSelector, 3); // tempt
        EntityHelpers.removeGoalOfPriority(goalSelector, 4); // avoid goals
        EntityHelpers.removeGoalOfPriority(goalSelector, 5); // vanilla raid garden
        goalSelector.addGoal(4, new TFCAvoidEntityGoal<>(this, PathfinderMob.class, 8.0F, 2.2D, 2.2D, TFCTags.Entities.HUNTS_LAND_PREY));
        goalSelector.addGoal(5, new RaidGardenGoal(this));
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, type, data, tag);
        this.setRabbitType(getRandomRabbitType(level, blockPosition()));
        return spawnData;
    }

    @Override
    public Rabbit getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        Rabbit rabbit = TFCEntities.RABBIT.get().create(level);
        int i = this.getRandomRabbitType(level, blockPosition());
        if (this.random.nextInt(20) != 0)
        {
            i = other instanceof Rabbit otherRabbit && random.nextBoolean() ? otherRabbit.getRabbitType() : getRabbitType();
        }
        rabbit.setRabbitType(i);
        return rabbit;
    }

    private int getRandomRabbitType(ServerLevelAccessor level, BlockPos pos)
    {
        final int i = random.nextInt(100);
        final ChunkData data = EntityHelpers.getChunkDataForSpawning(level, pos);
        final float rain = data.getRainfall(pos);
        final float temp = data.getAverageTemp(pos);
        if (temp < 0)
        {
            return i < 80 ? TYPE_WHITE : TYPE_WHITE_SPLOTCHED;
        }
        else if (rain < 125)
        {
            return TYPE_GOLD;
        }
        return i < 50 ? TYPE_BROWN : (i < 90 ? TYPE_SALT : TYPE_BLACK);
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
                if (!ForgeEventFactory.getMobGriefingEvent(rabbit.level, rabbit))
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
                Level level = rabbit.level;
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
