package com.example.musicapptraining.di

import android.content.Context
import android.content.SharedPreferences
import com.example.musicapptraining.utilities.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getSharedPreferences(@ApplicationContext context : Context):SharedPreferences{
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME,Context.MODE_PRIVATE)
    }
}