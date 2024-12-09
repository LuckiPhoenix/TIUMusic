package com.example.TIUMusic.Login

import android.app.Application
import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Upsert
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Singleton

/*
* Không Phận sự cấm vào!
* */


//DAO is the interface that contains the methods used for accessing the database.
@Dao
interface AuthDao {
    @Upsert
    suspend fun insertAuth(user: User)

    @Query("SELECT * FROM User WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM user WHERE email = :email AND password = :password")
    suspend fun authenticate(email: String, password: String): User?

    // Update profile picture
    @Query("UPDATE User SET profilePicture = :profilePicture WHERE email = :email")
    suspend fun updateProfilePicture(email: String, profilePicture: Int)

    // Update playlists
    @Query("UPDATE User SET playlists = :playlists WHERE email = :email")
    suspend fun updatePlaylists(email: String, playlists: MutableList<Playlist>)
}

//This is the database itself, in singleton (i.e: there is only one instance of the database)
@Database(entities = [User::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): AuthDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }

    }
}


interface UserRepository {
    suspend fun insertAuth(user: User)
    suspend fun getUserByEmail(email: String): User?
    suspend fun authenticate(email: String, password: String): User?
}

class UserRepositoryImpl @Inject constructor(
    private val authDao: AuthDao
) : UserRepository {
    override suspend fun insertAuth(user: User) = authDao.insertAuth(user)
    override suspend fun getUserByEmail(email: String): User? = authDao.getUserByEmail(email)
    override suspend fun authenticate(email: String, password: String): User? = authDao.authenticate(email, password)
}
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()

    // User-friendly errors
    fun getErrorMessage(): String {
        return when (this) {
            is Error -> {
                when (exception) {
                    is AuthenticationException -> "Incorrect email or password."
                    is EmailNotFoundException -> "Email not found."
                    is EmailExistsException -> "Email already exists."
                    else -> exception.message ?: "Something went wrong :("
                }
            }
            else -> ""
        }
    }
}
class AuthenticationException(message: String) : Exception(message)
class EmailNotFoundException(message: String) : Exception(message)
class EmailExistsException(message: String) : Exception(message)

