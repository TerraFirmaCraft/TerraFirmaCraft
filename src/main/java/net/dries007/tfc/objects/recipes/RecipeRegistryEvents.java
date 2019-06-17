/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryModifiable;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.items.ceramics.ItemMold;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RecipeRegistryEvents
{
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        IForgeRegistry<IRecipe> r = event.getRegistry();

        // TOOLHEAD + STICK -> TOOL
        // todo: move to an IRecipeFactory and jsonify
        r.register(new MetalToolRecipe(Metal.ItemType.PICK_HEAD, Metal.ItemType.PICK).setRegistryName(MOD_ID, "metal_pick"));
        r.register(new MetalToolRecipe(Metal.ItemType.SHOVEL_HEAD, Metal.ItemType.SHOVEL).setRegistryName(MOD_ID, "metal_shovel"));
        r.register(new MetalToolRecipe(Metal.ItemType.AXE_HEAD, Metal.ItemType.AXE).setRegistryName(MOD_ID, "metal_axe"));
        r.register(new MetalToolRecipe(Metal.ItemType.HOE_HEAD, Metal.ItemType.HOE).setRegistryName(MOD_ID, "metal_hoe"));
        r.register(new MetalToolRecipe(Metal.ItemType.CHISEL_HEAD, Metal.ItemType.CHISEL).setRegistryName(MOD_ID, "metal_chisel"));
        r.register(new MetalToolRecipe(Metal.ItemType.SWORD_BLADE, Metal.ItemType.SWORD).setRegistryName(MOD_ID, "metal_sword"));
        r.register(new MetalToolRecipe(Metal.ItemType.MACE_HEAD, Metal.ItemType.MACE).setRegistryName(MOD_ID, "metal_mace"));
        r.register(new MetalToolRecipe(Metal.ItemType.SAW_BLADE, Metal.ItemType.SAW).setRegistryName(MOD_ID, "metal_saw"));
        r.register(new MetalToolRecipe(Metal.ItemType.JAVELIN_HEAD, Metal.ItemType.JAVELIN).setRegistryName(MOD_ID, "metal_javelin"));
        r.register(new MetalToolRecipe(Metal.ItemType.HAMMER_HEAD, Metal.ItemType.HAMMER).setRegistryName(MOD_ID, "metal_hammer"));
        r.register(new MetalToolRecipe(Metal.ItemType.PROPICK_HEAD, Metal.ItemType.PROPICK).setRegistryName(MOD_ID, "metal_propick"));
        r.register(new MetalToolRecipe(Metal.ItemType.KNIFE_BLADE, Metal.ItemType.KNIFE).setRegistryName(MOD_ID, "metal_knife"));
        r.register(new MetalToolRecipe(Metal.ItemType.SCYTHE_BLADE, Metal.ItemType.SCYTHE).setRegistryName(MOD_ID, "metal_scythe"));

        // MOLD -> ITEM + MOLD (left in grid)
        // todo: move to an IRecipeFactory and jsonify
        for (Metal.ItemType type : Metal.ItemType.values())
        {
            ItemMold mold = ItemMold.get(type);
            if (mold == null) continue; // Skip types that don't have mods.
            r.register(new UnmoldRecipe(mold).setRegistryName(MOD_ID, "unmold_" + type.name().toLowerCase()));
        }

        // todo: in 1.13 move to json overrides
        // This causes massive log spawm
        // See https://github.com/MinecraftForge/MinecraftForge/pull/4541#issuecomment-354033516
        if (ConfigTFC.GENERAL.removeVanillaRecipes)
        {
            IForgeRegistryModifiable modRegistry = (IForgeRegistryModifiable) event.getRegistry();

            //misc AKA too lazy to categorize somehow
            modRegistry.remove(new ResourceLocation("minecraft:sugar"));
            modRegistry.remove(new ResourceLocation("minecraft:speckled_melon"));
            modRegistry.remove(new ResourceLocation("minecraft:sea_lantern"));
            modRegistry.remove(new ResourceLocation("minecraft:rabbit_stew_from_red_mushroom"));
            modRegistry.remove(new ResourceLocation("minecraft:rabbit_stew_from_brown_mushroom"));
            modRegistry.remove(new ResourceLocation("minecraft:pumpkin_pie"));
            modRegistry.remove(new ResourceLocation("minecraft:prismarine_bricks"));
            modRegistry.remove(new ResourceLocation("minecraft:prismarine"));
            modRegistry.remove(new ResourceLocation("minecraft:paper"));
            modRegistry.remove(new ResourceLocation("minecraft:mushroom_stew"));
            modRegistry.remove(new ResourceLocation("minecraft:melon_seeds"));
            modRegistry.remove(new ResourceLocation("minecraft:melon_block"));
            modRegistry.remove(new ResourceLocation("minecraft:lit_pumpkin"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_apple"));
            modRegistry.remove(new ResourceLocation("minecraft:glowstone"));
            modRegistry.remove(new ResourceLocation("minecraft:furnace"));
            modRegistry.remove(new ResourceLocation("minecraft:flint_and_steel"));
            modRegistry.remove(new ResourceLocation("minecraft:fishing_rod"));
            modRegistry.remove(new ResourceLocation("minecraft:fire_charge"));
            modRegistry.remove(new ResourceLocation("minecraft:fermented_spider_eye"));
            modRegistry.remove(new ResourceLocation("minecraft:crafting_table"));
            modRegistry.remove(new ResourceLocation("minecraft:cookie"));
            modRegistry.remove(new ResourceLocation("minecraft:compass"));
            modRegistry.remove(new ResourceLocation("minecraft:comparator"));
            modRegistry.remove(new ResourceLocation("minecraft:coarse_dirt"));
            modRegistry.remove(new ResourceLocation("minecraft:chest"));
            modRegistry.remove(new ResourceLocation("minecraft:cauldron"));
            modRegistry.remove(new ResourceLocation("minecraft:carrot_on_a_stick"));
            modRegistry.remove(new ResourceLocation("minecraft:cake"));
            modRegistry.remove(new ResourceLocation("minecraft:bucket"));
            modRegistry.remove(new ResourceLocation("minecraft:brewing_stand"));
            modRegistry.remove(new ResourceLocation("minecraft:bread"));
            modRegistry.remove(new ResourceLocation("minecraft:bookshelf"));
            modRegistry.remove(new ResourceLocation("minecraft:boat"));
            modRegistry.remove(new ResourceLocation("minecraft:beetroot_soup"));
            modRegistry.remove(new ResourceLocation("minecraft:beacon"));
            modRegistry.remove(new ResourceLocation("minecraft:armor_stand"));
            modRegistry.remove(new ResourceLocation("minecraft:anvil"));

            //breakydowny, buildyupy things.
            modRegistry.remove(new ResourceLocation("minecraft:wheat"));
            modRegistry.remove(new ResourceLocation("minecraft:slime_ball"));
            modRegistry.remove(new ResourceLocation("minecraft:slime"));
            modRegistry.remove(new ResourceLocation("minecraft:lapis_lazuli"));
            modRegistry.remove(new ResourceLocation("minecraft:lapis_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_ingot_from_nuggets"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_ingot_from_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_nugget"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_block"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_bars"));
            modRegistry.remove(new ResourceLocation("minecraft:hay_block"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_nugget"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_ingot_from_nuggets"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_ingot_from_block"));
            modRegistry.remove(new ResourceLocation("minecraft:gold_block"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_prismarine"));
            modRegistry.remove(new ResourceLocation("minecraft:emerald_block"));
            modRegistry.remove(new ResourceLocation("minecraft:emerald"));
            modRegistry.remove(new ResourceLocation("minecraft:coal_block"));
            modRegistry.remove(new ResourceLocation("minecraft:coal"));
            modRegistry.remove(new ResourceLocation("minecraft:bone_block"));
            modRegistry.remove(new ResourceLocation("minecraft:bone_meal_from_bone"));
            modRegistry.remove(new ResourceLocation("minecraft:bone_meal_from_block"));

            //nether
            modRegistry.remove(new ResourceLocation("minecraft:red_nether_brick"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:quartz_block"));
            modRegistry.remove(new ResourceLocation("minecraft:pillar_quartz_block"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_wart_block"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:nether_brick"));
            modRegistry.remove(new ResourceLocation("minecraft:magma_cream"));
            modRegistry.remove(new ResourceLocation("minecraft:magma"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_quartz_block"));

            //end
            modRegistry.remove(new ResourceLocation("minecraft:purpur_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_pillar"));
            modRegistry.remove(new ResourceLocation("minecraft:purpur_block"));
            modRegistry.remove(new ResourceLocation("minecraft:ender_eye"));
            modRegistry.remove(new ResourceLocation("minecraft:end_rod"));
            modRegistry.remove(new ResourceLocation("minecraft:end_crystal"));
            modRegistry.remove(new ResourceLocation("minecraft:end_bricks"));

            //Armor Dye
            modRegistry.remove(new ResourceLocation("minecraft:armordye"));

            //Fire Works
            modRegistry.remove(new ResourceLocation("minecraft:fireworks"));

            //leather
            modRegistry.remove(new ResourceLocation("minecraft:leather_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:leather_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:leather"));

            //General wood
            modRegistry.remove(new ResourceLocation("minecraft:wooden_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_button"));
            modRegistry.remove(new ResourceLocation("minecraft:wooden_door"));
            modRegistry.remove(new ResourceLocation("minecraft:trapdoor"));
            //birch
            modRegistry.remove(new ResourceLocation("minecraft:birch_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_door"));
            modRegistry.remove(new ResourceLocation("minecraft:birch_boat"));
            //acacia
            modRegistry.remove(new ResourceLocation("minecraft:acacia_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_door"));
            modRegistry.remove(new ResourceLocation("minecraft:acacia_boat"));
            //dark oak
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_boat"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_door"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:dark_oak_wooden_slab"));
            //jungle
            modRegistry.remove(new ResourceLocation("minecraft:jungle_boat"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_door"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:jungle_wooden_slab"));
            //oak
            modRegistry.remove(new ResourceLocation("minecraft:oak_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:oak_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:oak_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:fence"));
            //spruce
            modRegistry.remove(new ResourceLocation("minecraft:spruce_wooden_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_planks"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_fence_gate"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_fence"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_door"));
            modRegistry.remove(new ResourceLocation("minecraft:spruce_boat"));

            //redstone
            modRegistry.remove(new ResourceLocation("minecraft:tripwire_hook"));
            modRegistry.remove(new ResourceLocation("minecraft:trapped_chest"));
            modRegistry.remove(new ResourceLocation("minecraft:sticky_piston"));
            modRegistry.remove(new ResourceLocation("minecraft:redstone_lamp"));
            modRegistry.remove(new ResourceLocation("minecraft:piston"));
            modRegistry.remove(new ResourceLocation("minecraft:observer"));
            modRegistry.remove(new ResourceLocation("minecraft:daylight_detector"));

            //rail
            modRegistry.remove(new ResourceLocation("minecraft:rail"));
            modRegistry.remove(new ResourceLocation("minecraft:minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:hopper_minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:furnace_minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:detector_rail"));
            modRegistry.remove(new ResourceLocation("minecraft:chest_minecart"));
            modRegistry.remove(new ResourceLocation("minecraft:activator_rail"));

            //Stone
            modRegistry.remove(new ResourceLocation("minecraft:stone_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_brick_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:stone_brick_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_granite"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_diorite"));
            modRegistry.remove(new ResourceLocation("minecraft:polished_andesite"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_cobblestone_wall"));
            modRegistry.remove(new ResourceLocation("minecraft:mossy_cobblestone"));
            modRegistry.remove(new ResourceLocation("minecraft:granite"));
            modRegistry.remove(new ResourceLocation("minecraft:diorite"));
            modRegistry.remove(new ResourceLocation("minecraft:cobblestone_wall"));
            modRegistry.remove(new ResourceLocation("minecraft:cobblestone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_stonebrick"));
            modRegistry.remove(new ResourceLocation("minecraft:andesite"));

            //sandstone
            modRegistry.remove(new ResourceLocation("minecraft:smooth_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:smooth_red_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone_stairs"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone_slab"));
            modRegistry.remove(new ResourceLocation("minecraft:red_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_sandstone"));
            modRegistry.remove(new ResourceLocation("minecraft:chiseled_red_sandstone"));

            //iron
            modRegistry.remove(new ResourceLocation("minecraft:iron_trapdoor"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_pickaxe"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:iron_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:hopper"));
            modRegistry.remove(new ResourceLocation("minecraft:heavy_weighted_pressure_plate"));

            //gold
            modRegistry.remove(new ResourceLocation("minecraft:golden_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:golden_pickaxe"));

            //chainmail
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:chainmail_boots"));

            //diamond
            modRegistry.remove(new ResourceLocation("minecraft:diamond_helmet"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_chestplate"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_leggings"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_boots"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_hoe"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_axe"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_sword"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_shovel"));
            modRegistry.remove(new ResourceLocation("minecraft:diamond_pickaxe"));

            //arrows
            modRegistry.remove(new ResourceLocation("minecraft:tippedarrow"));
            modRegistry.remove(new ResourceLocation("minecraft:spectral_arrow"));

            //white
            modRegistry.remove(new ResourceLocation("minecraft:white_concrete_powder"));

            //Yellow
            modRegistry.remove(new ResourceLocation("minecraft:yellow_dye_from_sunflower"));
            modRegistry.remove(new ResourceLocation("minecraft:yellow_dye_from_dandelion"));
            modRegistry.remove(new ResourceLocation("minecraft:yellow_concrete_powder"));

            //red
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_rose_bush"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_poppy"));
            modRegistry.remove(new ResourceLocation("minecraft:red_dye_from_beetroot"));
            modRegistry.remove(new ResourceLocation("minecraft:red_concrete_powder"));

            //purple
            modRegistry.remove(new ResourceLocation("minecraft:purple_concrete_powder"));

            //pink
            modRegistry.remove(new ResourceLocation("minecraft:pink_dye_from_red_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_dye_from_pink_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_dye_from_peony"));
            modRegistry.remove(new ResourceLocation("minecraft:pink_concrete_powder"));

            //orange
            modRegistry.remove(new ResourceLocation("minecraft:orange_dye_from_orange_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:orange_concrete_powder"));

            //magenta
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_lilac"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_lapis_red_pink"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_lapis_ink_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_dye_from_allium"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_concrete_powder"));

            //lime
            modRegistry.remove(new ResourceLocation("minecraft:lime_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:magenta_concrete_powder"));

            //gray
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_white_tulip"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_oxeye_daisy"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_ink_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_gray_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_dye_from_azure_bluet"));
            modRegistry.remove(new ResourceLocation("minecraft:light_gray_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:gray_dye"));
            modRegistry.remove(new ResourceLocation("minecraft:gray_concrete_powder"));

            //blue
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_dye_from_lapis_bonemeal"));
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_dye_from_blue_orchid"));
            modRegistry.remove(new ResourceLocation("minecraft:light_blue_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_wool"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_stained_hardened_clay"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_stained_glass"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:blue_bed_from_white_bed"));

            //green
            modRegistry.remove(new ResourceLocation("minecraft:green_concrete_powder"));

            //cyan
            modRegistry.remove(new ResourceLocation("minecraft:cyan_dye"));
            modRegistry.remove(new ResourceLocation("minecraft:cyan_concrete_powder"));

            //brown
            modRegistry.remove(new ResourceLocation("minecraft:brown_concrete_powder"));

            //black
            modRegistry.remove(new ResourceLocation("minecraft:black_wool"));
            modRegistry.remove(new ResourceLocation("minecraft:black_stained_hardened_clay"));
            modRegistry.remove(new ResourceLocation("minecraft:black_stained_glass"));
            modRegistry.remove(new ResourceLocation("minecraft:black_concrete_powder"));
            modRegistry.remove(new ResourceLocation("minecraft:black_bed_from_white_bed"));
        }

    }
}
