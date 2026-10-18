package com.example.foodplanner.data;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlannerDao_Impl implements PlannerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PriceItem> __insertionAdapterOfPriceItem;

  private final EntityInsertionAdapter<Recipe> __insertionAdapterOfRecipe;

  private final EntityInsertionAdapter<Ingredient> __insertionAdapterOfIngredient;

  private final EntityInsertionAdapter<PlannedMeal> __insertionAdapterOfPlannedMeal;

  private final EntityInsertionAdapter<GroceryCheck> __insertionAdapterOfGroceryCheck;

  private final EntityDeletionOrUpdateAdapter<Recipe> __updateAdapterOfRecipe;

  private final SharedSQLiteStatement __preparedStmtOfClearPrice;

  private final SharedSQLiteStatement __preparedStmtOfClearIngredients;

  private final SharedSQLiteStatement __preparedStmtOfDeleteRecipe;

  private final SharedSQLiteStatement __preparedStmtOfDeleteMeal;

  private final SharedSQLiteStatement __preparedStmtOfUncheck;

  public PlannerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPriceItem = new EntityInsertionAdapter<PriceItem>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `PriceItem` (`name`,`price`,`basis`) VALUES (?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PriceItem entity) {
        statement.bindString(1, entity.getName());
        statement.bindDouble(2, entity.getPrice());
        statement.bindString(3, entity.getBasis());
      }
    };
    this.__insertionAdapterOfRecipe = new EntityInsertionAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Recipe` (`id`,`name`,`servings`,`sourceUrl`,`instructions`,`cuisine`) VALUES (nullif(?, 0),?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Recipe entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getServings());
        if (entity.getSourceUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSourceUrl());
        }
        if (entity.getInstructions() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getInstructions());
        }
        if (entity.getCuisine() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCuisine());
        }
      }
    };
    this.__insertionAdapterOfIngredient = new EntityInsertionAdapter<Ingredient>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `Ingredient` (`id`,`recipeId`,`name`,`quantity`,`unit`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Ingredient entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getRecipeId());
        statement.bindString(3, entity.getName());
        if (entity.getQuantity() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getQuantity());
        }
        statement.bindString(5, entity.getUnit());
      }
    };
    this.__insertionAdapterOfPlannedMeal = new EntityInsertionAdapter<PlannedMeal>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `PlannedMeal` (`id`,`date`,`mealType`,`recipeId`,`auto`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlannedMeal entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getDate());
        statement.bindString(3, entity.getMealType());
        statement.bindLong(4, entity.getRecipeId());
        final int _tmp = entity.getAuto() ? 1 : 0;
        statement.bindLong(5, _tmp);
      }
    };
    this.__insertionAdapterOfGroceryCheck = new EntityInsertionAdapter<GroceryCheck>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `GroceryCheck` (`key`) VALUES (?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final GroceryCheck entity) {
        statement.bindString(1, entity.getKey());
      }
    };
    this.__updateAdapterOfRecipe = new EntityDeletionOrUpdateAdapter<Recipe>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `Recipe` SET `id` = ?,`name` = ?,`servings` = ?,`sourceUrl` = ?,`instructions` = ?,`cuisine` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Recipe entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindLong(3, entity.getServings());
        if (entity.getSourceUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getSourceUrl());
        }
        if (entity.getInstructions() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getInstructions());
        }
        if (entity.getCuisine() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCuisine());
        }
        statement.bindLong(7, entity.getId());
      }
    };
    this.__preparedStmtOfClearPrice = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM PriceItem WHERE name = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearIngredients = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM Ingredient WHERE recipeId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteRecipe = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM Recipe WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteMeal = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM PlannedMeal WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUncheck = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM GroceryCheck WHERE `key` = ?";
        return _query;
      }
    };
  }

  @Override
  public Object setPrice(final PriceItem item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPriceItem.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertRecipe(final Recipe recipe, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfRecipe.insertAndReturnId(recipe);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertIngredients(final List<Ingredient> items,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfIngredient.insert(items);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertMeal(final PlannedMeal meal, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlannedMeal.insert(meal);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object check(final GroceryCheck item, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfGroceryCheck.insert(item);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRecipe(final Recipe recipe, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfRecipe.handle(recipe);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object saveRecipe(final Recipe recipe, final List<Ingredient> items,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PlannerDao.DefaultImpls.saveRecipe(PlannerDao_Impl.this, recipe, items, __cont), $completion);
  }

  @Override
  public Object clearPrice(final String name, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearPrice.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, name);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearPrice.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearIngredients(final long recipeId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearIngredients.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, recipeId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearIngredients.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteRecipe(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteRecipe.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteRecipe.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteMeal(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteMeal.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteMeal.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object uncheck(final String key, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUncheck.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, key);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUncheck.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Recipe>> recipes() {
    final String _sql = "SELECT * FROM Recipe ORDER BY name COLLATE NOCASE";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"Recipe"}, new Callable<List<Recipe>>() {
      @Override
      @NonNull
      public List<Recipe> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfServings = CursorUtil.getColumnIndexOrThrow(_cursor, "servings");
          final int _cursorIndexOfSourceUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceUrl");
          final int _cursorIndexOfInstructions = CursorUtil.getColumnIndexOrThrow(_cursor, "instructions");
          final int _cursorIndexOfCuisine = CursorUtil.getColumnIndexOrThrow(_cursor, "cuisine");
          final List<Recipe> _result = new ArrayList<Recipe>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Recipe _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final int _tmpServings;
            _tmpServings = _cursor.getInt(_cursorIndexOfServings);
            final String _tmpSourceUrl;
            if (_cursor.isNull(_cursorIndexOfSourceUrl)) {
              _tmpSourceUrl = null;
            } else {
              _tmpSourceUrl = _cursor.getString(_cursorIndexOfSourceUrl);
            }
            final String _tmpInstructions;
            if (_cursor.isNull(_cursorIndexOfInstructions)) {
              _tmpInstructions = null;
            } else {
              _tmpInstructions = _cursor.getString(_cursorIndexOfInstructions);
            }
            final String _tmpCuisine;
            if (_cursor.isNull(_cursorIndexOfCuisine)) {
              _tmpCuisine = null;
            } else {
              _tmpCuisine = _cursor.getString(_cursorIndexOfCuisine);
            }
            _item = new Recipe(_tmpId,_tmpName,_tmpServings,_tmpSourceUrl,_tmpInstructions,_tmpCuisine);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Ingredient>> ingredients() {
    final String _sql = "SELECT * FROM Ingredient";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"Ingredient"}, new Callable<List<Ingredient>>() {
      @Override
      @NonNull
      public List<Ingredient> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfRecipeId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipeId");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfQuantity = CursorUtil.getColumnIndexOrThrow(_cursor, "quantity");
          final int _cursorIndexOfUnit = CursorUtil.getColumnIndexOrThrow(_cursor, "unit");
          final List<Ingredient> _result = new ArrayList<Ingredient>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Ingredient _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpRecipeId;
            _tmpRecipeId = _cursor.getLong(_cursorIndexOfRecipeId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final Double _tmpQuantity;
            if (_cursor.isNull(_cursorIndexOfQuantity)) {
              _tmpQuantity = null;
            } else {
              _tmpQuantity = _cursor.getDouble(_cursorIndexOfQuantity);
            }
            final String _tmpUnit;
            _tmpUnit = _cursor.getString(_cursorIndexOfUnit);
            _item = new Ingredient(_tmpId,_tmpRecipeId,_tmpName,_tmpQuantity,_tmpUnit);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PlannedMeal>> meals() {
    final String _sql = "SELECT * FROM PlannedMeal";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"PlannedMeal"}, new Callable<List<PlannedMeal>>() {
      @Override
      @NonNull
      public List<PlannedMeal> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfMealType = CursorUtil.getColumnIndexOrThrow(_cursor, "mealType");
          final int _cursorIndexOfRecipeId = CursorUtil.getColumnIndexOrThrow(_cursor, "recipeId");
          final int _cursorIndexOfAuto = CursorUtil.getColumnIndexOrThrow(_cursor, "auto");
          final List<PlannedMeal> _result = new ArrayList<PlannedMeal>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlannedMeal _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpDate;
            _tmpDate = _cursor.getString(_cursorIndexOfDate);
            final String _tmpMealType;
            _tmpMealType = _cursor.getString(_cursorIndexOfMealType);
            final long _tmpRecipeId;
            _tmpRecipeId = _cursor.getLong(_cursorIndexOfRecipeId);
            final boolean _tmpAuto;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfAuto);
            _tmpAuto = _tmp != 0;
            _item = new PlannedMeal(_tmpId,_tmpDate,_tmpMealType,_tmpRecipeId,_tmpAuto);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<String>> checked() {
    final String _sql = "SELECT `key` FROM GroceryCheck";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"GroceryCheck"}, new Callable<List<String>>() {
      @Override
      @NonNull
      public List<String> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<String> _result = new ArrayList<String>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final String _item;
            _item = _cursor.getString(0);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<PriceItem>> prices() {
    final String _sql = "SELECT * FROM PriceItem";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"PriceItem"}, new Callable<List<PriceItem>>() {
      @Override
      @NonNull
      public List<PriceItem> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPrice = CursorUtil.getColumnIndexOrThrow(_cursor, "price");
          final int _cursorIndexOfBasis = CursorUtil.getColumnIndexOrThrow(_cursor, "basis");
          final List<PriceItem> _result = new ArrayList<PriceItem>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PriceItem _item;
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpPrice;
            _tmpPrice = _cursor.getDouble(_cursorIndexOfPrice);
            final String _tmpBasis;
            _tmpBasis = _cursor.getString(_cursorIndexOfBasis);
            _item = new PriceItem(_tmpName,_tmpPrice,_tmpBasis);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object deleteAutoDinners(final List<String> dates,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
        _stringBuilder.append("DELETE FROM PlannedMeal WHERE auto = 1 AND mealType = 'Dinner' AND date IN (");
        final int _inputSize = dates.size();
        StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
        _stringBuilder.append(")");
        final String _sql = _stringBuilder.toString();
        final SupportSQLiteStatement _stmt = __db.compileStatement(_sql);
        int _argIndex = 1;
        for (String _item : dates) {
          _stmt.bindString(_argIndex, _item);
          _argIndex++;
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
