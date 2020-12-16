package com.miketheshadow.ouchmybones;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class InjureCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(ChatColor.YELLOW + "/injure [add/remove] [player] [type] [minutes]");
            sender.sendMessage(ChatColor.GRAY + "If minutes is not specified then it is assumed to be permanent");
            sender.sendMessage(ChatColor.YELLOW + "/injure list");
            sender.sendMessage(ChatColor.GRAY + "Lists the current injuries you can give");
            return true;
        }
        if(args[0].equals("list")) {
            sender.sendMessage(ChatColor.YELLOW + "BLINDNESS,ONE_LEGGED,LEGLESS,BROKEN_LEG,\n" +
                    "MISSING_ARM_RIGHT,MISSING_ARM_LEFT, BROKEN_ARM_RIGHT,BROKEN_ARM_LEFT,\n" +
                    "BROKEN_BACK,HEADACHE,CONCUSSION,MAIMED");
            return true;
        }
        if(args.length < 3) return false;
        Player player = Bukkit.getPlayer(args[1]);
        InjuryType type = InjuryType.valueOf(args[2]);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Unable to find player " + args[1]);
            return true;
        }
        if(args[0].equals("add")) {
            if(type == InjuryType.BLINDNESS && args.length == 3) {
                addConstantAilment(player,type);
                player.sendMessage(OuchMyBones.configuration.getString("blindness_activated"));
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            else if(type == InjuryType.BLINDNESS) {
                int duration = Integer.parseInt(args[3]);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,(duration * 60) * 20,0,false,false,false));
                player.sendMessage(OuchMyBones.configuration.getString("blindness_activated"));
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            else if(type == InjuryType.BROKEN_ARM_LEFT || type == InjuryType.BROKEN_ARM_RIGHT) {
                if(args.length != 4) {
                    sender.sendMessage(ChatColor.RED + "Please add a duration! If it's meant to be permanent use MISSING instead");
                    return true;
                }
                int duration = Integer.parseInt(args[3]);
                addConstantAilment(player, type);
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())) return;
                        ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).remove(type);
                    }
                }).runTaskLater(OuchMyBones.INSTANCE,(duration * 60) * 20);
                if(type == InjuryType.BROKEN_ARM_LEFT) player.sendMessage(OuchMyBones.configuration.getString("broken_arm_message_left"));
                if(type == InjuryType.BROKEN_ARM_RIGHT) player.sendMessage(OuchMyBones.configuration.getString("broken_arm_message_right"));
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
                return true;
            }
            else if(type == InjuryType.MISSING_ARM_LEFT || type == InjuryType.MISSING_ARM_RIGHT) {
                addConstantAilment(player, type);
                if(type == InjuryType.MISSING_ARM_LEFT) player.sendMessage(OuchMyBones.configuration.getString("missing_arm_message_left"));
                if(type == InjuryType.MISSING_ARM_RIGHT) player.sendMessage(OuchMyBones.configuration.getString("missing_arm_message_right"));
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
                return true;
            }
            else if(type == InjuryType.CONCUSSION || type == InjuryType.HEADACHE) {
                if(args.length != 4) {
                    sender.sendMessage(ChatColor.RED + "Please add a duration! This debuff isn't meant to be permanent!");
                    return true;
                }
                int duration = Integer.parseInt(args[3]);
                int queueDelay = 1;
                String reminder = "";
                if(type == InjuryType.CONCUSSION) {
                    reminder = OuchMyBones.configuration.getString("concussion_reminder");
                    player.sendMessage(reminder);
                } else {
                    reminder = OuchMyBones.configuration.getString("headache_reminder");
                    player.sendMessage(reminder);
                    queueDelay = 5;
                }
                int totalLoops = (duration / queueDelay);
                if(addConstantAilment(player,type)) {
                    sender.sendMessage(ChatColor.RED + "Player already has this ailment!");
                    return true;
                }
                for(int i = 0; i < totalLoops - 1;i++) {
                    String finalReminder = reminder;
                    (new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(!ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())) return;
                            if(ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())
                                    && ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).contains(type)) {
                                player.addPotionEffect(new PotionEffect(
                                        PotionEffectType.CONFUSION
                                        ,OuchMyBones.configuration.getInt("dizzy_duration_seconds") * 20
                                        ,1,false,false,false));
                                player.sendMessage(finalReminder);

                            }
                        }
                    }).runTaskLater(OuchMyBones.INSTANCE,((queueDelay * i) * 60) * 20);
                }
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            else if(type == InjuryType.MAIMED) {
                addConstantAilment(player,type);
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())) return;
                        ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).remove(type);
                    }
                }).runTaskLater(OuchMyBones.INSTANCE,2400);
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            else if(type == InjuryType.LEGLESS || type == InjuryType.ONE_LEGGED) {
                addConstantAilment(player,type);
                if(type == InjuryType.LEGLESS) player.sendMessage(OuchMyBones.configuration.getString("missing_both_legs_message"));
                if(type == InjuryType.ONE_LEGGED) player.sendMessage(OuchMyBones.configuration.getString("missing_leg_message"));
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            else if(type == InjuryType.BROKEN_LEG) {
                addConstantAilment(player,type);
                if(args.length != 4) {
                    sender.sendMessage(ChatColor.RED + "Please add a duration! This debuff isn't meant to be permanent!");
                    return true;
                }
                int duration = Integer.parseInt(args[3]);
                (new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(!ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())) return;
                        ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).remove(type);
                    }
                }).runTaskLater(OuchMyBones.INSTANCE,(duration * 60) * 20);
                sender.sendMessage(ChatColor.GREEN + "Added" + type + " to " + player.getDisplayName() + "!");
            }
            return true;
        }
        if(args[0].equals("remove")) {
            if(ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())
                    && ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).contains(type)) {
                ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).removeIf(t -> t == type);
                sender.sendMessage("Removed ailment: " + type);
                DataStorage.save();
            }
            else {
                sender.sendMessage(ChatColor.DARK_GRAY + "This player either doesn't have this effect or it is removable with /effect clear [player] [effect]");
            }
        }
        return true;
    }

    boolean addConstantAilment(Player player, InjuryType type) {
        if(ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())) {
            if(ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).contains(type)) return true;
            ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).add(type);
        } else {
            List<InjuryType> types = new ArrayList<>();
            types.add(type);
            ConstantAilments.injuredPlayers.put(player.getUniqueId().toString(),types);
        }
        DataStorage.save();
        return false;
    }
}
