/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;

import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;

/**
 * Interface for use in {@link Rock.BlockType} registration calls.
 */
public interface RegistryRock extends StringRepresentable
{
    RockCategory category();

    /**
     * @return A block of this rock, of the provided type. It is <strong>not necessary</strong> to implement this for every type, only the ones that are needed.
     */
    Supplier<Block> getBlock(Rock.BlockType type);

    /**
     * @return A rock anvil block of this rock. It is <strong>not necessary</strong> to implement this for every type, only the ones that are needed.
     */
    Supplier<Block> getAnvil();
}
