package com.mrvijay.runningapp.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.mrvijay.runningapp.db.RunningDatabase
import com.mrvijay.runningapp.others.Constants
import com.mrvijay.runningapp.others.Constants.KEY_FIRST_TIME_TOOGLE
import com.mrvijay.runningapp.others.Constants.KEY_NAME
import com.mrvijay.runningapp.others.Constants.KEY_WEIGHT
import com.mrvijay.runningapp.others.Constants.RUNNING_DATABASE_NAME
import com.mrvijay.runningapp.others.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRunningDatabase(@ApplicationContext app:Context)= Room.databaseBuilder(
        app,RunningDatabase::class.java,RUNNING_DATABASE_NAME).build()

    @Singleton
    @Provides
    fun provideRunningDao(db:RunningDatabase)=db.getRunDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context)=
        app.getSharedPreferences(SHARED_PREFERENCES_NAME,MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref:SharedPreferences)=sharedPref.getString(KEY_NAME,"")?: ""

    @Singleton
    @Provides
    fun provideFirstTimeToogle(sharedPref:SharedPreferences)=sharedPref.getBoolean(
        KEY_FIRST_TIME_TOOGLE,true)


    @Singleton
    @Provides
    fun provideWeight(sharedPref:SharedPreferences)=sharedPref.getFloat(KEY_WEIGHT,80f)

}