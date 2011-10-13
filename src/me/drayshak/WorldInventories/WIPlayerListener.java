package me.drayshak.WorldInventories;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerPortalEvent;

public class WIPlayerListener extends PlayerListener
{
    private static WorldInventories plugin;
 
    public WIPlayerListener(final WorldInventories tplugin)
    {
        plugin = tplugin;
    }
    
    @Override
    public void onPlayerPortal(PlayerPortalEvent event)
    {
        if(event.isCancelled()) return;
        
        Player player = event.getPlayer();

        String fromworld = event.getFrom().getWorld().getName();

        Location toLocation = event.getTo();
        if(toLocation == null)
        {
            player.sendMessage(ChatColor.RED + "Couldn't get your destination world - can't change inventory!");
            return;
        } // Fix MultiVerse bug
        
        String toworld = toLocation.getWorld().getName();

        if(toworld.equals(fromworld))
        {
            // Something odd happens with MultiVerse-SignPortals, try to fix

            toworld = player.getLocation().getWorld().getName();
        }

        if(!fromworld.equals(toworld))
        {
            WorldInventories.logStandard("Player " + player.getName() + " used a portal from " + fromworld + " to " + toworld);

            Group fromgroup = WorldInventories.findFirstGroupForWorld(fromworld);
            Group togroup = WorldInventories.findFirstGroupForWorld(toworld);

            plugin.savePlayerInventory(player, fromgroup, plugin.getPlayerInventory(player));

            String fromgroupname = "default";
            if(fromgroup != null) fromgroupname = fromgroup.getName();             

            String togroupname = "default";
            if(togroup != null) togroupname = togroup.getName();            

            if(!fromgroupname.equals(togroupname))
            {
                plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player, togroup));
                player.sendMessage(ChatColor.GREEN + "Changed inventory set to group: " + togroupname);
            }
            else
            {
                player.sendMessage(ChatColor.GREEN + "No inventory change necessary for group: " + togroupname);
            }
        }
    }
    
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        
        String fromworld = event.getFrom().getWorld().getName();
        String toworld = event.getTo().getWorld().getName();
        
        if(!fromworld.equals(toworld))
        {
            WorldInventories.logStandard("Player " + player.getName() + " teleported from " + fromworld + " to " + toworld);
            
            Group fromgroup = WorldInventories.findFirstGroupForWorld(fromworld);
            Group togroup = WorldInventories.findFirstGroupForWorld(toworld);
            
            plugin.savePlayerInventory(player, fromgroup, plugin.getPlayerInventory(player));
      
            String fromgroupname = "default";
            if(fromgroup != null) fromgroupname = fromgroup.getName();             
            
            String togroupname = "default";
            if(togroup != null) togroupname = togroup.getName();            

            if(!fromgroupname.equals(togroupname))
            {
                plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player, togroup));
                player.sendMessage(ChatColor.GREEN + "Changed inventory set to group: " + togroupname);
            }
            else
            {
                player.sendMessage(ChatColor.GREEN + "No inventory change necessary for group: " + togroupname);
            }
        }
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();
        
        WorldInventories.logStandard("Player joined world: " + world);
        
        Group tGroup = WorldInventories.findFirstGroupForWorld(world);
        
        // Don't load if we don't care where we are (default group)
        if(tGroup != null)
        {
            WorldInventories.logStandard("Loading inventory of " + player.getName());
            plugin.setPlayerInventory(player, plugin.loadPlayerInventory(player, tGroup));
            
            String groupname = "default";
            if(tGroup != null) groupname = tGroup.getName();
            
            player.sendMessage(ChatColor.GREEN + "Changed inventory to group: " + groupname);
        }
    }
    
    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        String world = player.getLocation().getWorld().getName();
        
        WorldInventories.logStandard("Player " + player.getName() + " quit from world: " + world);
        
        Group tGroup = WorldInventories.findFirstGroupForWorld(world);
        
        // Don't save if we don't care where we are (default group)
        if(tGroup != null)
        {    
            WorldInventories.logStandard("Saving inventory of " + player.getName());
            plugin.savePlayerInventory(player, WorldInventories.findFirstGroupForWorld(world), plugin.getPlayerInventory(player));
        }
    }
}
