package test.db.namespace;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper{
	 
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/test.db.namespace/databases/";
 
    private static String DB_NAME = "KCal";
 
    private SQLiteDatabase myDataBase; 
 // Database Name
    private static final String DATABASE_NAME = "KCal";
 
    // Contacts table name
    private static final String TABLE_CAL = "KCalendar";
 
    private final Context myContext;
 
    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {
 
    	super(context, DB_NAME, null, 3);
        this.myContext = context;
    }	
 
  /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase(); 
    	dbExist = false;
    	if(dbExist){
    		//do nothing - database already exist
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
               //of your application so we are gonna be able to overwrite that database with our database.
        	this.getReadableDatabase();
 
        	try {
 
    			copyDataBase();
 
    		} catch (IOException e) {
 
        		throw new Error("Error copying database");
 
        	}
    	}
 
    }
 
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = DB_PATH + DB_NAME;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
 
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }
 
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
 
    	//Open your local db as the input stream
    	InputStream myInput = myContext.getAssets().open(DB_NAME);
 
    	// Path to the just created empty db
    	String outFileName = DB_PATH + DB_NAME;
 
    	//Open the empty db as the output stream
    	OutputStream myOutput = new FileOutputStream(outFileName);
 
    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
    	while ((length = myInput.read(buffer))>0){
    		myOutput.write(buffer, 0, length);
    	}
 
    	//Close the streams
    	myOutput.flush();
    	myOutput.close();
    	myInput.close();
 
    }
 
    public void openDataBase() throws SQLException{
 
    	//Open the database
        String myPath = DB_PATH + DB_NAME;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    	
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();
 
    	    super.close();
 
	}
 
	@Override
	public void onCreate(SQLiteDatabase db) {
 
	}
 
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
 
	}
 
        // Add your public helper methods to access and get content from the database.
       // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
       // to you to create adapters for your views.
	  public Cursor getMonthInformation(int month)
	    {
	    	String[] Months =  {"MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC","JAN", "FEB"};
	    	String strMonth = Months[month-1];
	    	String countQuery; 
	    	if(month ==  1)
	    	{
	    		//2012 March
	    		countQuery = "SELECT * FROM " +  TABLE_CAL + " WHERE Date LIKE '%Mar'  LIMIT 9";
	    		
	    	} else
	    	if(month == 2)
	    	{
	    		//2012 Apr
	    		countQuery = "SELECT * FROM " +  TABLE_CAL + " WHERE Date LIKE '%Apr'  LIMIT 30";
	    	}else
	    	{
	    	countQuery = "SELECT  * FROM " + TABLE_CAL + " WHERE Date LIKE " + "\'%" + strMonth + "\'";
	    	} 
	    	 
	    	String table_query = "SELECT * FROM KCalendar";
	    	 String tb_query = "SELECT name FROM sqlite_master WHERE type = \"table\" ";
	    	 
	    	 SQLiteDatabase sql = this.getReadableDatabase();
	    	 
	    	 Cursor cursor1 = sql.rawQuery(tb_query, null);
	    	 Map <String,String> map = myDataBase.getSyncedTables();
	         //Cursor cursor = myDataBase.rawQuery(countQuery, null);
	    	 String tab = new String();
	    	 int count = cursor1.getCount();
	    	 if(cursor1.moveToFirst())
	    	 {
	    		tab = cursor1.getString(0);
	    	 }
	    	 String t = new String(tab);
	         return cursor1;
	    	
	    }
	 
	}