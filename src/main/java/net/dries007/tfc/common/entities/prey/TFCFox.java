/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.prey;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.plant.fruit.Lifecycle;
import net.dries007.tfc.common.blocks.plant.fruit.SeasonalPlantBlock;
import net.dries007.tfc.common.entities.EntityHelpers;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.TFCAvoidEntityGoal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.chunkdata.ChunkData;

public class TFCFox extends Fox
{
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID_TFC = SynchedEntityData.defineId(TFCFox.class, EntityDataSerializers.INT);

    public TFCFox(EntityType<? extends Fox> type, Level level)
    {
        super(type, level);
    }

    @Override
    protected void populateDefaultEquipmentSlots(RandomSource source, DifficultyInstance instance)
    {
        if (random.nextFloat() < 0.15f)
        {
            Helpers.randomItem(TFCTags.Items.FOX_SPAWNS_WITH, random).ifPresent(item -> setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(item)));
        }
    }

    @Override
    public Type getVariant()
    {
        return Fox.Type.byId(entityData.get(DATA_TYPE_ID_TFC)); // overloads vanilla entity data so we can set it
    }

    @Override
    public void setVariant(Fox.Type id)
    {
        entityData.set(DATA_TYPE_ID_TFC, id.getId());
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder)
    {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID_TFC, 0);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        EntityHelpers.removeGoalOfPriority(goalSelector, 3); // breed goal
        EntityHelpers.removeGoalOfClass(goalSelector, FoxEatBerriesGoal.class);
        goalSelector.addGoal(4, new TFCAvoidEntityGoal<>(this, PathfinderMob.class, 8f, 1.6f, 1.4f, TFCTags.Entities.HUNTS_LAND_PREY));
        goalSelector.addGoal(10, new TFCFoxEatBerriesGoal());
    }

    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor level, DifficultyInstance difficulty, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag)
    {
        final SpawnGroupData spawnData = super.finalizeSpawn(level, difficulty, type, data, tag);
        final ChunkData chunkData = EntityHelpers.getChunkDataForSpawning(level, blockPosition());
        setVariant(chunkData.getAverageTemp(blockPosition()) < 0 ? Type.SNOW : Type.RED);
        return spawnData;
    }

    @Override
    public TFCFox getBreedOffspring(ServerLevel level, AgeableMob other)
    {
        TFCFox fox = TFCEntities.FOX.get().create(level);
        setVariant(this.random.nextBoolean() ? this.getVariant() : ((TFCFox) other).getVariant());
        return fox;
    }

    @Override
    public boolean isFood(ItemStack stack)
    {
        return false;
    }

    public class TFCFoxEatBerriesGoal extends FoxEatBerriesGoal
    {
        public TFCFoxEatBerriesGoal()
        {
            super(1.2, 12, 1);
        }

        @Override
        protected boolean isValidTarget(LevelReader level, BlockPos pos)
        {
            BlockState state = level.getBlockState(pos);
            return Helpers.isBlock(state, TFCTags.Blocks.FOX_RAIDABLE) && isFruiting(state);
        }

        @Override
        protected void onReachedTarget()
        {
            TFCFox fox = TFCFox.this;
            Level level = fox.level();
            if (ForgeEventFactory.getMobGriefingEvent(level, fox))
            {
                BlockState currentState = level.getBlockState(blockPos);
                if (currentState.getBlock() instanceof SeasonalPlantBlock seasonal && fox.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty())
                {
                    ItemStack product = seasonal.getProductItem(fox.random);
                    if (!product.isEmpty())
                    {
                        fox.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 1f, 1f);
                        product.setCount(1);
                        fox.setItemSlot(EquipmentSlot.MAINHAND, product);
                        level.setBlockAndUpdate(blockPos, seasonal.stateAfterPicking(currentState));
                    }
                }
            }
        }

        @Override
        public boolean canUse()
        {
            return super.canUse() && TFCFox.this.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty();
        }

        private boolean isFruiting(BlockState state)
        {
            return state.hasProperty(SeasonalPlantBlock.LIFECYCLE) && state.getValue(SeasonalPlantBlock.LIFECYCLE) == Lifecycle.FRUITING;
        }
    }
}
