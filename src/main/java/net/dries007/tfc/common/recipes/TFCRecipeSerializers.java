/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCRecipeSerializers
{
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    public static final RegistryObject<CollapseRecipe.Serializer> COLLAPSE = SERIALIZERS.register("collapse", CollapseRecipe.Serializer::new);
    public static final RegistryObject<LandslideRecipe.Serializer> LANDSLIDE = SERIALIZERS.register("landslide", LandslideRecipe.Serializer::new);
}
