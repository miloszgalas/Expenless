package pl.edu.agh.controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import pl.edu.agh.model.Account;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.model.Transaction;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.service.TransactionService;
import pl.edu.agh.util.Router;
import pl.edu.agh.util.View;

import java.io.IOException;
import java.text.DateFormat;
import java.util.List;

public class AccountDetailsController {

    @FXML
    private Label balance;
    @FXML
    private TableColumn<Transaction, String> nameColumn;
    @FXML
    private TableColumn<Transaction, Double> priceColumn;
    @FXML
    private TableColumn<Transaction, String> dateColumn;
    @FXML
    private TableColumn<Transaction, String> descriptionColumn;
    @FXML
    @Setter
    private TableView<Transaction> transactionsTable;

    @Setter
    private CategoryService categoryService;

    @Setter
    private AccountService accountService;

    @Setter
    private TransactionService transactionService;

    @Setter
    private Account account;

    @FXML
    private TreeView<String> categoryTreeView = new TreeView<>();

    @FXML
    private Button addButton;

    @Setter
    private List<Transaction> transactions;

    @FXML
    public void initialize() {

        new Thread(() -> {
            List<Category> categoryList = categoryService.getAllCategories();
            transactions = transactionService.getAllTransactionsOfAccount(account);

            Platform.runLater(() -> {
                TreeItem<String> rootItem = new TreeItem<>("Categories");
                rootItem.setExpanded(true);

                for (Category cat : categoryList) {
                    TreeItem<String> categoryTreeItem = new TreeItem<>(cat.getName());

                    for (Subcategory subcat : cat.getSubcategories()) {
                        if (subcat != null) {
                            categoryTreeItem.getChildren().add(new TreeItem<>(subcat.getName()));
                        }
                    }
                    rootItem.getChildren().add(categoryTreeItem);
                }
                categoryTreeView.setRoot(rootItem);
                categoryTreeView.setShowRoot(false);

                setTableView(transactions);
                balance.setText(account.getBalance() + " PLN");
                balance.setTextFill(account.getBalance() >= 0 ? Color.GREEN : Color.RED);
            });
        }).start();

    }

    private void setTableView(List<Transaction> transactions) {
        transactionsTable.setItems(FXCollections.observableList(transactions));
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrice()));
        dateColumn.setCellValueFactory(data -> new SimpleStringProperty(DateFormat.
                getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).
                format(data.getValue().getDate())));
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
    }

    @FXML
    public void backButtonClicked(MouseEvent event) {
        Router.routeTo(View.ACCOUNTS);
    }

    public void addButtonClicked(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/transactionDialog.fxml"));
        Pane page = loader.load();

        TransactionDialogController controller = loader.getController();
        controller.setAccount(account);
        controller.setAccountService(accountService);
        controller.setTransactionService(transactionService);

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Transaction");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);
        controller.dateTextField.setPromptText("dd.MM.yyyy HH:mm");
        dialogStage.showAndWait();
        refresh();
    }

    private void refresh(){
        transactionsTable.setItems(FXCollections.observableList(transactionService.getAllTransactionsOfAccount(account)));
        balance.setText(account.getBalance() + " PLN");
        balance.setTextFill(account.getBalance() >= 0 ? Color.GREEN : Color.RED);
    }
}
