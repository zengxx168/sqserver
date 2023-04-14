package io.game.sq.core.user.service;

import io.game.sq.core.user.domain.User;

public interface IUserService {

    public User byObjectId(String id);
}
