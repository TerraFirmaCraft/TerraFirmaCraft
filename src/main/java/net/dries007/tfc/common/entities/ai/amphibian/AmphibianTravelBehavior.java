/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import com.google.common.collect.ImmutableMap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import net.dries007.tfc.common.entities.ai.BrainObjects;
import net.dries007.tfc.common.entities.aquatic.AmphibiousAnimal;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

public class AmphibianTravelBehavior extends Behavior<AmphibiousAnimal>
{
    public static boolean canGoAway(AmphibiousAnimal entity)
    {
        return entity.isInWaterOrBubble() && entity.level.getDayTime() > 12000 && isTimeToTravel(entity);
    }

    public static boolean canGoHome(AmphibiousAnimal entity)
    {
        return entity.level.getDayTime() < 12000 && isTimeToTravel(entity);
    }

    public static void doGoAway(AmphibiousAnimal entity)
    {
        Brain<AmphibiousAnimal> brain = entity.getBrain();
        final double x = entity.getX() + Helpers.triangle(entity.getRandom(), 32);
        final double y = entity.getY();
        final double z = entity.getZ() + Helpers.triangle(entity.getRandom(), 32);
        final BlockPos pos = new BlockPos(x, y, z);
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.setMemory(BrainObjects.TRAVEL_POS.get(), pos);
        brain.setMemory(BrainObjects.NEXT_TRAVEL_TIME.get(), Calendars.SERVER.getTicks() + TIME_BETWEEN_TRAVELS);
    }

    public static void goHome(AmphibiousAnimal entity)
    {
        Brain<AmphibiousAnimal> brain = entity.getBrain();
        brain.eraseMemory(MemoryModuleType.LOOK_TARGET);
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);
        brain.setMemory(BrainObjects.TRAVEL_POS.get(), brain.getMemory(MemoryModuleType.HOME).orElseThrow().pos());
        brain.setMemory(BrainObjects.NEXT_TRAVEL_TIME.get(), Calendars.SERVER.getTicks() + TIME_BETWEEN_TRAVELS);
    }

    private static boolean isTimeToTravel(AmphibiousAnimal entity)
    {
        return entity.getBrain().getMemory(BrainObjects.NEXT_TRAVEL_TIME.get()).map(time -> Calendars.SERVER.getTicks() > time).orElse(true);
    }

    private static final int TIME_BETWEEN_TRAVELS = ICalendar.TICKS_IN_DAY / 2;

    public AmphibianTravelBehavior()
    {
        super(ImmutableMap.of(BrainObjects.TRAVEL_POS.get(), MemoryStatus.VALUE_PRESENT), 300);
    }

    @Override
    protected void start(ServerLevel level, AmphibiousAnimal animal, long time)
    {
        final BlockPos goalPos = getTravelPos(animal);
        if (!goalPos.closerThan(animal.blockPosition(), 30))
        {
            BehaviorUtils.setWalkAndLookTargetMemories(animal, goalPos.offset(animal.getRandom().nextInt(3), 0, animal.getRandom().nextInt(3)), 0.5F, 20);
        }
        else
        {
            animal.getBrain().eraseMemory(BrainObjects.TRAVEL_POS.get());
            animal.getBrain().useDefaultActivity();
        }
    }

    private BlockPos getTravelPos(AmphibiousAnimal animal)
    {
        return animal.getBrain().getMemory(BrainObjects.TRAVEL_POS.get()).orElseThrow();
    }
}
