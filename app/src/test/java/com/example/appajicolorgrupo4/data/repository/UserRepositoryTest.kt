package com.example.appajicolorgrupo4.data.repository

import com.example.appajicolorgrupo4.data.local.user.UserDao
import io.mockk.mockk
import org.junit.Before
import org.junit.Test

class UserRepositoryTest {

    private lateinit var userRepository: UserRepository
    private val userDao: UserDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        userRepository = UserRepository(userDao)
    }

    @Test
    fun `repository initializes successfully`() {
        assert(userRepository != null)
    }
}
