package net.dries007.tfc.compat.wailatop;

import java.util.List;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import mcp.MethodsReturnNonnullByDefault;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.api.recipes.barrel.BarrelRecipe;
import net.dries007.tfc.api.types.ICrop;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.agriculture.*;
import net.dries007.tfc.objects.blocks.stone.BlockFarmlandTFC;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.*;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.Month;
import net.dries007.tfc.util.climate.ClimateTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;


@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class WailaBlocks implements IWailaDataProvider
{
    public static void callbackRegister(IWailaRegistrar registrar)
    {

        registrar.addConfig("TFC", "tfc.displayTemp", true);
        registrar.addConfig("TFC", "tfc.newtotfc", true);


        WailaBlocks dataProvider = new WailaBlocks();

        //Stack
        registrar.registerStackProvider(dataProvider, BlockOreTFC.class);
        registrar.registerStackProvider(dataProvider, BlockCropTFC.class);
        //Head
        registrar.registerHeadProvider(dataProvider, BlockOreTFC.class);
        registrar.registerHeadProvider(dataProvider, BlockBarrel.class);
        registrar.registerHeadProvider(dataProvider, TECropBase.class);
        registrar.registerHeadProvider(dataProvider, BlockCropDead.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeLeaves.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeTrunk.class);
        registrar.registerHeadProvider(dataProvider, BlockFruitTreeBranch.class);

        //Body
        registrar.registerBodyProvider(dataProvider, BlockOreTFC.class);
        registrar.registerBodyProvider(dataProvider, TEPitKiln.class);
        registrar.registerBodyProvider(dataProvider, TEFirePit.class);
        registrar.registerBodyProvider(dataProvider, TEBloomery.class);
        registrar.registerBodyProvider(dataProvider, TEBlastFurnace.class);
        registrar.registerBodyProvider(dataProvider, TECharcoalForge.class);
        registrar.registerBodyProvider(dataProvider, TEBarrel.class);
        registrar.registerBodyProvider(dataProvider, TELogPile.class);
        registrar.registerBodyProvider(dataProvider, TEPlacedItemFlat.class);
        registrar.registerBodyProvider(dataProvider, TECropBase.class);
        registrar.registerBodyProvider(dataProvider, BlockFruitTreeLeaves.class);
        registrar.registerBodyProvider(dataProvider, BlockBerryBush.class);
        registrar.registerBodyProvider(dataProvider, BlockCropDead.class);

        //Tail
        registrar.registerTailProvider(dataProvider, BlockRockVariant.class);


    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        ItemStack itemstack = ItemStack.EMPTY;

        if (b instanceof BlockOreTFC)
        {
            itemstack = getOreTFCStack(accessor, config);
        }
        else if (b instanceof BlockCropSimple)
        {
            itemstack = getCropSimpleStack(accessor, config);
        }
        return itemstack;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        if (b instanceof BlockOreTFC) currenttip = getOreTFCHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockBarrel) currenttip = getBarrelHead(itemStack, currenttip, accessor, config);
        if (te instanceof TECropBase) currenttip = getCropSimpleHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockCropDead) currenttip = getCropDeadHead(itemStack, currenttip, accessor, config);
        if (b instanceof BlockFruitTreeTrunk | b instanceof BlockFruitTreeLeaves | b instanceof BlockFruitTreeBranch)
        {
            currenttip = getFruitTreeHead(itemStack, currenttip, accessor, config);
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TileEntity te = accessor.getTileEntity();

        // Mechanics
        if (b instanceof BlockOreTFC) currenttip = getOreTFCBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEPlacedItemFlat)
            currenttip = getPlacedItemFlatBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEPitKiln) currenttip = getPitKilnBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TECharcoalForge)
            currenttip = getCharcoalForgeBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TELogPile) currenttip = getLogPileBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBarrel) currenttip = getBarrelBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBloomery) currenttip = getBloomeryBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEBlastFurnace)
            currenttip = getBlastFurnaceBody(itemStack, currenttip, accessor, config);
        else if (te instanceof TEFirePit) currenttip = getFirepitBody(itemStack, currenttip, accessor, config);
            // Crops and Trees
        else if (te instanceof TECropBase) currenttip = getCropSimpleBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currenttip = getFruitTreeLeavesBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockBerryBush) currenttip = getBerryBushBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockCropDead) currenttip = getCropDeadBody(itemStack, currenttip, accessor, config);
        else if (b instanceof BlockFruitTreeLeaves)
            currenttip = getFruitTreeLeavesBody(itemStack, currenttip, accessor, config);

        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (config.getConfig("tfc.displayTemp"))
        {
            currenttip.add(new TextComponentTranslation("waila.tfc.temperature").getFormattedText() + " : " + String.valueOf(Math.round(ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), 0))) + new TextComponentTranslation("waila.tfc.tempsymbol").getFormattedText());
        }
        return currenttip;
    }

    private List<String> getFirepitBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        return currenttip;
    }

    private List<String> getPitKilnBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        Block b = accessor.getBlock();
        TEPitKiln te = (TEPitKiln) accessor.getTileEntity();
        String key;
        boolean isLit = te.isLit();
        long litTick = te.getLitTick();

        if (isLit)
        {
            long remainingMinutes = Math.round(((long) ConfigTFC.GENERAL.pitKilnTime - (CalendarTFC.PLAYER_TIME.getTicks() - litTick)) / 1200)+ 1;
            key = remainingMinutes + " " + new TextComponentTranslation("waila.tfc.remaining").getFormattedText();
        }
        else
        {
            int straw = te.getStrawCount();
            int logs = te.getLogCount();
            if (straw == 8 && logs == 8)
            {
                key = new TextComponentTranslation("unlit").getFormattedText();
            }
            else
            {
                key = straw + " " + new TextComponentTranslation("waila.tfc.straw").getFormattedText() + " " + logs + " " + new TextComponentTranslation("waila.tfc.logs").getFormattedText();
            }
        }
        currenttip.add(key);

        return currenttip;
    }

    private List<String> getOreTFCBody(ItemStack itemStack, List<java.lang.String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        int metadata = accessor.getMetadata();
        Ore.Grade gradevalue = Ore.Grade.valueOf(metadata);
        Metal metal = b.ore.getMetal();
        String orename = b.ore.toString();
        String key;

        if (gradevalue == Ore.Grade.NORMAL)
        {
            key = new TextComponentTranslation("waila.tfc.normal").getFormattedText() + " " + new TextComponentTranslation("item.tfc.ore." + orename + ".name").getFormattedText();
        }
        else
        {
            String gradename = gradevalue.toString().toLowerCase();
            key = new TextComponentTranslation("item.tfc.ore." + orename + "." + gradename + ".name").getFormattedText();
        }
        currenttip.add(key);
        if (metal != null && config.getConfig("tfc.newtotfc"))
        {
            currenttip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
        }

        return currenttip;
    }

    private ItemStack getOreTFCStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        BlockStateContainer state = b.getBlockState();
        ItemStack itemstack = ItemOreTFC.get(b.ore, 1);

        return itemstack;
    }

    private List<String> getBlastFurnaceBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        TEBlastFurnace te = (TEBlastFurnace) accessor.getTileEntity();
        NBTTagCompound nbt = accessor.getNBTData();
        // Something is borked with Waila reading the nbt second time round. Dunno
        return currenttip;
    }

    private List<String> getFruitTreeHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        String name = accessor.getBlock().getTranslationKey();
        String output = "waila.tfc.fruit_trees." + name.substring(name.lastIndexOf(".") + 1) + ".name";

        currenttip.set(0, new TextComponentTranslation(output).getFormattedText());

        return currenttip;
    }

    private ItemStack getCropSimpleStack(IWailaDataAccessor accessor, IWailaConfigHandler config)
    {

        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        ICrop crop = b.getCrop();
        IBlockState blockstate = accessor.getBlockState();
        int curStage = blockstate.getValue(b.getStageProperty());
        ItemStack itemStack = crop.getFoodDrop(curStage);


        return itemStack;
    }

    private List<String> getCropDeadHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropDead b = (BlockCropDead) accessor.getBlock();
        ICrop crop = b.getCrop();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation("tile.tfc.crop." + crop.toString().toLowerCase() + ".name").getFormattedText());
        return currenttip;
    }

    private List<String> getCropSimpleHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockCropTFC b = (BlockCropTFC) accessor.getBlock();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());

        return currenttip;
    }

    private List<String> getPlacedItemFlatBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        if (itemStack.getItem() instanceof ItemSmallOre)
        {
            ItemSmallOre nugget = (ItemSmallOre) itemStack.getItem();
            Ore ore = nugget.getOre();
            Metal metal = ore.getMetal();
            if (metal != null && config.getConfig("tfc.newtotfc"))
            {
                currenttip.add("(" + new TextComponentTranslation(metal.getTranslationKey()).getFormattedText() + ")");
            }
        }
        if (itemStack.getItem() instanceof ItemRock)
        {
            ItemRock pebble = (ItemRock) itemStack.getItem();
            Rock rock = pebble.getRock(itemStack);
            if (rock.isFluxStone() && config.getConfig("tfc.newtotfc"))
            {
                currenttip.add("(" + new TextComponentTranslation("waila.tfc.fluxstone").getFormattedText() + ")");
            }

        }
        return currenttip;

    }

    private List<String> getBarrelHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockBarrel b = (BlockBarrel) accessor.getBlock();
        TEBarrel te = (TEBarrel) accessor.getTileEntity();

        if (te.isSealed())
        {
            String sealedDate;
            sealedDate = te.getSealedDate();
            currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".sealed.name").getFormattedText());
            currenttip.add(new TextComponentTranslation("waila.tfc.sealed").getFormattedText() + ":" + sealedDate);


        }
        else
        {
            currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        }
        return currenttip;
    }

    private List<String> getCropDeadBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        currenttip.add(new TextComponentTranslation("waila.tfc.deadcrop").getFormattedText());
        return currenttip;
    }


    private List<String> getBerryBushBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockBerryBush b = (BlockBerryBush) accessor.getBlock();
        String text = "";

        for (int i = 0; i < 12; i++)
        {
            if (b.bush.isHarvestMonth(Month.valueOf(i)))
            {
                text = text + " " + new TextComponentTranslation("tfc.enum.month." + Month.valueOf(i).name().toLowerCase()).getFormattedText();

            }
        }
        currenttip.add(text.trim());

        return currenttip;
    }

    private List<String> getFruitTreeLeavesBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockFruitTreeLeaves b = (BlockFruitTreeLeaves) accessor.getBlock();
        String text = "";

        for (int i = 0; i < 12; i++)
        {
            if (b.tree.isHarvestMonth(Month.valueOf(i)))
            {
                text = text + " " + new TextComponentTranslation("tfc.enum.month." + Month.valueOf(i).name().toLowerCase()).getFormattedText();

            }
        }
        currenttip.add(text.trim());

        return currenttip;
    }

    private List<String> getCropSimpleBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TECropBase te = (TECropBase) accessor.getTileEntity();
        BlockCropSimple bs = (BlockCropSimple) accessor.getBlock();
        ICrop crop = bs.getCrop();
        int maxStage = crop.getMaxStage();
        float totalGrowthTime = crop.getGrowthTime();
        IBlockState blockstate = accessor.getBlockState();
        int curStage = blockstate.getValue(bs.getStageProperty());
        boolean isWild = blockstate.getValue(BlockCropTFC.WILD);
        long tick = te.getLastUpdateTick();
        float totalTime = totalGrowthTime * maxStage;
        float currentTime = (curStage * totalGrowthTime) + (CalendarTFC.PLAYER_TIME.getTicks() - tick);
        int completionPerc = Math.round(currentTime / totalTime * 100);
        float temp = ClimateTFC.getActualTemp(accessor.getWorld(), accessor.getPosition(), -tick);
        float rainfall = ChunkDataTFC.getRainfall(accessor.getWorld(), accessor.getPosition());
        String text;
        if (isWild)
        {
            text = new TextComponentTranslation("waila.tfc.wild").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else if (crop.isValidForGrowth(temp, rainfall))
        {
            text = TextFormatting.GREEN.toString() + new TextComponentTranslation("waila.tfc.growing").getFormattedText();
            if (completionPerc <= 100)
            {
                text = text + TextFormatting.GRAY.toString() + " : " + completionPerc + "%";
            }
        }
        else
        {
            text = TextFormatting.RED.toString() + new TextComponentTranslation("waila.tfc.notgrowing").getFormattedText();
        }

        if (completionPerc > 100)
        {
            //Should test here if crop is 'pickable' and indicate as the action is different for each
            text = text + TextFormatting.GREEN.toString() + " : " + new TextComponentTranslation("waila.tfc.mature").getFormattedText();
        }
        currenttip.add(text);

        return currenttip;
    }

    private List<String> getBloomeryBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> getBarrelBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        TEBarrel te = (TEBarrel) accessor.getTileEntity();

        String result;
        NBTTagCompound itemTag = te.getItemTag();

        NBTTagCompound tank = itemTag.getCompoundTag("tank");
        String fluid = tank.getString("FluidName");
        String fullfluid = new TextComponentTranslation("fluid." + fluid).getFormattedText();
        int amount = tank.getInteger("Amount");

        if (te.isSealed())
        {
            BarrelRecipe recipe = te.getRecipe();
            if (recipe != null)
            {

                result = new TextComponentTranslation("waila.tfc.making").getFormattedText() + " " + recipe.getResultName();
                currenttip.add(result);
            }
            else
            {
                result = new TextComponentTranslation("waila.tfc.norecipe").getFormattedText();
                currenttip.add(result);
            }
        }
        if (amount > 0)
        {
            result = new TextComponentTranslation("waila.tfc.contains").getFormattedText() + " " + amount + " units of " + fullfluid;
            currenttip.add(result);
        }

        return currenttip;
    }

    private List<String> getLogPileBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> getCharcoalForgeBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        //todo
        return currenttip;
    }

    private List<String> getOreTFCHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config)
    {
        BlockOreTFC b = (BlockOreTFC) accessor.getBlock();
        currenttip.set(0, TextFormatting.WHITE.toString() + new TextComponentTranslation(b.getTranslationKey() + ".name").getFormattedText());
        return currenttip;
    }

}
