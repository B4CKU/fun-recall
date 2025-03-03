package com.zhaba.funrecall.mixin;


import com.zhaba.funrecall.FunRecall;
import com.zhaba.funrecall.RecallDataTrackerAccessor;
import com.zhaba.funrecall.RecallEffect;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin implements RecallDataTrackerAccessor {
    private LivingEntity livingEntity() {
        //pretty way to access 'this' with mixins.
        //learned it from studying Spell Engine made by Daedelus. shoutouts to this absolute legend
        return (LivingEntity) ((Object) this);
    }


    //this TrackedData keeps the recall process synced between clients - without it,
    //only the recalling player would see the recall particles.
    //at all times it either stores the value of the game tick in which the recall was started or -1 if an entity
    //isn't currently recalling.
    @Unique
    private static final TrackedData<Long> RECALL_TIME = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.LONG);

    @Inject(method = "initDataTracker", at = @At(value="TAIL"))
    public void initDataTracker(CallbackInfo ci) {
        if (FunRecall.isRecallableEntity(livingEntity())) {
            livingEntity().getDataTracker().startTracking(RECALL_TIME, (long)-1);
        }
    }

    public long dataTrackerGetRecallTime() {
        return livingEntity().getDataTracker().get(RECALL_TIME);
    }


    public void dataTrackerSetRecallTime(long recallTime) {
        livingEntity().getDataTracker().set(RECALL_TIME, recallTime);
    }


    @Inject(method = "applyMovementEffects", at = @At(value="TAIL"))
    public void applyMovementEffects(BlockPos pos, CallbackInfo ci) {
        if (FunRecall.isRecallableEntity(livingEntity())) {
            RecallEffect.interruptRecall(livingEntity());
        }
    }

    @Inject(method = "tickStatusEffects", at = @At(value="TAIL"))
    public void tickStatusEffects(CallbackInfo ci) {
        if ( !((FunRecall.isRecallableEntity(livingEntity())) && ((RecallDataTrackerAccessor)livingEntity()).dataTrackerGetRecallTime() >= 0) ) return;


        long duration = livingEntity().getWorld().getTime() - ((RecallDataTrackerAccessor)livingEntity()).dataTrackerGetRecallTime();
        duration = Math.min(500, Math.max(0, duration) );



        //variable that goes from 0 to 20, then snaps back to 0 and goes to 20 again
        int timer = (int) duration % 20;

        //two phase modifier vars, one delayed by half the phase. they dictate the position of the next particle
        double phaseModifier1 = timer * 0.1 * Math.PI;
        double phaseModifier2 = (timer + 10) * 0.1 * Math.PI;

        //variable responsible for changing the size of the particle circle
        double sizeModifier = 1.5 - (duration/100f);

        //(x = sin a, y = cos a) creates a point that traces out a circle when interpolating the 'a' value between 0 and 2*PI
        livingEntity().getWorld().addParticle(FunRecall.RECALL_DUST_PARTICLE, livingEntity().getX() + (sizeModifier * Math.sin(phaseModifier1)), livingEntity().getY(), livingEntity().getZ() + (sizeModifier * Math.cos(phaseModifier1)), 0.0d, 10.0d, 0.0d);
        livingEntity().getWorld().addParticle(FunRecall.RECALL_DUST_PARTICLE, livingEntity().getX() + (sizeModifier * Math.sin(phaseModifier2)), livingEntity().getY(), livingEntity().getZ() + (sizeModifier * Math.cos(phaseModifier2)), 0.0d, 10.0d, 0.0d);
    }
}
