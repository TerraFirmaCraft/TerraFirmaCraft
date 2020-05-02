/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.entity.ai;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.objects.te.TENestBox;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.ICalendar;

public class EntityAIFindNest extends EntityAIBase
{
    private final double speed;
    private final EntityAnimal theCreature;
    private final World theWorld;
    //This is a helper map to prevent chickens not choose unreachable nest boxes.
    private final Map<BlockPos, Long> failureDepressionMap;
    private int currentTick;
    private int maxSittingTicks;
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
        if (theCreature instanceof IAnimalTFC && ((IAnimalTFC) theCreature).getType() == IAnimalTFC.Type.OVIPAROUS)
        {
            IAnimalTFC animal = (IAnimalTFC) theCreature;
            return animal.isReadyForAnimalProduct() && this.getNearbyNest();
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        if (this.end || !this.isNestBlock(this.theWorld, nestPos))
        {
            this.end = false;
            this.theCreature.getNavigator().clearPath();
            if (this.theCreature.isRiding()) this.theCreature.dismountRidingEntity();
            return false;
        }
        return true;
    }

    @Override
    public void startExecuting()
    {
        this.theCreature.getNavigator().tryMoveToXYZ(this.nestPos.getX() + 0.5D, this.nestPos.getY() + 1, this.nestPos.getZ() + 0.5D, this.speed);
        this.currentTick = 0;
        this.end = false;
        this.maxSittingTicks = this.theCreature.getRNG().nextInt(200) + 100;
    }

    @Override
    public void updateTask()
    {
        ++this.currentTick;
        if (nestPos == null) return;
        if (this.theCreature.getDistanceSq(nestPos) > 1.25D)
        {
            this.theCreature.getNavigator().tryMoveToXYZ(this.nestPos.getX() + 0.5D, this.nestPos.getY(), this.nestPos.getZ() + 0.5D, this.speed);
            if (this.currentTick > 200)
            {
                //We never reached it in 10 secs, lets give up on this nest box
                failureDepressionMap.put(nestPos, theWorld.getTotalWorldTime() + ICalendar.TICKS_IN_HOUR * 4);
                this.end = true;
            }
        }
        else
        {
            TENestBox te = Helpers.getTE(this.theWorld, nestPos, TENestBox.class);
            if (te != null && theCreature instanceof IAnimalTFC && ((IAnimalTFC) theCreature).getType() == IAnimalTFC.Type.OVIPAROUS)
            {
                IAnimalTFC animal = (IAnimalTFC) theCreature;
                if (!te.hasBird())
                {
                    te.seatOnThis(theCreature);
                    this.currentTick = 0;
                }
                if (this.currentTick >= this.maxSittingTicks)
                {
                    List<ItemStack> eggs = animal.getProducts();
                    for (ItemStack egg : eggs)
                    {
                        te.insertEgg(egg);
                    }
                    animal.setFertilized(false);
                    animal.setProductsCooldown();
                    this.end = true;
                }
                else if (te.getBird() != theCreature)
                {
                    //Used by another bird, give up on this one for now
                    failureDepressionMap.put(nestPos, theWorld.getTotalWorldTime() + ICalendar.TICKS_IN_HOUR * 4);
                    this.end = true;
                }
            }
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
        if (world == null || pos == null) return false;
        if (failureDepressionMap.containsKey(pos))
        {
            long time = failureDepressionMap.get(pos);
            if (time > world.getTotalWorldTime())
                return false;
            else
                failureDepressionMap.remove(pos);
        }
        TENestBox te = Helpers.getTE(world, pos, TENestBox.class);
        return te != null && te.hasFreeSlot() && (!te.hasBird() || te.getBird() == this.theCreature);
    }
}