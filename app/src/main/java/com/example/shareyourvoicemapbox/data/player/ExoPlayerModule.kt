package com.example.shareyourvoicemapbox.data.player

import android.content.Context
import com.example.shareyourvoicemapbox.domain.player.ExoPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object ExoPlayerModule {
    @ViewModelScoped
    @Provides
    fun provideExoPlayer(
        @ApplicationContext context: Context
    ): ExoPlayer {
        return ExoPLayerImpl(context)
    }
}