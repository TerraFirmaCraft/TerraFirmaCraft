/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.Block;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalOviparous;
import net.dries007.tfc.util.calendar.CalendarTFC;

public class EntityAIFindNest extends EntityAIBase
{
    private final double speed;
    private EntityAnimal theCreature;
    private int currentTick;
    private World theWorld;
    private int maxSittingTicks;

    //To prevent chickens from trying to sit in unreachable nests. See below in updateTask, if the chicken doesnt move >0.5 m in 40 ticks, it
    //gives up, and waits 1 day before trying to sit in a nest box located at the specified coordinates
    private Map<BlockPos, Long> failureDepressionMap;
    private double compoundDistance;
    private int lastCheckedTick;
    private boolean end;

    private BlockPos nestPos = null;

    public EntityAIFindNest(EntityAnimal eAnimal, double speed)
    {
        this.theCreature = eAnimal;
        this.speed = speed;
        this.theWorld = eAnimal.world;
        this.failureDepressionMap = new HashMap<>();
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute()
    {
        if (theCreature instanceof EntityAnimalOviparous)
        {
            EntityAnimalOviparous ent = (EntityAnimalOviparous) theCreature;
            return ent.isReadyToLayEggs() && this.getNearbyNest();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (nestPos == null) return false;
        if (this.theCreature.getDistanceSq(nestPos) < 0.2)
            this.theCreature.getNavigator().clearPath();

        if (this.end)
        {
            this.end = false;
            return end;
        }
        return this.currentTick <= this.maxSittingTicks && this.isNestBlock(this.theWorld, nestPos);
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting()
    {
        this.theCreature.getNavigator().tryMoveToXYZ(this.nestPos.getX() + 0.5D, this.nestPos.getY() + 1, this.nestPos.getZ() + 0.5D, this.speed);
        this.currentTick = 0;
        this.compoundDistance = 0;
        this.lastCheckedTick = 0;
        this.end = false;
        this.maxSittingTicks = this.theCreature.getRNG().nextInt(this.theCreature.getRNG().nextInt(1200) + 1200) + 1200;
    }

    @Override
    public void updateTask()
    {
        ++this.currentTick;
        if (nestPos == null) return;
        if (this.theCreature.getDistanceSq(nestPos.up()) > 1.0D)
        {
            this.theCreature.getNavigator().tryMoveToXYZ(this.nestPos.getX() + 0.5D, this.nestPos.getY() + 1, this.nestPos.getZ() + 0.5D, this.speed);
            this.compoundDistance += this.theCreature.getDistance(this.theCreature.lastTickPosX, this.theCreature.lastTickPosY, this.theCreature.lastTickPosZ);
            if (this.currentTick - 40 > this.lastCheckedTick)
            {
                if (this.compoundDistance < 0.5)
                {
                    failureDepressionMap.put(nestPos, CalendarTFC.INSTANCE.getTotalTime() + CalendarTFC.TICKS_IN_DAY);
                    this.end = true;
                }
                else
                {
                    this.lastCheckedTick = this.currentTick;
                }
            }
        }
        else
        {
            //todo Lay Eggs

        }
    }

    private boolean getNearbyNest()
    {
        int i = (int) this.theCreature.posY;
        double d0 = Double.MAX_VALUE;
        for (BlockPos.MutableBlockPos pos : BlockPos.getAllInBoxMutable(theCreature.getPosition().add(-16, 0, -16), theCreature.getPosition().add(16, 4, 16)))
        {
            if (this.isNestBlock(this.theWorld, pos) && this.theWorld.isAirBlock(pos.up()))
            {
                double d1 = this.theCreature.getDistanceSq(pos);

                if (d1 < d0)
                {
                    this.nestPos = pos.toImmutable();
                    d0 = d1;
                }
            }
        }
        return d0 < Double.MAX_VALUE;
    }

    private boolean isNestBlock(World world, BlockPos pos)
    {
        if (failureDepressionMap.containsKey(pos))
        {
            long time = failureDepressionMap.get(pos);
            if (time > CalendarTFC.INSTANCE.getTotalTime())
                return false;
            else
                failureDepressionMap.remove(pos);
        }

        Block block = world.getBlockState(pos).getBlock();
        //Todo change to nest
        //TENestBox tileentitynest = (TENestBox) world.getTileEntity(x, y, z);
        //if (!tileentitynest.hasBird() || tileentitynest.getBird() == theCreature)
        return block == BlocksTFC.THATCH;
    }
}