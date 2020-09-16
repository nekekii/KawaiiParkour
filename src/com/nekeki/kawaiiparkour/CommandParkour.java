package com.nekeki.kawaiiparkour;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.sql.Time;

public class CommandParkour implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length > 0) {
            //TODO: Fix commands order
            if(args[0].toLowerCase().equals("create")) {
                if(sender instanceof Player) {
                    if(args.length > 1) {
                        if(args[1].equals("null")) {
                            sender.sendMessage(ChatColor.RED + "You can't name your course that, sorry!");
                        } else {
                            sender.sendMessage(ChatColor.DARK_PURPLE + "Started parkour creation for " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + ". Please run through your course. Iron Plate = Start, Stone Plate = Checkpoint, Gold Plate = Finish");
                            TimeManager.playerSetup((Player) sender);
                            TimeManager.parkourSetup((Player) sender, args[1]);
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please include a name for the parkour!");
                    }
                } else {
                    sender.sendMessage("You need to be a player to use this command, silly!");
                }
            } else if(args[0].toLowerCase().equals("cancel")) {
                if(sender instanceof Player) {
                    TimeManager.playerSetup((Player) sender);
                    TimeManager.cancelSetup((Player) sender);
                } else {
                    sender.sendMessage("You need to be a player to use this command, silly!");
                }
            } else if(args[0].toLowerCase().equals("checkpoint")) {
                if(sender instanceof Player) {
                    TimeManager.playerSetup((Player) sender);
                    TimeManager.commandCheckpoint((Player) sender);
                } else {
                    sender.sendMessage("You need to be a player to use this command, silly!");
                }
            } else if(args[0].toLowerCase().equals("reset")) {
                if(sender instanceof Player) {
                    TimeManager.playerSetup((Player) sender);
                    TimeManager.commandReset((Player) sender);
                } else {
                    sender.sendMessage("You need to be a player to use this command, silly!");
                }
            } else if(args[0].toLowerCase().equals("delete")) {
                if(args.length > 1) {
                    if(sender instanceof Player) {
                        TimeManager.playerSetup((Player) sender);
                        if(TimeManager.preDeleteParkour(args[1], (Player) sender)) {
                            sender.sendMessage(ChatColor.DARK_PURPLE + "Are you sure you want to delete " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + "? This will delete all times. Use " + ChatColor.LIGHT_PURPLE + "/parkour confirm " + ChatColor.DARK_PURPLE + "to confirm.");
                        } else {
                            sender.sendMessage(ChatColor.RED + "That is not a parkour! This is case sensitive.");
                        }
                    } else {
                        TimeManager.deleteParkour(args[1], true);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "You need to include a course name to delete!");
                }
            } else if(args[0].toLowerCase().equals("confirm")) {
              if(sender instanceof Player) {
                  TimeManager.playerSetup((Player) sender);
                  if(TimeManager.getToBeDeleted((Player) sender).equals("null")) {
                      sender.sendMessage(ChatColor.RED + "You are not in the deletion process.");
                  } else {
                      TimeManager.deleteParkour(TimeManager.getToBeDeleted((Player) sender), false);
                      sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted parkour " + ChatColor.LIGHT_PURPLE + TimeManager.getToBeDeleted((Player) sender) + ChatColor.DARK_PURPLE + ".");
                      TimeManager.resetToBeDeleted((Player) sender);
                  }
              } else {
                  sender.sendMessage("You need to be a player to use this command, silly!");
              }
            } else if(args[0].toLowerCase().equals("cleartimes")) {
                TimeManager.deleteAllTimes(args[1]);
                sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted all personal bests and record time for " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + ".");
            } else if(args[0].toLowerCase().equals("cleartoptime")) {
                TimeManager.deleteRecordTime(args[1]);
                sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted record time for " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + ".");
            } else if(args[0].toLowerCase().equals("help")) {
                //TODO: Check perms and fix command order
                sender.sendMessage(ChatColor.DARK_PURPLE + "KawaiiParkour help - v1.0");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour checkpoint" + ChatColor.DARK_PURPLE + " - Puts you to your last checkpoint.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour reset" + ChatColor.DARK_PURPLE + " - Puts you to the beginning of the parkour.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour create <name>" + ChatColor.DARK_PURPLE + " - Sets up a new parkour course with given name.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cancel" + ChatColor.DARK_PURPLE + " - Cancels parkour creation process.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour delete <name>" + ChatColor.DARK_PURPLE + " - Deletes a parkour with given name.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour option <option>" + ChatColor.DARK_PURPLE + " - Toggles selected option.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cleartimes <name>" + ChatColor.DARK_PURPLE + " - Clears all times for given course.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cleartoptime <name>" + ChatColor.DARK_PURPLE + " - Clears record time for given course.");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour help" + ChatColor.DARK_PURPLE + " - Shows this page.");
            }
            return true;
        } else {
            return false;
        }

    }

}
