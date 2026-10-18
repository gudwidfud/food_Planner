package com.example.foodplanner.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDb_Impl extends AppDb {
  private volatile PlannerDao _plannerDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(5) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `Recipe` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `servings` INTEGER NOT NULL, `sourceUrl` TEXT, `instructions` TEXT, `cuisine` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `Ingredient` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `recipeId` INTEGER NOT NULL, `name` TEXT NOT NULL, `quantity` REAL, `unit` TEXT NOT NULL, FOREIGN KEY(`recipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_Ingredient_recipeId` ON `Ingredient` (`recipeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `PlannedMeal` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT NOT NULL, `mealType` TEXT NOT NULL, `recipeId` INTEGER NOT NULL, `auto` INTEGER NOT NULL, FOREIGN KEY(`recipeId`) REFERENCES `Recipe`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_PlannedMeal_recipeId` ON `PlannedMeal` (`recipeId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `GroceryCheck` (`key` TEXT NOT NULL, PRIMARY KEY(`key`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `PriceItem` (`name` TEXT NOT NULL, `price` REAL NOT NULL, `basis` TEXT NOT NULL, PRIMARY KEY(`name`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f19c1fa9a70dcaf0d063781c9ab468fb')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `Recipe`");
        db.execSQL("DROP TABLE IF EXISTS `Ingredient`");
        db.execSQL("DROP TABLE IF EXISTS `PlannedMeal`");
        db.execSQL("DROP TABLE IF EXISTS `GroceryCheck`");
        db.execSQL("DROP TABLE IF EXISTS `PriceItem`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsRecipe = new HashMap<String, TableInfo.Column>(6);
        _columnsRecipe.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipe.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipe.put("servings", new TableInfo.Column("servings", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipe.put("sourceUrl", new TableInfo.Column("sourceUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipe.put("instructions", new TableInfo.Column("instructions", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecipe.put("cuisine", new TableInfo.Column("cuisine", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysRecipe = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesRecipe = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecipe = new TableInfo("Recipe", _columnsRecipe, _foreignKeysRecipe, _indicesRecipe);
        final TableInfo _existingRecipe = TableInfo.read(db, "Recipe");
        if (!_infoRecipe.equals(_existingRecipe)) {
          return new RoomOpenHelper.ValidationResult(false, "Recipe(com.example.foodplanner.data.Recipe).\n"
                  + " Expected:\n" + _infoRecipe + "\n"
                  + " Found:\n" + _existingRecipe);
        }
        final HashMap<String, TableInfo.Column> _columnsIngredient = new HashMap<String, TableInfo.Column>(5);
        _columnsIngredient.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredient.put("recipeId", new TableInfo.Column("recipeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredient.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredient.put("quantity", new TableInfo.Column("quantity", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsIngredient.put("unit", new TableInfo.Column("unit", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysIngredient = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysIngredient.add(new TableInfo.ForeignKey("Recipe", "CASCADE", "NO ACTION", Arrays.asList("recipeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesIngredient = new HashSet<TableInfo.Index>(1);
        _indicesIngredient.add(new TableInfo.Index("index_Ingredient_recipeId", false, Arrays.asList("recipeId"), Arrays.asList("ASC")));
        final TableInfo _infoIngredient = new TableInfo("Ingredient", _columnsIngredient, _foreignKeysIngredient, _indicesIngredient);
        final TableInfo _existingIngredient = TableInfo.read(db, "Ingredient");
        if (!_infoIngredient.equals(_existingIngredient)) {
          return new RoomOpenHelper.ValidationResult(false, "Ingredient(com.example.foodplanner.data.Ingredient).\n"
                  + " Expected:\n" + _infoIngredient + "\n"
                  + " Found:\n" + _existingIngredient);
        }
        final HashMap<String, TableInfo.Column> _columnsPlannedMeal = new HashMap<String, TableInfo.Column>(5);
        _columnsPlannedMeal.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlannedMeal.put("date", new TableInfo.Column("date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlannedMeal.put("mealType", new TableInfo.Column("mealType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlannedMeal.put("recipeId", new TableInfo.Column("recipeId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlannedMeal.put("auto", new TableInfo.Column("auto", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlannedMeal = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysPlannedMeal.add(new TableInfo.ForeignKey("Recipe", "CASCADE", "NO ACTION", Arrays.asList("recipeId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesPlannedMeal = new HashSet<TableInfo.Index>(1);
        _indicesPlannedMeal.add(new TableInfo.Index("index_PlannedMeal_recipeId", false, Arrays.asList("recipeId"), Arrays.asList("ASC")));
        final TableInfo _infoPlannedMeal = new TableInfo("PlannedMeal", _columnsPlannedMeal, _foreignKeysPlannedMeal, _indicesPlannedMeal);
        final TableInfo _existingPlannedMeal = TableInfo.read(db, "PlannedMeal");
        if (!_infoPlannedMeal.equals(_existingPlannedMeal)) {
          return new RoomOpenHelper.ValidationResult(false, "PlannedMeal(com.example.foodplanner.data.PlannedMeal).\n"
                  + " Expected:\n" + _infoPlannedMeal + "\n"
                  + " Found:\n" + _existingPlannedMeal);
        }
        final HashMap<String, TableInfo.Column> _columnsGroceryCheck = new HashMap<String, TableInfo.Column>(1);
        _columnsGroceryCheck.put("key", new TableInfo.Column("key", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysGroceryCheck = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesGroceryCheck = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoGroceryCheck = new TableInfo("GroceryCheck", _columnsGroceryCheck, _foreignKeysGroceryCheck, _indicesGroceryCheck);
        final TableInfo _existingGroceryCheck = TableInfo.read(db, "GroceryCheck");
        if (!_infoGroceryCheck.equals(_existingGroceryCheck)) {
          return new RoomOpenHelper.ValidationResult(false, "GroceryCheck(com.example.foodplanner.data.GroceryCheck).\n"
                  + " Expected:\n" + _infoGroceryCheck + "\n"
                  + " Found:\n" + _existingGroceryCheck);
        }
        final HashMap<String, TableInfo.Column> _columnsPriceItem = new HashMap<String, TableInfo.Column>(3);
        _columnsPriceItem.put("name", new TableInfo.Column("name", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPriceItem.put("price", new TableInfo.Column("price", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPriceItem.put("basis", new TableInfo.Column("basis", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPriceItem = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPriceItem = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPriceItem = new TableInfo("PriceItem", _columnsPriceItem, _foreignKeysPriceItem, _indicesPriceItem);
        final TableInfo _existingPriceItem = TableInfo.read(db, "PriceItem");
        if (!_infoPriceItem.equals(_existingPriceItem)) {
          return new RoomOpenHelper.ValidationResult(false, "PriceItem(com.example.foodplanner.data.PriceItem).\n"
                  + " Expected:\n" + _infoPriceItem + "\n"
                  + " Found:\n" + _existingPriceItem);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "f19c1fa9a70dcaf0d063781c9ab468fb", "63f529d614bc8a7ebf84e0a8ffaf9ad3");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "Recipe","Ingredient","PlannedMeal","GroceryCheck","PriceItem");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `Recipe`");
      _db.execSQL("DELETE FROM `Ingredient`");
      _db.execSQL("DELETE FROM `PlannedMeal`");
      _db.execSQL("DELETE FROM `GroceryCheck`");
      _db.execSQL("DELETE FROM `PriceItem`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PlannerDao.class, PlannerDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PlannerDao dao() {
    if (_plannerDao != null) {
      return _plannerDao;
    } else {
      synchronized(this) {
        if(_plannerDao == null) {
          _plannerDao = new PlannerDao_Impl(this);
        }
        return _plannerDao;
      }
    }
  }
}
