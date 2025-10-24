/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.compat.emi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.EmiWorldInteractionRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.Tags;

import net.dries007.tfc.client.screen.CalendarScreen;
import net.dries007.tfc.client.screen.ClimateScreen;
import net.dries007.tfc.client.screen.NutritionScreen;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.wood.Wood;
import net.dries007.tfc.common.container.TFCContainerTypes;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.common.recipes.SimplePotRecipe;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeSerializers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.compat.emi.handlers.EmiAnvilHandler;
import net.dries007.tfc.compat.emi.handlers.EmiFirepitHandler;
import net.dries007.tfc.compat.emi.handlers.EmiForgeHandler;
import net.dries007.tfc.compat.emi.handlers.EmiGrillHandler;
import net.dries007.tfc.compat.emi.handlers.EmiSewingHandler;
import net.dries007.tfc.compat.emi.handlers.EmiWeldingHandler;
import net.dries007.tfc.compat.emi.recipe.ComparableRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAdvancedShapedRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAdvancedShapelessRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAlloyingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiAnvilRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiBlastFurnaceRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiBloomeryRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiCastingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiChiselRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiGlassworkingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiHeatingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiInstantBarrelRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiInstantFluidBarrelRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiJamPotRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiKnappingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiLoomRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiQuernRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiSealedBarrelRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiSewingRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiSimplePotRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiSoupPotRecipe;
import net.dries007.tfc.compat.emi.recipe.EmiWeldingRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.KnappingType;

@EmiEntrypoint
public final class EmiIntegration implements EmiPlugin
{
    private static final List<EmiRecipeCategory> CATEGORIES = new ArrayList<>();
    public static final EmiRecipeCategory ALLOYING = createCategory("alloying", TFCBlocks.CRUCIBLE);
    public static final EmiRecipeCategory BLOOMERY = createCategory("bloomery", TFCBlocks.BLOOMERY);
    public static final EmiRecipeCategory BLAST_FURNACE = createCategory("blast_furnace", TFCBlocks.BLAST_FURNACE);
    public static final EmiRecipeCategory CASTING = createCategory("casting", TFCItems.MOLDS.get(Metal.ItemType.INGOT));
    public static final EmiRecipeCategory CHISEL = createCategory("chisel", TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.CHISEL));
    public static final EmiRecipeCategory ANVIL = createCategory("anvil", TFCBlocks.METALS.get(Metal.BRONZE).get(Metal.BlockType.ANVIL));
    public static final EmiRecipeCategory HEATING = createCategory("heating", TFCBlocks.FIREPIT);
    public static final EmiRecipeCategory BARREL = createCategory("barrel", TFCBlocks.WOODS.get(Wood.OAK).get(Wood.BlockType.BARREL));
    public static final EmiRecipeCategory WELDING = createCategory("welding", TFCItems.METAL_ITEMS.get(Metal.WROUGHT_IRON).get(Metal.ItemType.HAMMER));
    public static final EmiRecipeCategory GLASSWORKING = createCategory("glassworking", TFCItems.BLOWPIPE_WITH_GLASS);
    public static final EmiRecipeCategory POT = createCategory("pot", TFCItems.POT);
    public static final EmiRecipeCategory LOOM = createCategory("loom", TFCItems.BURLAP_CLOTH);
    public static final EmiRecipeCategory QUERN = createCategory("quern", TFCBlocks.QUERN);
    public static final EmiRecipeCategory SCRAPING = createCategory("scraping", TFCItems.METAL_ITEMS.get(Metal.BLACK_BRONZE).get(Metal.ItemType.KNIFE));
    public static final EmiRecipeCategory SEWING = createCategory("sewing", TFCItems.BONE_NEEDLE);

    public static final HashMap<KnappingType, EmiRecipeCategory> KNAPPING = new HashMap<>();


    private static EmiRecipeCategory createCategory(String name, ItemLike item)
    {
        EmiRecipeCategory category = new EmiRecipeCategory(Helpers.identifier(name), EmiStack.of(item));
        CATEGORIES.add(category);
        return category;
    }

    private static EmiRecipeCategory createCategory(String name, ItemStack item)
    {
        EmiRecipeCategory category = new EmiRecipeCategory(Helpers.identifier(name), EmiStack.of(item));
        CATEGORIES.add(category);
        return category;
    }

    private static <C extends RecipeInput, T extends Recipe<C>> List<RecipeHolder<T>> recipes(RecipeManager manager, Supplier<RecipeType<T>> type)
    {
        return manager.getAllRecipesFor(type.get()).stream().toList();
    }

    @Override
    public void register(EmiRegistry registry)
    {
        //TODO add drag+drop handlers for things that make sense?
        //TODO fix?: patchouli integration does not work for displayed items in EMI, because it does not post RenderTooltipEvents
        registerCategories(registry);
        registerWorkstations(registry);
        registerRecipes(registry);
        registerRecipeHandlers(registry);
        registerExclusionZones(registry);

        // warning: ghosts and ghouls ahead
        overrideRecipes(registry);
    }

    private void registerCategories(EmiRegistry registry)
    {
        for (var entry : KnappingType.MANAGER.getElements().entrySet())
        {
            KnappingType knappingType = entry.getValue();
            EmiRecipeCategory category = createCategory(entry.getKey().getPath() + "_knapping", knappingType.icon());
            KNAPPING.put(knappingType, category);
        }

        for (EmiRecipeCategory category : CATEGORIES)
        {
            registry.addCategory(category);
            category.sorter = basicSorter();
        }
        ALLOYING.sorter = sortByInput();
        GLASSWORKING.sorter = sortByInput();
    }

    private void registerWorkstations(EmiRegistry registry)
    {
        registry.addWorkstation(HEATING, EmiStack.of(TFCBlocks.FIREPIT));
        registry.addWorkstation(HEATING, EmiStack.of(TFCBlocks.GRILL));
        registry.addWorkstation(BARREL, EmiIngredient.of(TFCTags.Items.BARRELS));
        registry.addWorkstation(ALLOYING, EmiStack.of(TFCBlocks.CRUCIBLE));
        registry.addWorkstation(ALLOYING, EmiIngredient.of(TFCTags.Items.FIRED_VESSELS));
        registry.addWorkstation(ANVIL, EmiIngredient.of(TFCTags.Blocks.ANVILS));
        registry.addWorkstation(WELDING, EmiIngredient.of(TFCTags.Blocks.ANVILS));
        registry.addWorkstation(BLAST_FURNACE, EmiStack.of(TFCBlocks.BLAST_FURNACE));
        registry.addWorkstation(CASTING, EmiIngredient.of(TFCTags.Items.FIRED_MOLDS));
        registry.addWorkstation(BLOOMERY, EmiStack.of(TFCBlocks.BLOOMERY));
        registry.addWorkstation(CHISEL, EmiIngredient.of(TFCTags.Items.TOOLS_CHISEL));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.BLOWPIPE_WITH_GLASS));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.CERAMIC_BLOWPIPE_WITH_GLASS));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.GEM_SAW));
        registry.addWorkstation(GLASSWORKING, EmiStack.of(TFCItems.JACKS));
        registry.addWorkstation(LOOM, EmiIngredient.of(TFCBlocks.WOODS.values().stream().map(wood -> wood.get(Wood.BlockType.LOOM)).map(EmiStack::of).toList()));
        registry.addWorkstation(QUERN, EmiStack.of(TFCBlocks.QUERN));
        registry.addWorkstation(QUERN, EmiStack.of(TFCItems.HANDSTONE));
        registry.addWorkstation(POT, EmiStack.of(TFCBlocks.POT));

        for (var knap : KNAPPING.entrySet())
        {
            KnappingType type = knap.getKey();
            EmiRecipeCategory category = knap.getValue();
            registry.addWorkstation(category, EmiIngredient.of(Arrays.stream(type.inputItem().getItems()).map(EmiStack::of).toList()));
        }
    }

    private void registerRecipes(EmiRegistry registry)
    {
        basicRecipeMapping(registry, TFCRecipeTypes.HEATING, EmiHeatingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.ALLOY, EmiAlloyingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.ANVIL, EmiAnvilRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.WELDING, EmiWeldingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BLAST_FURNACE, EmiBlastFurnaceRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.CASTING, EmiCastingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BLOOMERY, EmiBloomeryRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.CHISEL, EmiChiselRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.GLASSWORKING, EmiGlassworkingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.LOOM, EmiLoomRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.QUERN, EmiQuernRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.SEWING, EmiSewingRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BARREL_SEALED, EmiSealedBarrelRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BARREL_INSTANT, EmiInstantBarrelRecipe::new);
        basicRecipeMapping(registry, TFCRecipeTypes.BARREL_INSTANT_FLUID, EmiInstantFluidBarrelRecipe::new);

        for (RecipeHolder<PotRecipe> entry : recipes(registry.getRecipeManager(), TFCRecipeTypes.POT))
        {
            ResourceLocation id = entry.id();
            PotRecipe recipe = entry.value();
            var serializer = recipe.getSerializer();
            if (serializer == TFCRecipeSerializers.POT_JAM.get())
            {
                registry.addRecipe(new EmiJamPotRecipe(id, (JamPotRecipe) recipe));
            }
            else if (serializer == TFCRecipeSerializers.POT_SOUP.get())
            {
                registry.addRecipe(new EmiSoupPotRecipe(id, (SoupPotRecipe) recipe));
            }
            else if (serializer == TFCRecipeSerializers.POT_SIMPLE.get())
            {
                registry.addRecipe(new EmiSimplePotRecipe(id, (SimplePotRecipe) recipe));
            }
        }

        for (RecipeHolder<KnappingRecipe> entry : recipes(registry.getRecipeManager(), TFCRecipeTypes.KNAPPING))
        {
            KnappingRecipe recipe = entry.value();
            KnappingType type = recipe.knappingType().get();
            EmiRecipeCategory category = KNAPPING.get(type);
            registry.addRecipe(new EmiKnappingRecipe(category, entry.id(), recipe));
        }

        for (RecipeHolder<ScrapingRecipe> entry : recipes(registry.getRecipeManager(), TFCRecipeTypes.SCRAPING))
        {
            ResourceLocation id = entry.id();
            ScrapingRecipe recipe = entry.value();
            ItemStack extra = recipe.getExtraDrop().getEmptyStack();
            EmiWorldInteractionRecipe.Builder builder = EmiWorldInteractionRecipe.builder()
                .id(id)
                .leftInput(EmiIngredient.of(recipe.getIngredient()), EmiHelpers.addTooltipToSlot("tfc.tooltip.scraping.placement"))
                .rightInput(EmiIngredient.of(TFCTags.Blocks.SCRAPING_SURFACE), true)
                .rightInput(EmiHelpers.damagedTool(EmiIngredient.of(TFCTags.Items.TOOLS_KNIFE), 16), true)
                .output(EmiStack.of(recipe.getResultItem(null)));
            if (!extra.isEmpty())
            {
                builder.output(EmiStack.of(extra));
            }
            registry.addRecipe(builder.build());
        }

        // TODO include forge WI recipe, but currently there is no item representation of it to display

        registry.addRecipe(EmiWorldInteractionRecipe.builder()
            .id(EmiHelpers.syntheticId("build_firepit"))
            .leftInput(EmiIngredient.of(TFCTags.Items.FIREPIT_LOGS))
            .leftInput(EmiIngredient.of(TFCTags.Items.FIREPIT_STICKS, 4))
            .leftInput(EmiIngredient.of(TFCTags.Items.FIREPIT_KINDLING), EmiHelpers.addTooltipToSlot("tfc.tooltip.kindling"))
            .rightInput(EmiHelpers.damagedTool(EmiStack.of(TFCItems.FIRESTARTER), 1), false)
            .output(EmiStack.of(TFCBlocks.FIREPIT))
            .build()
        );

        registry.addRecipe(
            EmiHelpers.useItemOn(
                "build_grill",
                EmiStack.of(TFCItems.WROUGHT_IRON_GRILL),
                EmiStack.of(TFCBlocks.FIREPIT),
                EmiStack.of(TFCBlocks.GRILL)
            )
        );
        registry.addRecipe(
            EmiHelpers.useItemOn(
                "build_pot",
                EmiStack.of(TFCItems.POT),
                EmiStack.of(TFCBlocks.FIREPIT),
                EmiStack.of(TFCBlocks.POT)
            )
        );
        registry.addRecipe(
            EmiHelpers.useItemOn(
                "build_stove_pot",
                EmiStack.of(TFCItems.POT),
                EmiStack.of(TFCBlocks.STOVE),
                EmiStack.of(TFCBlocks.STOVE_POT)
            )
        );

        //TODO replace with a tag maybe?
        List<ItemLike> wattle = new ArrayList<>(TFCBlocks.STAINED_WATTLE.values());
        wattle.add(TFCBlocks.UNSTAINED_WATTLE);
        registry.addRecipe(
            EmiHelpers.useItemOn(
                "daub_wattle",
                List.of(
                    EmiIngredient.of(Tags.Items.RODS_WOODEN, 4),
                    EmiStack.of(TFCItems.DAUB)
                ),
                EmiStack.of(TFCBlocks.WATTLE),
                EmiStack.of(TFCBlocks.UNSTAINED_WATTLE),
                false
            )
        );
        for (DyeColor color : DyeColor.values())
        {
            registry.addRecipe(
                EmiHelpers.useItemOn(
                    "dye_wattle/" + color.getName(),
                    EmiIngredient.of(color.getTag()),
                    EmiIngredient.of(wattle.stream().map(EmiStack::of).toList()),
                    EmiStack.of(TFCBlocks.STAINED_WATTLE.get(color))
                )
            );
        }

    }

    private void registerRecipeHandlers(EmiRegistry registry)
    {
        registry.addRecipeHandler(TFCContainerTypes.ANVIL.get(), new EmiWeldingHandler());
        registry.addRecipeHandler(TFCContainerTypes.ANVIL.get(), new EmiAnvilHandler());
        // TODO this will require a custom packet to handle fluids / fluid containers
        //registry.addRecipeHandler(TFCContainerTypes.BARREL.get(), new EmiBarrelHandler());
        registry.addRecipeHandler(TFCContainerTypes.FIREPIT.get(), new EmiFirepitHandler());
        registry.addRecipeHandler(TFCContainerTypes.GRILL.get(), new EmiGrillHandler());
        registry.addRecipeHandler(TFCContainerTypes.CHARCOAL_FORGE.get(), new EmiForgeHandler());
        registry.addRecipeHandler(TFCContainerTypes.SEWING_TABLE.get(), new EmiSewingHandler());
    }

    private void registerExclusionZones(EmiRegistry registry)
    {
        registry.addExclusionArea(InventoryScreen.class, EmiHelpers.inventoryTabExclusionArea());
        registry.addExclusionArea(CalendarScreen.class, EmiHelpers.inventoryTabExclusionArea());
        registry.addExclusionArea(NutritionScreen.class, EmiHelpers.inventoryTabExclusionArea());
        registry.addExclusionArea(ClimateScreen.class, EmiHelpers.inventoryTabExclusionArea());
    }

    /**
     * EMI currently only ignores crafting recipes that extend CustomRecipe, rather than using Recipe#isSpecial... for whatever reason
     * see EMI's <a href="https://github.com/emilyploszaj/emi/blob/4014299650cb42f08283148f4fae6f4707e20627/xplat/src/main/java/dev/emi/emi/VanillaPlugin.java#L409-L429">VanillaPlugin</a>
     * TODO: if this ever changes add recipe handling for our special crafting types e.g. AdvancedShapelessRecipe
     *
     * Ew.
     */
    private void overrideRecipes(EmiRegistry registry)
    {
        List<ResourceLocation> removedRecipes = new ArrayList<>();
        for (RecipeHolder<CraftingRecipe> entry : registry.getRecipeManager().getAllRecipesFor(RecipeType.CRAFTING))
        {
            ResourceLocation id = entry.id();
            CraftingRecipe recipe = entry.value();

            if (recipe instanceof AdvancedShapelessRecipe asr)
            {
                removedRecipes.add(id);
                // Recipe ID has to be different because removing a recipe prevents it from EVER being added, or re-added
                // No way to remove the filter either
                registry.addRecipe(new EmiAdvancedShapelessRecipe(id.withPath("/" + id.getPath()), asr));
            }
            else if (recipe instanceof AdvancedShapedRecipe asr)
            {
                removedRecipes.add(id);
                registry.addRecipe(new EmiAdvancedShapedRecipe(id.withPath("/" + id.getPath()), asr));
            }
        }
        registry.removeRecipes(r -> removedRecipes.contains(r.getId()));

    }

    private static <C extends RecipeInput, T extends Recipe<C>> void basicRecipeMapping(EmiRegistry registry, Supplier<RecipeType<T>> type, BiFunction<ResourceLocation, T, EmiRecipe> mapper)
    {
        for (RecipeHolder<T> recipe : recipes(registry.getRecipeManager(), type))
        {
            registry.addRecipe(mapper.apply(recipe.id(), recipe.value()));
        }
    }

    private static Comparator<EmiRecipe> sortByInput()
    {
        return Comparator.comparingInt(r -> r.getInputs().size());
    }

    private static Comparator<EmiRecipe> basicSorter()
    {
        return (o1, o2) -> {
            if (o1 instanceof ComparableRecipe recipe)
            {
                return recipe.compareTo(o2);
            }
            return 0;
        };
    }

}
