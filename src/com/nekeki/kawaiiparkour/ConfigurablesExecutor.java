package com.nekeki.kawaiiparkour;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

public class ConfigurablesExecutor {

    static Plugin plugin = KawaiiParkour.getPlugin(KawaiiParkour.class);

    public void firework(Player player) {
        if (plugin.getConfig().getBoolean("doFireworks")) {

            for (int i = 1; i < 3; i++) {
                Firework firework = (Firework) player.getLocation().getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.setPower(i);
                meta.addEffect(FireworkEffect.builder().withColor(Color.FUCHSIA).flicker(true).build());
                firework.setFireworkMeta(meta);
            }

        }
    }

    public void sound(Player player) {

        if (plugin.getConfig().getBoolean("doSounds")) {
            player.playSound(player.getLocation(), Sound.ENTITY_CAT_AMBIENT, 1, 1);
        }

    }

    public void setOption(CommandSender sender, String option) {
        if (option.equalsIgnoreCase("firework")) {
            boolean doFirework = plugin.getConfig().getBoolean("doFireworks");
            doFirework = !doFirework;
            plugin.getConfig().set("doFireworks", doFirework);
            sender.sendMessage(ChatColor.DARK_PURPLE + "Set fireworks to: " + ChatColor.LIGHT_PURPLE + doFirework);
        } else if (option.toLowerCase().equals("sound")) {
            boolean doSounds = plugin.getConfig().getBoolean("doSounds");
            doSounds = !doSounds;
            plugin.getConfig().set("doSounds", doSounds);
            sender.sendMessage(ChatColor.DARK_PURPLE + "Set sounds to: " + ChatColor.LIGHT_PURPLE + doSounds);
        } else {
            sender.sendMessage(ChatColor.RED + "That is not a valid option! Current options: firework, sound");
        }

    }

}
