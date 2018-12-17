/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.recipes.AnvilRecipe;
import net.dries007.tfc.api.recipes.KnappingRecipe;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemUnfiredMold;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;
import net.dries007.tfc.util.forge.ForgeRule;

import static net.dries007.tfc.api.types.Metal.ItemType.*;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.util.forge.ForgeRule.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
public final class DefaultRecipes
{
    @SubscribeEvent
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        /* STONE TOOL HEADS */

        for (Rock.ToolType type : Rock.ToolType.values())
        {
            KnappingRecipe r = new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, type)), type.getPattern());
            event.getRegistry().register(r.setRegistryName(MOD_ID, type.name().toLowerCase() + "_head"));
        }
        // these recipes cover all cases where multiple stone items can be made
        // recipes are already mirror checked
        event.getRegistry().registerAll(
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_2"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_3"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "XX   ").setRegistryName(MOD_ID, "hoe_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "   XX").setRegistryName(MOD_ID, "hoe_head_2")
        );

        /* CLAY ITEMS */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                event.getRegistry().register(new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemUnfiredMold.get(type)), type.getPattern()).setRegistryName(MOD_ID, type.name().toLowerCase() + "_mold"));
            }
        }

        // todo: uncomment these as more items / blocks are added
        event.getRegistry().registerAll(
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_VESSEL), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_small_vessel"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_JUG), " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ").setRegistryName(MOD_ID, "clay_jug"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_POT), "X   X", "X   X", "X   X", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_pot"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 2), "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 4), "X   X", " XXX ", "     ", "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl_2")
            //new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(BlocksTFC.CERAMICS_LARGE_VESSEL), "XXXX", "X  X", "X  X", "X  X", "XXXX").setRegistryName(MOD_ID, "clay_large_vessel"),
        );

        /* LEATHER ITEMS */

        // todo: leather recipes

        /* FIRE CLAY ITEMS */

        // todo: fire clay recipes
    }

    @SubscribeEvent
    public static void onRegisterAnvilRecipeEvent(RegistryEvent.Register<AnvilRecipe> event)
    {
        IForgeRegistry<AnvilRecipe> r = event.getRegistry();

        // Basic Components
        addAnvil(r, INGOT, SHEET, false, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST);
        addAnvil(r, DOUBLE_INGOT, DOUBLE_SHEET, false, HIT_LAST, HIT_SECOND_LAST, HIT_THIRD_LAST);

        // Tools
        addAnvil(r, INGOT, PICK_HEAD, true, PUNCH_LAST, BEND_NOT_LAST, DRAW_NOT_LAST);
        addAnvil(r, INGOT, SHOVEL_HEAD, true, PUNCH_LAST, HIT_NOT_LAST);
        addAnvil(r, INGOT, AXE_HEAD, true, PUNCH_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, INGOT, HOE_HEAD, true, PUNCH_LAST, HIT_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, HAMMER_HEAD, true, PUNCH_LAST, SHRINK_NOT_LAST);
        addAnvil(r, INGOT, PROPICK_HEAD, true, PUNCH_LAST, DRAW_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SAW_BLADE, true, HIT_LAST, HIT_SECOND_LAST);
        addAnvil(r, INGOT, SWORD_BLADE, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_INGOT, MACE_HEAD, true, HIT_LAST, SHRINK_NOT_LAST, BEND_NOT_LAST);
        addAnvil(r, INGOT, SCYTHE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, INGOT, KNIFE_BLADE, true, HIT_LAST, DRAW_SECOND_LAST, DRAW_THIRD_LAST);
        addAnvil(r, INGOT, JAVELIN_HEAD, true, HIT_LAST, HIT_SECOND_LAST, DRAW_THIRD_LAST);

        // Armor
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_HELMET, true, HIT_LAST, BEND_SECOND_LAST, BEND_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_CHESTPLATE, true, HIT_LAST, HIT_SECOND_LAST, UPSET_THIRD_LAST);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_GREAVES, true, BEND_ANY, DRAW_ANY, HIT_ANY);
        addAnvil(r, DOUBLE_SHEET, UNFINISHED_BOOTS, true, BEND_LAST, BEND_SECOND_LAST, SHRINK_THIRD_LAST);

        // todo: more anvil recipes
    }

    private static void addAnvil(IForgeRegistry<AnvilRecipe> registry, Metal.ItemType inputType, Metal.ItemType outputType, boolean onlyToolMetals, ForgeRule... rules)
    {
        // Helper method for adding all recipes that take ItemType -> ItemType
        for (Metal metal : TFCRegistries.METALS.getValuesCollection())
        {
            if (onlyToolMetals && !metal.isToolMetal())
                continue;

            // Create a recipe for each metal / item type combination
            ItemStack input = new ItemStack(ItemMetal.get(metal, inputType));
            ItemStack output = new ItemStack(ItemMetal.get(metal, outputType));
            if (!input.isEmpty() && !output.isEmpty())
            {
                registry.register(new AnvilRecipe(new ResourceLocation(MOD_ID, (outputType.name() + "_" + metal.getRegistryName().getPath()).toLowerCase()), input, output, metal.getTier(), rules));
            }
        }
    }

    @SubscribeEvent
    public static void onRegisterWeldingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        // todo: anvil welding recipes
    }
}
