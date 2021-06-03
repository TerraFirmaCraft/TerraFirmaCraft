/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.aquatic;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.entities.TFCEntities;
import net.dries007.tfc.common.entities.ai.LayEggBlockGoal;

public class PenguinEntity extends AbstractTurtleEntity
{
    public PenguinEntity(EntityType<? extends TurtleEntity> entityType, World world)
    {
        super(entityType, world);
    }

    @Override
    protected void registerGoals()
    {
        super.registerGoals();
        goalSelector.addGoal(1, new LayEggBlockGoal(this, 1.0D, TFCBlocks.TURTLE_EGG));
    }

    @Override
    protected float getBlockSpeedFactor()
    {
        final Block block = level.getBlockState(blockPosition()).getBlock();
        return block.is(Blocks.SNOW) ? 1.0F : super.getBlockSpeedFactor();
    }

    @Override
    @Nullable
    public AgeableEntity getBreedOffspring(ServerWorld p_241840_1_, AgeableEntity p_241840_2_)
    {
        return TFCEntities.PENGUIN.get().create(p_241840_1_);
    }
}
