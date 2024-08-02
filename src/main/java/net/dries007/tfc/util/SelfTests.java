/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.fluids.FluidStack;
import org.slf4j.Logger;

import net.dries007.tfc.common.TFCCreativeTabs;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TFCBlockEntity;
import net.dries007.tfc.common.blocks.PouredGlassBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.devices.IngotPileBlock;
import net.dries007.tfc.common.blocks.devices.ScrapingBlock;
import net.dries007.tfc.common.blocks.devices.SheetPileBlock;
import net.dries007.tfc.common.blocks.plant.BodyPlantBlock;
import net.dries007.tfc.common.blocks.plant.BranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.GrowingBranchingCactusBlock;
import net.dries007.tfc.common.blocks.plant.Plant;
import net.dries007.tfc.common.blocks.plant.fruit.GrowingFruitTreeBranchBlock;
import net.dries007.tfc.common.blocks.rock.RockDisplayCategory;
import net.dries007.tfc.common.component.food.Nutrient;
import net.dries007.tfc.common.component.forge.ForgeStep;
import net.dries007.tfc.common.component.forge.ForgingBonus;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.mold.IMold;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.TFCRecipeTypes;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Day;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.KoppenClimateClassification;
import net.dries007.tfc.util.data.Drinkable;
import net.dries007.tfc.world.chunkdata.ForestType;

/**
 * These are various tests, including utilities for addons to utilize these if they wish, to execute tests of the mod runtime, for example
 * validating that translations, models, loot tables, all exist. This also houses a series of datapack validations, which are useful to
 * provide to pack makers if they so desire.
 * <p>
 * Self tests (client and server) are enabled via the system property {@code tfc.enableDebugSelfTests}, which can be set in your {@code build.gradle}
 * if desired. Datapack self tests are enabled via a config option.
 */
public final class SelfTests
{
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final boolean THROW_ON_SELF_TEST_FAIL = false; // todo 1.21, re-enable
    private static final boolean RUN_SELF_TESTS = Boolean.getBoolean("tfc.enableDebugSelfTests");

    private static boolean EXTERNAL_ERROR = false;

    @SuppressWarnings({"ConstantConditions", "deprecation"})
    public static void runWorldVersionTest()
    {
        assert SharedConstants.WORLD_VERSION == 3953 : "If this fails, you need to update the world version here, AND in resources/generate_trees.py, then run `python resources trees`. This updates them and avoids triggering DFU when placed!";
    }

    public static void runClientSelfTests()
    {
        NeoForge.EVENT_BUS.post(new ClientSelfTestEvent()); // For other mods, as this is invoked via a tricky mixin
        if (RUN_SELF_TESTS)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateModels(),
                validateTranslationsAndCreativeTabs()
            );
            LOGGER.info("Client self tests passed in {}", tick.stop());
        }
    }

    public static void runServerSelfTests(MinecraftServer server)
    {
        if (RUN_SELF_TESTS)
        {
            final Stopwatch tick = Stopwatch.createStarted();
            throwIfAny(
                validateOwnBlockLootTables(server),
                EXTERNAL_ERROR
            );
            LOGGER.info("Server self tests passed in {}", tick.stop());
        }
    }

    public static void runDataPackTests(RecipeManager manager)
    {
        if (TFCConfig.COMMON.enableDatapackTests.get())
        {
            final Stopwatch tick = Stopwatch.createStarted();
            if (!(validateJugDrinkable()
                | validatePotFluidUsability(manager)
                | validateBarrelFluidUsability(manager)
                | validateUniqueBloomeryRecipes(manager)
                | validateUniqueLoomRecipes(manager)
                | validateMoldsCanContainCastingIngredients(manager)
                | validateHeatingRecipeIngredientsAreHeatable(manager))
            )
            {
                LOGGER.info("Data pack self tests passed in {}", tick.stop());
            }
        }
    }

    // Public Self Test API

    /**
     * Used in {@code stream().flatMap(states(state predicate))} to obtain all blocks matching a state predicate.
     */
    public static Function<Holder<? extends Block>, Stream<BlockState>> states(Predicate<BlockState> filter)
    {
        return block -> block.value().getStateDefinition().getPossibleStates().stream().filter(filter);
    }

    /**
     * Validates that a translation exists for a component.
     */
    public static boolean validateTranslation(Logger logger, Set<String> missingTranslations, Component component)
    {
        if (component.getContents() instanceof TranslatableContents translatable)
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
    public static boolean validateBlockLootTables(MinecraftServer server, List<Block> blocks, Logger logger)
    {
        final Collection<ResourceLocation> lootTables = server
            .reloadableRegistries()
            .get()
            .registryOrThrow(Registries.LOOT_TABLE)
            .keySet();
        final List<Block> missingLootTables = blocks.stream()
            .filter(b -> !lootTables.contains(b.getLootTable().location()))
            .filter(b -> !b.defaultBlockState().isAir())
            .toList();

        return logRegistryErrors("{} blocks found with a non-existent loot table:", missingLootTables, logger);
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

    public static <T> boolean logRegistryErrors(String error, Collection<T> errors, Logger logger)
    {
        if (!errors.isEmpty())
        {
            logger.error(error, errors.size());
            errors.forEach(e -> logger.error("  {} of {}", e.toString(), e.getClass().getSimpleName()));
            return true;
        }
        return false;
    }

    public static void throwIfAny(boolean... errors)
    {
        for (boolean error : errors)
        {
            if (error && THROW_ON_SELF_TEST_FAIL)
            {
                throw new AssertionError("Self Tests Failed! Fix the above errors!");
            }
        }
    }

    // Private TFC Self Tests

    public static void reportExternalError()
    {
        EXTERNAL_ERROR = true;
    }

    private static boolean validateOwnBlockLootTables(MinecraftServer server)
    {
        final Set<Block> expectedNoLootTableBlocks = Stream.of(TFCBlocks.PLACED_ITEM, TFCBlocks.PIT_KILN, TFCBlocks.LOG_PILE, TFCBlocks.BURNING_LOG_PILE, TFCBlocks.BLOOM, TFCBlocks.MOLTEN, TFCBlocks.SCRAPING, TFCBlocks.THATCH_BED, TFCBlocks.INGOT_PILE, TFCBlocks.DOUBLE_INGOT_PILE, TFCBlocks.SHEET_PILE, TFCBlocks.PLANTS.get(Plant.GIANT_KELP_PLANT), TFCBlocks.PUMPKIN, TFCBlocks.MELON, TFCBlocks.CAKE, TFCBlocks.CALCITE, TFCBlocks.ICICLE, TFCBlocks.RIVER_WATER, TFCBlocks.SPRING_WATER, TFCBlocks.LIGHT, TFCBlocks.SALTWATER_BUBBLE_COLUMN, TFCBlocks.FRESHWATER_BUBBLE_COLUMN, TFCBlocks.HOT_POURED_GLASS, TFCBlocks.GLASS_BASIN, TFCBlocks.JARS)
            .map(Supplier::get)
            .collect(Collectors.toSet());
        final Set<Class<?>> expectedNoLootTableClasses = ImmutableSet.of(BodyPlantBlock.class, GrowingFruitTreeBranchBlock.class, LiquidBlock.class, BranchingCactusBlock.class, GrowingBranchingCactusBlock.class, PouredGlassBlock.class);
        return validateBlockLootTables(server, TFCBlocks.BLOCKS.getEntries()
            .stream()
            .map(Holder::value)
            .filter(b -> !expectedNoLootTableBlocks.contains(b) && !expectedNoLootTableClasses.contains(b.getClass()))
            .toList(), LOGGER);
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

        final List<BlockState> missingModelErrors = TFCBlocks.BLOCKS.getEntries()
            .stream()
            .flatMap(states(s -> s.getRenderShape() == RenderShape.MODEL && shaper.getBlockModel(s) == missingModel))
            .toList();
        final List<BlockState> missingParticleErrors = TFCBlocks.BLOCKS.getEntries()
            .stream()
            .flatMap(states(s -> !s.isAir() && !(s.getBlock() instanceof IngotPileBlock) && !(s.getBlock() instanceof SheetPileBlock) && !(s.getBlock() instanceof ScrapingBlock) && shaper.getParticleIcon(s) == missingParticle))
            .toList();

        return logErrors("{} block states with missing models:", missingModelErrors, LOGGER)
            | logErrors("{} block states with missing particles:", missingParticleErrors, LOGGER);
    }

    /**
     * Detects any missing translation keys, for all items, in all creative tabs.
     */
    @SuppressWarnings("ConstantConditions")
    private static boolean validateTranslationsAndCreativeTabs()
    {
        final Set<String> missingTranslations = Bootstrap.getMissingTranslations();
        final List<ItemStack> stacks = new ArrayList<>();
        final Set<Item> items = new HashSet<>();

        boolean error = false;

        TFCCreativeTabs.generators().forEach(gen -> gen.accept(/* We don't use parameters, this is fine*/ null, (stack, visibility) -> {
            stacks.add(stack);
            items.add(stack.getItem());
        }));

        final Set<Item> technicalItemsWithNoTab = Stream.of(
            List.of(
                TFCBlocks.SNOW_PILE,
                TFCBlocks.ICE_PILE,
                TFCBlocks.BLOOM,
                TFCBlocks.MOLTEN,
                TFCBlocks.LIGHT,
                TFCBlocks.POURED_GLASS,
                TFCItems.FILLED_PAN
            ),
            TFCBlocks.COLORED_POURED_GLASS.values(),
            TFCBlocks.ROCK_ANVILS.values()
        )
            .<ItemLike>flatMap(Collection::stream)
            .map(ItemLike::asItem)
            .collect(Collectors.toSet());
        final List<Item> missingItems = TFCItems.ITEMS.getEntries()
            .stream()
            .map(Holder::value)
            .filter(item -> !items.contains(item) && !technicalItemsWithNoTab.contains(item))
            .toList();

        error |= logErrors("{} items were not found in any TFC creative tab", missingItems, LOGGER);

        for (ItemStack stack : stacks)
        {
            error |= validateTranslation(LOGGER, missingTranslations, stack.getHoverName());
        }

        final SoundManager soundManager = Minecraft.getInstance().getSoundManager();
        BuiltInRegistries.SOUND_EVENT.forEach(sound -> Optional.ofNullable(soundManager.getSoundEvent(sound.getLocation()))
            .map(WeighedSoundEvents::getSubtitle)
            .ifPresent(subtitle -> validateTranslation(LOGGER, missingTranslations, subtitle)));

        for (var holder : TFCCreativeTabs.CREATIVE_TABS.getEntries())
        {
            error |= validateTranslation(LOGGER, missingTranslations, holder.value().getDisplayName());
        }

        for (Class<? extends Enum<?>> clazz : List.of(
            ForgeStep.class,
            ForgingBonus.class,
            Heat.class,
            Nutrient.class,
            Size.class,
            Weight.class,
            Day.class,
            Month.class,
            KoppenClimateClassification.class,
            ForestType.class,
            RockDisplayCategory.class
        ))
        {
            for (Enum<?> enumConstant : clazz.getEnumConstants())
            {
                error |= validateTranslation(LOGGER, missingTranslations, Helpers.translateEnum(enumConstant));
            }
        }

        error |= TFCBlockEntities.BLOCK_ENTITIES.getEntries()
            .stream()
            .anyMatch(type -> {
                final Block block = type.value().getValidBlocks().stream().findFirst().orElseThrow();
                final BlockEntity entity = type.value().create(BlockPos.ZERO, block.defaultBlockState());
                if (entity instanceof InventoryBlockEntity<?> inv)
                {
                    return validateTranslation(LOGGER, missingTranslations, inv.getDisplayName());
                }
                return false;
            });

        return error | logErrors("{} missing translation keys:", missingTranslations, LOGGER);
    }

    private static boolean validateJugDrinkable()
    {
        final List<Fluid> errors = Helpers.allFluids(TFCTags.Fluids.USABLE_IN_JUG)
            .filter(fluid -> Drinkable.get(fluid) == null)
            .toList();

        return logWarnings("{} fluids were in the tfc:usable_in_jug tag but lack a Drinkable json entry", errors, LOGGER);
    }

    private static boolean validatePotFluidUsability(RecipeManager manager)
    {
        final Set<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.POT.get()).stream()
            .flatMap(recipe -> RecipeHelpers.stream(recipe.value().getFluidIngredient()))
            .filter(fluid -> !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_POT))
            .collect(Collectors.toSet());
        return logErrors("{} fluids are listed in pot recipes that are not tagged as tfc:usable_in_pot", errors, LOGGER);
    }

    private static boolean validateBarrelFluidUsability(RecipeManager manager)
    {
        final Set<Fluid> errors = manager.getRecipes().stream()
            .filter(recipe -> recipe.value() instanceof BarrelRecipe)
            .map(recipe -> (BarrelRecipe) recipe.value())
            .flatMap(recipe -> Stream.concat(RecipeHelpers.stream(recipe.getInputFluid()), Stream.of(recipe.getOutputFluid().getFluid())))
            .filter(fluid -> !fluid.isSame(Fluids.EMPTY) && !Helpers.isFluid(fluid, TFCTags.Fluids.USABLE_IN_BARREL))
            .collect(Collectors.toSet());
        return logErrors("{} fluids are listed in barrel recipes that are not tagged as tfc:usable_in_barrel", errors, LOGGER);
    }

    private static boolean validateUniqueBloomeryRecipes(RecipeManager manager)
    {
        final List<Fluid> errors = manager.getAllRecipesFor(TFCRecipeTypes.BLOOMERY.get())
            .stream()
            .flatMap(recipe -> RecipeHelpers.stream(recipe.value().getInputFluid()))
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .filter(m -> m.getValue() > 1)
            .map(Map.Entry::getKey)
            .toList();
        return logErrors("{} fluids appeared in multiple bloomery recipes. Currently, every bloomery recipe must have a unique fluid input in order to work", errors, LOGGER);
    }

    private static boolean validateUniqueLoomRecipes(RecipeManager manager)
    {
        final List<Item> errors = manager.getAllRecipesFor(TFCRecipeTypes.LOOM.get()).stream()
            .flatMap(recipe -> Arrays.stream(recipe.value().getItemStackIngredient().ingredient().getItems()))
            .map(ItemStack::getItem)
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet().stream().filter(m -> m.getValue() > 1)
            .map(Map.Entry::getKey).toList();
        return logErrors("{} items appeared in multiple loom recipes. Currently, every loom recipe must have a unique item input in order to work", errors, LOGGER);
    }

    private static boolean validateMoldsCanContainCastingIngredients(RecipeManager manager)
    {
        final List<String> errors = manager.getAllRecipesFor(TFCRecipeTypes.CASTING.get())
            .stream()
            .map(holder -> {
                for (ItemStack stack : holder.value().getIngredient().getItems())
                {
                    final IMold mold = IMold.get(stack);
                    if (mold == null) return "Item is not a mold: " + stack;
                    for (FluidStack fluid : holder.value().getFluidIngredient().getFluids())
                        if (!mold.isFluidValid(0, fluid))
                            return "Mold " + stack + " cannot contain " + fluid;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .toList();

        return logErrors("{} mold recipes were invalid", errors, LOGGER);
    }

    private static boolean validateHeatingRecipeIngredientsAreHeatable(RecipeManager manager)
    {
        final List<ItemStack> errors = manager.getAllRecipesFor(TFCRecipeTypes.HEATING.get())
            .stream()
            .flatMap(recipe -> Arrays.stream(recipe.value().getIngredient().getItems()))
            .filter(stack -> HeatCapability.getDefinition(stack) == null)
            .toList();
        return logErrors("{} items found as ingredients to heating recipes without a heat definition!", errors, LOGGER);
    }

    /**
     * Fired from the entry point where client self tests are invoked in TFC
     * This is provided for convenience for any addon mods which wish to use this same entrypoint,
     * but don't want to duplicate the provided mixin.
     */
    public static class ClientSelfTestEvent extends Event {}
}
