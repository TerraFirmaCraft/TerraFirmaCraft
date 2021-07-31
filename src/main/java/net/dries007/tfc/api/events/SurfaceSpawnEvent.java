/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
@Event.HasResult
public class SurfaceSpawnEvent extends Event
{

    private EntityLivingBase entity;
    private World world;
    private BlockPos pos;

    public SurfaceSpawnEvent(EntityLivingBase entity, World world, BlockPos pos)
    {
        this.entity = entity;
        this.world = world;
        this.pos = pos;
    }

    public EntityLivingBase getEntity()
    {
        return entity;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

}
