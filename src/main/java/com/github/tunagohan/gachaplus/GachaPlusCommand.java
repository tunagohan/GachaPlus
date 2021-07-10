package com.github.tunagohan.gachaplus;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

/*
 * GachaListener
 * @license    LGPLv3
 * @copyright  Copyright com.github.tunagohan 2021
 * @author     tunagohan
 */
public class GachaPlusCommand {
  private GachaPlus gacha;
  private CommandSender sender;
  private String[] args;
  protected static final String META_CHEST = "gachaplus.chest";

  /**
   * Constructor of GachaCommand.
   * @param gacha GachaPlus gacha
   */
  public GachaPlusCommand(GachaPlus gacha) {
    try{
      this.gacha = gacha;
    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * Initialize
   * @param sender CommandSender CommandSender
   * @param args String[] Argument
   */
  public void initialize(CommandSender sender, String[] args){
    try{
      this.sender = sender;
      this.args = args;
    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * Finalize
   */
  public void finalize() {
    try{
      this.sender = null;
      this.args = null;
    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * Processing of command list.
   * @return boolean true:Success false:Failure
   */
  public boolean list() {
    List<String> glist = gacha.getDatabase().list();
    if(glist.size() <= 0) {
      GachaPlusUtility.sendMessage(sender, "Record not found.");
      return true;
    }

    for(String msg: glist) {
      GachaPlusUtility.sendMessage(sender, msg);
    }
    return true;
  }

  /**
   * Processing of command modify.
   * @return boolean true:Success false:Failure
   */
  public boolean modify() {
    if(args.length != 2) {
      return false;
    }

    if(!(sender instanceof Player)) {
      return false;
    }

    String gachaName = args[1];
    if(gacha.getDatabase().getGacha(gachaName) == null) {
      GachaPlusUtility.sendMessage(sender, "Record not found. gacha_name=" + gachaName);
      return true;
    }
    GachaPlusUtility.setPunch((Player)sender, gacha, gachaName);
    GachaPlusUtility.sendMessage(sender, "Please punching(right click) a chest of gachagacha. gacha_name=" + gachaName);
    return true;
  }

  /**
   * Processing of command delete.
   * @return boolean true:Success false:Failure
   */
  public boolean delete() {
    if(args.length != 2) {
      return false;
    }

    String gachaName = args[1];
    if(gacha.getDatabase().deleteGacha(gachaName)) {
      GachaPlusUtility.sendMessage(sender, "Deleted. gacha_name=" + gachaName);
      return true;
    }
    return false;
  }

  /**
   * Processing of command reload.
   * @return boolean true:Success false:Failure
   */
  public boolean reload() {
    gacha.reloadConfig();
    GachaPlusUtility.sendMessage(sender, "reloaded.");
    return true;
  }

  /**
   * Processing of command enable.
   * @return boolean true:Success false:Failure
   */
  public boolean enable() {
    gacha.onEnable();
    GachaPlusUtility.sendMessage(sender, "enabled.");
    return true;
  }

  /**
   * Processing of command fgdisable.
   * @return boolean true:Success false:Failure
   */
  public boolean disable() {
    gacha.onDisable();
    GachaPlusUtility.sendMessage(sender, "disabled.");
    return true;
  }
}
