package me.drayshak.WorldInventories;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.inventory.ItemStack;

public class WIEntityListener extends EntityListener
{
    private static WorldInventories plugin;
    
    public WIEntityListener(final WorldInventories tplugin)
    {
        plugin = tplugin;
    }
    
    @Override
    public void onEntityDeath(EntityDeathEvent event)
    {
        Entity entity = event.getEntity();
        if(entity instanceof Player)
        {
            Player player = (Player)event.getEntity();
            String world = player.getWorld().getName();
            
            Group togroup = WorldInventories.findFirstGroupForWorld(world);
            String togroupname = "default";
            if(togroup != null) togroupname = togroup.getName();               
            
            WorldInventories.logStandard("Player " + player.getName() + " died in world " + world + ", emptying inventory for group: " + togroupname);
            
            // Make the saved inventory blank so players can't duplicate by switching worlds and picking items back up
            plugin.savePlayerInventory(player.getName(), togroup, new WIPlayerInventory(new ItemStack[36], new ItemStack[4]));
            if(WorldInventories.doStats)
            {
                plugin.savePlayerStats(player, togroup, new WIPlayerStats(20, 20, 0, 0));
            }
        }
    }
}
