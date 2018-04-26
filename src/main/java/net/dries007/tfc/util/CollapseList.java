package net.dries007.tfc.util;

import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.util.math.BlockPos;

public class CollapseList
{
    private final HashSet<BlockPos> checked = new HashSet<>();
    private final LinkedList<CollapseData> check = new LinkedList<>();

    public CollapseList()
    {

    }

    public void add(CollapseData collapseData)
    {
        if (checked.contains(collapseData.pos)) return;
        check.add(collapseData);
    }

    public boolean isEmpty()
    {
        return check.isEmpty();
    }

    public CollapseData pop()
    {
        CollapseData data = check.pop();
        checked.add(data.pos);
        return data;
    }
}
