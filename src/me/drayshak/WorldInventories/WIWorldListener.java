package me.drayshak.WorldInventories;

import org.bukkit.entity.Player;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldSaveEvent;

public class WIWorldListener extends WorldListener
{
    private static WorldInventories plugin;
 
    public WIWorldListener(final WorldInventories tplugin)
    {
        plugin = tplugin;
    }
    
    @Override
    public void onWorldSave(WorldSaveEvent event)
    {
        WorldInventories.logStandard("Saving player inventories...");
        
        for(Player player : WorldInventories.bukkitServer.getOnlinePlayers())
        {
            String world = player.getLocation().getWorld().getName();

            Group tGroup = WorldInventories.findFirstGroupForWorld(world);

            // Don't save if we don't care where we are (default group)
            if(tGroup != null)
            {    
                plugin.savePlayerInventory(player, WorldInventories.findFirstGroupForWorld(world), plugin.getPlayerInventory(player));
            }
        }
        
        WorldInventories.logStandard("Done.");
    }    
}
