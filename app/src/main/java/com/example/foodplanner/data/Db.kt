package com.example.foodplanner.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity
data class Recipe(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val servings: Int = 2,
    val sourceUrl: String? = null,
    val instructions: String? = null,
    val cuisine: String? = null,
)

@Entity(
    foreignKeys = [ForeignKey(Recipe::class, ["id"], ["recipeId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("recipeId")],
)
data class Ingredient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recipeId: Long,
    val name: String,
    val quantity: Double?,
    val unit: String,
)

@Entity(
    foreignKeys = [ForeignKey(Recipe::class, ["id"], ["recipeId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("recipeId")],
)
data class PlannedMeal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // ISO yyyy-MM-dd
    val mealType: String,
    val recipeId: Long,
    val auto: Boolean = false,
)

/** key = "<weekStart>|<name>|<unit>" */
@Entity
data class GroceryCheck(@PrimaryKey val key: String)

@Dao
interface PlannerDao {
    @Query("SELECT * FROM Recipe ORDER BY name COLLATE NOCASE")
    fun recipes(): Flow<List<Recipe>>

    @Query("SELECT * FROM Ingredient")
    fun ingredients(): Flow<List<Ingredient>>

    @Query("SELECT * FROM PlannedMeal")
    fun meals(): Flow<List<PlannedMeal>>

    @Query("SELECT `key` FROM GroceryCheck")
    fun checked(): Flow<List<String>>

    @Query("SELECT * FROM PriceItem")
    fun prices(): Flow<List<PriceItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setPrice(item: PriceItem)

    @Query("DELETE FROM PriceItem WHERE name = :name")
    suspend fun clearPrice(name: String)

    @Insert
    suspend fun insertRecipe(recipe: Recipe): Long

    @Update
    suspend fun updateRecipe(recipe: Recipe)

    @Insert
    suspend fun insertIngredients(items: List<Ingredient>)

    @Query("DELETE FROM Ingredient WHERE recipeId = :recipeId")
    suspend fun clearIngredients(recipeId: Long)

    @Query("DELETE FROM Recipe WHERE id = :id")
    suspend fun deleteRecipe(id: Long)

    @Insert
    suspend fun insertMeal(meal: PlannedMeal)

    @Query("DELETE FROM PlannedMeal WHERE id = :id")
    suspend fun deleteMeal(id: Long)

    @Query("DELETE FROM PlannedMeal WHERE auto = 1 AND mealType = 'Dinner' AND date IN (:dates)")
    suspend fun deleteAutoDinners(dates: List<String>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun check(item: GroceryCheck)

    @Query("DELETE FROM GroceryCheck WHERE `key` = :key")
    suspend fun uncheck(key: String)

    @Transaction
    suspend fun saveRecipe(recipe: Recipe, items: List<Ingredient>) {
        val id = if (recipe.id == 0L) insertRecipe(recipe) else {
            updateRecipe(recipe); clearIngredients(recipe.id); recipe.id
        }
        insertIngredients(items.map { it.copy(id = 0, recipeId = id) })
    }
}

@Database(
    entities = [Recipe::class, Ingredient::class, PlannedMeal::class, GroceryCheck::class, PriceItem::class],
    version = 5,
    exportSchema = false,
)
abstract class AppDb : RoomDatabase() {
    abstract fun dao(): PlannerDao

    companion object {
        @Volatile private var instance: AppDb? = null
        fun get(context: Context): AppDb = instance ?: synchronized(this) {
            instance ?: Room.databaseBuilder(context.applicationContext, AppDb::class.java, "planner.db")
                .addMigrations(
                    object : androidx.room.migration.Migration(1, 2) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("CREATE TABLE IF NOT EXISTS `PriceItem` (`name` TEXT NOT NULL, `price` REAL NOT NULL, `basis` TEXT NOT NULL, PRIMARY KEY(`name`))")
                        }
                    },
                    object : androidx.room.migration.Migration(2, 3) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("ALTER TABLE `PlannedMeal` ADD COLUMN `auto` INTEGER NOT NULL DEFAULT 0")
                        }
                    },
                    object : androidx.room.migration.Migration(3, 4) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("ALTER TABLE `Recipe` ADD COLUMN `instructions` TEXT")
                        }
                    },
                    object : androidx.room.migration.Migration(4, 5) {
                        override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                            db.execSQL("ALTER TABLE `Recipe` ADD COLUMN `cuisine` TEXT")
                        }
                    },
                )
                .build().also { instance = it }
        }
    }
}
