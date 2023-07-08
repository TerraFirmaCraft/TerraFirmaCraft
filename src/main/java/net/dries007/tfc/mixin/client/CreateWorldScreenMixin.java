/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client;

import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.dries007.tfc.TerraFirmaCraft;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin
{
    @Redirect(method = "openFresh", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/presets/WorldPresets;NORMAL:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC))
    private static ResourceKey<WorldPreset> changeDefaultWorldType()
    {
        // This selects the default UI element (which annoying, is not actually queried to create the default dimensions)
        return TerraFirmaCraft.PRESET;
    }
}
