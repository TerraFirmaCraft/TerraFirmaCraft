/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin;

import net.minecraft.server.dedicated.DedicatedServerProperties;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(DedicatedServerProperties.class)
public abstract class DedicatedServerPropertiesMixin
{
    @ModifyConstant(method = "<init>", constant = @Constant(stringValue = "default"))
    private static String selectDefaultWorldType(String type)
    {
        // See `Main` where this property switches between dedicated + game test server
        return Boolean.getBoolean("forge.gameTestServer") ? "flat" : "tfc:tng";
    }
}
