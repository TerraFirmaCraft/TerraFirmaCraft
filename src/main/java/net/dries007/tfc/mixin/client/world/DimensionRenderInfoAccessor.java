/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.client.world;

import net.minecraft.client.world.DimensionRenderInfo;
import net.minecraft.util.ResourceLocation;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import mcp.MethodsReturnNonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@SuppressWarnings("ConstantConditions")
@MethodsReturnNonnullByDefault
@Mixin(DimensionRenderInfo.class)
public interface DimensionRenderInfoAccessor
{
    @Accessor(value = "field_239208_a_") //effects
    static Object2ObjectMap<ResourceLocation, DimensionRenderInfo> accessor$Effects() { return null; }

    @Accessor(value = "field_239210_c_") // cloud level
    void accessor$setCloudLevel(float cloudLevel);
}
