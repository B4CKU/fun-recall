package com.zhaba.funrecall;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;
import java.util.Set;

public class RecallEffect extends StatusEffect {
    protected RecallEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xb38ef3);
    }

    //without the line below, the applyUpdateEffect would never be called, as far as i know
    @Override
    public boolean canApplyUpdateEffect(int remainingTicks, int level) {
        return true;
    }

    //function below runs every tick while an entity has this effect
    @Override
    public void applyUpdateEffect(LivingEntity entity, int level) {
        //this should make sure the code's only running on the server and only on players.
        //we check if it's a player, because non-players don't have spawn points, nor can they use their keyboards to trigger the recall
        //adding a special case for non-players is currently out of scope and wouldn't really be used in the current mod version
        if (!(entity instanceof ServerPlayerEntity player)) {
            return;
        }

        //TODO: mod icon and description
        //TODO: particles when teleporting

        //TODO: custom sound events and sounds
        //TODO: custom particles

        //TODO: when refactoring the code, look into replacing ServerPlayerEntity with LivingEntity and handle that gracefully, just so we don't crash in case someone actually does /effect give @a fun-recall:recall

        int duration = player.getStatusEffect(this).getDuration();

        playVfx(player, duration);

        if (duration % 15 == 0) {
            playSfx(player);
        }

        if (duration == 1) {
            triggerTeleport(player);
        }
    }

    private void playSfx(ServerPlayerEntity player) {
        //1.2f volume, because this sound is quiet AF
        //the randomness changes the pitch to be less monotone
        //TODO: change the volume when i swap out the sound
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.2f, 0.9f + (player.getRandom().nextFloat() * 0.2f));
    }

    private void playVfx(PlayerEntity player, int duration) {
        //this sorcery lets us access the ServerWorld object rather than ClientWorld you'd get from player.getWorld()
        //and it's necessary, because spawnParticles() isn't available on ClientWorld and it's the only particle function
        //that i've found to send data packets to other clients to sync up the particles
        //TODO: replace this abomination with a cleaner syntax
        ServerWorld world = player.getServer().getWorld(player.getWorld().getRegistryKey());

        //variable that goes from 0 to 20, then snaps back to 0 and goes to 20 again
        int timer = duration % 20;

        //two phase modifier vars, one delayed by half the phase. they dictate the position of the next particle
        double phaseModifier1 = timer * 0.1 * Math.PI;
        double phaseModifier2 = (timer + 10) * 0.1 * Math.PI;

        //variable responsible for changing the size of the particle circle
        double sizeModifier = 0.5 + (duration/100f);

        //(x = sin a, y = cos a) creates a point that traces out a circle when interpolating the 'a' value between 0 and 2*PI
        world.spawnParticles(ParticleTypes.WAX_OFF, player.getX() + (sizeModifier * Math.sin(phaseModifier1)), player.getY(), player.getZ() + (sizeModifier * Math.cos(phaseModifier1)), 0, 0.0d, 1.0d, 0.0d, 10);
        world.spawnParticles(ParticleTypes.WAX_OFF, player.getX() + (sizeModifier * Math.sin(phaseModifier2)), player.getY(), player.getZ() + (sizeModifier * Math.cos(phaseModifier2)), 0, 0.0d, 1.0d, 0.0d, 10);
    }

    private void triggerTeleport(ServerPlayerEntity player) {
        //get the position of the recalling player
        ServerWorld world = player.server.getWorld( player.getSpawnPointDimension() );
        BlockPos respawnPosition = player.getSpawnPointPosition();
        float respawnAngle = player.getSpawnAngle();

        //if the spawn is in a dimension that doesn't exist anymore, or we don't have a respawnPosition for some reason - spawn in the overworld
        if( world == null || respawnPosition == null) {
            //FIXME: change the overworld line to get the world spawn dimension of the server. will only matter in case someone uses /setworldspawn
            world = player.getServer().getWorld(ServerWorld.OVERWORLD);
            respawnPosition = world.getSpawnPos();
        }

        //find a good place to plop the player down - we don't want to teleport them *inside* of their bed, just next to it
        Optional<Vec3d> targetPos = PlayerEntity.findRespawnPosition(world, respawnPosition, respawnAngle, false, true);

        //if the bed is blocked, recall to the world spawn
        if(targetPos.isEmpty()) {
            //FIXME: same as above
            world = player.getServer().getWorld(ServerWorld.OVERWORLD);
            respawnPosition = world.getSpawnPos();
        }
        else {
            //overwrite the old respawn position, just so i don't have to make a new variable, or make several if/else statements on the teleport line below
            respawnPosition = new BlockPos(((int) targetPos.get().getX()), (int) targetPos.get().getY(), (int) targetPos.get().getZ());
        }

        //we play these twice, once before the recall and once after the recall, so people on both ends can hear you
        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

        //we do this, so we can teleport our boats/horses/pigs/whatevers back with us
        Entity vehicle = player.getVehicle();
        player.teleport(world, respawnPosition.getX(), respawnPosition.getY(), respawnPosition.getZ(), player.getYaw(), player.getPitch());
        if (vehicle != null) {
            vehicle.teleport(world, respawnPosition.getX(), respawnPosition.getY(), respawnPosition.getZ(), Set.of(), player.getYaw(), player.getPitch());
        }

        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
    }

    public static void interruptRecall(LivingEntity player) {
        StatusEffectInstance recallInstance = player.getStatusEffect(FunRecall.RECALL_EFFECT);
        if(recallInstance != null) {
            player.removeStatusEffect(FunRecall.RECALL_EFFECT);
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.BLOCK_RESPAWN_ANCHOR_DEPLETE.value(), SoundCategory.PLAYERS, 0.4f, 1.0f);
            player.addStatusEffect(new StatusEffectInstance(FunRecall.RECALL_EXHAUSTION_EFFECT, 100));
        }
    }
}
