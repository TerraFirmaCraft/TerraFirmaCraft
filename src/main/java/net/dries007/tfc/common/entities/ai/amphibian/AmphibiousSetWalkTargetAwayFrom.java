/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.amphibian;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class AmphibiousSetWalkTargetAwayFrom
{
    public AmphibiousSetWalkTargetAwayFrom()
    {
    }

    public static OneShot<PathfinderMob> entity(MemoryModuleType<LivingEntity> walkTargetAwayFromMemory, Function<LivingEntity, Float> speedModifier, int desiredDistance, boolean hasTarget)
    {
        return create(walkTargetAwayFromMemory, speedModifier, desiredDistance, hasTarget, Entity::position);
    }

    private static <T> OneShot<PathfinderMob> create(MemoryModuleType<LivingEntity> walkTargetAwayFromMemory, Function<LivingEntity, Float> speedModifier, int desiredDistance, boolean hasTarget, Function<T, Vec3> toPosition)
    {
        return BehaviorBuilder.create((m) -> m.group(m.registered(MemoryModuleType.WALK_TARGET), m.present(walkTargetAwayFromMemory)).apply(m, (pathToTarget, memoryAccessor) -> (level, mob, distance) -> {
            Optional<WalkTarget> optional = m.tryGet(pathToTarget);
            if (optional.isPresent() && !hasTarget)
            {
                return false;
            }
            else
            {
                Vec3 currentPos = mob.position();
                LivingEntity attacker = m.get(memoryAccessor);
                Vec3 escapeFromPos = attacker.position();
                if (!currentPos.closerThan(escapeFromPos, desiredDistance))
                {
                    return false;
                }
                else
                {
                    // If the existing destination position is in more or less the opposite direction of the threat, don't find new vector
                    if (optional.isPresent() && (optional.get()).getSpeedModifier() == speedModifier.apply(mob))
                    {
                        if (isTargetPosValidForEscape(currentPos, escapeFromPos, optional.get().getTarget().currentPosition()))
                        {
                            return false;
                        }
                    }

                    boolean isOceanPredator = Helpers.isEntity(attacker, TFCTags.Entities.OCEAN_PREDATORS);

                    // Evaluate positions for escape, favors land positions if an ocean predator is attacking it
                    Vec3 bestLandEscapePos = currentPos;
                    Vec3 bestWaterEscapePos = currentPos;
                    boolean foundLand = false;
                    boolean foundWater = false;
                    for (int i = 0; i < 10; ++i)
                    {
                        Vec3 destination = DefaultRandomPos.getPosAway(mob, 10, 7, escapeFromPos);
                        if (destination != null)
                        {
                            final BlockPos pos = BlockPos.containing(destination);
                            final BlockState state = level.getBlockState(pos.above());
                            if (Helpers.isFluid(state.getFluidState(), TFCTags.Fluids.ANY_INFINITE_WATER))
                            {
                                bestWaterEscapePos = getVectorFartherFromChaser(escapeFromPos, bestWaterEscapePos, destination);
                                foundWater = true;
                                if (!isOceanPredator)
                                {
                                    break;
                                }
                            }
                            else
                            {
                                final BlockState belowState = level.getBlockState(pos);
                                if (!belowState.isEmpty() && !Helpers.isFluid(belowState.getFluidState(), TFCTags.Fluids.ANY_INFINITE_WATER))
                                {
                                    bestLandEscapePos = getVectorFartherFromChaser(escapeFromPos, bestLandEscapePos, destination);
                                    foundLand = true;
                                    if (isOceanPredator)
                                    {
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    final Vec3 bestEscapePos;
                    if (isOceanPredator)
                    {
                        bestEscapePos = foundLand ? bestLandEscapePos : (foundWater ? bestWaterEscapePos : null);
                    }
                    else
                    {
                        bestEscapePos = foundWater ? bestWaterEscapePos : (foundLand ? bestLandEscapePos : null);
                    }

                    if (bestEscapePos == null)
                    {
                        return false;
                    }
                    pathToTarget.set(new WalkTarget(bestEscapePos, speedModifier.apply(mob), 0));

                    return true;
                }
            }
        }));
    }

    // Compare two possible escape positions for which is farther from the location they are avoiding
    private static Vec3 getVectorFartherFromChaser(Vec3 escapeFromPos, Vec3 vector0, Vec3 vector1)
    {
        final Vec3 delta0 = escapeFromPos.subtract(vector0);
        final Vec3 delta1 = escapeFromPos.subtract(vector1);
        if (delta0.lengthSqr() > delta1.lengthSqr())
        {
            return vector0;
        }
        else
        {
            return vector1;
        }
    }

    private static boolean isTargetPosValidForEscape(Vec3 currentPos, Vec3 escapeFromPos, Vec3 destinationPos)
    {
        Vec3 destinationDelta = destinationPos.subtract(currentPos);
        Vec3 escapeFromDelta = escapeFromPos.subtract(currentPos);
        return destinationDelta.dot(escapeFromDelta) < 0.0;
    }
}
