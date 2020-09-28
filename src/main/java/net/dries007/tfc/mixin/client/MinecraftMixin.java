package net.dries007.tfc.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.concurrent.RecursiveEventLoop;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

/**
 * This is a hack to remove the "Experimental Settings" screen which will pop up every time you generate or load a TFC world.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin extends RecursiveEventLoop<Runnable>
{
    private MinecraftMixin(String name)
    {
        super(name);
    }

    @ModifyVariable(method = "doLoadLevel", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft$WorldSelectionType;NONE:Lnet/minecraft/client/Minecraft$WorldSelectionType;", ordinal = 0), ordinal = 2, index = 11, name = "flag2")
    private boolean modify$doLoadLevel$flag2(boolean flag2)
    {
        if (TFCConfig.CLIENT.ignoreExperimentalWorldGenWarning.get())
        {
            TerraFirmaCraft.LOGGER.warn("Experimental world gen... dragons or some such.. blah blah.");
            return false;
        }
        return flag2;
    }
}
