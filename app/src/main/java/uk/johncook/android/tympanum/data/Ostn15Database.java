package uk.johncook.android.tympanum.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.Executors;

@Database(entities = {Ostn15Point.class}, version = 1)
public abstract class Ostn15Database extends RoomDatabase {
    private static Ostn15Database INSTANCE;

    public abstract Ostn15Dao ostn15Dao();

    public static synchronized Ostn15Database getInstance(final Context context) {
        if (INSTANCE == null) {
            Executors.newSingleThreadExecutor().execute(
                    () -> INSTANCE = Room.databaseBuilder(context, Ostn15Database.class, "osten15.db")
                            .createFromAsset("databases/ostn15.db")
                            .fallbackToDestructiveMigration()
                            .build());
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}