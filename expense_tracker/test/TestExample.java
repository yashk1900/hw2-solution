// package test;
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
    public void testAddTransactionSucceeds_View() {

        // Pre-condition: List of transactions is empty
        List<Transaction> transactions = model.getTransactions();
        assertEquals(0, transactions.size());

        // Perform the action: Add a transaction
        double amount = 50.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));

        // Post-condition: List of transactions contains only : 1 transaction row and 1 sum total row
        assertEquals(2, view.getTableModel().getRowCount());
        assertEquals(1, (int)view.getTableModel().getValueAt(0, 0));
        assertEquals(amount, (double)view.getTableModel().getValueAt(0, 1), 0.01);
        assertEquals(category, view.getTableModel().getValueAt(0, 2));

        // Check the total amount
        assertEquals(amount, (double)view.getTableModel().getValueAt(1, 3), 0.01);
    }

    //2
    @Test
    public void testInvalidInputHandling(){

        //////////// Scenario 1
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
        
        // Attempting to add a transaction with an invalid amount
        double invalidAmount = -10.0; // Invalid amount
        String Category = "food";
        
        // Attempt to add an invalid transaction
        boolean added = controller.addTransaction(invalidAmount, Category);
        
        // Check that the transaction was not added
        assertEquals(0, model.getTransactions().size());
        assertEquals(0, getTotalCost(), 0.01);
        
        // Check that the addition was unsuccessful
        assertFalse(added);
 
        //////////// Scenario 2
        // Attempt to add a transaction with an invalid category
        double Amount = 10.0;
        String invalidCategory = "foods";
        
        // Attempt to add an invalid transaction
        added = controller.addTransaction(Amount, invalidCategory);
        
        // Check that the transaction was not added
        assertEquals(0, model.getTransactions().size());
        assertEquals(0, getTotalCost(), 0.01);
        
        // Check that the addition was unsuccessful
        assertFalse(added);
        
        //////////// Scenario 3
        //Reviewing via view for error messages
        JFormattedTextField textField = new JFormattedTextField();
        textField.setValue("100");
        view.setAmountField(textField);
 
        JTextField categoryField = new JTextField();
        categoryField.setText("foods");
        view.setCategoryField(categoryField);
        
        //jOptionPane is not showing before adding transaction
        assertFalse(jOptionPane.isShowing());
        
        //Attempting to add transaction on view
        view.getAddTransactionBtn().doClick();
 
        //jOptionPane view after adding
        assertTrue(jOptionPane.isShowing());
 
        //jOptionPane message check
        assertEquals("Invalid amount or category entered", jOptionPane.getMessage());
 
        //jOptionPane message type check
        assertEquals(jOptionPane.ERROR_MESSAGE, jOptionPane.getMessageType());
 
        //no transaction added
        assertEquals(0, model.getTransactions().size());
 
        // Checkung that the total amount is still 0
        assertEquals(0, getTotalCost(), 0.01);
    }

    //// 3
    @Test
    public void testAmountFilterTransaction(){
        
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add transactions
        double amount1 = 100.0;
        String category1 = "food";
        assertTrue(controller.addTransaction(amount1, category1));

        double amount2 = 100.0;
        String category2 = "food";
        assertTrue(controller.addTransaction(amount2, category2));

        double amount3 = 200.0;
        String category3 = "bills";
        assertTrue(controller.addTransaction(amount3, category3));

        //Applying filter
        AmountFilter amountFilter = new AmountFilter(amount1);
        controller.setFilter(amountFilter);
        List<Transaction> filteredTransactions = amountFilter.filter(model.getTransactions());

        //check that correct filtered transactions returned
        assertEquals(2, filteredTransactions.size());

        for (Transaction transaction : filteredTransactions){
            assertEquals(amount1, transaction.getAmount(), 0.01);
        }

        controller.applyFilter();

        JTable table = view.getTransactionsTable();

        //Checking that the first two transactions are highlighted and the 3rd one is not
        assertEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(0, 0), 0, 0).getBackground());
        assertEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(1, 0), 1, 0).getBackground());
        assertNotEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(2, 0), 2, 0).getBackground());

    }

    //// 4
    @Test
    public void testCategoryFilterTransaction(){
        
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());

        // Perform the action: Add transactions
        double amount1 = 100.0;
        String category1 = "food";
        assertTrue(controller.addTransaction(amount1, category1));

        double amount2 = 100.0;
        String category2 = "bills";
        assertTrue(controller.addTransaction(amount2, category2));

        double amount3 = 200.0;
        String category3 = "bills";
        assertTrue(controller.addTransaction(amount3, category3));

        //Applying filter
        CategoryFilter categoryFilter = new CategoryFilter(category3);
        controller.setFilter(categoryFilter);
        List<Transaction> filteredTransactions = categoryFilter.filter(model.getTransactions());

        //check that correct filtered transactions returned
        assertEquals(2, filteredTransactions.size());

        for (Transaction transaction : filteredTransactions){
            assertEquals(category3, transaction.getCategory());
        }

        controller.applyFilter();

        JTable table = view.getTransactionsTable();

        //Checking that the last two transactions are highlighted and the 1st one is not
        assertNotEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(0, 0), 0, 0).getBackground());
        assertEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(1, 0), 1, 0).getBackground());
        assertEquals(new Color(173, 255, 168), table.prepareRenderer(view.getTransactionsTable().getCellRenderer(2, 0), 2, 0).getBackground());
    }

    //// 5
    @Test
    public void testInvalidRemoveTransaction_Undo(){

        //jOptionPane not showing before clicking on "remove transaction"
        assertFalse(jOptionPane.isShowing());

        view.getRemoveTransactionBtn().doClick();

        //jOptionPane showing after clicking "remove transaction"
        assertTrue(jOptionPane.isShowing());

        //jOptionPane message wording check
        assertEquals("Please select a valid entry to remove", jOptionPane.getMessage());
    
        //jOptionPane message type check -- Error
        assertEquals(jOptionPane.ERROR_MESSAGE, jOptionPane.getMessageType());

    }

    //// 6
    @Test
    public void testRemoveTransactionSuccess_Undo() {
        // Pre-condition: List of transactions is empty
        assertEquals(0, model.getTransactions().size());
    
        // Perform the action: Add transactions to UI
        double amount = 100.0;
        String category = "food";
        assertTrue(controller.addTransaction(amount, category));
    
        amount = 100.0;
        category = "bills";
        assertTrue(controller.addTransaction(amount, category));
    
        amount = 200.0;
        category = "bills";
        assertTrue(controller.addTransaction(amount, category));
    
        //Total transactions check
        assertEquals(3, model.getTransactions().size());
    
        //Total Cost check
        assertEquals(400, (double)view.getTableModel().getValueAt(3, 3), 0.01);

        // Perform the action: Undo the transaction......removing the transaction with food category [index 0]
        controller.removeTransaction(0);

        //All remaining transactions are of bills category
        for (Transaction transaction : model.getTransactions()){
            assertEquals("bills", transaction.getCategory());
        }
    
        //Transactions Left
        assertEquals(2, model.getTransactions().size());
    
        //Updated total cost
        assertEquals(300, (double)view.getTableModel().getValueAt(2, 3), 0.01);
    }

}