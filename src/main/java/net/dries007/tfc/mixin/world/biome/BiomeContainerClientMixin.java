package net.dries007.tfc.mixin.world.biome;

import org.apache.logging.log4j.Logger;
import net.minecraft.world.biome.BiomeContainer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BiomeContainer.class)
public class BiomeContainerClientMixin
{
    @Unique
    private static boolean WARNED_ALREADY = false;

    @Redirect(method = "<init>(Lnet/minecraft/util/IObjectIntIterable;[I)V", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;warn(Ljava/lang/String;)V"))
    private void redirect$init$warn(Logger logger, String message)
    {
        if (!WARNED_ALREADY)
        {
            WARNED_ALREADY = true;
            logger.warn("This error has been truncated by TFC in BiomeContainerClientMixin!");
            logger.warn("Since this tends to be a bit spammy (100 MB log file go brrrr), future errors of this kind will be ignored...");
            logger.warn(message);
        }
    }
}
