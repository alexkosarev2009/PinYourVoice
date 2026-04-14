package com.example.shareyourvoicemapbox.data.player

import com.example.shareyourvoicemapbox.domain.player.AudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object AudioPlayerModule {
    @Provides
    @ViewModelScoped
    fun provideAudioPlayer(): AudioPlayer {
        return AudioPlayerImpl()
    }
}