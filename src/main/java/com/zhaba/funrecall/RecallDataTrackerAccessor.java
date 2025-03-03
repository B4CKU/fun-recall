package com.zhaba.funrecall;


public interface RecallDataTrackerAccessor{
    //this interface allows me to access dataTracker added in LivingEntityMixin.java.
    //without it, if i can't make a non-mixin static function in that class, as that causes the mixins to crash
    //and i can't make it non-static, because i can't access the specific var containing the type of tracked data,
    //because it was defined in that class.

    void dataTrackerSetRecallTime(long recallTime);
    long dataTrackerGetRecallTime();
}