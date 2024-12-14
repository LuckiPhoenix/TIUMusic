package com.example.TIUMusic.Login

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.TIUMusic.R
import com.example.TIUMusic.SongData.MusicItem
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/*
* Không phận sự cấm vào!
* */

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase): AuthDao {
        return database.userDao()
    }
}
@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    companion object {
        private const val PREFS_NAME = "UserSessionPrefs"
        private const val KEY_USER_EMAIL = "current_user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _userAuthStatus = MutableLiveData<Result<User?>>()
    val userAuthStatus: LiveData<Result<User?>> = _userAuthStatus

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser

    private val _resetPasswordStatus = MutableLiveData<Result<Boolean>>()
    val resetPasswordStatus: LiveData<Result<Boolean>> = _resetPasswordStatus

    private val _playlist = MutableLiveData<Playlist?>()
    val playlist: LiveData<Playlist?> = _playlist

    // Check if user is already logged in on ViewModel initialization
    init {
        checkPersistentSession()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false) &&
                sharedPreferences.getString(KEY_USER_EMAIL, null) != null
    }

    private fun checkPersistentSession() {
        viewModelScope.launch {
            val isLoggedIn = isLoggedIn()
            val userEmail = sharedPreferences.getString(KEY_USER_EMAIL, null)

            if (isLoggedIn && userEmail != null) {
                val user = userRepository.getUserByEmail(userEmail)
                if (user != null) {
                    _currentUser.postValue(user)
                }
            }
        }
    }

    fun getCurrentUserEmail(): String? {
        return if (isLoggedIn()) {
            sharedPreferences.getString(KEY_USER_EMAIL, null)
        } else {
            null
        }
    }

    suspend fun getCurrentUser(): User? {
        val email = getCurrentUserEmail()
        return email?.let { userRepository.getUserByEmail(it) }
    }

    fun logout() {
        // Clear login state
        sharedPreferences.edit().apply {
            remove(KEY_IS_LOGGED_IN)
            remove(KEY_USER_EMAIL)
        }.apply()

        // Clear current user
        _currentUser.postValue(null)
    }
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6
    }

    fun insertUser(user: User) {
        viewModelScope.launch {
            _userAuthStatus.postValue(Result.Loading)
            try {
                // Validate email format
                if (!isValidEmail(user.email)) {
                    _userAuthStatus.postValue(Result.Error(Exception("Invalid email format")))
                    return@launch
                }

                // Validate password length
                if (!isValidPassword(user.password)) {
                    _userAuthStatus.postValue(Result.Error(Exception("Password must be at least 6 characters long")))
                    return@launch
                }

                val existingUser = userRepository.getUserByEmail(user.email)
                if (existingUser != null) {
                    _userAuthStatus.postValue(Result.Error(EmailExistsException("Email already exists")))
                } else {
                    userRepository.insertAuth(user)
                    _userAuthStatus.postValue(Result.Success(user))
                }
            } catch (e: Exception) {
                _userAuthStatus.postValue(Result.Error(e))
            }
        }
    }

    fun authenticate(email: String, password: String) {
        viewModelScope.launch {
            _userAuthStatus.postValue(Result.Loading)
            try {
                // Validate email format
                if (!isValidEmail(email)) {
                    _userAuthStatus.postValue(Result.Error(Exception("Invalid email format")))
                    return@launch
                }

                // Validate password length
                if (!isValidPassword(password)) {
                    _userAuthStatus.postValue(Result.Error(Exception("Password must be at least 6 characters long")))
                    return@launch
                }

                val user = userRepository.authenticate(email, password)
                if (user != null) {
                    sharedPreferences.edit().apply {
                        putBoolean(KEY_IS_LOGGED_IN, true)
                        putString(KEY_USER_EMAIL, user.email)
                    }.apply()
                    _currentUser.postValue(user)
                    _userAuthStatus.postValue(Result.Success(user))
                } else {
                    _userAuthStatus.postValue(Result.Error(AuthenticationException("Incorrect Email and/or Password")))
                }
            } catch (e: Exception) {
                _userAuthStatus.postValue(Result.Error(e))
            }
        }
    }

    fun checkEmailExists(email: String) {
        viewModelScope.launch {
            _resetPasswordStatus.postValue(Result.Loading)
            try {
                // Validate email format
                if (!isValidEmail(email)) {
                    _resetPasswordStatus.postValue(Result.Error(Exception("Invalid email format")))
                    return@launch
                }

                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _resetPasswordStatus.postValue(Result.Success(true))
                } else {
                    _resetPasswordStatus.postValue(Result.Error(EmailNotFoundException("Email not found")))
                }
            } catch (e: Exception) {
                _resetPasswordStatus.postValue(Result.Error(e))
            }
        }
    }

    fun updatePassword(email: String, newPassword: String) {
        viewModelScope.launch {
            _resetPasswordStatus.postValue(Result.Loading)
            try {
                // Validate password length
                if (!isValidPassword(newPassword)) {
                    _resetPasswordStatus.postValue(Result.Error(Exception("Password must be at least 6 characters long")))
                    return@launch
                }

                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    val updatedUser = user.copy(password = newPassword)
                    userRepository.insertAuth(updatedUser)
                    _resetPasswordStatus.postValue(Result.Success(true))
                } else {
                    _resetPasswordStatus.postValue(Result.Error(Exception("User not found")))
                }
            } catch (e: Exception) {
                _resetPasswordStatus.postValue(Result.Error(e))
            }
        }
    }
    fun updateUsername(username: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                currentUser.fullName = username
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Notify observers

            }
        }
    }

    fun updateProfilePicture(newProfilePicture: String?) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                // Delete old profile picture if it exists
                currentUser.profilePicture?.let { oldProfilePicture ->
                    deleteFileIfExists(oldProfilePicture)
                }

                // Update with new profile picture
                currentUser.profilePicture = newProfilePicture
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    fun listenTo(songId: Int) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                if (userRepository.userHasListenTo(currentUser.email, songId))
                    userRepository.updateUserListenTo(currentUser.email, songId);
                else
                    userRepository.insertListenHistory(
                        ListenHistory(
                            currentUser.email,
                            songId,
                            0,
                            DateTimeFormatter.ISO_INSTANT.format(Instant.now()))
                    )
            }
        }
    }

    private fun deleteFileIfExists(filePath: String) {
        val file = File(filePath)
        if (file.exists()) {
            file.delete()
        }
    }

    // Add a new playlist
    fun addPlaylist(title: String, picture: Int? = R.drawable.profile_pic) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                val newPlaylist = Playlist(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    picture = picture,
                    songs = mutableListOf()
                )
                currentUser.playlists.add(newPlaylist)
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    // Remove a playlist by ID
    fun removePlaylist(playlistId: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                currentUser.playlists.removeAll { it.id == playlistId }
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    // Edit a playlist's title
    fun editPlaylistTitle(playlistId: String, newTitle: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                currentUser.playlists.find { it.id == playlistId }?.title = newTitle
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    // Add a song to a playlist
    fun addSongToPlaylist(playlistId: String, song: MusicItem) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                currentUser.playlists.find { it.id == playlistId }?.songs?.add(song)
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    // Remove a song from a playlist
    fun removeSongFromPlaylist(playlistId: String, songId: String) {
        viewModelScope.launch {
            val currentUser = getCurrentUser()
            if (currentUser != null) {
                currentUser.playlists.find { it.id == playlistId }?.songs?.removeAll { it.videoId == songId }
                userRepository.insertAuth(currentUser)
                _currentUser.postValue(currentUser) // Update LiveData
            }
        }
    }

    // Get a playlist by ID
    fun getPlaylistById(playlistId: String) {
        viewModelScope.launch {
            runCatching {
                getCurrentUser()
            }.onSuccess { user ->
                val playlist = user?.playlists?.find { it.id == playlistId }
                _playlist.value = playlist
            }.onFailure {
                Log.d("LogNav", "Error")
            }
        }
    }
}