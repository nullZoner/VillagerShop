package it.arena.nullzoner.listeners;

import it.arena.nullzoner.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class ListenerVillagerShop implements Listener {

    private ArrayList<String> openShopPlayer = new ArrayList<>();
    private Main main = Main.getInstance();

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        try {
           if (event.getEntity().getType() == EntityType.VILLAGER)
               if (!main.getConfig().getString(ChatColor.stripColor(event.getEntity().getName())).equalsIgnoreCase("null"))
                    event.setCancelled(true);
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event){
        try {
            if (event.getRightClicked().getType() == EntityType.VILLAGER)
                if (!main.getConfig().getString(ChatColor.stripColor(event.getRightClicked().getName())).equalsIgnoreCase("null")) {
                    String nameShop = ChatColor.stripColor(event.getRightClicked().getName());

                    event.setCancelled(true);

                    Inventory inventory = Bukkit.createInventory(null, 9 * 6, ChatColor.stripColor(event.getRightClicked().getName()));

                    if (main.getConfig().getInt(nameShop + ".MaxID") == 0) {
                        main.getConfig().set(nameShop + ".MaxID", 0);
                        main.saveConfig();
                    }

                    int x = main.getConfig().getInt(nameShop + ".MaxID");

                    openShopPlayer.add(event.getPlayer().getName());

                    for(int i=0; i < x; i ++) {
                        ItemStack itemStack = new ItemStack(Material.getMaterial(main.getConfig().getInt(nameShop + "."+ i + ".id")), main.getConfig().getInt(nameShop + "." + i + ".amount"), (byte) main.getConfig().getInt(nameShop + "." + i + ".data"));
                        ItemMeta itemMeta = itemStack.getItemMeta();

                        if (Objects.equals(main.getConfig().getString(nameShop + "." + i + ".option"), "buy")) {
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(ChatColor.GREEN.toString() + ChatColor.BOLD.toString() + "COMPRA");
                            lore.add(ChatColor.GRAY + "Prezzo: " + ChatColor.YELLOW + String.valueOf(main.getConfig().getInt(nameShop + "." + i + ".price")));
                            itemMeta.setLore(lore);
                        } else {
                            ArrayList<String> lore = new ArrayList<>();
                            lore.add(ChatColor.RED.toString() + ChatColor.BOLD.toString() + "VENDI");
                            lore.add(ChatColor.GRAY + "Prezzo: " + ChatColor.YELLOW + String.valueOf(main.getConfig().getInt(nameShop + "." + i + ".price")));
                            itemMeta.setLore(lore);
                        }
                        itemStack.setItemMeta(itemMeta);

                        inventory.setItem(i, itemStack);
                    }
                    event.getPlayer().openInventory(inventory);
                }
        } catch (NullPointerException ignored) {}
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (openShopPlayer.contains(event.getPlayer().getName()))
            openShopPlayer.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try {
            if (openShopPlayer.contains(event.getWhoClicked().getName())) {
                event.setCancelled(true);

                int price = main.getConfig().getInt(event.getInventory().getName() + "." + event.getSlot() + ".price");
                int amount = main.getConfig().getInt(event.getInventory().getName() + "." + event.getSlot() + ".amount");
                int id = main.getConfig().getInt(event.getInventory().getName() + "." + event.getSlot() + ".id");
                int data = main.getConfig().getInt(event.getInventory().getName() + "." + event.getSlot() + ".data");

                if (Objects.equals(main.getConfig().getString(event.getInventory().getName() + "." + event.getSlot() + ".option"), "buy")) {
                    if (main.economy.has((OfflinePlayer) event.getWhoClicked(), (double) price)) {
                        if (event.getInventory().getContents().length > 36) {
                            main.economy.withdrawPlayer((OfflinePlayer) event.getWhoClicked(), (double) price);
                            event.getWhoClicked().sendMessage(ChatColor.GREEN + "$" + String.valueOf(price) + " sono stati rimossi dal tuo account.");

                            event.getWhoClicked().getInventory().addItem(new ItemStack(Material.getMaterial(id), amount, (byte) data));
                            event.getWhoClicked().closeInventory();
                        } else {
                            event.getWhoClicked().sendMessage(ChatColor.RED + "Svuota l'inventario.");
                            event.getWhoClicked().closeInventory();
                        }
                    } else {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "Non hai abbastanza soldi.");
                        event.getWhoClicked().closeInventory();
                    }
                } else {
                    if (event.getWhoClicked().getInventory().contains(new ItemStack(Material.getMaterial(id), amount, (byte) data))) {
                        main.economy.depositPlayer((OfflinePlayer) event.getWhoClicked(), main.getConfig().getInt(event.getInventory().getName() + "." + event.getSlot() + ".price"));
                        event.getWhoClicked().sendMessage(ChatColor.GREEN + "$" + String.valueOf(price) + " sono stati aggiunti al tuo account.");

                        event.getWhoClicked().getInventory().remove(new ItemStack(Material.getMaterial(id), amount, (byte) data));
                        event.getWhoClicked().closeInventory();
                    } else {
                        event.getWhoClicked().sendMessage(ChatColor.RED + "Non hai l'item richiesto!");
                        event.getWhoClicked().closeInventory();
                    }
                }
            }
        } catch (NullPointerException ignored) {}
    }
}
