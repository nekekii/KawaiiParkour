package com.nekeki.kawaiiparkour;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class CommandParkour implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            return false;
        }

        switch (args[0].toLowerCase()) {
            case "reset":
                this.resetAction(sender);
                break;
            case "checkpoint":
                this.checkpointAction(sender);
                break;
            case "info":
                this.infoAction(sender, args);
                break;
            case "list":
                this.listAction(sender);
                break;
            case "create":
                this.createAction(sender, args);
                break;
            case "cancel":
                this.cancelAction(sender);
                break;
            case "delete":
                this.deleteAction(sender, args);
                break;
            case "confirm":
                this.confirmAction(sender);
                break;
            case "cleartimes":
                this.clearTimesAction(sender, args);
                break;
            case "cleartoptime":
                this.clearTopTimeAction(sender, args);
                break;
            case "option":
                this.optionAction(sender, args);
                break;
            case "help":
                this.helpAction();
                break;
            default:
                this.helpAction();
        }

        return true;
    }

    private void helpAction(CommandSender sender) {
        sender.sendMessage(ChatColor.DARK_PURPLE + "KawaiiParkour help - v1.0");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour checkpoint" + ChatColor.DARK_PURPLE + " - Puts you to your last checkpoint.");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour reset" + ChatColor.DARK_PURPLE + " - Puts you to the beginning of the parkour.");
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour info <name>" + ChatColor.LIGHT_PURPLE + " - Shows info about a course.");
        if(sender.hasPermission("KawaiiParkour.list") || sender.hasPermission("KawaiiParkour.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour list" + ChatColor.LIGHT_PURPLE + " - Lists all courses.");
        }
        if(sender.hasPermission("KawaiiParkour.create") || sender.hasPermission("KawaiiParkour.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour create <name>" + ChatColor.DARK_PURPLE + " - Sets up a new parkour course with given name.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cancel" + ChatColor.DARK_PURPLE + " - Cancels parkour creation process.");
        }
        if(sender.hasPermission("KawaiiParkour.delete") || sender.hasPermission("KawaiiParkour.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour delete <name>" + ChatColor.DARK_PURPLE + " - Deletes a parkour with given name.");
        }
        if(sender.hasPermission("KawaiiParkour.cleartimes") || sender.hasPermission("KawaiiParkour.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cleartimes <name>" + ChatColor.DARK_PURPLE + " - Clears all times for given course.");
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour cleartoptime <name>" + ChatColor.DARK_PURPLE + " - Clears top time for given course");
        }
        if(sender.hasPermission("KawaiiParkour.option") || sender.hasPermission("KawaiiParkour.admin")) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour option <option>" + ChatColor.DARK_PURPLE + " - Toggles given option.");
        }
        sender.sendMessage(ChatColor.LIGHT_PURPLE + "/parkour help" + ChatColor.DARK_PURPLE + " - Shows this page.");
    }

    private void resetAction(CommandSender sender) {
        //Sends someone to parkour start
        if(sender instanceof Player) {
            TimeManager.playerSetup((Player) sender);
            TimeManager.commandReset((Player) sender);
        } else {
            sender.sendMessage("You need to be a player to use this command, silly!");
        }
    }

    private void checkpointAction(CommandSender sender) {
        //Sends someone to their last checkpoint
        if(sender instanceof Player) {
            TimeManager.playerSetup((Player) sender);
            TimeManager.commandCheckpoint((Player) sender);
        } else {
            sender.sendMessage("You need to be a player to use this command, silly!");
        }
    }

    private void infoAction(CommandSender sender, String[] args) {
        //Sends info about parkour course
        if(args.length > 1) {
            TimeManager.sendParkourInfo(sender, args[1]);
        } else {
            sender.sendMessage(ChatColor.RED + "Please enter a course name to show info about!");
        }
    }

    private void listAction(CommandSender sender) {
        //Sends list of all parkour courses
        if(sender.hasPermission("KawaiiParkour.list") || sender.hasPermission("KawaiiParkour.admin")) {
            TimeManager.sendParkourList(sender);
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void createAction(CommandSender sender, String[] args) {
        //Creates a parkour course
        if(sender.hasPermission("KawaiiParkour.create") || sender.hasPermission("KawaiiParkour.admin")) {
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
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void cancelAction(CommandSender sender) {
        //Cancels parkour course creation process
        if(sender instanceof Player) {
            TimeManager.playerSetup((Player) sender);
            TimeManager.cancelSetup((Player) sender);
        } else {
            sender.sendMessage("You need to be a player to use this command, silly!");
        }
    }

    private void deleteAction(CommandSender sender, String[] args) {
        //Deletes parkour course
        if(sender.hasPermission("KawaiiParkour.delete") || sender.hasPermission("KawaiiParkour.admin")) {
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
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void confirmAction(CommandSender sender) {
        //Confirms parkour deletion
        if(sender.hasPermission("KawaiiParkour.delete") || sender.hasPermission("KawaiiParkour.admin")) {
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
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void clearTimesAction(CommandSender sender, String[] args) {
        //Clears all times from course
        if(sender.hasPermission("KawaiiParkour.cleartimes") || sender.hasPermission("KawaiiParkour.admin")) {
            if(args.length > 1) {
                TimeManager.deleteAllTimes(args[1]);
                sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted all personal bests and record time for " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + ".");
            } else {
                sender.sendMessage(ChatColor.RED + "Please enter a course name to clear all times from!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void clearTopTimeAction(CommandSender sender, String[] args) {
        //Clears record time from course
        if(sender.hasPermission("KawaiiParkour.cleartimes") || sender.hasPermission("KawaiiParkour.admin")) {
            if(args.length > 1) {
                TimeManager.deleteRecordTime(args[1]);
                sender.sendMessage(ChatColor.DARK_PURPLE + "Deleted record time for " + ChatColor.LIGHT_PURPLE + args[1] + ChatColor.DARK_PURPLE + ".");
            } else {
                sender.sendMessage(ChatColor.RED + "Please enter a course name to clear top time from!");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }

    private void optionAction(CommandSender sender, String[] args) {
        //Changes plugin options
        if(sender.hasPermission("KawaiiParkour.option") || sender.hasPermission("KawaiiParkour.admin")) {
            if(args.length > 1) {
                ConfigurablesExecutor configurablesExecutor = new ConfigurablesExecutor();
                configurablesExecutor.setOption(sender, args[1]);
            } else {
                sender.sendMessage(ChatColor.RED + "Please include an option! Current options: firework, sound");
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Heck! I'm sorry. You don't have permission to run this command.");
        }
    }
}
