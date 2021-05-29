package net.dries007.tfc.common.entities.aquatic;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.world.World;

import net.dries007.tfc.common.blocks.TFCBlocks;
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
}
