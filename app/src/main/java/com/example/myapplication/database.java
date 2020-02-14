package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class database extends SQLiteOpenHelper {
    private static String databasename="mydatabase";
    SQLiteDatabase sqLiteDatabase;
    public database(@Nullable Context context) {
        super(context, databasename,null, 1);
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table users (Email text primary key,password integer,name text,age integer,weight integer,height integer,gender text,plann text,activ text,foodtype text,clusterid integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists users");
        onCreate(db);
    }
    void insert(String email,int pass,String name,int age,int weight,int height,String gender,String plan,String activ,String foodtypr,int clusterid){
        sqLiteDatabase =getWritableDatabase();
        ContentValues row = new ContentValues();
        row.put("Email",email);
        row.put("password",pass);
        row.put("name",name);
        row.put("age",age);
        row.put("weight",weight);
        row.put("height",height);
        row.put("gender",gender);
        row.put("plann",plan);
        row.put("activ",activ);
        row.put("foodtype",foodtypr);
        row.put("clusterid",clusterid);
        sqLiteDatabase.insert("users",null,row);
        sqLiteDatabase.close();
    }
}
