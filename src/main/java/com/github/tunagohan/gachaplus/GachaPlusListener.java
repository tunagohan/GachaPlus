package com.github.tunagohan.gachaplus;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Random;
import java.util.regex.Pattern;

/*
 * GachaListener
 * @license    LGPLv3
 * @copyright  Copyright com.github.tunagohan 2021
 * @author     tunagohan
 */
public class GachaPlusListener implements Listener{
  private GachaPlus gacha;

  /**
   * Constructor of GachaListener.
   */
  public GachaPlusListener(GachaPlus gacha) {
    try{
      this.gacha = gacha;
    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * On sign change
   * @param SignChangeEvent event
   */
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onSignChange(SignChangeEvent event) {
    try {
      Economy economy = GachaPlus.getEconomy();

      if(!event.getLine(0).toLowerCase().equals("[gacha]")) {
        return;
      }

      if(!event.getPlayer().hasPermission("gachaplus.create")) {
        return;
      }

      Location signLoc = event.getBlock().getLocation();
      if(gacha.getDatabase().isGacha(signLoc)) {
        event.setCancelled(true);
        GachaPlusUtility.sendMessage(event.getPlayer(), "It is already registered. To continue, please delete first.");
        return;
      }

      String gachaName = event.getLine(1);
      String gachaDisplayName = event.getLine(2);
      Integer gachaPrice = Integer.parseInt(Objects.requireNonNull(event.getLine(3)));
      String worldName = signLoc.getWorld().getName();
      Integer x = signLoc.getBlockX();
      Integer y = signLoc.getBlockY();
      Integer z = signLoc.getBlockZ();
      Pattern p = Pattern.compile("^[0-9a-zA-Z_]+$");
      if(!p.matcher(gachaName).find()) {
        event.setCancelled(true);
        GachaPlusUtility.sendMessage(event.getPlayer(), "Please enter the second line of the signboard with one-byte alphanumeric underscore.");
        return;
      }

      Integer gachaId = gacha.getDatabase().getGacha(gachaName, gachaDisplayName, gachaPrice, worldName, x, y, z);
      if(gachaId == null) {
        event.setCancelled(true);
        throw new Exception("Can not get gacha. gachaName=" + gachaName);
      }

      event.setLine(0, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("sign-line1-prefix") + gachaDisplayName));
      event.setLine(1, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("sign-line2-prefix") + gachaName));
      event.setLine(2, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("sign-line3")));
      event.setLine(3, ChatColor.translateAlternateColorCodes('&', economy.format(gachaPrice)));

    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * On player interact
   * @param PlayerInteractEvent event
   */
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerInteract(PlayerInteractEvent event) {
    if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
      return;
    }
    Block clickedBlock = event.getClickedBlock();
    BlockData data = clickedBlock.getBlockData();
    if (data instanceof Sign || data instanceof WallSign) {
      signProc(event);
    }else if(data.getMaterial().equals(Material.CHEST)) {
      chestProc(event);
    }
  }

  /**
   * Sign process.
   * @param PlayerInteractEvent event
   */
  private void signProc(PlayerInteractEvent event) {
    try {
      Sign sign = (Sign) event.getClickedBlock().getState();
      Player p = event.getPlayer();

      Economy economy = GachaPlus.getEconomy();

      Location signLoc = sign.getLocation();
      if(!gacha.getDatabase().isGacha(signLoc)) {
        return;
      }
      event.setCancelled(true);

      String gachaName = sign.getLine(1);
      Integer gachaPrice = gacha.getDatabase().getGachaPrice(gachaName);

      if(gachaPrice == null) {
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', "gachaPriceがnullっぽい"));
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', gachaName));
        return;
      }

      if(!economy.has(p, gachaPrice)) {
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', "お金が足りません"));
        return;
      }

      EconomyResponse r = economy.withdrawPlayer(p, gachaPrice);
      if(!r.transactionSuccess()) {
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', r.errorMessage));
        return;
      }

      gacha.getCommand();

      Chest chest = gacha.getDatabase().getGachaChest(signLoc);
      if(chest == null) {
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("not-found-chest1")));
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("not-found-chest2")));
        return;
      }

      p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - 1);

      Inventory iv = chest.getInventory();
      int pick = new Random().nextInt(iv.getSize());
      ItemStack pickItem = iv.getItem(pick);
      if(pickItem == null) {
        GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("not-found-pick")));
        return;
      }

      ItemStack sendItem = pickItem.clone();
      p.getInventory().addItem(sendItem);
      GachaPlusUtility.sendMessage(p, ChatColor.translateAlternateColorCodes('&', gacha.getConfig().getString("found-pick")));

    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }

  /**
   * Chest process.
   * @param PlayerInteractEvent event
   */
  private void chestProc(PlayerInteractEvent event) {
    try {
      Player p = event.getPlayer();
      if(!p.getType().equals(EntityType.PLAYER)){
        return;
      }

      if(!event.getClickedBlock().getType().equals(Material.CHEST)) {
        return;
      }

      if(!GachaPlusUtility.isInPunch(p)) {
        return;
      } else {
        event.setCancelled(true);
      }

      String gachaName = GachaPlusUtility.getGachaNameInPunch(p);
      GachaPlusUtility.removePunch(p, gacha);
      if(gachaName == null) {
        return;
      }

      Location loc = event.getClickedBlock().getLocation();
      if(gacha.getDatabase().updateGachaChest(gachaName, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ())) {
        GachaPlusUtility.sendMessage(p, "Updated. gacha_name=" + gachaName);
        return;
      }

    } catch (Exception e){
      GachaPlusUtility.logStackTrace(e);
    }
  }
}
