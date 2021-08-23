package net.dries007.tfc.mixin;

import net.minecraft.util.datafix.DataFixers;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.mojang.datafixers.DataFixerBuilder;
import net.dries007.tfc.util.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DataFixers.class)
public abstract class DataFixersMixin
{
    @Inject(method = "addFixers", at = @At("HEAD"), cancellable = true)
    private static void noDataFixingForYou(DataFixerBuilder builder, CallbackInfo ci)
    {
        if (!FMLEnvironment.production && Debug.DISABLE_DFU) ci.cancel();
    }
}
