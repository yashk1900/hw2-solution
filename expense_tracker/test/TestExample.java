// package test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import java.util.Date;
import java.util.List;

import javax.swing.text.View;
import java.text.ParseException;
import org.junit.Before;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;
import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import view.ExpenseTrackerView;
import java.awt.*;
import javax.swing.*;
import static org.junit.Assert.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

public class TestExample {
  
  private ExpenseTrackerModel model;
  private ExpenseTrackerView view;
  private ExpenseTrackerController controller;
  private AmountFilter amountFilter;
  private CategoryFilter categoryFilter;
  private JOptionPane jOptionPane;

  @Before
  public void setup() {
    model = new ExpenseTrackerModel();
    view = new ExpenseTrackerView();
    controller = new ExpenseTrackerController(model, view);
    jOptionPane = new JOptionPane();
    ExpenseTrackerApp.initialize(view, controller, jOptionPane);
  }

    public double getTotalCost() {
        double totalCost = 0.0;
        List<Transaction> allTransactions = model.getTransactions(); // Using the model's getTransactions method
        for (Transaction transaction : allTransactions) {
            totalCost += transaction.getAmount();
        }
        return totalCost;
    }


    public void checkTransaction(double amount, String category, Transaction transaction) {
	assertEquals(amount, transaction.getAmount(), 0.01);
        assertEquals(category, transaction.getCategory());
        String transactionDateString = transaction.getTimestamp();
        Date transactionDate = null;
        try {
            transactionDate = Transaction.dateFormatter.parse(transactionDateString);
        }
        catch (ParseException pe) {
            pe.printStackTrace();
            transactionDate = null;
        }
        Date nowDate = new Date();
        assertNotNull(transactionDate);
        assertNotNull(nowDate);
        // They may differ by 60 ms
        assertTrue(nowDate.getTime() - transactionDate.getTime() < 60000);
    }

    //1 -- Existing
    @Test
    public void testAddTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add a transaction
	    double amount = 50.0;
	    String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        // Post-condition: List of transactions contains only
	    // The added transaction	
        assertEquals(1, model.getTransactions().size());
    
        // Check the contents of the list
	    Transaction firstTransaction = model.getTransactions().get(0);
	    checkTransaction(amount, category, firstTransaction);
	
	    // Check the total amount
        assertEquals(amount, getTotalCost(), 0.01);
    }

    //2 -- Existing
    @Test
    public void testRemoveTransaction() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add and remove a transaction
	    double amount = 50.0;
	    String category = "food";
        Transaction addedTransaction = new Transaction(amount, category);
        model.addTransaction(addedTransaction);
    
        // Pre-condition: List of transactions contains only
	    // The added transaction
        assertEquals(1, model.getTransactions().size());
	    Transaction firstTransaction = model.getTransactions().get(0);
	    checkTransaction(amount, category, firstTransaction);

	    assertEquals(amount, getTotalCost(), 0.01);
	
	    // Perform the action: Remove the transaction
        model.removeTransaction(addedTransaction);
    
        // Post-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());
    
        // Check the total cost after removing the transaction
        double totalCost = getTotalCost();
        assertEquals(0.00, totalCost, 0.01);
    }

    //New Test Cases
    
    //1
    @Test
    public void testAddTransactionSucceedsView() {

        // Pre-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());

        // Perform the action: Add a transaction
        double amount = 100.0;
        String category = "bills";
        assertTrue(controller.addTransaction(amount, category));

        // Post-condition: List of transactions contains only : 1 transaction row and 1 sum total row
        assertEquals(2, view.getTableModel().getRowCount());
        assertEquals(1, (int)view.getTableModel().getValueAt(0, 0));
        assertEquals(amount, (double)view.getTableModel().getValueAt(0, 1), 0.01);
        assertEquals(category, view.getTableModel().getValueAt(0, 2));

        // Check the total amount
        assertEquals(amount, (double)view.getTableModel().getValueAt(1, 3), 0.01);
    }

}