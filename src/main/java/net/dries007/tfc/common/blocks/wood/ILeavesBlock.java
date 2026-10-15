/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.wood;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.IBlockRain;
import net.dries007.tfc.util.Helpers;

/**
 * Leaves blocks, which MUST
 * 1. Have the PERSISTENT properties as per {@link TFCLeavesBlock}
 * 2. Be treated as non-solid blocks, except for the purpose of snow layers
 */
public interface ILeavesBlock extends IBlockRain
{
    /**
     * Leaves are solid for entities in {@link TFCTags.Entities#PERCHES_ON_LEAVES}, so they can perch on tree tops. If one of them ends up inside leaves anyway, they stay passable for it until it is out.
     */
    static VoxelShape getCollisionShape(CollisionContext context)
    {
        return context instanceof EntityCollisionContext entityContext
            && entityContext.getEntity() instanceof Entity entity
            && Helpers.isEntity(entity, TFCTags.Entities.PERCHES_ON_LEAVES)
            && !(entity.getInBlockState().getBlock() instanceof ILeavesBlock) ? Shapes.block() : Shapes.empty();
    }
}
