/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util.agriculture;

import java.util.Random;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;

import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSimple;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropSpreading;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.food.ItemFoodTFC;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.skills.Skill;
import net.dries007.tfc.util.skills.SkillTier;
import net.dries007.tfc.world.classic.worldgen.WorldGenWildCrops;

import static net.dries007.tfc.util.agriculture.Crop.CropType.*;

public enum Crop implements ICrop
{
    // these definitions are defined in the spreadsheet at
    // https://docs.google.com/spreadsheets/d/1Ghw3dCmVO5Gv0MMGBydUxox_nwLYmmcZkGSbbf0QSAE/edit#gid=893781093
    // It should be modified first, and then the resulting definitions copied to this space here
    BARLEY(Food.BARLEY, 0f, 1f, 26f, 33f, 50f, 70f, 310f, 330f, 8, 0.4f, SIMPLE),
    MAIZE(Food.MAIZE, 10f, 19f, 40f, 45f, 110f, 140f, 400f, 450f, 6, 0.6f, SIMPLE),
    OAT(Food.OAT, 0f, 3f, 30f, 34f, 50f, 100f, 350f, 400f, 8, 0.5f, SIMPLE),
    RICE(Food.RICE, 20f, 22f, 40f, 45f, 250f, 300f, 450f, 500f, 8, 0.6f, SIMPLE),
    RYE(Food.RYE, 0f, 4f, 35f, 40f, 50f, 100f, 400f, 450f, 8, 0.5f, SIMPLE),
    WHEAT(Food.WHEAT, 0f, 3f, 30f, 34f, 50f, 100f, 350f, 400f, 8, 0.5f, SIMPLE),
    BEET(Food.BEET, -5f, 0f, 20f, 25f, 50f, 70f, 300f, 320f, 7, 0.6f, SIMPLE),
    CABBAGE(Food.CABBAGE, -10f, 0f, 27f, 33f, 50f, 60f, 280f, 300f, 6, 0.6f, SIMPLE),
    CARROT(Food.CARROT, 3f, 10f, 30f, 36f, 50f, 100f, 400f, 450f, 5, 0.6f, SIMPLE),
    GARLIC(Food.GARLIC, -20f, -1f, 18f, 26f, 50f, 60f, 310f, 340f, 5, 0.65f, SIMPLE),
    GREEN_BEAN(Food.GREEN_BEAN, 2f, 9f, 35f, 41f, 70f, 150f, 410f, 450f, 7, 0.45f, PICKABLE),
    ONION(Food.ONION, -1f, 10f, 37f, 40f, 70f, 200f, 410f, 450f, 7, 0.4f, SIMPLE),
    POTATO(Food.POTATO, 0f, 4f, 30f, 35f, 50f, 100f, 390f, 440f, 7, 0.55f, SIMPLE),
    SOYBEAN(Food.SOYBEAN, 8f, 12f, 30f, 36f, 55f, 160f, 410f, 450f, 7, 0.5f, SIMPLE),
    SQUASH(Food.SQUASH, 5f, 14f, 33f, 37f, 45f, 90f, 390f, 440f, 8, 0.5f, SIMPLE),
    SUGARCANE(Food.SUGARCANE, 12f, 20f, 38f, 45f, 50f, 160f, 410f, 450f, 8, 0.5f, SIMPLE),
    TOMATO(Food.TOMATO, 0f, 8f, 36f, 40f, 50f, 120f, 390f, 430f, 8, 0.45f, PICKABLE),
    RED_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.RED_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 4f, 12f, 32f, 38f, 50f, 100f, 400f, 450f, 7, 0.55f, PICKABLE),
    YELLOW_BELL_PEPPER(() -> new ItemStack(ItemFoodTFC.get(Food.YELLOW_BELL_PEPPER)), () -> new ItemStack(ItemFoodTFC.get(Food.GREEN_BELL_PEPPER)), 4f, 12f, 32f, 38f, 50f, 100f, 400f, 450f, 7, 0.55f, PICKABLE),
    JUTE(() -> new ItemStack(ItemsTFC.JUTE), () -> ItemStack.EMPTY, 5f, 11f, 37f, 42f, 50f, 100f, 410f, 450f, 6, 0.5f, SIMPLE);

    static
    {
        for (ICrop crop : values())
        {
            WorldGenWildCrops.register(crop);
        }
    }

    /**
     * the count to add to the amount of food dropped when applying the skill bonus
     *
     * @param skill  agriculture skill of the harvester
     * @param random random instance to use, generally Block.RANDOM
     * @return amount to add to item stack count
     */
    public static int getSkillFoodBonus(Skill skill, Random random)
    {
        return random.nextInt(2 + (int) (6 * skill.getTotalLevel()));
    }

    /**
     * the count to add to the amount of seeds dropped when applying the skill bonus
     *
     * @param skill  agriculture skill of the harvester
     * @param random random instance to use, generally Block.RANDOM
     * @return amount to add to item stack count
     */
    public static int getSkillSeedBonus(Skill skill, Random random)
    {
        if (skill.getTier().isAtLeast(SkillTier.ADEPT) && random.nextInt(10 - 2 * skill.getTier().ordinal()) == 0)
            return 1;
        else
            return 0;
    }

    // how this crop generates food items
    private final Supplier<ItemStack> foodDrop;
    private final Supplier<ItemStack> foodDropEarly;
    // temperature compatibility range
    private final float tempMinAlive, tempMinGrow, tempMaxGrow, tempMaxAlive;
    // rainfall compatibility range
    private final float rainMinAlive, rainMinGrow, rainMaxGrow, rainMaxAlive;
    // growth
    private final int growthStages; // the number of blockstates the crop has for growing, ignoring wild state
    private final float growthTime; // Time is measured in % of months, scales with calendar month length
    // which crop block behavior implementation is used
    private final CropType type;

    Crop(Food foodDrop, float tempMinAlive, float tempMinGrow, float tempMaxGrow, float tempMaxAlive, float rainMinAlive, float rainMinGrow, float rainMaxGrow, float rainMaxAlive, int growthStages, float growthTime, CropType type)
    {
        this(() -> new ItemStack(ItemFoodTFC.get(foodDrop)), () -> ItemStack.EMPTY, tempMinAlive, tempMinGrow, tempMaxGrow, tempMaxAlive, rainMinAlive, rainMinGrow, rainMaxGrow, rainMaxAlive, growthStages, growthTime, type);
    }

    Crop(Supplier<ItemStack> foodDrop, Supplier<ItemStack> foodDropEarly, float tempMinAlive, float tempMinGrow, float tempMaxGrow, float tempMaxAlive, float rainMinAlive, float rainMinGrow, float rainMaxGrow, float rainMaxAlive, int growthStages, float growthTime, CropType type)
    {
        this.foodDrop = foodDrop;
        this.foodDropEarly = foodDropEarly;

        this.tempMinAlive = tempMinAlive;
        this.tempMinGrow = tempMinGrow;
        this.tempMaxGrow = tempMaxGrow;
        this.tempMaxAlive = tempMaxAlive;

        this.rainMinAlive = rainMinAlive;
        this.rainMinGrow = rainMinGrow;
        this.rainMaxGrow = rainMaxGrow;
        this.rainMaxAlive = rainMaxAlive;

        this.growthStages = growthStages;
        this.growthTime = growthTime; // This is measured in % of months

        this.type = type;
    }

    @Override
    public long getGrowthTicks()
    {
        return (long) (growthTime * CalendarTFC.CALENDAR_TIME.getDaysInMonth() * ICalendar.TICKS_IN_DAY);
    }

    @Override
    public int getMaxStage()
    {
        return growthStages - 1;
    }

    @Override
    public boolean isValidConditions(float temperature, float rainfall)
    {
        return tempMinAlive < temperature && temperature < tempMaxAlive && rainMinAlive < rainfall && rainfall < rainMaxAlive;
    }

    @Override
    public boolean isValidForGrowth(float temperature, float rainfall)
    {
        return tempMinGrow < temperature && temperature < tempMaxGrow && rainMinGrow < rainfall && rainfall < rainMaxGrow;
    }

    @Nonnull
    @Override
    public ItemStack getFoodDrop(int currentStage)
    {
        if (currentStage == getMaxStage())
        {
            return foodDrop.get();
        }
        else if (currentStage == getMaxStage() - 1)
        {
            return foodDropEarly.get();
        }
        return ItemStack.EMPTY;
    }

    public BlockCropTFC createGrowingBlock()
    {
        if (type == SIMPLE || type == PICKABLE)
        {
            return BlockCropSimple.create(this, type == PICKABLE);
        }
        else if (type == SPREADING)
        {
            return BlockCropSpreading.create(this);
        }
        throw new IllegalStateException("Invalid growthstage property " + growthStages + " for crop");
    }

    public BlockCropDead createDeadBlock()
    {
        return new BlockCropDead(this);
    }

    enum CropType
    {
        SIMPLE, PICKABLE, SPREADING
    }
}
