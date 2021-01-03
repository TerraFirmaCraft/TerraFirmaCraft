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
    @Accessor(value = "EFFECTS")
    static Object2ObjectMap<ResourceLocation, DimensionRenderInfo> accessor$Effects() { return null; }

    @Accessor(value = "cloudLevel")
    void accessor$setCloudLevel(float cloudLevel);
}
