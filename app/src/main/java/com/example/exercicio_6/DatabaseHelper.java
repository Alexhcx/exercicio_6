package com.example.exercicio_6;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mysteries.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_ENTITIES = "entities";
    private static final String COLUMN_ENTITY_ID = "id";
    private static final String COLUMN_ENTITY_NAME = "name";
    private static final String COLUMN_ENTITY_DESCRIPTION = "description";
    private static final String COLUMN_ENTITY_IMAGE = "image";
    private static final String TABLE_SIGHTINGS = "sightings";
    private static final String COLUMN_SIGHTING_ID = "id";
    private static final String COLUMN_SIGHTING_ENTITY_ID = "entity_id";
    private static final String COLUMN_SIGHTING_DATA = "data";
    private static final String COLUMN_SIGHTING_HORARIO = "horario";
    private static final String COLUMN_SIGHTING_LOCAL = "local";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ENTITIES_TABLE = "CREATE TABLE " + TABLE_ENTITIES + "("
                + COLUMN_ENTITY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ENTITY_NAME + " TEXT,"
                + COLUMN_ENTITY_DESCRIPTION + " TEXT,"
                + COLUMN_ENTITY_IMAGE + " TEXT" + ")"; // Adiciona a coluna image
        db.execSQL(CREATE_ENTITIES_TABLE);

        String CREATE_SIGHTINGS_TABLE = "CREATE TABLE " + TABLE_SIGHTINGS + "("
                + COLUMN_SIGHTING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_SIGHTING_ENTITY_ID + " INTEGER,"
                + COLUMN_SIGHTING_DATA + " TEXT,"
                + COLUMN_SIGHTING_HORARIO + " TEXT,"
                + COLUMN_SIGHTING_LOCAL + " TEXT,"
                + "FOREIGN KEY(" + COLUMN_SIGHTING_ENTITY_ID + ") REFERENCES " + TABLE_ENTITIES + "(" + COLUMN_ENTITY_ID + ")" + ")";
        db.execSQL(CREATE_SIGHTINGS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTITIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SIGHTINGS);
        onCreate(db);
    }

    public void addEntity(Entity entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ENTITY_NAME, entity.getName());
        values.put(COLUMN_ENTITY_DESCRIPTION, entity.getDescription());
        values.put(COLUMN_ENTITY_IMAGE, entity.getImage());

        db.insert(TABLE_ENTITIES, null, values);
        db.close();
    }

    public void updateEntity(Entity entity) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ENTITY_NAME, entity.getName());
        values.put(COLUMN_ENTITY_DESCRIPTION, entity.getDescription());
        values.put(COLUMN_ENTITY_IMAGE, entity.getImage());

        db.update(TABLE_ENTITIES, values, COLUMN_ENTITY_ID + " = ?", new String[]{String.valueOf(entity.getId())});
        db.close();
    }

    public void deleteEntity(int entityId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENTITIES, COLUMN_ENTITY_ID + " = ?", new String[]{String.valueOf(entityId)});
        db.close();
    }

    public List<Entity> getAllEntities() {
        List<Entity> entities = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_ENTITIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Entity entity = new Entity();
                entity.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_ID)));
                entity.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_NAME)));
                entity.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_DESCRIPTION)));
                entity.setImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_IMAGE))); // Recupera a imagem

                entities.add(entity);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return entities;
    }

    public Entity getEntityById(int entityId) {
        Entity entity = null;

        String selectQuery = "SELECT * FROM " + TABLE_ENTITIES + " WHERE " + COLUMN_ENTITY_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(entityId)});

        if (cursor.moveToFirst()) {
            entity = new Entity();
            entity.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_ID)));
            entity.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_NAME)));
            entity.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_DESCRIPTION)));
            entity.setImage(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ENTITY_IMAGE)));
        }

        cursor.close();
        db.close();

        return entity;
    }

    public void addSighting(int entityId, String data, String horario, String local) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SIGHTING_ENTITY_ID, entityId);
        values.put(COLUMN_SIGHTING_DATA, data);
        values.put(COLUMN_SIGHTING_HORARIO, horario);
        values.put(COLUMN_SIGHTING_LOCAL, local);

        db.insert(TABLE_SIGHTINGS, null, values);
        db.close();
    }

    public void updateSighting(int id, int entityId, String data, String horario, String local) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_SIGHTING_ENTITY_ID, entityId);
        values.put(COLUMN_SIGHTING_DATA, data);
        values.put(COLUMN_SIGHTING_HORARIO, horario);
        values.put(COLUMN_SIGHTING_LOCAL, local);

        db.update(TABLE_SIGHTINGS, values, COLUMN_SIGHTING_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public void deleteSighting(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_SIGHTINGS, COLUMN_SIGHTING_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Sighting> getSightingsByEntityId(int entityId) {
        List<Sighting> sightings = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_SIGHTINGS + " WHERE " + COLUMN_SIGHTING_ENTITY_ID + " = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(entityId)});

        if (cursor.moveToFirst()) {
            do {
                Sighting sighting = new Sighting();
                sighting.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SIGHTING_ID)));
                sighting.setEntityId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SIGHTING_ENTITY_ID)));
                sighting.setData(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIGHTING_DATA))); // Nova coluna para a data
                sighting.setHorario(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIGHTING_HORARIO))); // Nova coluna para o horário
                sighting.setLocal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SIGHTING_LOCAL))); // Nova coluna para o local

                sightings.add(sighting);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return sightings;
    }
}