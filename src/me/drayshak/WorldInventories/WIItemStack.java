package me.drayshak.WorldInventories;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.material.MaterialData;

public class WIItemStack implements Serializable
{
    private static final long serialVersionUID = -6239771143618730223L;
    private int type = 0;
    private int amount = 0;
    private WIMaterialData data = null;
    private short durability = 0; 
    private Map<Integer, Integer> enchantments = new HashMap<Integer, Integer>();
    
    public WIItemStack(final int ttype, final int tamount, final short tdamage, final Byte tdata, final Map<Enchantment, Integer> enchantments)
    {
        this.type = ttype;
        this.amount = tamount;
        this.durability = tdamage;
        if (tdata != null)
        {
            Material tMat = Material.getMaterial(ttype);

            if (tMat == null)   this.data = new WIMaterialData(ttype, tdata);
            else 
            {
                final MaterialData mdata = tMat.getNewData(tdata);
                this.data = new WIMaterialData(mdata.getItemTypeId(), mdata.getData());
            }
            
            this.durability = tdata;
        }
        if (enchantments != null && enchantments.size() > 0)
        {
        	for(Map.Entry<Enchantment, Integer> entry : enchantments.entrySet())
        	{
        		this.enchantments.put(entry.getKey().getId(), entry.getValue());
        	}
        }
        
    }
    
    public int getTypeId()
    {
         return this.type;
    }
    
    public int getAmount()
    {
        return this.amount;
    }
    
    public short getDurability()
    {
        return this.durability;
    }
    
    public WIMaterialData getData()
    {
        return this.data;
    }
    
    public Boolean hasEnchantments()
    {
    	return this.enchantments != null && this.enchantments.size() > 0;
    }
    public Map<Enchantment, Integer> getEnchantments()
    {
    	Map<Enchantment,Integer> ret = new HashMap<Enchantment,Integer>();
    	for(Map.Entry<Integer, Integer> entry: enchantments.entrySet())
    	{
    		ret.put(Enchantment.getById(entry.getKey()), entry.getValue());
    	}
       	return ret;
    }
    
}
