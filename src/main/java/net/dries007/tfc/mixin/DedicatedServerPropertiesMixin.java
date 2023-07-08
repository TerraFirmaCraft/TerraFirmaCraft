/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.dedicated.DedicatedServerProperties;
import net.minecraft.world.level.levelgen.presets.WorldPreset;
import net.minecraft.world.level.levelgen.presets.WorldPresets;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.dries007.tfc.TerraFirmaCraft;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin
{
    @Redirect(method = "<init>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/levelgen/presets/WorldPresets;NORMAL:Lnet/minecraft/resources/ResourceKey;", opcode = Opcodes.GETSTATIC), require = 0)
    private ResourceKey<WorldPreset> changeDefaultWorldType()
    {
        // See `Main` for where this property switches between game test server and dedicated.
        return Boolean.getBoolean("forge.gameTestServer") ? WorldPresets.FLAT : TerraFirmaCraft.PRESET;
    }
}
