/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mezz.jei.api.recipe.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferRegistry;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodPreservation;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipeFoodTraits;
import net.dries007.tfc.api.recipes.heat.HeatRecipeMetalMelting;
import net.dries007.tfc.api.recipes.knapping.KnappingType;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.client.gui.*;
import net.dries007.tfc.compat.jei.categories.*;
import net.dries007.tfc.compat.jei.wrappers.*;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.wood.BlockLoom;
import net.dries007.tfc.objects.container.ContainerInventoryCrafting;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.metal.ItemAnvil;
import net.dries007.tfc.objects.items.metal.ItemMetalChisel;
import net.dries007.tfc.objects.items.metal.ItemMetalTool;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.recipes.SaltingRecipe;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;

@JEIPlugin
public final class TFCJEIPlugin implements IModPlugin
{
    public static final String ALLOY_UID = TerraFirmaCraft.MOD_ID + ".alloy";
    public static final String ANVIL_UID = TerraFirmaCraft.MOD_ID + ".anvil";
    public static final String BARREL_UID = TerraFirmaCraft.MOD_ID + ".barrel";
    public static final String BLAST_FURNACE_UID = TerraFirmaCraft.MOD_ID + ".blast_furnace";
    public static final String BLOOMERY_UID = TerraFirmaCraft.MOD_ID + ".bloomery";
    public static final String CASTING_UID = TerraFirmaCraft.MOD_ID + ".casting";
    public static final String CHISEL_UID = TerraFirmaCraft.MOD_ID + ".chisel";
    public static final String HEAT_UID = TerraFirmaCraft.MOD_ID + ".heat";
    public static final String KNAP_CLAY_UID = TerraFirmaCraft.MOD_ID + ".knap.clay";
    public static final String KNAP_FIRECLAY_UID = TerraFirmaCraft.MOD_ID + ".knap.fireclay";
    public static final String KNAP_LEATHER_UID = TerraFirmaCraft.MOD_ID + ".knap.leather";
    public static final String KNAP_STONE_UID = TerraFirmaCraft.MOD_ID + ".knap.stone";
    public static final String METAL_HEAT_UID = TerraFirmaCraft.MOD_ID + ".metal_heat";
    public static final String LOOM_UID = TerraFirmaCraft.MOD_ID + ".loom";
    public static final String QUERN_UID = TerraFirmaCraft.MOD_ID + ".quern";
    public static final String ROCK_LAYER_UID = TerraFirmaCraft.MOD_ID + ".rock_layer";
    public static final String VEIN_UID = TerraFirmaCraft.MOD_ID + ".vein";
    public static final String WELDING_UID = TerraFirmaCraft.MOD_ID + ".welding";

    private static IModRegistry REGISTRY;

    /**
     * Helper method to return a collection containing all possible itemstacks registered in JEI
     *
     * @return Collection of ItemStacks
     */
    public static Collection<ItemStack> getAllIngredients()
    {
        return REGISTRY.getIngredientRegistry().getAllIngredients(VanillaTypes.ITEM);
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry)
    {
        //Add new JEI recipe categories
        registry.addRecipeCategories(new AlloyCategory(registry.getJeiHelpers().getGuiHelper(), ALLOY_UID));
        registry.addRecipeCategories(new AnvilCategory(registry.getJeiHelpers().getGuiHelper(), ANVIL_UID));
        registry.addRecipeCategories(new BarrelCategory(registry.getJeiHelpers().getGuiHelper(), BARREL_UID));
        registry.addRecipeCategories(new BlastFurnaceCategory(registry.getJeiHelpers().getGuiHelper(), BLAST_FURNACE_UID));
        registry.addRecipeCategories(new BloomeryCategory(registry.getJeiHelpers().getGuiHelper(), BLOOMERY_UID));
        registry.addRecipeCategories(new CastingCategory(registry.getJeiHelpers().getGuiHelper(), CASTING_UID));
        registry.addRecipeCategories(new ChiselCategory(registry.getJeiHelpers().getGuiHelper(), CHISEL_UID));
        registry.addRecipeCategories(new HeatCategory(registry.getJeiHelpers().getGuiHelper(), HEAT_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_CLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_FIRECLAY_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_LEATHER_UID));
        registry.addRecipeCategories(new KnappingCategory(registry.getJeiHelpers().getGuiHelper(), KNAP_STONE_UID));
        registry.addRecipeCategories(new LoomCategory(registry.getJeiHelpers().getGuiHelper(), LOOM_UID));
        registry.addRecipeCategories(new MetalHeatingCategory(registry.getJeiHelpers().getGuiHelper(), METAL_HEAT_UID));
        registry.addRecipeCategories(new QuernCategory(registry.getJeiHelpers().getGuiHelper(), QUERN_UID));
        registry.addRecipeCategories(new RockLayerCategory(registry.getJeiHelpers().getGuiHelper(), ROCK_LAYER_UID));
        registry.addRecipeCategories(new VeinCategory(registry.getJeiHelpers().getGuiHelper(), VEIN_UID));
        registry.addRecipeCategories(new WeldingCategory(registry.getJeiHelpers().getGuiHelper(), WELDING_UID));
    }

    @Override
    public void register(IModRegistry registry)
    {
        REGISTRY = registry;

        //Wraps all quern recipes
        List<SimpleRecipeWrapper> quernList = TFCRegistries.QUERN.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(quernList, QUERN_UID); //Register recipes to quern category
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.QUERN), QUERN_UID); //Register BlockQuern as the device that do quern recipes

        //Wraps all heating recipes, if they return ingredient(1 or more) -> itemstacks(1 or more)
        List<HeatRecipeWrapper> heatList = TFCRegistries.HEAT.getValuesCollection()
            .stream()
            .filter(r -> r.getOutputs().size() > 0 && r.getIngredients().size() > 0)
            .map(HeatRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(heatList, HEAT_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.FIREPIT), HEAT_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CHARCOAL_FORGE), HEAT_UID);

        //Wraps all anvil recipes
        List<AnvilRecipeWrapper> anvilList = TFCRegistries.ANVIL.getValuesCollection()
            .stream()
            .map(AnvilRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(anvilList, ANVIL_UID);

        //Wraps all welding recipes
        List<WeldingRecipeWrapper> weldList = TFCRegistries.WELDING.getValuesCollection()
            .stream()
            .map(WeldingRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(weldList, WELDING_UID);

        //Wraps all loom recipes
        List<SimpleRecipeWrapper> loomRecipes = TFCRegistries.LOOM.getValuesCollection()
            .stream()
            .map(SimpleRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(loomRecipes, LOOM_UID);
        for (Tree tree : TFCRegistries.TREES.getValuesCollection())
        {
            registry.addRecipeCatalyst(new ItemStack(BlockLoom.get(tree)), LOOM_UID);
        }

        // Alloy Recipes
        List<AlloyRecipeWrapper> alloyRecipes = TFCRegistries.ALLOYS.getValuesCollection().stream()
            .map(AlloyRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(alloyRecipes, ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), ALLOY_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRED_VESSEL), ALLOY_UID);

        // Clay Knapping
        List<KnappingRecipeWrapper> clayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection().stream()
            .filter(recipe -> recipe.getType() == KnappingType.CLAY)
            .map(recipe -> new KnappingRecipeWrapper(recipe, registry.getJeiHelpers().getGuiHelper()))
            .collect(Collectors.toList());

        registry.addRecipes(clayknapRecipes, KNAP_CLAY_UID);
        for (ItemStack stack : OreDictionary.getOres("clay"))
        {
            registry.addRecipeCatalyst(stack, KNAP_CLAY_UID);
        }

        // Fire Clay Knapping
        List<KnappingRecipeWrapper> fireclayknapRecipes = TFCRegistries.KNAPPING.getValuesCollection().stream()
            .filter(recipe -> recipe.getType() == KnappingType.FIRE_CLAY)
            .map(recipe -> new KnappingRecipeWrapper(recipe, registry.getJeiHelpers().getGuiHelper()))
            .collect(Collectors.toList());

        registry.addRecipes(fireclayknapRecipes, KNAP_FIRECLAY_UID);
        for (ItemStack stack : OreDictionary.getOres("fireClay"))
        {
            registry.addRecipeCatalyst(stack, KNAP_FIRECLAY_UID);
        }

        // Leather Knapping
        List<KnappingRecipeWrapper> leatherknapRecipes = TFCRegistries.KNAPPING.getValuesCollection().stream()
            .filter(recipe -> recipe.getType() == KnappingType.LEATHER)
            .map(recipe -> new KnappingRecipeWrapper(recipe, registry.getJeiHelpers().getGuiHelper()))
            .collect(Collectors.toList());

        registry.addRecipes(leatherknapRecipes, KNAP_LEATHER_UID);
        for (ItemStack stack : OreDictionary.getOres("leather"))
        {
            registry.addRecipeCatalyst(stack, KNAP_LEATHER_UID);
        }

        // Leather Knapping Recipes
        List<KnappingRecipeWrapper> stoneknapRecipes = TFCRegistries.KNAPPING.getValuesCollection().stream()
            .filter(recipe -> recipe.getType() == KnappingType.STONE)
            .flatMap(recipe -> TFCRegistries.ROCKS.getValuesCollection().stream().map(rock -> new KnappingRecipeWrapper.Stone(recipe, registry.getJeiHelpers().getGuiHelper(), rock)))
            .collect(Collectors.toList());

        registry.addRecipes(stoneknapRecipes, KNAP_STONE_UID);
        for (Rock rock : TFCRegistries.ROCKS.getValuesCollection())
        {
            registry.addRecipeCatalyst(new ItemStack(ItemRock.get(rock)), KNAP_STONE_UID);
        }

        //Wraps all barrel recipes
        List<BarrelRecipeWrapper> barrelRecipes = TFCRegistries.BARREL.getValuesCollection()
            .stream().filter(recipe -> recipe instanceof BarrelRecipeFoodTraits || recipe instanceof BarrelRecipeFoodPreservation || recipe.getOutputStack() != ItemStack.EMPTY || recipe.getOutputFluid() != null)
            .map(BarrelRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(barrelRecipes, BARREL_UID);
        for (Item barrelItem : BlocksTFC.getAllBarrelItemBlocks())
        {
            registry.addRecipeCatalyst(new ItemStack(barrelItem), BARREL_UID);
        }

        //Wraps all bloomery recipes
        List<BloomeryRecipeWrapper> bloomeryList = TFCRegistries.BLOOMERY.getValuesCollection()
            .stream()
            .map(BloomeryRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(bloomeryList, BLOOMERY_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.BLOOMERY), BLOOMERY_UID);

        //Wraps all blast furnace recipes
        List<BlastFurnaceRecipeWrapper> blastList = TFCRegistries.BLAST_FURNACE.getValuesCollection()
            .stream()
            .map(BlastFurnaceRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(blastList, BLAST_FURNACE_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.BLAST_FURNACE), BLAST_FURNACE_UID);

        //Wraps all metal melting recipes
        List<MetalHeatingRecipeWrapper> heatMetalList = new ArrayList<>();
        getAllIngredients().forEach(stack -> {
            HeatRecipeMetalMelting recipe = (HeatRecipeMetalMelting) TFCRegistries.HEAT.getValuesCollection()
                .stream().filter(x -> x instanceof HeatRecipeMetalMelting)
                .filter(x -> x.isValidInput(stack, Metal.Tier.TIER_VI))
                .findFirst().orElse(null);
            if (recipe != null)
            {
                FluidStack fluidStack = recipe.getOutputFluid(stack);
                // Don't add not meltable (ie: iron ore)
                if (fluidStack != null && FluidsTFC.getMetalFromFluid(fluidStack.getFluid()) == recipe.getMetal())
                {
                    MetalHeatingRecipeWrapper wrapper = new MetalHeatingRecipeWrapper(stack, recipe.getMetal(), fluidStack.amount, recipe.getTransformTemp());
                    heatMetalList.add(wrapper);
                }
            }
        });
        registry.addRecipes(heatMetalList, METAL_HEAT_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), METAL_HEAT_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRED_VESSEL), METAL_HEAT_UID);

        //Wraps all chisel recipes
        List<ChiselRecipeWrapper> chiselList = TFCRegistries.CHISEL.getValuesCollection()
            .stream()
            .map(ChiselRecipeWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(chiselList, CHISEL_UID);

        //Wraps all rock layers
        List<RockLayerWrapper> rockLayerList = TFCRegistries.ROCKS.getValuesCollection()
            .stream()
            .map(RockLayerWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(rockLayerList, ROCK_LAYER_UID);

        //Wraps all veins
        List<VeinWrapper> veinList = VeinRegistry.INSTANCE.getVeins().values()
            .stream().map(VeinWrapper::new)
            .collect(Collectors.toList());

        registry.addRecipes(veinList, VEIN_UID);

        // Register metal related stuff (put everything here for performance + sorted registration)
        List<UnmoldRecipeWrapper> unmoldList = new ArrayList<>();
        List<CastingRecipeWrapper> castingList = new ArrayList<>();
        List<Metal> tierOrdered = TFCRegistries.METALS.getValuesCollection()
            .stream()
            .sorted(Comparator.comparingInt(metal -> metal.getTier().ordinal()))
            .collect(Collectors.toList());
        for (Metal metal : tierOrdered)
        {
            if (Metal.ItemType.ANVIL.hasType(metal))
            {
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), ANVIL_UID);
                registry.addRecipeCatalyst(new ItemStack(ItemAnvil.get(metal, Metal.ItemType.ANVIL)), WELDING_UID);
            }
            if (Metal.ItemType.CHISEL.hasType(metal))
            {
                registry.addRecipeCatalyst(new ItemStack(ItemMetalChisel.get(metal, Metal.ItemType.CHISEL)), CHISEL_UID);
            }
            if (Metal.ItemType.PROPICK.hasType(metal))
            {
                registry.addRecipeCatalyst(new ItemStack(ItemMetalTool.get(metal, Metal.ItemType.PROPICK)), ROCK_LAYER_UID);
                registry.addRecipeCatalyst(new ItemStack(ItemMetalTool.get(metal, Metal.ItemType.PROPICK)), VEIN_UID);
            }
            for (Metal.ItemType type : Metal.ItemType.values())
            {
                if (type.hasMold(metal))
                {
                    unmoldList.add(new UnmoldRecipeWrapper(metal, type));
                    castingList.add(new CastingRecipeWrapper(metal, type));
                }
            }
        }
        registry.addRecipes(unmoldList, VanillaRecipeCategoryUid.CRAFTING);
        registry.addRecipes(castingList, CASTING_UID);
        registry.addRecipeCatalyst(new ItemStack(BlocksTFC.CRUCIBLE), CASTING_UID);
        registry.addRecipeCatalyst(new ItemStack(ItemsTFC.FIRED_VESSEL), CASTING_UID);

        //Click areas
        registry.addRecipeClickArea(GuiKnapping.class, 97, 44, 22, 15, KNAP_CLAY_UID, KNAP_FIRECLAY_UID, KNAP_LEATHER_UID, KNAP_STONE_UID);
        registry.addRecipeClickArea(GuiAnvilTFC.class, 26, 24, 9, 14, ANVIL_UID, WELDING_UID);
        registry.addRecipeClickArea(GuiBarrel.class, 92, 21, 9, 14, BARREL_UID);
        registry.addRecipeClickArea(GuiCrucible.class, 139, 100, 10, 15, ALLOY_UID);
        registry.addRecipeClickArea(GuiCrucible.class, 82, 100, 10, 15, METAL_HEAT_UID);
        registry.addRecipeClickArea(GuiFirePit.class, 79, 37, 18, 10, HEAT_UID);

        // Fix inventory tab overlap see https://github.com/TerraFirmaCraft/TerraFirmaCraft/issues/646
        registry.addAdvancedGuiHandlers(new TFCInventoryGuiHandler<>(GuiInventory.class));
        registry.addAdvancedGuiHandlers(new TFCInventoryGuiHandler<>(GuiCalendar.class));
        registry.addAdvancedGuiHandlers(new TFCInventoryGuiHandler<>(GuiNutrition.class));
        registry.addAdvancedGuiHandlers(new TFCInventoryGuiHandler<>(GuiSkills.class));

        //Add JEI descriptions for basic mechanics

        registry.addIngredientInfo(new ItemStack(BlocksTFC.PIT_KILN, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.description.tfc.pit_kiln").getFormattedText());
        registry.addIngredientInfo(new ItemStack(BlocksTFC.PLACED_ITEM, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.description.tfc.placed_item").getFormattedText());
        registry.addIngredientInfo(new ItemStack(Items.COAL, 1, 1), VanillaTypes.ITEM, new TextComponentTranslation("jei.description.tfc.charcoal_pit").getFormattedText());


        //Custom handlers
        registry.handleRecipes(SaltingRecipe.class, SaltingRecipeWrapper::new, VanillaRecipeCategoryUid.CRAFTING);

        //ContainerInventoryCrafting - Add ability to transfer recipe items
        IRecipeTransferRegistry transferRegistry = registry.getRecipeTransferRegistry();
        transferRegistry.addRecipeTransferHandler(ContainerInventoryCrafting.class, VanillaRecipeCategoryUid.CRAFTING, 1, 9, 10, 36);
    }
}
