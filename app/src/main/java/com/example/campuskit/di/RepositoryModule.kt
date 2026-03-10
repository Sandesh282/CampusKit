package com.example.campuskit.di

import com.example.campuskit.data.academic.remote.api.StudentHubApi
import com.example.campuskit.data.academic.repository.DefaultAcademicRepository
import com.example.campuskit.domain.academic.repository.AcademicRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAcademicRepository(
        impl: DefaultAcademicRepository
    ): AcademicRepository
    
    companion object {
        @Provides
        @Singleton
        fun provideMoshi(): Moshi {
            return Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        }

        @Provides
        @Singleton
        fun provideStudentHubApi(moshi: Moshi): StudentHubApi {
            return Retrofit.Builder()
                .baseUrl("https://studenthub.axiosiiitl.dev/") // StudentHub backend
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(StudentHubApi::class.java)
        }
    }
}
