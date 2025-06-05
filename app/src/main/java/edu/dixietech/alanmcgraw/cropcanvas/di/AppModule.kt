package edu.dixietech.alanmcgraw.cropcanvas.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.PreferencesUserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserAuthenticator
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserRepository
import edu.dixietech.alanmcgraw.cropcanvas.data.auth.UserRepositoryImpl
import edu.dixietech.alanmcgraw.cropcanvas.data.database.CropCanvasDatabase
import edu.dixietech.alanmcgraw.cropcanvas.data.database.RoomCropCanvasDatabase
import edu.dixietech.alanmcgraw.cropcanvas.data.network.CropCanvasApi
import edu.dixietech.alanmcgraw.cropcanvas.data.network.KtorCropCanvasApi
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepository
import edu.dixietech.alanmcgraw.cropcanvas.data.repository.CropCanvasRepositoryImpl
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


private val Context.userPreferences: DataStore<Preferences> by preferencesDataStore(
    name = "user_preferences"
)

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun providesUserAuthenticator(
        @ApplicationContext context: Context
    ): UserAuthenticator =
        PreferencesUserAuthenticator(context.userPreferences)

    @Provides
    @Singleton
    fun providesCropCanvasApi(): CropCanvasApi =
        KtorCropCanvasApi(Dispatchers.IO)

    @Provides
    fun providesUserRepository(
        auth: UserAuthenticator,
        network: CropCanvasApi
    ): UserRepository = UserRepositoryImpl(
        auth = auth,
        network = network,
    )

    @Provides
    fun providesCropCanvasRepository(
        network: CropCanvasApi,
        userAuthenticator: UserAuthenticator,
    ): CropCanvasRepository = CropCanvasRepositoryImpl(
        network = network,
        userAuthenticator = userAuthenticator,
        context = Dispatchers.Default
    )

    @Provides
    @Singleton
    fun providesCropCanvasDataBase(
        @ApplicationContext context: Context
    ): CropCanvasDatabase =
        Room.databaseBuilder(
            context,
            RoomCropCanvasDatabase::class.java,
            "crop_canvas_database"
    ).build()
}