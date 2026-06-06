package com.example.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// --- Room Entities ---

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val nativeLanguage: String // Filipino, Nepalese, Indonesian, Vietnamese, Burmese, English
)

@Entity(tableName = "favorite_words")
data class FavoriteWord(
    @PrimaryKey val wordId: Int, // Refers to Vocabulary.id
    val addedTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "quiz_records")
data class QuizRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val correctCount: Int,
    val totalQuestions: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// --- Room DAOs ---

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserProfile(profile: UserProfile)

    @Query("SELECT * FROM favorite_words")
    fun getFavoriteWords(): Flow<List<FavoriteWord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteWord(fav: FavoriteWord)

    @Query("DELETE FROM favorite_words WHERE wordId = :id")
    suspend fun removeFavoriteWord(id: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_words WHERE wordId = :id)")
    fun isFavoriteWord(id: Int): Flow<Boolean>

    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC")
    fun getQuizRecords(): Flow<List<QuizRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addQuizRecord(record: QuizRecord)

    @Query("DELETE FROM quiz_records")
    suspend fun clearHistory()
}

// --- Room AppDatabase ---

@Database(entities = [UserProfile::class, FavoriteWord::class, QuizRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssw_nursing_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- Unified Repository ---

class AppRepository(private val userDao: UserDao) {
    val userProfile: Flow<UserProfile?> = userDao.getUserProfile()
    val favoriteWords: Flow<List<FavoriteWord>> = userDao.getFavoriteWords()
    val quizHistory: Flow<List<QuizRecord>> = userDao.getQuizRecords()

    suspend fun saveProfile(name: String, language: String) {
        userDao.saveUserProfile(UserProfile(name = name, nativeLanguage = language))
    }

    suspend fun toggleFavorite(wordId: Int, isFav: Boolean) {
        if (isFav) {
            userDao.removeFavoriteWord(wordId)
        } else {
            userDao.addFavoriteWord(FavoriteWord(wordId = wordId))
        }
    }

    fun isFavorite(wordId: Int): Flow<Boolean> = userDao.isFavoriteWord(wordId)

    suspend fun addQuizResult(correct: Int, total: Int) {
        userDao.addQuizRecord(QuizRecord(correctCount = correct, totalQuestions = total))
    }

    suspend fun clearQuizHistory() {
        userDao.clearHistory()
    }
}
