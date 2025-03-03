package com.zhaba.funrecall.networking;

import com.zhaba.funrecall.FunRecall;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

public class RecallPacket {
    public static final Identifier RECALL_PACKET_ID = Identifier.of(FunRecall.MOD_ID, "recall_packet");

    static public void handleRecallPacket(ServerPlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }

        if (player.getStatusEffect(FunRecall.RECALL_EFFECT) != null) {
            return;
        }

        if (player.getStatusEffect(FunRecall.RECALL_EXHAUSTION_EFFECT) != null) {
            return;
        }

        player.addStatusEffect(new StatusEffectInstance(FunRecall.RECALL_EFFECT, 100, 0, false, false, true));
        player.getWorld().playSound(null, player.getBlockPos(), FunRecall.RECALL_CHANNEL, SoundCategory.PLAYERS, 0.4f, 1.2f);
    }
}
