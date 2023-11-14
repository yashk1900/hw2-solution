import javax.swing.JOptionPane;
import javax.swing.JTable;
import controller.ExpenseTrackerController;
import model.ExpenseTrackerModel;
import model.Transaction;
import view.ExpenseTrackerView;
import model.Filter.AmountFilter;
import model.Filter.CategoryFilter;
import java.util.List;

public class ExpenseTrackerApp {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    // Create MVC components
    ExpenseTrackerModel model = new ExpenseTrackerModel();
    ExpenseTrackerView view = new ExpenseTrackerView();
    ExpenseTrackerController controller = new ExpenseTrackerController(model, view);

  //     // Add action listener to the "Apply Category Filter" button
  //   view.addApplyCategoryFilterListener(e -> {
  //     try{
  //     String categoryFilterInput = view.getCategoryFilterInput();
  //     CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
  //     if (categoryFilterInput != null) {
  //         // controller.applyCategoryFilter(categoryFilterInput);
  //         controller.setFilter(categoryFilter);
  //         controller.applyFilter();
  //     }
  //    }catch(IllegalArgumentException exception) {
  //   JOptionPane.showMessageDialog(view, exception.getMessage());
  //   view.toFront();
  //  }});


  //   // Add action listener to the "Apply Amount Filter" button
  //   view.addApplyAmountFilterListener(e -> {
  //     try{
  //     double amountFilterInput = view.getAmountFilterInput();
  //     AmountFilter amountFilter = new AmountFilter(amountFilterInput);
  //     if (amountFilterInput != 0.0) {
  //         controller.setFilter(amountFilter);
  //         controller.applyFilter();
  //     }
  //   }catch(IllegalArgumentException exception) {
  //   JOptionPane.showMessageDialog(view,exception.getMessage());
  //   view.toFront();
  //  }});

  initialize(view, controller, new JOptionPane());
}

  public static void initialize(ExpenseTrackerView view, ExpenseTrackerController controller, JOptionPane jOptionPane) {
        // Initialize view
        view.setVisible(true);
        
        // Handle add transaction button on click
        view.getAddTransactionBtn().addActionListener(e -> {
        // Get transaction data from view
        double amount = view.getAmountField();
        String category = view.getCategoryField();

        // Call controller to add transaction
        boolean added = controller.addTransaction(amount, category);

        //Returning dialog box toFront.....and creating a non blocking code
        if (!added) {
          jOptionPane.setMessage("Invalid amount or category entered");
          jOptionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
          javax.swing.JDialog dlg = jOptionPane.createDialog("MessageBox");
          dlg.setModal(false);
          dlg.setVisible(true);
          view.toFront();
        }
      });

        // Add action listener to the "Apply Category Filter" button
        view.addApplyCategoryFilterListener(e -> {
            try{
                String categoryFilterInput = view.getCategoryFilterInput();
                CategoryFilter categoryFilter = new CategoryFilter(categoryFilterInput);
                if (categoryFilterInput != null) {
                    controller.setFilter(categoryFilter);
                    controller.applyFilter();
                }
            }catch(IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(view, exception.getMessage());
                view.toFront();
            }});

        // Add action listener to the "Apply Amount Filter" button
        view.addApplyAmountFilterListener(e -> {
            try{
                double amountFilterInput = view.getAmountFilterInput();
                AmountFilter amountFilter = new AmountFilter(amountFilterInput);
                if (amountFilterInput != 0.0) {
                    controller.setFilter(amountFilter);
                    controller.applyFilter();
                }
            }catch(IllegalArgumentException exception) {
                JOptionPane.showMessageDialog(view,exception.getMessage());
                view.toFront();
            }});

        //Adding action listeners to remove transaction button to follow through with the intended on click functionality
        view.getRemoveTransactionBtn().addActionListener(e -> {
            int index = view.getSelectedRowIndex();
            boolean removed = controller.removeTransaction(index);
            if (!removed) {
                //returning jdialog with non blocking features
                jOptionPane.setMessage("Please select a valid entry to remove");
                jOptionPane.setMessageType(JOptionPane.ERROR_MESSAGE);
                javax.swing.JDialog dlg = jOptionPane.createDialog("MessageBox");
                dlg.setModal(false);
                dlg.setVisible(true);
                view.toFront();
            }
        });
  }
}