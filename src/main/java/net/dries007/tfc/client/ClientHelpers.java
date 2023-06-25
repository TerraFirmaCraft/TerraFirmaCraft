/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.searchtree.SearchRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.client.screen.PetCommandScreen;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.entities.livestock.pet.TamableMammal;
import org.jetbrains.annotations.Nullable;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Client side methods for proxy use
 */
public final class ClientHelpers
{
    public static final Direction[] DIRECTIONS_AND_NULL = new Direction[] {Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.DOWN, Direction.UP, null};

    public static final ResourceLocation GUI_ICONS = new ResourceLocation(MOD_ID, "textures/gui/icons.png");

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