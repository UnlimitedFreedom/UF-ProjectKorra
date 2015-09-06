package com.projectkorra.projectkorra.firebending;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AvatarState;
import com.projectkorra.projectkorra.ability.StockAbility;
import com.projectkorra.projectkorra.ability.api.CoreAbility;
import com.projectkorra.projectkorra.util.Flight;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.waterbending.WaterMethods;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class FireJet extends CoreAbility {

    private static double defaultfactor = config.get().getDouble("Abilities.Fire.FireJet.Speed");
    private static long defaultduration = config.get().getLong("Abilities.Fire.FireJet.Duration");
    private static boolean isToggle = config.get().getBoolean("Abilities.Fire.FireJet.IsAvatarStateToggle");

    private Player player;
    private long time;
    private long duration = defaultduration;
    private double factor = defaultfactor;

    public FireJet(Player player) {
        /* Initial Checks */
        if (containsPlayer(player, FireJet.class)) {
            getAbilityFromPlayer(player, FireJet.class).remove();
            return;
        }
        BendingPlayer bPlayer = GeneralMethods.getBendingPlayer(player.getName());
        if (bPlayer.isOnCooldown("FireJet")) {
            return;
        }
        /* End Initial Checks */
        reloadVariables();

        factor = FireMethods.getFirebendingDayAugment(defaultfactor, player.getWorld());
        Block block = player.getLocation().getBlock();
        if (FireStream.isIgnitable(player, block) || block.getType() == Material.AIR || AvatarState.isAvatarState(player)) {
            player.setVelocity(player.getEyeLocation().getDirection().clone().normalize().multiply(factor));
            block.setType(Material.FIRE);
            this.player = player;
            // canfly = player.getAllowFlight();
            new Flight(player);
            player.setAllowFlight(true);
            time = System.currentTimeMillis();
            // timers.put(player, time);
            //instances.put(player, this);
            putInstance(player, this);
            bPlayer.addCooldown("FireJet", config.get().getLong("Abilities.Fire.FireJet.Cooldown"));
        }

    }

    public static boolean checkTemporaryImmunity(Player player) {
        if (containsPlayer(player, FireJet.class)) {
            return true;
        }
        return false;
    }

    public static ArrayList<Player> getPlayers() {
        ArrayList<Player> players = new ArrayList<Player>();
        for (Integer id : getInstances(StockAbility.FireJet).keySet()) {
            players.add(getAbility(id).getPlayer());
        }
        return players;
    }

    public long getDuration() {
        return duration;
    }

    public double getFactor() {
        return factor;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public StockAbility getStockAbility() {
        return StockAbility.FireJet;
    }

    @Override
    public boolean progress() {
        if (player.isDead() || !player.isOnline()) {
            // player.setAllowFlight(canfly);
            remove();
            return false;
        }
        if ((WaterMethods.isWater(player.getLocation().getBlock()) || System.currentTimeMillis() > time + duration) && (!AvatarState.isAvatarState(player) || !isToggle)) {
            // player.setAllowFlight(canfly);
            remove();
        } else {
            if (GeneralMethods.rand.nextInt(2) == 0) {
                FireMethods.playFirebendingSound(player.getLocation());
            }
            ParticleEffect.FLAME.display(player.getLocation(), 0.6F, 0.6F, 0.6F, 0, 20);
            ParticleEffect.SMOKE.display(player.getLocation(), 0.6F, 0.6F, 0.6F, 0, 20);
            double timefactor;
            if (AvatarState.isAvatarState(player) && isToggle) {
                timefactor = 1;
            } else {
                timefactor = 1 - ((double) (System.currentTimeMillis() - time)) / (2.0 * duration);
            }
            Vector velocity = player.getEyeLocation().getDirection().clone().normalize().multiply(factor * timefactor);
            // Vector velocity = player.getVelocity().clone();
            // velocity.add(player.getEyeLocation().getDirection().clone()
            // .normalize().multiply(factor * timefactor));
            player.setVelocity(velocity);
            player.setFallDistance(0);
        }
        return true;
    }

    @Override
    public void reloadVariables() {
        defaultfactor = config.get().getDouble("Abilities.Fire.FireJet.Speed");
        defaultduration = config.get().getLong("Abilities.Fire.FireJet.Duration");
        isToggle = config.get().getBoolean("Abilities.Fire.FireJet.IsAvatarStateToggle");
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

}
