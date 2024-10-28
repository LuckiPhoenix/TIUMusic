package com.example.TIUMusic.Login

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.launch
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
        ).build()
    }

    @Provides
    @Singleton
    fun provideAuthDao(database: AppDatabase): AuthDao {
        return database.userDao()
    }
}

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userAuthStatus = MutableLiveData<Result<User?>>()
    val userAuthStatus: LiveData<Result<User?>> = _userAuthStatus

    private val _userDetails = MutableLiveData<User?>()
    val userDetails: LiveData<User?> = _userDetails

    private val _resetPasswordStatus = MutableLiveData<Result<Boolean>>()
    val resetPasswordStatus: LiveData<Result<Boolean>> = _resetPasswordStatus

    fun insertUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.insertAuth(user)
                _userDetails.postValue(user)
            } catch (e: Exception) {
                _userAuthStatus.postValue(Result.Error(e))
            }
        }
    }

    fun authenticate(email: String, password: String) {
        viewModelScope.launch {
            _userAuthStatus.postValue(Result.Loading)
            try {
                val user = userRepository.authenticate(email, password)
                if (user != null) {
                    _userAuthStatus.postValue(Result.Success(user))
                } else {
                    _userAuthStatus.postValue(Result.Error(Exception("Authentication failed")))
                }
            } catch (e: Exception) {
                _userAuthStatus.postValue(Result.Error(e))
            }
        }
    }

    fun getUserByEmail(email: String) {
        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            _userDetails.postValue(user)
        }
    }

    fun checkEmailExists(email: String) {
        viewModelScope.launch {
            _resetPasswordStatus.postValue(Result.Loading)
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    _resetPasswordStatus.postValue(Result.Success(true))
                } else {
                    _resetPasswordStatus.postValue(Result.Error(Exception("Email not found")))
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

}