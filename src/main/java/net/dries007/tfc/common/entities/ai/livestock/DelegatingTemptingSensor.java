package net.dries007.tfc.common.entities.ai.livestock;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.entities.land.TFCAnimal;

/**
 * {@link net.minecraft.world.entity.ai.sensing.TemptingSensor} but it just uses the method in the animal class because why should it not...
 */
public class DelegatingTemptingSensor extends Sensor<TFCAnimal>
{
    public static final int TEMPTATION_RANGE = 10;
    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(TEMPTATION_RANGE).ignoreLineOfSight();

    public DelegatingTemptingSensor() { }

    @Override
    protected void doTick(ServerLevel level, TFCAnimal animal)
    {
        Brain<?> brain = animal.getBrain();
        List<ServerPlayer> list = level.players().stream()
            .filter(EntitySelector.NO_SPECTATORS)
            .filter(p -> TEMPT_TARGETING.test(animal, p))
            .filter(p -> animal.closerThan(p, TEMPTATION_RANGE))
            .filter(p -> playerHoldingTemptation(p, animal))
            .sorted(Comparator.comparingDouble(animal::distanceToSqr))
            .toList();
        if (!list.isEmpty())
        {
            Player player = list.get(0);
            brain.setMemory(MemoryModuleType.TEMPTING_PLAYER, player);
        }
        else
        {
            brain.eraseMemory(MemoryModuleType.TEMPTING_PLAYER);
        }

    }

    private boolean playerHoldingTemptation(Player player, TFCAnimal animal)
    {
        return this.isTemptation(player.getMainHandItem(), animal) || this.isTemptation(player.getOffhandItem(), animal);
    }

    private boolean isTemptation(ItemStack stack, TFCAnimal animal)
    {
        return animal.isFood(stack);
    }

    @Override
    public Set<MemoryModuleType<?>> requires()
    {
        return ImmutableSet.of(MemoryModuleType.TEMPTING_PLAYER);
    }
}
