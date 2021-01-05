/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.entities.animals.chicken;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.world.World;

public class RoosterEntity extends TFCChickenEntity
{
    public RoosterEntity(EntityType<? extends ChickenEntity> type, World worldIn)
    {
        super(type, worldIn);
    }
}
