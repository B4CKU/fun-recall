package com.zhaba.funrecall.mixin;


import com.zhaba.funrecall.RecallEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "applyMovementEffects", at = @At(value="TAIL"))
    public void applyMovementEffects(BlockPos pos, CallbackInfo ci) {
        //as far as i know, using this would've been fine normally, but the IDE won't let us do that, so we have to do this bit of sorcery to trick it
        LivingEntity player = (LivingEntity) (Object) this;

        RecallEffect.interruptRecall(player);
    }
}
