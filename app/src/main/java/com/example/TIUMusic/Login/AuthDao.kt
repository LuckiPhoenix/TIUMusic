package com.example.TIUMusic.Login

import android.content.Context
import android.util.Log
import androidx.room.AutoMigration
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import androidx.room.Upsert
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.TIUMusic.MusicDB.MusicDao
import com.example.TIUMusic.MusicDB.Song
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
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

    @Query("SELECT EXISTS(SELECT * FROM ListenHistory WHERE userEmail = :userEmail AND songId = :songId)")
    suspend fun userHasListenTo(userEmail : String, songId : Int) : Boolean;

    @Query("UPDATE ListenHistory " +
            "SET listenCount = listenCount + 1, lastListenDate = datetime('now', 'localtime') " +
            "WHERE userEmail = :userEmail AND songId = :songId")
    suspend fun updateUserListenTo(userEmail : String, songId: Int);

    @Query("SELECT songId FROM ListenHistory WHERE userEmail = :userEmail ORDER BY lastListenDate DESC LIMIT :limit")
    suspend fun getRecentListenedSongs(userEmail: String, limit : Int) : List<Int>;

    @Query("SELECT songId FROM ListenHistory WHERE userEmail = :userEmail ORDER BY listenCount DESC LIMIT :limit")
    suspend fun getMostListenedSongs(userEmail: String, limit : Int) : List<Int>;

    @Upsert
    suspend fun insertListenHistory(listenHistory: ListenHistory);
}


//This is the database itself, in singleton (i.e: there is only one instance of the database)
@Database(entities = [User::class, ListenHistory::class], version = 4)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): AuthDao;

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
    suspend fun userHasListenTo(userEmail : String, songId : Int) : Boolean;
    suspend fun updateUserListenTo(userEmail : String, songId: Int);
    suspend fun getMostListenedSongs(userEmail: String, limit : Int = 10) : List<Int>;
    suspend fun getRecentListenedSongs(userEmail: String, limit : Int = 10) : List<Int>;
    suspend fun insertListenHistory(listenHistory: ListenHistory);
}

class UserRepositoryImpl @Inject constructor(
    private val authDao: AuthDao
) : UserRepository {
    override suspend fun insertAuth(user: User) = authDao.insertAuth(user)
    override suspend fun getUserByEmail(email: String): User? = authDao.getUserByEmail(email)
    override suspend fun authenticate(email: String, password: String): User? = authDao.authenticate(email, password)
    override suspend fun userHasListenTo(userEmail : String, songId : Int) : Boolean = authDao.userHasListenTo(userEmail, songId)
    override suspend fun updateUserListenTo(userEmail : String, songId: Int) = authDao.updateUserListenTo(userEmail, songId);
    override suspend fun getRecentListenedSongs(userEmail: String, limit : Int) : List<Int> = authDao.getRecentListenedSongs(userEmail, limit);
    override suspend fun getMostListenedSongs(userEmail: String, limit : Int) : List<Int> = authDao.getMostListenedSongs(userEmail, limit);
    override suspend fun insertListenHistory(listenHistory: ListenHistory) = authDao.insertListenHistory(listenHistory);
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

