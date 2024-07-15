/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.ai.prey;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.entities.ai.TFCBrain;
import net.dries007.tfc.common.entities.ai.pet.MoveOntoBlockBehavior;
import net.dries007.tfc.common.entities.prey.Pest;
import net.dries007.tfc.util.Helpers;

public class PestFeastBehavior extends MoveOntoBlockBehavior<Pest>
{
    public PestFeastBehavior(MemoryModuleType<?> memory, boolean erase)
    {
        super(memory, erase);
    }

    @Override
    protected void afterReached(Pest mob)
    {
        final Level level = mob.level();
        final BlockPos pos = mob.blockPosition();
        final @Nullable IItemHandler itemHandler = Helpers.getCapability(Capabilities.ItemHandler.BLOCK, level, pos);
        if (itemHandler != null)
        {
            for (int slot = 0; slot < itemHandler.getSlots(); slot++)
            {
                if (Helpers.isItem(itemHandler.getStackInSlot(slot), TFCTags.Items.FOODS))
                {
                    mob.setItemSlot(EquipmentSlot.MAINHAND, itemHandler.extractItem(slot, 1, false));
                    for (int i = 1; i < 6; i++)
                    {
                        // the infestation is getting worse
                        // no this is not an infinite loop b/c at most it's a 1/20 chance (for inf level == 5)
                        Helpers.tickInfestation(level, pos, i, null);
                    }
                    return;
                }
            }
            // we did not find anything smelly, stop trying
            mob.getBrain().eraseMemory(TFCBrain.SMELLY_POS.get());
        }
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, Pest animal)
    {
        return super.checkExtraStartConditions(level, animal) && animal.getMainHandItem().isEmpty();
    }

    @Override
    protected Optional<BlockPos> getNearestTarget(Pest mob)
    {
        return mob.getBrain().getMemory(TFCBrain.SMELLY_POS.get()).map(GlobalPos::pos);
    }

    @Override
    protected boolean isTargetAt(ServerLevel level, BlockPos pos)
    {
        return level.getBlockEntity(pos) != null;
    }
}
