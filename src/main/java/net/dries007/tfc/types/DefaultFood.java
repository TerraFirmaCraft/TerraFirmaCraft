/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.registries.TFCRegistryEvent;
import net.dries007.tfc.api.types.Food;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)

public class DefaultFood
{

    /**
     * Default food ResourceLocations
     */
    //Fruit
    public static final ResourceLocation BANANA = new ResourceLocation(MOD_ID, "banana");
    public static final ResourceLocation BLACKBERRY = new ResourceLocation(MOD_ID, "blackberry");
    public static final ResourceLocation BLUEBERRY = new ResourceLocation(MOD_ID, "blueberry");
    public static final ResourceLocation BUNCHBERRY = new ResourceLocation(MOD_ID, "bunchberry");
    public static final ResourceLocation CHERRY = new ResourceLocation(MOD_ID, "cherry");
    public static final ResourceLocation CLOUDBERRY = new ResourceLocation(MOD_ID, "cloudberry");
    public static final ResourceLocation CRANBERRY = new ResourceLocation(MOD_ID, "cranberry");
    public static final ResourceLocation ELDERBERRY = new ResourceLocation(MOD_ID, "elderberry");
    public static final ResourceLocation GOOSEBERRY = new ResourceLocation(MOD_ID, "gooseberry");
    public static final ResourceLocation GREENAPPLE = new ResourceLocation(MOD_ID, "greenapple");
    public static final ResourceLocation LEMON = new ResourceLocation(MOD_ID, "lemon");
    public static final ResourceLocation OLIVE = new ResourceLocation(MOD_ID, "olive");
    public static final ResourceLocation ORANGE = new ResourceLocation(MOD_ID, "orange");
    public static final ResourceLocation PEACH = new ResourceLocation(MOD_ID, "peach");
    public static final ResourceLocation PLUM = new ResourceLocation(MOD_ID, "plum");
    public static final ResourceLocation RASPBERRY = new ResourceLocation(MOD_ID, "raspberry");
    public static final ResourceLocation REDAPPLE = new ResourceLocation(MOD_ID, "redapple");
    public static final ResourceLocation SNOWBERRY = new ResourceLocation(MOD_ID, "snowberry");
    public static final ResourceLocation STRAWBERRY = new ResourceLocation(MOD_ID, "strawberry");
    public static final ResourceLocation WINTERGREENBERRY = new ResourceLocation(MOD_ID, "wintergreenberry");

    //Grain
    public static final ResourceLocation BARLEY = new ResourceLocation(MOD_ID, "barley");
    public static final ResourceLocation BARLEYGRAIN = new ResourceLocation(MOD_ID, "barleygrain");
    public static final ResourceLocation BARLEYFLOUR = new ResourceLocation(MOD_ID, "barleyflour");
    public static final ResourceLocation BARLEYDOUGH = new ResourceLocation(MOD_ID, "barleydough");
    public static final ResourceLocation BARLEYBREAD = new ResourceLocation(MOD_ID, "barleybread");
    public static final ResourceLocation MAIZE = new ResourceLocation(MOD_ID, "maize");
    public static final ResourceLocation CORNMEALFLOUR = new ResourceLocation(MOD_ID, "cornmealflour");
    public static final ResourceLocation CORNMEALDOUGH = new ResourceLocation(MOD_ID, "cornmealdough");
    public static final ResourceLocation CORNBREAD = new ResourceLocation(MOD_ID, "cornbread");
    public static final ResourceLocation OAT = new ResourceLocation(MOD_ID, "oat");
    public static final ResourceLocation OATGRAIN = new ResourceLocation(MOD_ID, "oatgrain");
    public static final ResourceLocation OATFLOUR = new ResourceLocation(MOD_ID, "oatflour");
    public static final ResourceLocation OATDOUGH = new ResourceLocation(MOD_ID, "oatdough");
    public static final ResourceLocation OATBREAD = new ResourceLocation(MOD_ID, "oatbread");
    public static final ResourceLocation RICE = new ResourceLocation(MOD_ID, "rice");
    public static final ResourceLocation RICEGRAIN = new ResourceLocation(MOD_ID, "ricegrain");
    public static final ResourceLocation RICEFLOUR = new ResourceLocation(MOD_ID, "riceflour");
    public static final ResourceLocation RICEDOUGH = new ResourceLocation(MOD_ID, "ricedough");
    public static final ResourceLocation RICEBREAD = new ResourceLocation(MOD_ID, "ricebread");
    public static final ResourceLocation RYE = new ResourceLocation(MOD_ID, "rye");
    public static final ResourceLocation RYEGRAIN = new ResourceLocation(MOD_ID, "ryegrain");
    public static final ResourceLocation RYEFLOUR = new ResourceLocation(MOD_ID, "ryeflour");
    public static final ResourceLocation RYEDOUGH = new ResourceLocation(MOD_ID, "ryedough");
    public static final ResourceLocation RYEBREAD = new ResourceLocation(MOD_ID, "ryebread");
    public static final ResourceLocation WHEAT = new ResourceLocation(MOD_ID, "wheat");
    public static final ResourceLocation WHEATGRAIN = new ResourceLocation(MOD_ID, "wheatgrain");
    public static final ResourceLocation WHEATFLOUR = new ResourceLocation(MOD_ID, "wheatflour");
    public static final ResourceLocation WHEATDOUGH = new ResourceLocation(MOD_ID, "wheatdough");
    public static final ResourceLocation WHEATBREAD = new ResourceLocation(MOD_ID, "wheatbread");

    //Vegetable
    public static final ResourceLocation BEET = new ResourceLocation(MOD_ID, "beet");
    public static final ResourceLocation CABBAGE = new ResourceLocation(MOD_ID, "cabbage");
    public static final ResourceLocation CARROT = new ResourceLocation(MOD_ID, "carrot");
    public static final ResourceLocation GARLIC = new ResourceLocation(MOD_ID, "garlic");
    public static final ResourceLocation GREENBEAN = new ResourceLocation(MOD_ID, "greenbean");
    public static final ResourceLocation GREENBELLPEPPER = new ResourceLocation(MOD_ID, "greenbellpepper");
    public static final ResourceLocation ONION = new ResourceLocation(MOD_ID, "onion");
    public static final ResourceLocation POTATO = new ResourceLocation(MOD_ID, "potato");
    public static final ResourceLocation REDBELLPEPPER = new ResourceLocation(MOD_ID, "redbellpepper");
    public static final ResourceLocation SEAWEED = new ResourceLocation(MOD_ID, "seaweed");
    public static final ResourceLocation SOYBEAN = new ResourceLocation(MOD_ID, "soybean");
    public static final ResourceLocation SQUASH = new ResourceLocation(MOD_ID, "squash");
    public static final ResourceLocation TOMATO = new ResourceLocation(MOD_ID, "tomato");
    public static final ResourceLocation YELLOWBELLPEPPER = new ResourceLocation(MOD_ID, "yellowbellpepper");

    //Meat
    public static final ResourceLocation BEEF = new ResourceLocation(MOD_ID, "beef");
    public static final ResourceLocation CALAMARI = new ResourceLocation(MOD_ID, "calamari");
    public static final ResourceLocation FISH = new ResourceLocation(MOD_ID, "fish");
    public static final ResourceLocation HORSEMEAT = new ResourceLocation(MOD_ID, "horsemeat");
    public static final ResourceLocation MUTTON = new ResourceLocation(MOD_ID, "mutton");
    public static final ResourceLocation PORKCHOP = new ResourceLocation(MOD_ID, "porkchop");
    public static final ResourceLocation POULTRY = new ResourceLocation(MOD_ID, "poultry");
    public static final ResourceLocation VENISON = new ResourceLocation(MOD_ID, "venison");

    //Animal Product
    public static final ResourceLocation EGG = new ResourceLocation(MOD_ID, "egg");
    public static final ResourceLocation MILK = new ResourceLocation(MOD_ID, "milk");
    public static final ResourceLocation CHEESE = new ResourceLocation(MOD_ID, "cheese");

    //Other

    public static final ResourceLocation JUTE = new ResourceLocation(MOD_ID, "jute");
    public static final ResourceLocation SUGARCANE = new ResourceLocation(MOD_ID, "sugarcane");

    @SubscribeEvent
    public static void onPreRegisterFoodCategory(TFCRegistryEvent.RegisterPreBlock<Food> event) //todo Set all the nutrient and decay values
    {
        event.getRegistry().registerAll(
            new Food.Builder(BANANA,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BLACKBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BLUEBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BUNCHBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CHERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CLOUDBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CRANBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(ELDERBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(GOOSEBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(GREENAPPLE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(LEMON,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OLIVE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(ORANGE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(PEACH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(PLUM,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RASPBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(REDAPPLE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(SNOWBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(STRAWBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WINTERGREENBERRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),

            new Food.Builder(BARLEY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BARLEYGRAIN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BARLEYFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BARLEYDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(BARLEYBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(MAIZE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CORNBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CORNMEALFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CORNMEALDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OAT,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OATGRAIN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OATFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OATDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(OATBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RICE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RICEGRAIN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RICEFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RICEDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RICEBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RYE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RYEGRAIN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RYEFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RYEDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(RYEBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WHEAT,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WHEATGRAIN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WHEATFLOUR,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WHEATDOUGH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(WHEATBREAD,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),

            new Food.Builder(BEET,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CABBAGE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CARROT,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(GARLIC,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(GREENBEAN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(GREENBELLPEPPER,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(ONION,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(POTATO,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(REDBELLPEPPER,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(SEAWEED,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(SOYBEAN,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(SQUASH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(TOMATO,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(YELLOWBELLPEPPER,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),

            new Food.Builder(BEEF,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CALAMARI,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(FISH,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(HORSEMEAT,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(MUTTON,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(PORKCHOP,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(POULTRY,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(VENISON,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),

            new Food.Builder(EGG,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(CHEESE,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),
            new Food.Builder(MILK,1, 1, 1, 1, 1, 1, 1, 1, true, 1).build(),

            new Food.Builder(JUTE,1, 1, 1, 1, 1, 1, 1, 1, false, 1).build(),
            new Food.Builder(SUGARCANE,1, 1, 1, 1, 1, 1, 1, 1, false, 1).build()


        );
    }
}