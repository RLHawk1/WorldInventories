package me.drayshak.WorldInventories;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;


public class WIPlayerListener extends PlayerListener
{
    private static WorldInventories plugin;
 
    public WIPlayerListener(final WorldInventories tplugin) {
        plugin = tplugin;
    }
    
    public static WIPlayerInventory getPlayerInventory(Player player)
    {
        return new WIPlayerInventory(player.getInventory().getContents(), player.getInventory().getArmorContents());
    }
    
    public static void setPlayerInventory(Player player, WIPlayerInventory playerInventory)
    {
        if(playerInventory != null)
        {
            player.getInventory().setContents(playerInventory.getItems());
            player.getInventory().setArmorContents(playerInventory.getArmour());
        }
    }
    
    public static void savePlayerInventory(Player player, Group group, WIPlayerInventory toStore)
    {        
        FileOutputStream fOS = null;
        ObjectOutputStream obOut = null;
        
        if(!plugin.getDataFolder().exists()) plugin.getDataFolder().mkdir();
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = plugin.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".inventory";
        
        try
        {
            fOS = new FileOutputStream(path);
            obOut = new ObjectOutputStream(fOS);
            obOut.writeObject(toStore);
            obOut.close();
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to save inventory for player: " + player.getName() + ": " + e.getMessage());
        }
    }
    
    public static WIPlayerInventory loadPlayerInventory(Player player, Group group)
    {
        WIPlayerInventory playerInventory = null;
        
        FileInputStream fIS = null;
        ObjectInputStream obIn = null;
        
        String path = File.separator;
        
        // Use default group
        if(group == null)   path += "default";
        else                path += group.getName();

        path = plugin.getDataFolder().getAbsolutePath() + path;
        
        File file = new File(path);
        if(!file.exists()) file.mkdir();
        
        path += File.separator + player.getName() + ".inventory";
        
        try
        {
            fIS = new FileInputStream(path);
            obIn = new ObjectInputStream(fIS);
            playerInventory = (WIPlayerInventory) obIn.readObject();
            obIn.close();
        }
        catch (FileNotFoundException e)
        {
            WorldInventories.logError("Player " + player.getName() + " will get a new item file on next save (clearing now).");
            player.getInventory().clear();
            ItemStack[] armour = new ItemStack[4];
            for(int i = 0; i < 4; i++)
            {
                armour[i] = new ItemStack(Material.AIR);
            }
            
            player.getInventory().setArmorContents(armour);
        }
        catch (Exception e)
        {
            WorldInventories.logError("Failed to load inventory for player: " + player.getName() + ", giving empty inventory: " + e.getMessage());
        }
                
        return playerInventory;
    }
    
    @Override
    public void onPlayerTeleport(PlayerTeleportEvent event)
    {
        Player player = event.getPlayer();
        
        String fromworld = event.getFrom().getWorld().getName();
        String toworld = event.getTo().getWorld().getName();
        
        WorldInventories.logStandard("Player " + player.getName() + " teleported from " + fromworld + " to " + toworld);
        
        if(!fromworld.equals(toworld))
        {
            Group fromgroup = WorldInventories.findFirstGroupForWorld(fromworld);
            Group togroup = WorldInventories.findFirstGroupForWorld(toworld);
            
            savePlayerInventory(player, fromgroup, getPlayerInventory(player));
            setPlayerInventory(player, loadPlayerInventory(player, togroup));
            
            String groupname = "default";
            if(togroup != null) groupname = togroup.getName();
            
            player.sendMessage(ChatColor.GREEN + "Changed inventory to group: " + groupname);
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
            WorldInventories.logStandard("Loading inventory of " + player);
            setPlayerInventory(player, loadPlayerInventory(player, tGroup));
            
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
        
        WorldInventories.logStandard("Player left world: " + world);
        
        Group tGroup = WorldInventories.findFirstGroupForWorld(world);
        
        // Don't save if we don't care where we are (default group)
        if(tGroup != null)
        {    
            WorldInventories.logStandard("Saving inventory of " + player);
            savePlayerInventory(player, WorldInventories.findFirstGroupForWorld(world), getPlayerInventory(player));
        }
    }
}
