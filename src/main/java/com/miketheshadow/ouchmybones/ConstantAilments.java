package com.miketheshadow.ouchmybones;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.List;

public class ConstantAilments extends BukkitRunnable {

    public static HashMap<String, List<InjuryType>> injuredPlayers = new HashMap<>();

    @Override
    public void run() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            if(injuredPlayers.containsKey(player.getUniqueId().toString())) {
                List<InjuryType> injuries = injuredPlayers.get(player.getUniqueId().toString());
                    if(injuries.contains(InjuryType.BLINDNESS)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS,200,0,false,false,false));
                    }
                    if(injuries.contains(InjuryType.ONE_LEGGED)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,200,2,false,false,false));
                    }
                    else if(injuries.contains(InjuryType.LEGLESS)) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,200,6,false,false,false));
                    }
            }
        }
    }
}
