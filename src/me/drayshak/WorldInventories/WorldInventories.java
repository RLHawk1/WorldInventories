package me.drayshak.WorldInventories;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

public class WorldInventories extends JavaPlugin
{
    public static final Logger log = Logger.getLogger("Minecraft");
    protected static Configuration config;
    private static PluginManager pluginManager = null;
    public static Server bukkitServer = null;
    private ArrayList<Group> groups = null;
    // NetBeans complains about these log lines but message formatting breaks for me
    public static void logStandard(String line)
    {
        log.log(Level.INFO, "[WorldInventories] " + line);
    }
    
    public static void logError(String line)
    {
        log.log(Level.SEVERE, "[WorldInventories] " + line);
    }
    
    public static void logDebug(String line)
    {
        log.log(Level.FINE, "[WorldInventories] " + line);
    }
    
    private void createDefConfigIfNecessary()
    {
        boolean bConfigChanged = false;
        
        List<String> worldgroups = WorldInventories.config.getKeys("groups");
        if(worldgroups == null)
        {
            bConfigChanged = true;
            
            ArrayList<String> examplegroups = new ArrayList<String>();
            examplegroups.add("examplegroupone");
            examplegroups.add("examplegrouptwo");
            
            config.setProperty("groups", examplegroups);
            
            String exampleworlds = "exampleworldone,exampleworldtwo";
            
            config.setProperty("groups.exampleworldone", exampleworlds);
            
            String exampleworldstwo = "exampleworldthree";
            
            config.setProperty("groups.examplegrouptwo", exampleworldstwo);
        }
        
        if(bConfigChanged) config.save();
    }
    
    private boolean loadConfiguration()
    {
        this.groups = new ArrayList<Group>();
        
        List<String> nodes =  WorldInventories.config.getKeys("groups");
        WorldInventories.logStandard(Integer.toString(nodes.size()));
        for(String group : nodes)
        {
            List<String> worldnames = WorldInventories.config.getStringList("groups." + group, null);
            if(worldnames != null)
            {
                this.groups.add(new Group(group, worldnames));
            }
        }
        
        return true;
    }
    
    @Override
    public void onEnable()
    {
        WorldInventories.logStandard("Initialising...");
        
        boolean bInitialised = true;
        
        WorldInventories.bukkitServer = this.getServer();
        WorldInventories.pluginManager = WorldInventories.bukkitServer.getPluginManager();
        
        config = this.getConfiguration();
        config.load();
        
        WorldInventories.logStandard("Loading configuration...");
        this.createDefConfigIfNecessary();
        boolean bConfiguration = this.loadConfiguration();
        
        if(!bConfiguration)
        {
            WorldInventories.logError("Failed to load configuration.");
            bInitialised = false;
        }
        else
        {
            WorldInventories.logStandard("Loaded configuration successfully");
            // TODO: Do stuff here
        }
        
        if(bInitialised)
        {
            WorldInventories.logStandard("Initialised successfully!");
            
        }
        else WorldInventories.logError("Failed to initialise.");
        
    }
    
    @Override
    public void onDisable()
    {
        // TODO: Save current information!
        WorldInventories.logStandard("Plugin disabled");
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        String command = cmd.getName();
        
        if(command.equalsIgnoreCase("witoggleplayer"))
        {
            if(args.length == 2)
            {
                boolean bToggle = false;
                
                if(args[0].equalsIgnoreCase("on"))          bToggle = true;
                else if(args[0].equalsIgnoreCase("off"))    bToggle = false;
                else return false;
                
                Player player = WorldInventories.bukkitServer.getPlayerExact(args[1]);
                if(player == null) sender.sendMessage("Player not found!");
                else
                {
                    //TODO: Toggle inventory management for this player
                }
            }
            
            return true;
        }

        return false; 
    }    
}
