package com.nekeki.kawaiiparkour;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandKawaiiParkour implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //This returns plugin info
        sender.sendMessage(ChatColor.DARK_PURPLE + "KawaiiParkour by: " + ChatColor.LIGHT_PURPLE + "nekeki" + ChatColor.DARK_PURPLE + ". Version: " + ChatColor.LIGHT_PURPLE + "1.0");
        sender.sendMessage(ChatColor.DARK_PURPLE + "Get more info at: " + ChatColor.LIGHT_PURPLE + "nekeki.com/kawaiiparkour");
        return true;
    }

}

