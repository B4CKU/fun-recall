package com.zhaba.funrecall;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class RecallEffect extends StatusEffect {
    protected RecallEffect() {
        super(StatusEffectCategory.BENEFICIAL, 0xb38ef3);
    }

    @Override
    public boolean canApplyUpdateEffect(int remainingTicks, int level) {
        return remainingTicks == 1;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int level) {
        //we need this code running only on the server side of things
        if (entity.getEntityWorld().isClient) {
            return;
        }

        //TODO: teleport vehicles as well
        //TODO: particles when teleporting
        //TODO: button that adds the effect
        //TODO: interrupted when moving, hurting
        //TODO: debuff when interrupted/add weakness, fragility, slowness etc when gaining this effect - these buffs wouldn't get removed when interrupted
        //TODO: progress bar for the recall?
        //TODO: short cooldown on use (0.5s or so) just so you can't blind/deafen others with all the VFX and SFX
        //TODO: mod icon and description
        if (entity instanceof ServerPlayerEntity player) {

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

            //we play these twice, just so people you teleported away from and ones you teleported to, are aware of your recall
            //player should only hear these once, because of directional audio, right?
            //...right?
            //(even if they hear it twice, the effect doesn't sound odd, might need to look into it if i'm wrong)
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);
            player.teleport(world, respawnPosition.getX(), respawnPosition.getY(), respawnPosition.getZ(), player.getYaw(), player.getPitch());
            player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 0.4f, 1f);

        }
    }
}
