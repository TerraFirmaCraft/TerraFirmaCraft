package net.dries007.tfc.mixin.client.gui.screen;

import java.util.List;

import net.minecraft.client.gui.screen.BiomeGeneratorTypeScreens;

import mcp.MethodsReturnNonnullByDefault;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@MethodsReturnNonnullByDefault
@SuppressWarnings("ConstantConditions")
@Mixin(BiomeGeneratorTypeScreens.class)
public interface BiomeGeneratorTypeScreensAccessor
{
    @Accessor("PRESETS")
    static List<BiomeGeneratorTypeScreens> accessor$getPresets() { return null; }
}
