package com.miketheshadow.ouchmybones;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InteractEvents implements Listener {

    @EventHandler
    public void playerUserItemEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(!ConstantAilments.injuredPlayers.containsKey(event.getPlayer().getUniqueId().toString())) return;
        List<InjuryType> injuries = ConstantAilments.injuredPlayers.get(player.getUniqueId().toString());
        if((injuries.contains(InjuryType.MISSING_ARM_RIGHT) || injuries.contains(InjuryType.BROKEN_ARM_RIGHT)) && !player.getInventory().getItemInMainHand().getType().isAir()) {
            player.dropItem(true);
            player.sendMessage(OuchMyBones.configuration.getString("broken_arm_message_right"));
            event.setCancelled(true);
        }
        if(injuries.contains(InjuryType.MISSING_ARM_LEFT) || injuries.contains(InjuryType.BROKEN_ARM_LEFT)) {
            ItemStack stack = player.getInventory().getItemInOffHand();
            if(stack.getType().isAir()) return;
            player.getWorld().dropItem(player.getLocation(),stack);
            player.getInventory().remove(stack);
            player.sendMessage(OuchMyBones.configuration.getString("broken_arm_message_left"));
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void playerPlaceBlockEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getInventory().getItemInMainHand().getType().isBlock() && !player.getInventory().getItemInMainHand().getType().isAir()) {
            if(ConstantAilments.injuredPlayers.containsKey(player.getUniqueId().toString())
                    && ConstantAilments.injuredPlayers.get(player.getUniqueId().toString()).contains(InjuryType.MAIMED)) {
                event.setCancelled(true);
                player.sendMessage(OuchMyBones.configuration.getString("maimed_reminder"));
            }
        }
    }

}
