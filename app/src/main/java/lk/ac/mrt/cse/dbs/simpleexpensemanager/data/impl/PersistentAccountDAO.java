package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by shane on 20-Nov-16.
 */

public class PersistentAccountDAO implements AccountDAO {
    private SQLiteDatabase database;
    
    //Database is stored in the constructor to prevent reopening
    public PersistentAccountDAO(SQLiteDatabase db){
        this.database = db;
    }
    @Override
    public List<String> getAccountNumbersList() {
        //Iterator to loop
        Cursor result = database.rawQuery("SELECT account_no FROM account",null);

        //A list to store data
        List<String> accounts = new ArrayList<String>();

        //Adding data to the list
        if(result.moveToFirst()) {
            do {
                accounts.add(result.getString(result.getColumnIndex("account_no")));
            } while (result.moveToNext());
        }
        //Return the list
        return accounts;
    }

    @Override
    public List<Account> getAccountsList() {
        Cursor result = database.rawQuery("SELECT * FROM account",null);
        List<Account> accounts = new ArrayList<Account>();

        if(result.moveToFirst()) {
            do {
                Account account = new Account(result.getString(result.getColumnIndex("account_no")),
                        result.getString(result.getColumnIndex("bank")),
                        result.getString(result.getColumnIndex("holder")),
                        result.getDouble(result.getColumnIndex("initial_amount")));
                accounts.add(account);
            } while (result.moveToNext());
        }

        return accounts;
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        Cursor result = database.rawQuery("SELECT * FROM account WHERE account_no = " + accountNo,null);
        Account account = null;

        if(result.moveToFirst()) {
            do {
                account = new Account(result.getString(result.getColumnIndex("account_no")),
                        result.getString(result.getColumnIndex("bank")),
                        result.getString(result.getColumnIndex("holder")),
                        result.getDouble(result.getColumnIndex("initial_amount")));
            } while (result.moveToNext());
        }

        return account;
    }

    @Override
    public void addAccount(Account account) {
        //Prepared statements for insertions
        String sql = "INSERT INTO account (account_no, bank, holder, initial_amount) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);


        //Binding values
        statement.bindString(1, account.getAccountNo());
        statement.bindString(2, account.getBankName());
        statement.bindString(3, account.getAccountHolderName());
        statement.bindDouble(4, account.getBalance());

        //Execution
        statement.executeInsert();


    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        String sql = "DELETE FROM account WHERE account_no = ?";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1,accountNo);

        statement.executeUpdateDelete();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        String sql = "UPDATE account SET initial_amount = initial_amount + ?";
        SQLiteStatement statement = database.compileStatement(sql);
        if(expenseType == ExpenseType.EXPENSE){
            statement.bindDouble(1,-amount);
        }
        else{
            statement.bindDouble(1,amount);
        }

        statement.executeUpdateDelete();
    }
}
