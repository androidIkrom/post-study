package com.example.poststudy.di

import com.example.poststudy.data.repository.LocalRepositoryImpl
import com.example.poststudy.data.repository.NetworkRepositoryImpl
import com.example.poststudy.domain.repository.LocalRepository
import com.example.poststudy.domain.repository.NetworkRepository

object AppContainer {
    val localRepository: LocalRepository by lazy {
        LocalRepositoryImpl()
    }

    val networkRepository: NetworkRepository by lazy {
        NetworkRepositoryImpl()
    }
}
