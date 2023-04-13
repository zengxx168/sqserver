/*
 * Copyright (C) 2021 - 2023 . All Rights Reserved.
 */
package io.game.sq.bootstrap.sessions.domain;

import java.util.Objects;

/**
 * ChannelId
 * <pre>
 *     see {@link io.netty.channel.ChannelId#asLongText()}
 * </pre>
 *
 * @author zengxx
 * @date 2022-03-15
 */
public record ChannelId(String channelId) {
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ChannelId that)) {
            return false;
        }
        return Objects.equals(channelId, that.channelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(channelId);
    }
}
