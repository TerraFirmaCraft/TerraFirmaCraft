package net.dries007.tfc.objects.trees;

import net.dries007.tfc.Constants;
import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.trees.TreeSchematicManager.*;

import java.util.*;

/**
 * Ported and modified version of TFC2 code by Bioxx
 */
public class TreeRegistry {

    public static TreeRegistry instance = new TreeRegistry();
    private HashMap<Wood, TreeSchematicManager> treeList;

    public TreeRegistry()
    {
        treeList  = new HashMap<Wood, TreeSchematicManager>();
    }

    public void RegisterSchematic(Wood wood, TreeSchematic treeSchematic)
    {
        if(!treeList.containsKey(wood))
            treeList.put(wood, new TreeSchematicManager());
        treeList.get(wood).addSchematic(treeSchematic);
    }

    public String[] getTreeNames()
    {
        return (String[])treeList.keySet().toArray(new String[treeList.size()]);
    }

    public TreeSchematic getRandomTreeSchematic(Random random)
    {
        return treeList.get(random.nextInt(treeList.size())).getRandomSchematic(random);
    }

    /**
     * @return Returns a random schematic for a specific tree type at any growth stage
     */
    public TreeSchematic getRandomTreeSchematic(Random random, String treeID)
    {
        if(!treeList.containsKey(treeID)) return null;
        return treeList.get(treeID).getRandomSchematic(random);
    }

    /**
     * @return Returns a random schematic for a specific tree type at a specific growth stage
     */
    public TreeSchematic getRandomTreeSchematic(Random R, String treeID, int growthStage)
    {
        if(!treeList.containsKey(treeID)) return null;
        return treeList.get(treeID).getRandomSchematic(R, growthStage);
    }

    /**
     * @return Returns a specific schematic
     */
    public TreeSchematic getTreeSchematic(String treeID, int schemID, int growthStage)
    {
        if(!treeList.containsKey(treeID)) return null;
        return treeList.get(treeID).getSchematic(schemID, growthStage);
    }

    public void addTreeType(Wood wood)
    {
        treeList.put(wood, new TreeSchematicManager());
    }

    /**
     * @param name Name of the Tree type. Used as the Key in the hash map for lookups.
     * @return Tree Schematic Manager
     */
    public TreeSchematicManager managerFromString(String name)
    {
        for (Wood wood : Wood.values()) {
            if (wood.name() == name)
                if (treeList.containsKey(wood))
                    return treeList.get(wood);
        }
        return null;
    }

    /**
     * @param name Name of the Tree type. Used as the Key in the hash map for lookups.
     * @return Wood object
     */
    public Wood treeFromString(String name)
    {
        for (Wood wood : Wood.values()) {
            if (wood.name() == name)
                if (treeList.containsKey(wood))
                    return wood;
        }
        return null;
    }

    /**
     * Get random Wood object from registry.
     * @return Wood object
     */
    public Wood getRandomTree()
    {
        List<Wood> keysAsArray = new ArrayList<Wood>(treeList.keySet());
        Random random = new Random();
        return keysAsArray.get(random.nextInt(keysAsArray.size()));
    }

    //public String getRandomTreeTypeForIsland(Random r, ClimateTemp temp, Moisture moisture, boolean swamp)
    /*public Wood getRandomTreeType(Random random, boolean swamp)
    {
        ArrayList<String> list = new ArrayList<String>();
        Iterator iter = treeList.keySet().iterator();
        while(iter.hasNext())
        {
            String tree = (String) iter.next();
            //Palm trees are a special case and will always exist on subtropical and tropical islands.
//            if(tree.equals(WoodType.Palm.name()))
//                continue;
            TreeConfig tc = treeFromString(tree.);

//            if(swamp && !tc.isSwampTree)
//                continue;

            //Willows are only allowed to be Swamp Trees
            if(!swamp && tree.equalsIgnoreCase(Wood.WILLOW.name()))
            {
                continue;
            }

            //if(tc.minTemp.getMapTemp() <= temp.getMapTemp() && tc.maxTemp.getMapTemp() >= temp.getMapTemp() &&
            //        tc.minMoisture.getMoisture() <= moisture.getMoisture() && tc.maxMoisture.getMoisture() >= moisture.getMoisture())
                list.add(tree);
        }
        if(list.size() == 0)
            return "";
        if(list.size() == 1)
            return list.get(0);

        return list.get(r.nextInt(list.size()));
    }*/

    public static void loadTrees()
    {
        TreeRegistry treeRegistry = TreeRegistry.instance;

        //log.info("Loading Trees");
        for (Wood wood : Wood.values())
        {
            treeRegistry.addTreeType(wood);

            for(TreeSize treeSize : TreeSize.values())
            {
                for(int schematicID = 0; schematicID < 99; schematicID++)
                {
                    String schematicPath = Constants.TREEPATH + wood.name() + "/" + treeSize.name() + "_" + String.format("%02d", schematicID) + ".schematic";

                    TreeSchematic treeSchematic = new TreeSchematic(schematicPath, treeSize.name() + "_" + String.format("%02d", schematicID), wood);

                    TerraFirmaCraft.getLog().info("Tree schematic " + wood.name() + " loading");

                    if(treeSchematic.Load())
                    {
                        treeSchematic.PostProcess();
                        TreeRegistry.instance.RegisterSchematic(wood, treeSchematic);
                    }
                    else
                    {
                        TerraFirmaCraft.getLog().info("ERROR loading " + wood.name());
                        break;
                    }
                }
            }
        }
    }

    public enum TreeSize
    {
        SMALL,
        NORMAL,
        LARGE
    }
}
