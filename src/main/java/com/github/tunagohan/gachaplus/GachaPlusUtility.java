package com.github.tunagohan.gachaplus;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;

/*
 * GachaListener
 * @license    LGPLv3
 * @copyright  Copyright com.github.tunagohan 2021
 * @author     tunagohan
 */
public class GachaPlusUtility {

  /**
   * Output stack trace to log file.
   * @param Exception Exception
   */
  public static void logStackTrace(Exception e){
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    e.printStackTrace(pw);
    pw.flush();
    Bukkit.getLogger().log(Level.WARNING, sw.toString());
  }

  /**
   * Determine whether punch is being processed.
   * @param Player Player
   * @return boolean true:That's right false:That's not it
   */
  public static boolean isInPunch(Player player){
    if( player.hasMetadata(GachaPlusCommand.META_CHEST)){
      return true;
    }
    return false;
  }

  /**
   * get gacha name in punch.
   * @param Player Player
   * @return String gacha name
   */
  public static String getGachaNameInPunch(Player player){
    for(MetadataValue mv:player.getMetadata(GachaPlusCommand.META_CHEST)) {
      return mv.asString();
    }
    return null;
  }

  /**
   * Set punch processing.
   * @param Player Player
   * @param GachaPlus Gacha
   * @param String Gacha Name
   */
  public static void setPunch(Player player, GachaPlus gacha, String gachaName){
    removePunch(player, gacha);
    player.setMetadata(GachaPlusCommand.META_CHEST, new FixedMetadataValue(gacha, gachaName));
  }

  /**
   * Remove punch processing.
   * @param Player Player
   * @param GachaPlus Gacha
   */
  public static void removePunch(Player player, GachaPlus gacha){
    player.removeMetadata(GachaPlusCommand.META_CHEST, gacha);
  }

  /**
   * Send message to player
   * @param CommandSender CommandSender
   * @param String message
   */
  public static void sendMessage(CommandSender sender, String message){
    sender.sendMessage((Object)ChatColor.DARK_RED + "[GachaPlus]" + " " + (Object)ChatColor.RED + message);
  }

  /**
   * Scan Format
   * @param String format
   * @param String str
   * @return String value
   */
  public static String scanf(String format, String str) {
    return StringUtils.difference(format, str);
  }

  public static ArrayList<Player> getTarget(Plugin gacha, String selector) {
    return getTarget(gacha, selector, null);
  }

  public static ArrayList<Player> getTarget(Plugin gacha, String selector, CommandSender sender) {

    ArrayList<Player> list = new ArrayList<Player>();
    if(!selector.startsWith("@")) {
      Player p = gacha.getServer().getPlayer(selector);
      if(p != null) {
        list.add(p);
      }
    } else {
      switch(selector) {
        case "@a":
          list.addAll(gacha.getServer().getOnlinePlayers());
          break;

        case "@p":
          if((sender instanceof Player) || (sender instanceof BlockCommandSender)) {
            Location senderLocation = null;
            if(sender instanceof Player) {
              Player sp = (Player) sender;
              senderLocation = sp.getLocation();
            } else if(sender instanceof BlockCommandSender) {
              BlockCommandSender sb = (BlockCommandSender) sender;
              senderLocation = sb.getBlock().getLocation();
            }
            if(senderLocation != null) {
              list.add(getNearestPlayerByLocation(senderLocation));
            }
          }
          break;

        case "@s":
          if(sender instanceof Player) {
            list.add((Player) sender);
          }
          break;

        case "@r":
          ArrayList<Player> curPlayerList = new ArrayList<Player>();
          for (Player p : Bukkit.getOnlinePlayers()) {
            curPlayerList.add(p);
          }
          Random random = new Random();
          list.add(curPlayerList.get(random.nextInt(curPlayerList.size())));
          break;
      }
    }
    return list;
  }

  public static Player getNearestPlayerByLocation(Location l) {
    double lastDistance = Double.MAX_VALUE;
    Player latestPlayer = null;
    for(Player p : l.getWorld().getPlayers()) {
      double distance = p.getLocation().distance(l);
      if(distance < lastDistance) {
        lastDistance = distance;
        latestPlayer = p;
      }
    }
    return latestPlayer;
  }
}
