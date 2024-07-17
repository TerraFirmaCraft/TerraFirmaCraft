package net.dries007.tfc.data.providers;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.TFCArmorMaterials;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.data.Accessors;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.data.DataManager;
import net.dries007.tfc.util.data.ItemDamageResistance;

public class BuiltinItemDamageResist extends DataManagerProvider<ItemDamageResistance> implements Accessors
{
    public BuiltinItemDamageResist(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemDamageResistance.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(Metal.COPPER, 10, 6.25f, 10);
        add(Metal.BISMUTH_BRONZE, 10, 8.25f, 15);
        add(Metal.BLACK_BRONZE, 15, 8.25f, 10);
        add(Metal.BRONZE, 12.5f, 8.25f, 12.5f);
        add(Metal.WROUGHT_IRON, 20, 13.2f, 20);
        add(Metal.STEEL, 30, 16.5f, 25);
        add(Metal.BLACK_STEEL, 45, 33, 50);
        add(Metal.BLUE_STEEL, 50, 50, 62.5f);
        add(Metal.RED_STEEL, 62.5f, 50, 50);

        add("leather", 0, 3, 0);
        add("chainmail", 8, 8, 2);
    }

    private void add(String name, float piercing, float slashing, float crushing)
    {
        add(name + "_armor", new ItemDamageResistance(Ingredient.of(
            itemOf(ResourceLocation.withDefaultNamespace(name + "_helmet")),
            itemOf(ResourceLocation.withDefaultNamespace(name + "_chestplate")),
            itemOf(ResourceLocation.withDefaultNamespace(name + "_leggings")),
            itemOf(ResourceLocation.withDefaultNamespace(name + "_boots"))
        ), new PhysicalDamage(piercing, slashing, crushing)));
    }

    private void add(Metal metal, float piercing, float slashing, float crushing)
    {
        final Map<Metal.ItemType, TFCItems.ItemId> items = TFCItems.METAL_ITEMS.get(metal);
        add(metal.getSerializedName() + "_armor", new ItemDamageResistance(Ingredient.of(
            items.get(Metal.ItemType.HELMET),
            items.get(Metal.ItemType.CHESTPLATE),
            items.get(Metal.ItemType.GREAVES),
            items.get(Metal.ItemType.BOOTS)
        ), new PhysicalDamage(piercing, slashing, crushing)));
    }
}
