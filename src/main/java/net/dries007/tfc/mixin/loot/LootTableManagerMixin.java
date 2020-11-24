package net.dries007.tfc.mixin.loot;

import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.loot.LootTableManager;

import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LootTableManager.class)
public abstract class LootTableManagerMixin extends JsonReloadListener
{
    private LootTableManagerMixin(Gson gson_, String string_)
    {
        super(gson_, string_);
    }

    /**
     * Log a more useful message. Full stack trace is not useful. Concise, readable errors are useful.
     */
    @SuppressWarnings("UnresolvedMixinReference")
    @Redirect(method = "*(Lnet/minecraft/resources/IResourceManager;Lcom/google/common/collect/ImmutableMap$Builder;Lnet/minecraft/util/ResourceLocation;Lcom/google/gson/JsonElement;)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"), require = 0)
    private void redirect$apply$error(Logger logger, String message, Object p0, Object p1)
    {
        if (TFCConfig.COMMON.enableDevTweaks.get())
        {
            logger.error(message + " {}: {}", p0, p1.getClass().getSimpleName(), ((Exception) p1).getMessage());
        }
        else
        {
            logger.error(message, p0, p1); // Default behavior
        }
    }
}
