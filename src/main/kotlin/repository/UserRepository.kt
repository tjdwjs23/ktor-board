package com.codersee.repository

import board.ktor.model.User

class UserRepository {

  private val users = mutableListOf<User>()

  fun findAll(): List<User> =
    users

  fun findByUserId(id: Long): User? =
    users.firstOrNull { it.id == id }

  fun findByUsername(username: String): User? =
    users.firstOrNull { it.username == username }

  fun save(user: User): Boolean =
    users.add(user)
}