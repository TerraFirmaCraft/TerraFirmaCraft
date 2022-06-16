/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.registry;

import java.util.function.Supplier;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;

import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.blocks.rock.RockCategory;

/**
 * Interface for use in {@link Rock.BlockType} registration calls.
 *
 * For the methods that return {@link Supplier<Block>}s, it is <strong>not required</strong> to implement all possible inputs - only the ones that are personally needed, as {@link RegistryRock} should never leave your own mod/addon's control.
 */
public interface RegistryRock extends StringRepresentable
{
    RockCategory category();

    /**
     * @return A block of this rock, of the provided type.
     */
    Supplier<? extends Block> getBlock(Rock.BlockType type);

    /**
     * @return A rock anvil block of this rock.
     */
    Supplier<? extends Block> getAnvil();

    /**
     * @return A slab block of this rock and block type.
     */
    Supplier<? extends SlabBlock> getSlab(Rock.BlockType type);

    /**
     * @return A stair block of this rock and block type.
     */
    Supplier<? extends StairBlock> getStair(Rock.BlockType type);

    /**
     * @return A wall block of this rock and block type.
     */
    Supplier<? extends WallBlock> getWall(Rock.BlockType type);
}
