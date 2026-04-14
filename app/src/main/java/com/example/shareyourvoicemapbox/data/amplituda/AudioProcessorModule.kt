package com.example.shareyourvoicemapbox.data.amplituda

import android.content.Context
import com.example.shareyourvoicemapbox.domain.amplituda.AudioProcessor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AudioProcessorModule {
    @Provides
    @Singleton
    fun provideAudioProcessor(
        @ApplicationContext context: Context
    ): AudioProcessor {
        return AudioProcessorImpl(context)
    }
}