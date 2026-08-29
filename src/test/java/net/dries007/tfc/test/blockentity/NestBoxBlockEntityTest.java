/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.test.blockentity;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Test;

import net.dries007.tfc.common.blockentities.NestBoxBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.component.EggComponent;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.test.TestSetup;

import static org.junit.jupiter.api.Assertions.*;

public class NestBoxBlockEntityTest implements TestSetup
{
    private static final String INCUBATING_EGGS = "incubatingEggs";
    private static final HolderLookup.Provider REGISTRIES = RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY);

    @Test
    public void testFreshEggRemainsEligibleAfterReloadAndRotting()
    {
        final NestBoxBlockEntity nest = createNest();
        nest.getInventory().setStackInSlot(0, egg(true));

        final CompoundTag saved = nest.saveCustomOnly(REGISTRIES);
        assertTrue(isEligible(saved, 0));

        final NestBoxBlockEntity loaded = createNest();
        loaded.loadWithComponents(saved, REGISTRIES);
        FoodCapability.setRotten(loaded.getInventory().getStackInSlot(0));

        assertTrue(FoodCapability.isRotten(loaded.getInventory().getStackInSlot(0)));
        assertTrue(loaded.getInventory().getStackInSlot(0).getOrDefault(TFCComponents.EGG, EggComponent.DEFAULT).canHatch());
        assertTrue(isEligible(loaded.saveCustomOnly(REGISTRIES), 0));
    }

    @Test
    public void testRottenEggIsIneligibleOnInsertion()
    {
        final NestBoxBlockEntity nest = createNest();
        final ItemStack egg = egg(true);
        FoodCapability.setRotten(egg);

        assertTrue(FoodCapability.isRotten(egg));
        nest.getInventory().setStackInSlot(0, egg);

        assertTrue(egg.getOrDefault(TFCComponents.EGG, EggComponent.DEFAULT).canHatch());
        assertFalse(isEligible(nest.saveCustomOnly(REGISTRIES), 0));
        assertFalse(nest.getInventory().getStackInSlot(0).isEmpty());
    }

    @Test
    public void testRemovalClearsEligibilityBeforeRottenEggIsReinserted()
    {
        final NestBoxBlockEntity nest = createNest();
        nest.getInventory().setStackInSlot(0, egg(true));
        final ItemStack removed = nest.getInventory().extractItem(0, 1, false);

        assertFalse(isEligible(nest.saveCustomOnly(REGISTRIES), 0));

        FoodCapability.setRotten(removed);
        nest.getInventory().setStackInSlot(0, removed);

        assertFalse(isEligible(nest.saveCustomOnly(REGISTRIES), 0));
    }

    @Test
    public void testReplacementRecomputesEligibility()
    {
        final NestBoxBlockEntity nest = createNest();
        nest.getInventory().setStackInSlot(0, egg(true));
        nest.getInventory().setStackInSlot(0, egg(false));

        assertFalse(isEligible(nest.saveCustomOnly(REGISTRIES), 0));
    }

    @Test
    public void testLegacyEggIsMigratedButLaterChangesUseFreshnessGate()
    {
        final NestBoxBlockEntity original = createNest();
        original.getInventory().setStackInSlot(0, egg(true));
        final CompoundTag legacy = original.saveCustomOnly(REGISTRIES);
        legacy.remove(INCUBATING_EGGS);

        final NestBoxBlockEntity loaded = createNest();
        loaded.loadWithComponents(legacy, REGISTRIES);
        assertTrue(isEligible(loaded.saveCustomOnly(REGISTRIES), 0));

        final ItemStack rotten = egg(true);
        FoodCapability.setRotten(rotten);
        loaded.getInventory().setStackInSlot(0, rotten);

        assertFalse(isEligible(loaded.saveCustomOnly(REGISTRIES), 0));
    }

    private static NestBoxBlockEntity createNest()
    {
        return new NestBoxBlockEntity(BlockPos.ZERO, TFCBlocks.NEST_BOX.get().defaultBlockState());
    }

    private static ItemStack egg(boolean fertilized)
    {
        final ItemStack stack = new ItemStack(Items.EGG);
        final CompoundTag entity = new CompoundTag();
        entity.putString("id", "minecraft:chicken");
        stack.set(TFCComponents.EGG, new EggComponent(fertilized, 0, Optional.of(entity)));
        return stack;
    }

    private static boolean isEligible(CompoundTag tag, int slot)
    {
        return (tag.getInt(INCUBATING_EGGS) & 1 << slot) != 0;
    }
}
