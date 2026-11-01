package com.example.data.journal

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow

/**
 * A single entry in the Hermetic Journal, persisted with Room. This is the app's only
 * journal: reflections written by hand and entries inscribed from other screens (Oracle
 * decrees, deciphered axioms, consecrated sigils, planetary auspices) all live here.
 *
 * @param theme the Hermetic principle or subject the entry is filed under
 * @param sealGlyph the wax-seal glyph stamped on the entry (null for entries saved before
 *        the merge; shown with [DEFAULT_SEAL_GLYPH]). Nullable with no SQL default so the
 *        migration below matches Room's expected schema exactly.
 * @param contemplationMinutes set when the entry was written straight after a
 *        Contemplation Timer session; null for ordinary entries
 */
@Entity(tableName = "journal_entries")
data class JournalEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val updatedAt: Long,
    val title: String,
    val content: String,
    val theme: String,
    val contemplationMinutes: Int? = null,
    val sealGlyph: String? = null
)

const val DEFAULT_SEAL_GLYPH = "✦"

/** The entry's wax-seal glyph, falling back to the default for older entries. */
val JournalEntry.displayGlyph: String
    get() = sealGlyph ?: DEFAULT_SEAL_GLYPH

/** Version 1 -> 2: adds the wax-seal glyph column when the two journals were merged. */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE journal_entries ADD COLUMN sealGlyph TEXT"
        )
    }
}

@Dao
interface JournalDao {
    @Query("SELECT * FROM journal_entries ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<JournalEntry>>

    @Insert
    suspend fun insert(entry: JournalEntry): Long

    @Update
    suspend fun update(entry: JournalEntry)

    @Delete
    suspend fun delete(entry: JournalEntry)
}

@Database(entities = [JournalEntry::class], version = 2, exportSchema = false)
abstract class HermeticJournalDatabase : RoomDatabase() {
    abstract fun journalDao(): JournalDao

    companion object {
        @Volatile
        private var instance: HermeticJournalDatabase? = null
        @Volatile
        private var instanceContext: Context? = null

        /**
         * One database per application. The instance is rebuilt if the application context
         * changes, which only happens in tests (each Robolectric test creates a fresh app).
         */
        fun get(context: Context): HermeticJournalDatabase {
            val appContext = context.applicationContext
            instance?.let { if (instanceContext === appContext) return it }
            return synchronized(this) {
                val existing = instance
                if (existing != null && instanceContext === appContext) {
                    existing
                } else {
                    Room.databaseBuilder(appContext, HermeticJournalDatabase::class.java, "hermetic_journal.db")
                        .addMigrations(MIGRATION_1_2)
                        .build()
                        .also {
                            instance = it
                            instanceContext = appContext
                        }
                }
            }
        }
    }
}

/** Fixed list of themes a reflection can be filed under. */
object JournalThemes {
    const val GENERAL = "General"
    const val MEDITATION = "Meditation"

    val all = listOf(
        GENERAL,
        MEDITATION,
        "Mentalism",
        "Correspondence",
        "Vibration",
        "Polarity",
        "Rhythm",
        "Cause & Effect",
        "Gender",
        "Dreams"
    )
}
