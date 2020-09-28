package net.dries007.tfc.mixin.client.gui.screen;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.client.gui.screen.CreateWorldScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.WorldOptionsScreen;

import net.dries007.tfc.client.screen.TFCGeneratorTypePreset;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin
{
    /**
     * Set the selected preset to TFC, when the create world screen is invoked
     * This would be replaced by MinecraftForge#7353 if it goes through
     */
    @Inject(method = "create", at = @At(value = "RETURN"), cancellable = true)
    private static void inject$create(@Nullable Screen screen_, CallbackInfoReturnable<CreateWorldScreen> cir)
    {
        if (TFCConfig.CLIENT.setTFCWorldPresetAsDefault.get())
        {
            WorldOptionsScreen screen = cir.getReturnValue().worldGenSettingsComponent;
            WorldOptionsScreenAccessor screenAccessor = (WorldOptionsScreenAccessor) screen;

            // Need to set both the preset, and the settings
            // Settings creation is copied from WorldOptionsScreen -> what happens when you click the type button
            screenAccessor.accessor$setPreset(Optional.of(TFCGeneratorTypePreset.PRESET.get()));
            screenAccessor.accessor$setSettings(TFCGeneratorTypePreset.PRESET.get().create(screenAccessor.accessor$getRegistryHolder(), screenAccessor.accessor$getSettings().seed(), screenAccessor.accessor$getSettings().generateFeatures(), screenAccessor.accessor$getSettings().generateBonusChest()));
        }
    }
}
