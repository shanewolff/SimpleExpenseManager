package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.PersistentTransactionDAO;

/**
 * Created by shane on 20-Nov-16.
 */

public class PersistentExpenseManager extends ExpenseManager {
    private Context context;
    public PersistentExpenseManager(Context context){
         //setup function should be invoked inside the constructor to get expense manager initialized.
        this.context = context;
        setup();
    }
    @Override
    public void setup(){
        //Creating or opening a new or existing database
        SQLiteDatabase newdatabase = context.openOrCreateDatabase("140701M", context.MODE_PRIVATE, null);

        //Create the database
        newdatabase.execSQL("CREATE TABLE IF NOT EXISTS account(" +
                "account_no VARCHAR PRIMARY KEY," +
                "bank VARCHAR," +
                "holder VARCHAR," +
                "initial_amount REAL" +
                " );");

        //
        newdatabase.execSQL("CREATE TABLE IF NOT EXISTS transaction_log(" +
                "transaction_id INTEGER PRIMARY KEY," +
                "account_no VARCHAR," +
                "type INT," +
                "amount REAL," +
                "date DATE," +
                "FOREIGN KEY (account_no) REFERENCES account(account_no)" +
                ");");

        //Following functions hold DAO instances in memory until the end of the program

        PersistentAccountDAO accountDAO = new PersistentAccountDAO(newdatabase);
        setAccountsDAO(accountDAO);
        setTransactionsDAO(new PersistentTransactionDAO(newdatabase));
    }
}
