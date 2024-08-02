/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.screen.PetCommandScreen;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import net.dries007.tfc.util.Helpers;

/**
 * Client side methods for proxy use
 */
public final class ClientHelpers
{
    public static final Direction[] DIRECTIONS_AND_NULL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, null};

    public static final ResourceLocation GUI_ICONS = Helpers.identifier("textures/gui/icons.png");

    @Nullable
    @SuppressWarnings("ConstantValue")
    public static RecipeManager tryGetSafeRecipeManager()
    {
        final @Nullable Minecraft mc = Minecraft.getInstance();
        return mc != null && mc.level != null ? mc.level.getRecipeManager() : null;
    }

    @Nullable
    public static Level getLevel()
    {
        return Minecraft.getInstance().level;
    }

    public static Level getLevelOrThrow()
    {
        return Objects.requireNonNull(getLevel());
    }

    @Nullable
    public static Player getPlayer()
    {
        return Minecraft.getInstance().player;
    }

    public static Player getPlayerOrThrow()
    {
        return Objects.requireNonNull(getPlayer());
    }

    public static boolean useFancyGraphics()
    {
        return Minecraft.useFancyGraphics();
    }

    @Nullable
    public static BlockPos getTargetedPos()
    {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.level != null && mc.hitResult instanceof BlockHitResult block)
        {
            return block.getBlockPos();
        }
        return null;
    }

    public static boolean hasShiftDown()
    {
        return Screen.hasShiftDown();
    }

    public static void openPetScreen(TamableMammal mammal)
    {
        Minecraft.getInstance().setScreen(new PetCommandScreen(mammal));
    }

}