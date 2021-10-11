/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

/**
 * Client side methods for proxy use
 */
public final class ClientHelpers
{
    @Nullable
    public static Level getLevel()
    {
        return Minecraft.getInstance().level;
    }

    @Nullable
    public static Player getPlayer()
    {
        return Minecraft.getInstance().player;
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

    /**
     * Creates {@link ModelLayerLocation} in the default manner
     */
    public static ModelLayerLocation modelIdentifier(String name, String part)
    {
        return new ModelLayerLocation(new ResourceLocation(MOD_ID, name), part);
    }

    public static ModelLayerLocation modelIdentifier(String name)
    {
        return modelIdentifier(name, "main");
    }
}