/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.util.registry.RegistryHolder;

import static net.dries007.tfc.TerraFirmaCraft.*;

@SuppressWarnings("unused")
public class TFCRecipeSerializers
{
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MOD_ID);

    public static final Id<CollapseRecipe> COLLAPSE = register("collapse", BlockRecipe.serializer(CollapseRecipe::new));
    public static final Id<LandslideRecipe> LANDSLIDE = register("landslide", BlockRecipe.serializer(LandslideRecipe::new));
    public static final Id<ChiselRecipe> CHISEL = register("chisel", ChiselRecipe.CODEC, ChiselRecipe.STREAM_CODEC);

    public static final Id<HeatingRecipe> HEATING = register("heating", HeatingRecipe.CODEC, HeatingRecipe.STREAM_CODEC);
    public static final Id<QuernRecipe> QUERN = register("quern", QuernRecipe.CODEC, QuernRecipe.STREAM_CODEC);
    public static final Id<ScrapingRecipe> SCRAPING = register("scraping", ScrapingRecipe.CODEC, ScrapingRecipe.STREAM_CODEC);

    public static final Id<SimplePotRecipe> POT_SIMPLE = register("pot", SimplePotRecipe.CODEC, SimplePotRecipe.STREAM_CODEC);
    public static final Id<SoupPotRecipe> POT_SOUP = register("pot_soup", SoupPotRecipe.CODEC, SoupPotRecipe.STREAM_CODEC);
    public static final Id<JamPotRecipe> POT_JAM = register("pot_jam", JamPotRecipe.CODEC, JamPotRecipe.STREAM_CODEC);

    public static final Id<KnappingRecipe> KNAPPING = register("knapping", KnappingRecipe.CODEC, KnappingRecipe.STREAM_CODEC);
    public static final Id<AlloyRecipe> ALLOY = register("alloy", AlloyRecipe.CODEC, AlloyRecipe.STREAM_CODEC);
    public static final Id<CastingRecipe> CASTING = register("casting", CastingRecipe.CODEC, CastingRecipe.STREAM_CODEC);
    public static final Id<BloomeryRecipe> BLOOMERY = register("bloomery", BloomeryRecipe.CODEC, BloomeryRecipe.STREAM_CODEC);
    public static final Id<SealedBarrelRecipe> SEALED_BARREL = register("barrel_sealed", SealedBarrelRecipe.CODEC, SealedBarrelRecipe.STREAM_CODEC);
    public static final Id<InstantBarrelRecipe> INSTANT_BARREL = register("barrel_instant", InstantBarrelRecipe.CODEC, InstantBarrelRecipe.STREAM_CODEC);
    public static final Id<InstantFluidBarrelRecipe> INSTANT_FLUID_BARREL = register("barrel_instant_fluid", InstantFluidBarrelRecipe.CODEC, InstantFluidBarrelRecipe.STREAM_CODEC);
    public static final Id<LoomRecipe> LOOM = register("loom", LoomRecipe.CODEC, LoomRecipe.STREAM_CODEC);
    public static final Id<AnvilRecipe> ANVIL = register("anvil", AnvilRecipe.CODEC, AnvilRecipe.STREAM_CODEC);
    public static final Id<WeldingRecipe> WELDING = register("welding", WeldingRecipe.CODEC, WeldingRecipe.STREAM_CODEC);
    public static final Id<BlastFurnaceRecipe> BLAST_FURNACE = register("blast_furnace", BlastFurnaceRecipe.CODEC, BlastFurnaceRecipe.STREAM_CODEC);
    public static final Id<GlassworkingRecipe> GLASSWORKING = register("glassworking", GlassworkingRecipe.CODEC, GlassworkingRecipe.STREAM_CODEC);
    public static final Id<SewingRecipe> SEWING = register("sewing", SewingRecipe.CODEC, SewingRecipe.STREAM_CODEC);

    // Crafting

    public static final Id<FoodCombiningCraftingRecipe> FOOD_COMBINING_CRAFTING = register("food_combining", new RecipeSerializerImpl<>(FoodCombiningCraftingRecipe.INSTANCE));
    public static final Id<CastingCraftingRecipe> CASTING_CRAFTING = register("casting_crafting", new RecipeSerializerImpl<>(CastingCraftingRecipe.INSTANCE));

    public static final Id<AdvancedShapedRecipe> ADVANCED_SHAPED_CRAFTING = register("advanced_shaped_crafting", AdvancedShapedRecipe.CODEC, AdvancedShapedRecipe.STREAM_CODEC);
    public static final Id<AdvancedShapelessRecipe> ADVANCED_SHAPELESS_CRAFTING = register("advanced_shapeless_crafting", AdvancedShapelessRecipe.CODEC, AdvancedShapelessRecipe.STREAM_CODEC);

    private static <R extends Recipe<?>> Id<R> register(String name, MapCodec<R> codec, StreamCodec<RegistryFriendlyByteBuf, R> stream)
    {
        return register(name, new RecipeSerializerImpl<>(codec, stream));
    }

    private static <R extends Recipe<?>> Id<R> register(String name, RecipeSerializer<R> serializer)
    {
        return new Id<>(RECIPE_SERIALIZERS.register(name, () -> serializer));
    }

    public record Id<T extends Recipe<?>>(DeferredHolder<RecipeSerializer<?>, RecipeSerializer<T>> holder)
        implements RegistryHolder<RecipeSerializer<?>, RecipeSerializer<T>> {}
}