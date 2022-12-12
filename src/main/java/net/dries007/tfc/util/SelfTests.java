/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableSet;
import com.mojang.logging.LogUtils;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.EntityBlockExtension;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.Nutrient;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgingBonus;
import net.dries007.tfc.common.capabilities.heat.Heat;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.common.capabilities.size.Size;
import net.dries007.tfc.common.capabilities.size.Weight;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.util.calendar.Day;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.world.chunkdata.ForestType;
import net.dries007.tfc.world.chunkdata.PlateTectonicsClassification;

import static net.dries007.tfc.TerraFirmaCraft.*;

/**
 * Central location for all self tests
 * These are tests that are performed at runtime for various static analysis purposes.
 * None of these are executed unless {@link Helpers#ASSERTIONS_ENABLED} is {@code true}.
 */
public final class SelfTests
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean THROW_ON_SELF_TEST_FAIL = true;

    private static boolean EXTERNAL_TAG_LOADING_ERROR = false;
    private static boolean EXTERNAL_DATA_MANAGER_ERROR = false;

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static void runWorldVersionTest()
    {
        assert SharedConstants.WORLD_VERSION == 2975 : "If this fails, you need to update the world version here, AND in resources/generate_trees.py, then run `python resources trees`. This updates them and avoids triggering DFU when placed!";
    }

    public static void runClientSelfTests()
    {
        if (Helpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockEntities(),
                validateModels(),
                validateTranslations()
            );
            MinecraftForge.EVENT_BUS.post(new ClientSelfTestEvent()); // For other mods, as this is invoked via a tricky mixin
            LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    public static void runServerSelfTests()
    {
        if (Helpers.ASSERTIONS_ENABLED)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockLootTables(),
                validateOwnBlockMineableTags(),
                validateOwnBlockTags(),
                EXTERNAL_TAG_LOADING_ERROR,
                EXTERNAL_DATA_MANAGER_ERROR
            );
            LOGGER.info("Server self tests passed in {}", tick.stop());
        }
    }

    // Public Self Test API

    /**
     * Iterate the values of a registry, filtered by mod ID
     */
    public static <T extends IForgeRegistryEntry<T>> Stream<T> stream(IForgeRegistry<T> registry, String modID)
    {
        return registry.getValues().stream()
            .filter(e -> {
                assert e.getRegistryName() != null;
                return e.getRegistryName().getNamespace().equals(modID);
            });
    }

    /**
     * Used in {@code stream().flatMap(states(state predicate))} to obtain all blocks matching a state predicate.
     */
    public static Function<Block, Stream<BlockState>> states(Predicate<BlockState> filter)
    {
        return block -> block.getStateDefinition().getPossibleStates().stream().filter(filter);
    }

    /**
     * Validates that blocks declare the right combination of {@link EntityBlock}, {@link EntityBlockExtension} and {@link IForgeBlockExtension}, if they have a block entity.
     */
    public static boolean validateBlockEntities(Stream<Block> blocks, Logger logger)
    {
        final List<Block> fbeButNoEbe = new ArrayList<>(), ebeButNoFbe = new ArrayList<>(), ebButNoEbe = new ArrayList<>();
        blocks.forEach(b -> {
            if (b instanceof IForgeBlockExtension ex && ex.getExtendedProperties().hasBlockEntity() && !(b instanceof EntityBlockExtension))
            {
                fbeButNoEbe.add(b);
            }
            if (b instanceof EntityBlockExtension && (!(b instanceof IForgeBlockExtension ex) || !ex.getExtendedProperties().hasBlockEntity()))
            {
                ebeButNoFbe.add(b);
            }
            if (b instanceof EntityBlock && !(b instanceof EntityBlockExtension))
            {
                ebButNoEbe.add(b);
            }
        });

        return logRegistryErrors("{} blocks found that declare a block entity in IForgeBlockExtension but do not implement EntityBlockExtension", fbeButNoEbe, logger)
            | logRegistryErrors("{} blocks found that implement EntityBlockExtension but do not declare a block entity in IForgeBlockExtension", ebeButNoFbe, logger)
            | logRegistryErrors("{} blocks found that implement EntityBlock but do not implement EntityBlockExtension", ebButNoEbe, logger);
    }

    /**
     * Validates that a translation exists for all enum constants in the style of {@link Helpers#getEnumTranslationKey(Enum)}
     */
    public static <T extends Enum<?>> boolean validateTranslations(Logger logger, Set<String> missingTranslations, Class<? extends T> enumClass)
    {
        boolean errors = false;
        for (T enumConstant : enumClass.getEnumConstants())
        {
            errors |= validateTranslation(logger, missingTranslations, Helpers.translateEnum(enumConstant));
        }
        return errors;
    }

    /**
     * Validates that a translation exists for a component.
     */
    public static boolean validateTranslation(Logger logger, Set<String> missingTranslations, Component component)
    {
        if (component instanceof TranslatableComponent translatable)
        {
            if (!Language.getInstance().has(translatable.getKey()))
            {
                missingTranslations.add(translatable.getKey());
            }
        }
        else
        {
            logger.error("Tried to check the translation key of a non-translatable-component, this is almost certainly a bug, {}", component);
            return true;
        }
        return false;
    }

    /**
     * Validates that all blocks have a loot table defined.
     */
    public static boolean validateBlockLootTables(Stream<Block> blocks, Logger logger)
    {
        final Set<ResourceLocation> lootTables = ServerLifecycleHooks.getCurrentServer().getLootTables().getIds();
        final List<Block> missingLootTables = blocks
            .filter(b -> !lootTables.contains(b.getLootTable()))
            .toList();

        return logRegistryErrors("{} blocks found with a non-existent loot table:", missingLootTables, logger);
    }

    public static void validateDatapacks(RecipeManager manager)
    {
         throwIfAny(
             validateFoodsAreFoods(),
             validateJugDrinkable(),
             validateCollapseRecipeTags(manager),
             validateLandslideRecipeTags(manager),
             validateRockKnappingInputs(manager),
             validateMetalIngotsCanBePiled(),
             validateMetalSheetsCanBePiled(),
             validatePotFluidUsability(manager),
             validateBarrelFluidUsability(manager),
             validateUniqueBloomeryRecipes(manager),
             validateMoldsCanContainCastingIngredients(manager),
             validateHeatingRecipeIngredientsAreHeatable(manager)
         );
    }

    /**
     * Validates that all {@link WallBlock}s have the {@link BlockTags#WALLS} tag.
     * @deprecated Use {@link #validateBlocksHaveTag(Stream, TagKey, Logger)} instead.
     */
    @Deprecated(forRemoval = true)
    public static boolean validateWallBlockWallsTag(Stream<Block> blocks, Logger logger)
    {
        return validateBlocksHaveTag(blocks.filter(b -> b instanceof WallBlock), BlockTags.WALLS, logger);
    }

    public static boolean validateBlocksHaveTag(Stream<Block> blocks, TagKey<Block> tag, Logger logger)
    {
        return logRegistryErrors("{} blocks are missing the #" + tag.location() + " tag", blocks.filter(b -> !Helpers.isBlock(b, tag)).toList(), logger);
    }

    public static <T> boolean logErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {}", e));
            return true;
        }
        return false;
    }

    public static <T> boolean logWarnings(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.warn(error, errors.size());
            errors.forEach(e -> logger.warn("  {}", e));
            return true;
        }
        return false;
    }

    public static <T extends IForgeRegistryEntry<T>> boolean logRegistryErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {} of {}", e.getRegistryName(), e.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    public static void throwIfAny(boolean... errors)
    {
        for (boolean error : errors)
        {
            if (error && THROW_ON_SELF_TEST_FAIL && Helpers.ASSERTIONS_ENABLED)
            {
                throw new AssertionError("Self Tests Failed! Fix the above errors!");
            }
        }
    }

    // Private TFC Self Tests

    public static void reportExternalTagLoadingErrors()
    {
        EXTERNAL_TAG_LOADING_ERROR = true;
    }

    public static void reportExternalDataManagerError()
    {
        EXTERNAL_DATA_MANAGER_ERROR = true;
    }

    private static boolean validateOwnBlockEntities()
    {
        final List<BlockEntityType<?>> errors = stream(ForgeRegistries.BLOCK_ENTITIES, MOD_ID)
            .filter(type -> {
                final BlockEntity b = type.create(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
                return b instanceof TickCounterBlockEntity && b instanceof ICalendarTickable;
            })
            .toList();
        return logRegistryErrors("{} block entities implement ICalendarTickable through TickCounterBlockEntity, this is almost surely a bug", errors, LOGGER)
            | validateBlockEntities(stream(ForgeRegistries.BLOCKS, MOD_ID), LOGGER);
    }

    private static boolean validateOwnBlockLootTables()
    {
        final Set<Block> expectedNoLootTableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE, TFCBlocks.BLOOM, TFCBlocks.MOLTEN, TFCBlocks.SCRAPING, TFCBlocks.THATCH_BED, TFCBlocks.INGOT_PILE, TFCBlocks.SHEET_PILE, TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT), TFCBlocks.PUMPKIN, TFCBlocks.MELON, TFCBlocks.CAKE)
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<Class<?>> expectedNoLootTableClasses = ImmutableSet.of(BodyPlantBlock.class, GrowingFruitTreeBranchBlock.class);
        return validateBlockLootTables(stream(ForgeRegistries.BLOCKS, MOD_ID)
            .filter(b -> !expectedNoLootTableBlocks.contains(b)).filter(b -> !expectedNoLootTableClasses.contains(b.getClass())), LOGGER);
    }

    private static boolean validateOwnBlockMineableTags()
    {
        final Set<Block> expectedNotMineableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.SCRAPING, TFCBlocks.CANDLE, TFCBlocks.DYED_CANDLE.values(), TFCBlocks.CANDLE_CAKE, TFCBlocks.CAKE, TFCBlocks.DYED_CANDLE_CAKES.values()).<Supplier<? extends Block>>flatMap(Helpers::flatten).map(Supplier::get).collect(Collectors.toSet());
        final Set<TagKey<Block>> mineableTags = Set.of(
            BlockTags.MINEABLE_WITH_AXE, BlockTags.MINEABLE_WITH_HOE, BlockTags.MINEABLE_WITH_PICKAXE, BlockTags.MINEABLE_WITH_SHOVEL,
            TFCTags.Blocks.MINEABLE_WITH_PROPICK, TFCTags.Blocks.MINEABLE_WITH_HAMMER, TFCTags.Blocks.MINEABLE_WITH_KNIFE, TFCTags.Blocks.MINEABLE_WITH_SCYTHE, TFCTags.Blocks.MINEABLE_WITH_CHISEL
        );
        // All non-fluid, non-exceptional, blocks with hardness > 0, < infinity, should define a tool
        final List<Block> missingTag = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .filter(b -> !(b instanceof LiquidBlock)
                && b.defaultDestroyTime() > 0
                && !expectedNotMineableBlocks.contains(b)
                && mineableTags.stream().noneMatch(t -> Helpers.isBlock(b, t)))
            .toList();

        return logRegistryErrors("{} non-fluid blocks have no mineable_with_<tool> tag.", missingTag, LOGGER);
    }

    private static boolean validateOwnBlockTags()
    {
        return validateBlocksHaveTag(stream(ForgeRegistries.BLOCKS, MOD_ID).filter(b -> b instanceof WallBlock), BlockTags.WALLS, LOGGER)
            | validateBlocksHaveTag(stream(ForgeRegistries.BLOCKS, MOD_ID).filter(b -> b instanceof StairBlock), BlockTags.STAIRS, LOGGER)
            | validateBlocksHaveTag(stream(ForgeRegistries.BLOCKS, MOD_ID).filter(b -> b instanceof SlabBlock), BlockTags.SLABS, LOGGER);
    }

    /**
     * Detects any instances of missing models, or missing particles, among all models.
     */
    @SuppressWarnings("deprecation")
    private static boolean validateModels()
    {
        final BlockModelShaper shaper = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper();
        final BakedModel missingModel = shaper.getModelManager().getMissingModel();
        final TextureAtlasSprite missingParticle = missingModel.getParticleIcon();

        final List<BlockState> missingModelErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = stream(ForgeRegistries.BLOCKS, MOD_ID)
            .flatMap(states(s -> !s.isAir() && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        return logErrors("{} block states with missing models:", missingModelErrors, LOGGER)
            | logErrors("{} block states with missing particles:", missingParticleErrors, LOGGER);
    }

    /**
     * Detects any missing translation keys, for all items, in all creative tabs.
     */
    private static boolean validateTranslations()
    {
        final Set<String> missingTranslations = Bootstrap.getMissingTranslations();
        final NonNullList<ItemStack> items = NonNullList.create();

        stream(ForgeRegistries.ITEMS, MOD_ID).forEach(item -> {
            items.clear();
            item.fillItemCategory(CreativeModeTab.TAB_SEARCH, items);
            items.forEach(stack -> validateTranslation(LOGGER, missingTranslations, stack.getHoverName()));
        });

        final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        ForgeRegistries.SOUND_EVENTS.getKeys().forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound)).map(WeighedSoundEvents::getSubtitle).ifPresent(subtitle -> validateTranslation(LOGGER, missingTranslations, subtitle)));

        boolean error = false;
        for (CreativeModeTab tab : CreativeModeTab.TABS)
        {
            error |= validateTranslation(LOGGER, missingTranslations, tab.getDisplayName());
        }

        error |= Stream.of(ForgeStep.class, ForgingBonus.class, Metal.Tier.class, Heat.class, Nutrient.class, Size.class, Weight.class, Day.class, Month.class, PlateTectonicsClassification.class, KoppenClimateClassification.class, ForestType.class)
            .anyMatch(clazz -> validateTranslations(LOGGER, missingTranslations, clazz));

        return error | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }

    private static boolean validateFoodsAreFoods()
    {
        final List<Item> errors = Helpers.streamAllTagValues(TFCTags.Items.FOODS, ForgeRegistries.ITEMS)
            .filter(item -> !item.getDefaultInstance().getCapability(FoodCapability.CAPABILITY).isPresent())
            .toList();
        return logWarnings("{} items were in the tfc:foods tag but lacked a food definition", errors, LOGGER);
    }

    private static boolean validateJugDrinkable()
    {
        final List<Fluid> errors = Helpers.streamAllTagValues(TFCTags.Fluids.USABLE_IN_JUG, ForgeRegistries.FLUIDS)
            .filter(fluid -> Drinkable.get(fluid) == null)
            .toList();

        return logWarnings("{} fluids were in the tfc:usable_in_jug tag but lack a Drinkable json entry", errors, LOGGER);
    }

    private static boolean validateCollapseRecipeTags(RecipeManager manager)
    {
        final List<Block> errors = manager.getAllRecipesFor(TFCRecipeTypes.COLLAPSE.get()).stream()
            .flatMap(recipe -> recipe.getBlockIngredient().getValidBlocks().stream())
            .filter(block -> !Helpers.isBlock(block, TFCTags.Blocks.CAN_COLLAPSE))
            .toList();

        return logErrors("{} blocks were defined in a collapse recipe but lack the tfc:can_collapse tag", errors, LOGGER);
    }

    private static boolean validateLandslideRecipeTags(RecipeManager manager)
    {
        final List<Block> errors = manager.getAllRecipesFor(TFCRecipeTypes.LANDSLIDE.get()).stream()
            .flatMap(recipe -> recipe.getBlockIngredient().getValidBlocks().stream())
            .filter(block -> !Helpers.isBlock(block, TFCTags.Blocks.CAN_LANDSLIDE))
            .toList();

        return logErrors("{} blocks were defined in a landslide recipe but lack the tfc:can_landslide tag", errors, LOGGER);
    }

    private static boolean validateRockKnappingInputs(RecipeManager manager)
    {
        final List<ItemStack> errors = manager.getAllRecipesFor(TFCRecipeTypes.ROCK_KNAPPING.get()).stream()
            .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
            .filter(item -> !Helpers.isItem(item, TFCTags.Items.ROCK_KNAPPING))
            .toList();

        return logErrors("{} items were used as ingredients for a rock knapping recipe but do not have the tfc:rock_knapping tag", errors, LOGGER);
    }

    private static boolean validateMetalIngotsCanBePiled()
    {
        final Set<Item> allMetalIngots = Metal.MANAGER.getValues().stream()
            .flatMap(metal -> Arrays.stream(metal.getIngotIngredient().getItems())).map(ItemStack::getItem).collect(Collectors.toSet());

        final List<Item> tagButNoMetal = Helpers.streamAllTagValues(TFCTags.Items.PILEABLE_INGOTS, ForgeRegistries.ITEMS).filter(item -> !allMetalIngots.contains(item)).toList();
        final List<Item> metalButNoTag = allMetalIngots.stream().filter(item -> !Helpers.isItem(item, TFCTags.Items.PILEABLE_INGOTS)).toList();
        return logErrors("{} ingot items are in the tfc:pileable_ingots tag but not defined in a metal json's ingot ingredient", tagButNoMetal, LOGGER)
            || logErrors("{} ingot items are defined in a metal json's ingot ingredient but are absent from the tfc:pileable_ingots tag", metalButNoTag, LOGGER);
    }

    private static boolean validateMetalSheetsCanBePiled()
    {
        final Set<Item> allMetalSheets = Metal.MANAGER.getValues().stream()
            .flatMap(metal -> Arrays.stream(metal.getSheetIngredient().getItems())).map(ItemStack::getItem).collect(Collectors.toSet());

        final List<Item> tagButNoMetal = Helpers.streamAllTagValues(TFCTags.Items.PILEABLE_SHEETS, ForgeRegistries.ITEMS).filter(item -> !allMetalSheets.contains(item)).toList();
        final List<Item> metalButNoTag = allMetalSheets.stream().filter(item -> item != Items.BARRIER && !Helpers.isItem(item, TFCTags.Items.PILEABLE_SHEETS)).toList();
        return logErrors("{} sheet items are in the tfc:pileable_sheets tag but not defined in a metal json's sheet ingredient", tagButNoMetal, LOGGER)
            || logErrors("{} sheet items are defined in a metal json's sheet ingredient but are absent from the tfc:pileable_sheets tag", metalButNoTag, LOGGER);
    }

    private static boolean validatePotFluidUsability(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.POT.get()).stream()
            .flatMap(recipe -> recipe.getFluidIngredient().ingredient().getMatchingFluids().stream())
            .filter(fluid -> !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_POT))
            .toList();
        return logErrors("{} fluids are listed in pot recieps that are not tagged as tfc:usable_in_pot", errors, LOGGER);
    }

    private static boolean validateBarrelFluidUsability(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getRecipes().stream()
            .filter(recipe -> recipe instanceof BarrelRecipe)
            .map(recipe -> (BarrelRecipe) recipe)
            .flatMap(recipe -> Stream.concat(recipe.getInputFluid().ingredient().getMatchingFluids().stream(), Stream.of(recipe.getOutputFluid().getFluid())))
            .filter(fluid -> !fluid.isSame(Fluids.EMPTY) && !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_BARREL))
            .toList();
        return logErrors("{} fluids are listed in barrel recipes that are not tagged as tfc:usable_in_barrel", errors, LOGGER);
    }

    private static boolean validateUniqueBloomeryRecipes(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.BLOOMERY.get()).stream()
            .flatMap(recipe -> recipe.getInputFluid().ingredient().getMatchingFluids().stream())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream().filter(m -> m.getValue() > 1)
            .map(Map.Entry::getKey).toList();
        return logErrors("{} fluids appeared in multiple bloomery recipes. Currently, every bloomery recipe must have a unique fluid input in order to work", errors, LOGGER);
    }

    private static boolean validateMoldsCanContainCastingIngredients(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.CASTING.get()).stream()
            .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems())
                .filter(stack -> stack.getItem() instanceof MoldItem)
                .flatMap(stack ->
                    recipe.getFluidIngredient().ingredient().getMatchingFluids().stream().filter(fluid -> !Helpers.isFluid(fluid, ((MoldItem) stack.getItem()).getFluidTag()))
                )
            ).toList();

        return logErrors("{} fluids were found that were given as ingredients in a casting recipe that could not be put into the specified mold. This probably means that you need to add fluids to the tfc:usable_in_tool_head_mold or tfc:usable_in_ingot_mold tag.", errors, LOGGER);
    }

    private static boolean validateHeatingRecipeIngredientsAreHeatable(RecipeManager manager)
    {
        final List<ItemStack> errors = manager.getAllRecipesFor(TFCRecipeTypes.HEATING.get()).stream()
            .flatMap(recipe -> Arrays.stream(recipe.getIngredient().getItems()))
            .filter(stack -> HeatCapability.get(stack) == null).toList();
        return logErrors("{} items found as ingredients to heating recipes without a heat definition!", errors, LOGGER);
    }

    public static class ClientSelfTestEvent extends Event {}
}
