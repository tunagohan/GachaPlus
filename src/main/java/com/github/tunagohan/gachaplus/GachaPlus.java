package com.github.tunagohan.gachaplus;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/*
 * GachaListener
 * @license    LGPLv3
 * @copyright  Copyright com.github.tunagohan 2021
 * @author     tunagohan
 */
public class GachaPlus extends JavaPlugin{
    private GachaPlusDatabase database;
    private GachaPlusCommand command;
    private GachaPlusListener listener;

    private static Economy econ;

    /**
     * Get GachaDatabase instance.
     */
    public GachaPlusDatabase getDatabase() {
        return database;
    }

    /**
     * Get GachaCommand instance.
     */
    public GachaPlusCommand getCommand() {
        return command;
    }

    /**
     * Get GachaListener instance.
     */
    public GachaPlusListener getListener() {
        return listener;
    }

    /**
     * JavaPlugin method onEnable.
     */
    @Override
    public void onEnable(){
        try{
            getLogger().log(Level.INFO, "The Plugin Has Been Enabled!");

            // If there is no setting file, it is created
            if(!getDataFolder().exists()){
                getDataFolder().mkdir();
            }

            File configFile = new File(getDataFolder(), "config.yml");
            if(!configFile.exists()){
                saveDefaultConfig();
            }

            // Initialize the database.
            database = new GachaPlusDatabase(this);
            database.initialize();

            // Register event listener.
            PluginManager pm = getServer().getPluginManager();
            HandlerList.unregisterAll(this);    // clean up
            listener = new GachaPlusListener(this);
            pm.registerEvents(listener, this);

            // Instance prepared of GachaCommand.
            command = new GachaPlusCommand(this);

            if (!setupEconomy()) {
                this.getLogger().severe("Disabled due to no Vault dependency found!");
                pm.disablePlugin(this);
                return;
            }

        } catch (Exception e){
            GachaPlusUtility.logStackTrace(e);
        }
    }

    /**
     * JavaPlugin method onCommand.
     *
     * @return boolean true:Success false:Display the usage dialog set in plugin.yml
     */
    public boolean onCommand( CommandSender sender, Command commandInfo, String label, String[] args) {
        boolean hideUseageFlag = true;  // true:Success false:Display the usage dialog set in plugin.yml
        try{
            if(!commandInfo.getName().equals("gachaplus")) {
                return hideUseageFlag;
            }

            if(args.length <= 0) {
                return hideUseageFlag;
            }
            String subCommand = args[0];

            command.initialize(sender, args);
            switch(subCommand) {
                case "list":
                    if(sender.hasPermission("gachaplus.list")) {
                        hideUseageFlag = command.list();
                    }
                    break;

                case "modify":
                    if(sender.hasPermission("gachaplus.modify")) {
                        hideUseageFlag = command.modify();
                    }
                    break;

                case "delete":
                    if(sender.hasPermission("gachaplus.delete")) {
                        hideUseageFlag = command.delete();
                    }
                    break;

                case "enable":
                    if(sender.isOp()) {
                        hideUseageFlag = command.enable();
                    }
                    break;

                case "reload":
                    if(sender.isOp()) {
                        hideUseageFlag = command.reload();
                    }
                    break;

                case "disable":
                    if(sender.isOp()) {
                        hideUseageFlag = command.disable();
                    }
                    break;

                default:
                    hideUseageFlag = false;
            }
        }catch(Exception e){
            GachaPlusUtility.logStackTrace(e);
        }finally{
            command.finalize();
        }
        return hideUseageFlag;
    }

    /**
     * JavaPlugin method onDisable.
     */
    @Override
    public void onDisable(){
        try{
            database.finalize();
            command.finalize();

            // Unregister all event listener.
            HandlerList.unregisterAll(this);

            getLogger().log(Level.INFO, "The Plugin Has Been Disabled!");
        } catch (Exception e){
            GachaPlusUtility.logStackTrace(e);
        }
    }

    private boolean setupEconomy() {
        PluginManager pm = getServer().getPluginManager();

        if (pm.getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
