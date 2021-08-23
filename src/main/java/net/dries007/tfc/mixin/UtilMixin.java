package net.dries007.tfc.mixin;

import net.minecraft.Util;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import net.dries007.tfc.util.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Util.class)
public abstract class UtilMixin
{
    @Inject(method = "fetchChoiceType", at = @At("HEAD"), cancellable = true)
    private static void noChoiceTypeForYou(DSL.TypeReference type, String choiceName, CallbackInfoReturnable<Type<?>> cir)
    {
        if (!FMLEnvironment.production && Debug.DISABLE_DFU) cir.setReturnValue(null);
    }
}
