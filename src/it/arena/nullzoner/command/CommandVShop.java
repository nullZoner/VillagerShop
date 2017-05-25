package it.arena.nullzoner.command;

import it.arena.nullzoner.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CommandVShop implements CommandExecutor {

    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        Player player = (Player) commandSender;

        if (!(player.hasPermission("permesso.shop"))) {
            player.sendMessage("Non hai il permessso");
            return false;
        }


        if (!(strings.length > 0)) {
            player.sendMessage(("/vshop spawnmob <nameshop>\n/vshop additem <nameshop> <idmaterial> <data> <price> <amount> <buy/sell>").split("\n"));
            return false;
        }


        if (strings[0].equalsIgnoreCase("spawnmob")) {
            if (strings.length > 1) {
                if (String.valueOf(main.getConfig().get(strings[1])).equalsIgnoreCase("null")) {

                    LivingEntity entity = (LivingEntity) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 1000000000, 255, false, false));

                    entity.setCustomName(ChatColor.GREEN + strings[1]);

                    main.getConfig().set(strings[1], "");
                    main.saveConfig();
                } else {
                    player.sendMessage("Nome shop già utilizzato");
                    return true;
                }
            } else {
                return false;
            }

        } else if (strings[0].equalsIgnoreCase("additem")) {
            if (strings.length > 6) {
                int number = main.getConfig().getInt(strings[1] + ".MaxID");

                main.getConfig().set(strings[1] + "." + String.valueOf(number) + ".id", Integer.valueOf(strings[2]));
                main.getConfig().set(strings[1] + "." + String.valueOf(number) + ".data", Integer.valueOf(strings[3]));
                main.getConfig().set(strings[1] + "." + String.valueOf(number) + ".price", Integer.valueOf(strings[4]));
                main.getConfig().set(strings[1] + "." + String.valueOf(number) + ".amount", Integer.valueOf(strings[5]));
                main.getConfig().set(strings[1] + "." + String.valueOf(number) + ".option", strings[6]);
                main.getConfig().set(strings[1] + ".MaxID", number + 1);
                main.saveConfig();
                main.reloadConfig();
                main.reloadConfig();

                player.sendMessage("Fatto.");
            } else {
                player.sendMessage(("/vshop spawnmob <nameshop>\n/vshop additem <nameshop> <idmaterial> <data> <price> <amount> <buy/sell>").split("\n"));
                return false;
            }
        } else {
            player.sendMessage(("/vshop spawnmob <nameshop>\n/vshop additem <nameshop> <idmaterial> <data> <price> <amount> <buy/sell>").split("\n"));
            return true;
        }

        return false;
    }
}
