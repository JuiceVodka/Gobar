package si.uni_lj.fri.pbd.gobar

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat

class DatabaseHelper(context: Context) :SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){

    companion object{
        const val TAG = "DatabaseHelper"
        const val DATABASE_VERSION = 2
        const val DATABASE_NAME = "gobar_db"
        const val TABLE_MUSHROOM_DETAILS = "MushroomDetails"
        const val TABLE_MUSHROOM_LOCATION = "MushroomLocation"
        const val MUSHROOM_COMMON_NAME = "NameCommon"
        const val MUSHROOM_LATIN_NAME = "NameLatin"
        const val MUSHROOM_LAT = "Latitude"
        const val MUSHROOM_LONG = "Longitude"
        const val MUSHROOM_EDIBILITY = "Edibility"
        const val MUSHROOM_IMAGE = "Image"
        const val MUSHROOM_IS_PSYCHOACTIVE = "IsPsychoActive"
        const val MUSHROOM_IS_DISCOVERED = "IsDiscovered"
        const val MUSHROOM_NUMBER_FOUND = "NumberFound"
        const val _ID = "_id"
        const val _IDL = "_idr"
        val COLUMNS_DETAILS = arrayOf(_ID, MUSHROOM_COMMON_NAME, MUSHROOM_LATIN_NAME, MUSHROOM_EDIBILITY, MUSHROOM_IS_PSYCHOACTIVE, MUSHROOM_IS_DISCOVERED, MUSHROOM_IMAGE, MUSHROOM_NUMBER_FOUND)
        val COLUMNS_LOCATION = arrayOf(_IDL, MUSHROOM_COMMON_NAME, MUSHROOM_LATIN_NAME, MUSHROOM_LAT, MUSHROOM_LONG)
    }

    override fun onCreate(db: SQLiteDatabase?) {
        Log.d("DEBUG", "onCreate")
        val CREATE_LOCATION_TABLE = ("CREATE TABLE "+ TABLE_MUSHROOM_LOCATION+"("
                + _IDL+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MUSHROOM_COMMON_NAME + " TEXT NOT NULL,"
                + MUSHROOM_LATIN_NAME + " TEXT NOT NULL,"
                + MUSHROOM_LAT + " TEXT NOT NULL,"
                + MUSHROOM_LONG + " TEXT NOT NULL)")

        val CREATE_DETAILS_TABLE = ("CREATE TABLE "+ TABLE_MUSHROOM_DETAILS+"("
                + _ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MUSHROOM_COMMON_NAME + " TEXT NOT NULL,"
                + MUSHROOM_LATIN_NAME + " TEXT NOT NULL,"
                + MUSHROOM_EDIBILITY + " TEXT NOT NULL,"
                + MUSHROOM_IS_PSYCHOACTIVE + " INTEGER NOT NULL,"
                + MUSHROOM_IS_DISCOVERED + " INTEGER NOT NULL,"
                + MUSHROOM_IMAGE + " TEXT NOT NULL,"
                + MUSHROOM_NUMBER_FOUND + " INTEGER NOT NULL)")

        db?.execSQL(CREATE_LOCATION_TABLE)
        db?.execSQL(CREATE_DETAILS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        Log.d(TAG, "onUpgrade")

        db?.execSQL("DROP TABLE IF EXISTS "+ TABLE_MUSHROOM_DETAILS)
        db?.execSQL("DROP TABLE IF EXISTS "+ TABLE_MUSHROOM_LOCATION)
        onCreate(db)
    }

    fun deleteDatabase(context: Context){
        context.deleteDatabase(DATABASE_NAME)
    }

    fun returnDetails() :List<MushroomDetailsModel>{
        val cursor = this.readableDatabase?.rawQuery("SELECT * FROM " + TABLE_MUSHROOM_DETAILS, null);

        val mushroomDetailsList = mutableListOf<MushroomDetailsModel>()

        if (cursor?.moveToFirst() == true) {
            do {
                mushroomDetailsList.add(
                    MushroomDetailsModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getInt(4),
                        cursor.getInt(5),
                        cursor.getString(6),
                        cursor.getInt(7)
                    )
                )
            } while (cursor.moveToNext())
        }
        return mushroomDetailsList
    }
    fun returnLocations() :List<MushroomLocationModel>{
        val cursor = this.readableDatabase?.rawQuery("SELECT * FROM " + TABLE_MUSHROOM_DETAILS, null);

        val mushroomLocationsList = mutableListOf<MushroomLocationModel>()

        if (cursor?.moveToFirst() == true) {
            do {
                mushroomLocationsList.add(
                    MushroomLocationModel(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4)
                    )
                )
            } while (cursor.moveToNext())
        }
        return mushroomLocationsList

    }
}