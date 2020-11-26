package net.dries007.tfc.mixin.item.crafting;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.apache.logging.log4j.Logger;
import net.minecraft.client.resources.JsonReloadListener;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.config.TFCConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RecipeManager.class)
public abstract class RecipeManagerMixin extends JsonReloadListener
{
    @Shadow private Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> recipes;

    private RecipeManagerMixin(Gson gson_, String string_)
    {
        super(gson_, string_);
    }

    /**
     * Log a more useful message. Full stack trace is not useful. Concise, readable errors are useful.
     */
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Lorg/apache/logging/log4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V", remap = false), require = 0)
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

    /**
     * This fixes a stupid vanilla bug - when it logs "Loaded X recipes", it actually logs the number of recipe types, not the number of recipes.
     */
    @Redirect(method = "apply", at = @At(value = "INVOKE", target = "Ljava/util/Map;size()I"), require = 0)
    private int redirect$apply$size(Map<IRecipeType<?>, ImmutableMap.Builder<ResourceLocation, IRecipe<?>>> map)
    {
        if (TFCConfig.COMMON.enableDevTweaks.get())
        {
            return this.recipes.values().stream().mapToInt(Map::size).sum();
        }
        return map.size(); // Default behavior
    }
}
