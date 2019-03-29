package net.dries007.tfc.objects.items.food;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.Food;

public class ItemFoodTFC extends ItemFood
{
    public final Food food;
    private static final Map<Food, ItemFoodTFC> MAP = new HashMap<>();

    public static ItemFoodTFC get(Food food) { return MAP.get(food); }

    public static ItemStack get(Food food, int amount) { return new ItemStack(MAP.get(food), amount); }


    // TODO: 4/6/18 This is temporarily functioning like vanilla food until food system implemented.
    /** Number of ticks to run while 'EnumAction'ing until result. */
    public final int itemUseDuration;
    /** The amount this food item heals the player is healAmount in Vanilla, but is handled by caloriesAmount*/
    /** If this field is true, the food can be consumed even if the player don't need to eat. */
    private boolean alwaysEdible;
    /** represents the potion effect that will occurr upon eating this food. Set by setPotionEffect */
    //private PotionEffect potionId;
    /** probably of the set potion effect occurring */
    //private float potionEffectProbability;

    private final float mineralAmount;
    private final float carbAmount;
    private final float fatAmount;
    private final float proteinAmount;
    private final float vitaminAmount;
    private final float waterAmount;
    private final int caloriesAmount;
    private final float saturationModifier;
    private final boolean isEdible;
    private final float decayRate;


    public ItemFoodTFC(Food food, int calories, float saturation, boolean alwaysEdible)
    {
        super(calories,saturation, false);
        this.food = food;
        this.mineralAmount = food.getMineral();
        this.carbAmount = food.getCarb();
        this.fatAmount = food.getFat();
        this.proteinAmount = food.getProtein();
        this.vitaminAmount = food.getVitamin();
        this.waterAmount = food.getWaterContent();
        this.caloriesAmount = food.getCalories();
        this.saturationModifier = food.getSaturation();
        this.isEdible = food.isEdible();
        this.decayRate = food.getDecayRate();
        this.itemUseDuration = 32;
        this.alwaysEdible = false;
        if (MAP.put(food, this) != null) throw new IllegalStateException("There can only be one.");
    }
}