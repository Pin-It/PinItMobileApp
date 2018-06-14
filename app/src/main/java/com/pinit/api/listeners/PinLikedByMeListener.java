package com.pinit.api.listeners;

import com.pinit.api.models.Like;

public interface PinLikedByMeListener {
    void isLikedByMe(Like like);

    void isNotLikedByMe();
}
