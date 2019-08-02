/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class LootTablesTFC
{
    public static final ResourceLocation ANIMALS_BEAR = register("animals/bear");
    public static final ResourceLocation ANIMALS_CHICKEN = register("animals/chicken");
    public static final ResourceLocation ANIMALS_COW = register("animals/cow");
    public static final ResourceLocation ANIMALS_DEER = register("animals/deer");
    public static final ResourceLocation ANIMALS_PHEASANT = register("animals/pheasant");
    public static final ResourceLocation ANIMALS_PIG = register("animals/pig");
    public static final ResourceLocation ANIMALS_SHEEP = register("animals/sheep");

    private static ResourceLocation register(String id)
    {
        return LootTableList.register(new ResourceLocation(MOD_ID, id));
    }
}
