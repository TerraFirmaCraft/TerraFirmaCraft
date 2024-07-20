/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.food;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

@SuppressWarnings("unused")
public class FoodTraits
{
    public static final ResourceKey<Registry<FoodTrait>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("food_trait"));
    public static final Registry<FoodTrait> REGISTRY = new RegistryBuilder<>(KEY).sync(true).create();

    public static final DeferredRegister<FoodTrait> TRAITS = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    public static final DeferredHolder<FoodTrait, FoodTrait> SALTED = register("salted", TFCConfig.SERVER.traitSaltedModifier);
    public static final DeferredHolder<FoodTrait, FoodTrait> BRINED = register("brined", TFCConfig.SERVER.traitBrinedModifier); // No decay modifier, required to pickle foods
    public static final DeferredHolder<FoodTrait, FoodTrait> PICKLED = register("pickled", TFCConfig.SERVER.traitPickledModifier);
    public static final DeferredHolder<FoodTrait, FoodTrait> PRESERVED = register("preserved", TFCConfig.SERVER.traitPreservedModifier); // Large / Small vessels
    public static final DeferredHolder<FoodTrait, FoodTrait> PRESERVED_IN_VINEGAR = register("vinegar", TFCConfig.SERVER.traitVinegarModifier); // Used for the state of being sealed in vinegar
    public static final DeferredHolder<FoodTrait, FoodTrait> CHARCOAL_GRILLED = register("charcoal_grilled", TFCConfig.SERVER.traitCharcoalGrilledModifier); // Slight debuff from cooking in a charcoal forge
    public static final DeferredHolder<FoodTrait, FoodTrait> WOOD_GRILLED = register("wood_grilled", TFCConfig.SERVER.traitWoodGrilledModifier); // Slight buff when cooking in a grill
    public static final DeferredHolder<FoodTrait, FoodTrait> BURNT_TO_A_CRISP = register("burnt_to_a_crisp", TFCConfig.SERVER.traitBurntToACrispModifier); // Cooking food in something that's WAY TOO HOT too cook food in you fool!
    public static final DeferredHolder<FoodTrait, FoodTrait> WILD = register("wild", TFCConfig.SERVER.traitWildModifier); // wild pumpkins last a bit longer, just in case you don't see them right away.


    private static DeferredHolder<FoodTrait, FoodTrait> register(String name, ModConfigSpec.DoubleValue decayModifier)
    {
        return TRAITS.register(name, () -> new FoodTrait(() -> decayModifier.get().floatValue(), "tfc.tooltip.food_trait." + name));
    }
}
