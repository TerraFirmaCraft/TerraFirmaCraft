/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.recipes;

import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import net.dries007.tfc.api.types.Metal;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class RecipeRegistryEvents
{
    @SubscribeEvent
    public static void onRecipeRegister(RegistryEvent.Register<IRecipe> event)
    {
        IForgeRegistry<IRecipe> r = event.getRegistry();

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
    }
}
