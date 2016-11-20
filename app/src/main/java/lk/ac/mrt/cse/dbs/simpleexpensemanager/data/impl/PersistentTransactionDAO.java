package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by shane on 20-Nov-16.
 */

public class PersistentTransactionDAO implements TransactionDAO {
    private SQLiteDatabase database;

    public PersistentTransactionDAO(SQLiteDatabase db){
        this.database = db;
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        String sql = "INSERT INTO transaction_log (account_no, type, amount, date) VALUES (?,?,?,?)";
        SQLiteStatement statement = database.compileStatement(sql);

        statement.bindString(1,accountNo);
        statement.bindLong(2,(expenseType == ExpenseType.EXPENSE) ? 0 : 1);
        statement.bindDouble(3,amount);
        statement.bindLong(4,date.getTime());

        statement.executeInsert();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        Cursor resultSet = database.rawQuery("SELECT * FROM transaction_log",null);
        List<Transaction> transactions = new ArrayList<Transaction>();

        if(resultSet.moveToFirst()) {
            do{
                Transaction new_transaction = new Transaction(new Date(resultSet.getLong(resultSet.getColumnIndex("date"))),
                        resultSet.getString(resultSet.getColumnIndex("account_no")),
                        (resultSet.getInt(resultSet.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        resultSet.getDouble(resultSet.getColumnIndex("amount")));
                transactions.add(new_transaction);
            }while (resultSet.moveToNext());
        }
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        Cursor resultSet = database.rawQuery("SELECT * FROM transaction_log LIMIT " + limit,null);
        List<Transaction> transactions = new ArrayList<Transaction>();

        if(resultSet.moveToFirst()) {
            do {
                Transaction new_tansaction = new Transaction(new Date(resultSet.getLong(resultSet.getColumnIndex("date"))),
                        resultSet.getString(resultSet.getColumnIndex("account_no")),
                        (resultSet.getInt(resultSet.getColumnIndex("type")) == 0) ? ExpenseType.EXPENSE : ExpenseType.INCOME,
                        resultSet.getDouble(resultSet.getColumnIndex("amount")));
                transactions.add(new_tansaction);
            } while (resultSet.moveToNext());
        }

        return transactions;
    }
}
