package dev.sebastiano.bundel.storage;

import android.database.Cursor;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import dev.sebastiano.bundel.storage.model.DbNotification;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.functions.Function1;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation"})
public final class Dao_Impl extends Dao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DbNotification> __insertionAdapterOfDbNotification;

  private final SharedSQLiteStatement __preparedStmtOfClearNotifications;

  private final SharedSQLiteStatement __preparedStmtOfClearNotifications_1;

  private final SharedSQLiteStatement __preparedStmtOfDeleteNotificationById;

  public Dao_Impl(RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDbNotification = new EntityInsertionAdapter<DbNotification>(__db) {
      @Override
      public String createQuery() {
        return "INSERT OR REPLACE INTO `notifications` (`notification_id`,`uid`,`notification_key`,`timestamp`,`showTimestamp`,`isGroup`,`text`,`title`,`subText`,`titleBig`,`app_package`) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      public void bind(SupportSQLiteStatement stmt, DbNotification value) {
        stmt.bindLong(1, value.getId());
        if (value.getUniqueId() == null) {
          stmt.bindNull(2);
        } else {
          stmt.bindString(2, value.getUniqueId());
        }
        if (value.getKey() == null) {
          stmt.bindNull(3);
        } else {
          stmt.bindString(3, value.getKey());
        }
        stmt.bindLong(4, value.getTimestamp());
        final int _tmp;
        _tmp = value.getShowTimestamp() ? 1 : 0;
        stmt.bindLong(5, _tmp);
        final int _tmp_1;
        _tmp_1 = value.isGroup() ? 1 : 0;
        stmt.bindLong(6, _tmp_1);
        if (value.getText() == null) {
          stmt.bindNull(7);
        } else {
          stmt.bindString(7, value.getText());
        }
        if (value.getTitle() == null) {
          stmt.bindNull(8);
        } else {
          stmt.bindString(8, value.getTitle());
        }
        if (value.getSubText() == null) {
          stmt.bindNull(9);
        } else {
          stmt.bindString(9, value.getSubText());
        }
        if (value.getTitleBig() == null) {
          stmt.bindNull(10);
        } else {
          stmt.bindString(10, value.getTitleBig());
        }
        if (value.getAppPackageName() == null) {
          stmt.bindNull(11);
        } else {
          stmt.bindString(11, value.getAppPackageName());
        }
      }
    };
    this.__preparedStmtOfClearNotifications = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM notifications";
        return _query;
      }
    };
    this.__preparedStmtOfClearNotifications_1 = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM notifications WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteNotificationById = new SharedSQLiteStatement(__db) {
      @Override
      public String createQuery() {
        final String _query = "DELETE FROM notifications WHERE notification_id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertNotification(final DbNotification notification,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDbNotification.insert(notification);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteNotificationsById(final List<String> ids,
      final Continuation<? super Unit> continuation) {
    return RoomDatabaseKt.withTransaction(__db, new Function1<Continuation<? super Unit>, Object>() {
      @Override
      public Object invoke(Continuation<? super Unit> __cont) {
        return Dao_Impl.super.deleteNotificationsById(ids, __cont);
      }
    }, continuation);
  }

  @Override
  public Object clearNotifications(final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearNotifications.acquire();
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfClearNotifications.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object clearNotifications(final long olderThan,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearNotifications_1.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, olderThan);
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfClearNotifications_1.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Object deleteNotificationById(final String notificationId,
      final Continuation<? super Unit> continuation) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteNotificationById.acquire();
        int _argIndex = 1;
        if (notificationId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, notificationId);
        }
        __db.beginTransaction();
        try {
          _stmt.executeUpdateDelete();
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
          __preparedStmtOfDeleteNotificationById.release(_stmt);
        }
      }
    }, continuation);
  }

  @Override
  public Flow<List<DbNotification>> getNotifications() {
    final String _sql = "SELECT * FROM notifications";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[]{"notifications"}, new Callable<List<DbNotification>>() {
      @Override
      public List<DbNotification> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "notification_id");
          final int _cursorIndexOfUniqueId = CursorUtil.getColumnIndexOrThrow(_cursor, "uid");
          final int _cursorIndexOfKey = CursorUtil.getColumnIndexOrThrow(_cursor, "notification_key");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfShowTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "showTimestamp");
          final int _cursorIndexOfIsGroup = CursorUtil.getColumnIndexOrThrow(_cursor, "isGroup");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfSubText = CursorUtil.getColumnIndexOrThrow(_cursor, "subText");
          final int _cursorIndexOfTitleBig = CursorUtil.getColumnIndexOrThrow(_cursor, "titleBig");
          final int _cursorIndexOfAppPackageName = CursorUtil.getColumnIndexOrThrow(_cursor, "app_package");
          final List<DbNotification> _result = new ArrayList<DbNotification>(_cursor.getCount());
          while(_cursor.moveToNext()) {
            final DbNotification _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpUniqueId;
            if (_cursor.isNull(_cursorIndexOfUniqueId)) {
              _tmpUniqueId = null;
            } else {
              _tmpUniqueId = _cursor.getString(_cursorIndexOfUniqueId);
            }
            final String _tmpKey;
            if (_cursor.isNull(_cursorIndexOfKey)) {
              _tmpKey = null;
            } else {
              _tmpKey = _cursor.getString(_cursorIndexOfKey);
            }
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final boolean _tmpShowTimestamp;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfShowTimestamp);
            _tmpShowTimestamp = _tmp != 0;
            final boolean _tmpIsGroup;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsGroup);
            _tmpIsGroup = _tmp_1 != 0;
            final String _tmpText;
            if (_cursor.isNull(_cursorIndexOfText)) {
              _tmpText = null;
            } else {
              _tmpText = _cursor.getString(_cursorIndexOfText);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpSubText;
            if (_cursor.isNull(_cursorIndexOfSubText)) {
              _tmpSubText = null;
            } else {
              _tmpSubText = _cursor.getString(_cursorIndexOfSubText);
            }
            final String _tmpTitleBig;
            if (_cursor.isNull(_cursorIndexOfTitleBig)) {
              _tmpTitleBig = null;
            } else {
              _tmpTitleBig = _cursor.getString(_cursorIndexOfTitleBig);
            }
            final String _tmpAppPackageName;
            if (_cursor.isNull(_cursorIndexOfAppPackageName)) {
              _tmpAppPackageName = null;
            } else {
              _tmpAppPackageName = _cursor.getString(_cursorIndexOfAppPackageName);
            }
            _item = new DbNotification(_tmpId,_tmpUniqueId,_tmpKey,_tmpTimestamp,_tmpShowTimestamp,_tmpIsGroup,_tmpText,_tmpTitle,_tmpSubText,_tmpTitleBig,_tmpAppPackageName);
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

  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
