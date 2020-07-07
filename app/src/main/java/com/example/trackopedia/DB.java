package com.example.trackopedia;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class DB {

    private static final String DBNAME="Step_Counter";
    private static final String TBUSER="usertb";
    private static final String TBSTEPS="stepstb";
    private static final int DBVERSION=1;

    private static final String UNAME="uname";
    private static final String UAGE="uage";
    private static final String UHEIGHT="uheight";
    private static final String UWEIGHT="uweight";
    private static final String UGENDER="ugen";
    private static final String UCONT="ucont";
    private static final String UEMAIL="uemail";
    private static final String UPASS="upass";

    private static final String DATE="sdate";
    private static final String STEPS="steps";

    SQLiteDatabase sqldb;
    dbhelper dbh;
    Context con;


    public class dbhelper extends SQLiteOpenHelper
    {

        public dbhelper(Context context) {
            super(context, DBNAME, null, DBVERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table "+TBUSER+" (" + UNAME + " TEXT NOT NULL , " + UAGE + " TEXT NOT NULL ," +
                    UHEIGHT+" TEXT NOT NULL, " + UWEIGHT +" TEXT NOT NULL, " + UGENDER + " TEXT NOT NULL , " +
                    UCONT +" TEXT NOT NULL, " + UEMAIL + " TEXT NOT NULL , " + UPASS + " TEXT NOT NULL);");

            db.execSQL("create table "+TBSTEPS+" (" + UEMAIL + " TEXT NOT NULL , " + DATE + " TEXT NOT NULL ," +
                    STEPS + " TEXT NOT NULL);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TBUSER + "");
            db.execSQL("DROP TABLE IF EXISTS " + TBSTEPS + "");
        }
    }

    public DB(Context c)
    {
        con=c;
    }

    public DB open()
    {
        dbh=new dbhelper(con);
        sqldb=dbh.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbh.close();
    }

    public Boolean login(String user, String pass)
    {
        Boolean ans=false;
        Cursor c=sqldb.query(TBUSER,null,UEMAIL + " = '"+user+"' AND " + UPASS + " = '"+pass+"' ",null,null,null,null);
        if(c.getCount()>0)
        {
            ans=true;
        }
        return ans;
    }

    public Boolean register(String name, String age, String height, String weight, String gender, String cont, String email, String pass)
    {
        Boolean ans=false;
        ContentValues cv=new ContentValues();
        cv.put(UNAME,name);
        cv.put(UAGE,age);
        cv.put(UHEIGHT,height);
        cv.put(UWEIGHT,weight);
        cv.put(UGENDER,gender);
        cv.put(UCONT,cont);
        cv.put(UEMAIL,email);
        cv.put(UPASS,pass);
        sqldb.insert(TBUSER,null,cv);
        ans=true;

        return ans;
    }


    public Boolean checkemail(String email)
    {
        Boolean ans=false;

        Cursor c=sqldb.query(TBUSER,null,UEMAIL + " = '"+email+"'",null,null,null,null);
        if(c.getCount()>0)
        {
            ans=true;
        }
        return ans;
    }

    public String getdetails(String email)
    {
        String ans="no";
        Cursor c=sqldb.rawQuery("select ugen,uage,uheight,uweight from "+TBUSER+" where "+UEMAIL+" = '"+email+"'",null);
        if(c.getCount()>0)
        {
            c.moveToFirst();
            ans=c.getString(0)+"*"+c.getString(1)+"*"+c.getString(2)+"*"+c.getString(3);
        }
        return ans;
    }

    public void update(String date, String uid, String steps)
    {
        Cursor c=sqldb.query(TBSTEPS,new String[]{STEPS},DATE + " = '"+date+"' AND "+ UEMAIL +" = '"+uid+"'",null,null,null,null);
        int i=0;
        if(c.getCount()>0)
        {
            c.moveToFirst();
            i= Integer.parseInt(c.getString(0))+ Integer.parseInt(steps);

            ContentValues cv=new ContentValues();
            cv.put(STEPS,""+i);
            sqldb.update(TBSTEPS,cv,DATE + " = '"+date+"' AND "+ UEMAIL +" = '"+uid+"'",null);
        }
        else
        {
            ContentValues cv=new ContentValues();
            cv.put(UEMAIL,uid);
            cv.put(DATE,date);
            cv.put(STEPS,steps);
            sqldb.insert(TBSTEPS,null,cv);
        }
    }

    public String getsteps(String uid)
    {
        String ans="";
        Cursor c=sqldb.query(TBSTEPS,new String[]{DATE,STEPS},UEMAIL +" = '"+uid+"'",null,null,null,DATE);
        if(c.getCount()>0)
        {
            while (c.moveToNext())
            {
                ans+=c.getString(0)+"*"+c.getString(1)+"#";
            }
        }
        else
        {
            ans="no";
        }

        return ans;
    }


    public boolean updateData(String email, String age, String height, String weight, String contact){
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(UAGE, age);
            contentValues.put(UHEIGHT, height);
            contentValues.put(UWEIGHT, weight);
            contentValues.put(UCONT,contact);
            sqldb.update(TBUSER, contentValues, UEMAIL + " =  '"+email+"' ",null);

        }catch (Exception e){
            Toast.makeText(con,e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return true;
    }



}
