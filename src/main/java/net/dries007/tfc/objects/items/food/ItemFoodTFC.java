package net.dries007.tfc.objects.items.food;

import java.util.EnumMap;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;

import net.dries007.tfc.objects.Food;

public class ItemFoodTFC extends ItemFood
{
    public final Food food;
    private static final EnumMap<Food, ItemFoodTFC> MAP = new EnumMap<>(Food.class);
    public static ItemFoodTFC get(Food food) { return MAP.get(food); }

    public static ItemStack get(Food food, int amount) { return new ItemStack(MAP.get(food), amount); }


    // TODO: 4/6/18 This is temporarily functioning like vanilla food until food system implemented.
    /** Number of ticks to run while 'EnumAction'ing until result. */
    public final int itemUseDuration;
    /** The amount this food item heals the player. */
    private final int healAmount;
    private final float saturationModifier;
    /** If this field is true, the food can be consumed even if the player don't need to eat. */
    private boolean alwaysEdible;
    /** represents the potion effect that will occurr upon eating this food. Set by setPotionEffect */
    private PotionEffect potionId;
    /** probably of the set potion effect occurring */
    private float potionEffectProbability;


    public ItemFoodTFC(Food food, int amount, float saturation)
    {
        super(amount,saturation, false);
        this.food = food;
        this.itemUseDuration = 32;
        this.healAmount = amount;
        this.saturationModifier = saturation;
    }
}