package net.dries007.tfc.util.mechanical;

import java.util.ArrayList;
import java.util.List;

import net.dries007.tfc.common.capabilities.power.IRotator;

public class MechanicalNetwork
{
    public long id;
    public List<IRotator> members;
    public IRotator source;
    public boolean valid = true;
    public boolean dirty = true;

    public MechanicalNetwork(IRotator source)
    {
        this.id = source.getBlockPos().asLong();
        this.members = new ArrayList<>();
        members.add(source);
        this.source = source;
    }

    public void add(IRotator provider)
    {
        if (!members.contains(provider) && !provider.isSource())
        {
            members.add(provider);
        }
    }

    public void remove(IRotator provider)
    {
        if (provider.equals(source))
        {
            valid = false;
        }
        members.remove(provider);
    }
}
