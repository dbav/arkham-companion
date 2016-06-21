package ah.drawer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper 
{

	static final String dbName="ahDB";
	
	//Neighborhoods
	static final String neighborhoodTable="Neighborhood";
	static final String neiID="NeighborhoodID";
	static final String neiName="Name";
	static final String neiExpID="ExpansionID";

	//Locations
	static final String locTable="Location";
	static final String locID="locID";
	static final String locName="locName";
	static final String locNeiID = "neiID";
	
	//Expansions
	static final String expTable="Expansion";
	static final String expID="expID";
	static final String expName="expName";
	
	//Encounters
	static final String encounterTable="Encounter";
	static final String encID = "encID";
	static final String encLocID = "locID";
	static final String encText = "encText";
	
	//Cards
	static final String cardTable="Card";
	static final String cardID = "cardID";
	static final String cardNeiID = "neiID";
	static final String cardExpID = "expID";
	
	//CardsToEncounter
	static final String cardToEncTable = "CardToEncounter";
	static final String cardToEncCardID = "cardID";
	static final String cardToEncEncID = "encID";

	static final String viewEmps="ViewEmps";
	
	public static DatabaseHelper instance; 
	
	private DatabaseHelper(Context context) {
		  super(context, dbName, null,40); 
		  }
	
	static public DatabaseHelper getInstance(Context context)
	{
		if(instance == null)
		{
			instance = new DatabaseHelper(context);
		}
		
		return instance;
	}
	
	
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		 
		  db.execSQL("CREATE TABLE "+expTable+" ("+expID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
				  expName + " TEXT)");
		  
		  db.execSQL("CREATE TABLE "+neighborhoodTable+" ("+neiID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				    		neiName+" TEXT, "+neiExpID+" INTEGER NOT NULL ,FOREIGN KEY ("+neiExpID+") REFERENCES "+expTable+" ("+expID+"));");
		  
		  db.execSQL("CREATE TABLE "+locTable+" ("+locID+ " INTEGER PRIMARY KEY AUTOINCREMENT, "+
				  locName + " TEXT, "+locNeiID+" INTEGER NOT NULL ,FOREIGN KEY ("+locNeiID+") REFERENCES "+neighborhoodTable+" ("+neiID+"));");
		  
		  db.execSQL("CREATE TABLE "+encounterTable+" ("+encID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
		    		encText+" TEXT, "+encLocID+" INTEGER NOT NULL ,FOREIGN KEY ("+encLocID+") REFERENCES "+locTable+" ("+locID+"));");
		  
		  db.execSQL("CREATE TABLE "+cardTable+" ("+cardID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
				  //cardEncID+" INTEGER NOT NULL ,"+
				  cardExpID+" INTEGER NOT NULL ,"+
				  cardNeiID+" INTEGER NOT NULL ,"+
				  "FOREIGN KEY ("+cardExpID+") REFERENCES "+expTable+" ("+expID+"), "+
				  "FOREIGN KEY ("+cardNeiID+") REFERENCES "+neighborhoodTable+" ("+neiID+"));");//+
				  //"FOREIGN KEY ("+cardEncID+") REFERENCES "+encounterTable+" ("+encID+"));");
		  
		  db.execSQL("CREATE TABLE "+cardToEncTable+" ("+cardToEncCardID+" INTEGER NOT NULL , "+
				  //cardEncID+" INTEGER NOT NULL ,"+
				  cardToEncEncID+" INTEGER NOT NULL ,"+
				  "PRIMARY KEY ("+cardToEncCardID+","+cardToEncEncID+"), "+
				  "FOREIGN KEY ("+cardToEncCardID+") REFERENCES "+cardTable+" ("+cardID+"), "+
				  "FOREIGN KEY ("+cardToEncEncID+") REFERENCES "+encounterTable+" ("+neiID+"));");//+
		  
		  //For referential integrity
		  db.execSQL("CREATE TRIGGER fk_neiexp_expid " +
				    " BEFORE INSERT "+
				    " ON "+neighborhoodTable+
				    
				    " FOR EACH ROW BEGIN"+
				    " SELECT CASE WHEN ((SELECT "+expID+" FROM "+expTable+
				    " WHERE "+expID+"=new."+neiExpID+" ) IS NULL)"+
				    " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
				    "  END;");
		  
		  //For referential integrity
		  db.execSQL("CREATE TRIGGER fk_locnei_neiid " +
				    " BEFORE INSERT "+
				    " ON "+locTable+
				    
				    " FOR EACH ROW BEGIN"+
				    " SELECT CASE WHEN ((SELECT "+neiID+" FROM "+neighborhoodTable+
				    " WHERE "+neiID+"=new."+locNeiID+" ) IS NULL)"+
				    " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
				    "  END;");
		  
		  //For referential integrity
		  db.execSQL("CREATE TRIGGER fk_encloc_locid " +
				    " BEFORE INSERT "+
				    " ON "+encounterTable+
				    " FOR EACH ROW BEGIN"+
				    " SELECT CASE WHEN ((SELECT "+locID+" FROM "+locTable+
				    " WHERE "+locID+"=new."+encLocID+" ) IS NULL)"+
				    " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
				    "  END;");
		  
		//For referential integrity
		  db.execSQL("CREATE TRIGGER fk_cardtoencenc_encid " +
				    " BEFORE INSERT "+
				    " ON "+cardToEncTable+
				    " FOR EACH ROW BEGIN"+
				    " SELECT CASE WHEN ((SELECT "+encID+" FROM "+encounterTable+
				    " WHERE "+encID+"=new."+cardToEncEncID+" ) IS NULL)"+
				    " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
				    "  END;");
		  
		  db.execSQL("CREATE TRIGGER fk_cardtoencenc_cardid " +
				    " BEFORE INSERT "+
				    " ON "+cardToEncTable+
				    " FOR EACH ROW BEGIN"+
				    " SELECT CASE WHEN ((SELECT "+cardID+" FROM "+cardTable+
				    " WHERE "+cardID+"=new."+cardToEncCardID+" ) IS NULL)"+
				    " THEN RAISE (ABORT,'Foreign Key Violation') END;"+
				    "  END;");

		  Init.FetchExpansion(db);
		  Init.FetchBase(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
		// TODO Auto-generated method stub

		  db.execSQL("DROP TABLE IF EXISTS "+encounterTable);
		  db.execSQL("DROP TABLE IF EXISTS "+expTable);
		  db.execSQL("DROP TABLE IF EXISTS "+locTable);
		  db.execSQL("DROP TABLE IF EXISTS "+neighborhoodTable);
		  db.execSQL("DROP TABLE IF EXISTS "+cardTable);
		  db.execSQL("DROP TABLE IF EXISTS "+cardToEncTable);
		  
		  db.execSQL("DROP TRIGGER IF EXISTS fk_neiexp_expid");
		  db.execSQL("DROP TRIGGER IF EXISTS fk_locnei_neiid");
		  db.execSQL("DROP TRIGGER IF EXISTS fk_encloc_locid");
		  db.execSQL("DROP TRIGGER IF EXISTS fk_cardtoencenc_encid");
		  db.execSQL("DROP TRIGGER IF EXISTS fk_cardtoencenc_cardid");

		  db.execSQL("DROP VIEW IF EXISTS "+viewEmps);
		  onCreate(db);
	}
}
