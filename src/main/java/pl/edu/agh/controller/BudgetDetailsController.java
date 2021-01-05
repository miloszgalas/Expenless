package pl.edu.agh.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import org.hibernate.Hibernate;
import pl.edu.agh.model.Budget;
import pl.edu.agh.model.Category;
import pl.edu.agh.model.SubcategoryBudget;
import pl.edu.agh.model.Subcategory;
import pl.edu.agh.service.BudgetService;
import pl.edu.agh.service.CategoryService;
import pl.edu.agh.util.Router;
import pl.edu.agh.util.View;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

public class BudgetDetailsController {
    @Setter
    private BudgetService budgetService;
    @Setter
    private Budget budget;
    @Setter
    private CategoryService categoryService;

    @FXML
    private Label nameLabel;
    @FXML
    private TreeView<Object> categoryTree;

    @FXML
    public void backButtonClicked(MouseEvent event) {
        Router.routeTo(View.MENU);
    }

    @FXML
    public void addButtonClicked(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/budgetDetailsDialog.fxml"));
        Pane pane = fxmlLoader.load();
        BudgetDetailsDialogController controller = fxmlLoader.getController();
        controller.setBudgetService(budgetService);
        controller.setCategoryService(categoryService);
        controller.setBudget(budget);
        controller.loadData();
        controller.setBudgetDetailsController(this);

        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setScene(scene);
        stage.showAndWait();
    }

    public void loadData() {
        nameLabel.setText(budget.toString());
        new Thread(() -> {
            Hibernate.initialize(budget.getSubcategoryBudgetList());
            List<SubcategoryBudget> subcategoryBudgetList = budget.getSubcategoryBudgetList();
            TreeItem<Object> rootItem = new TreeItem<>("Categories");
            rootItem.setExpanded(true);
            List<Category> categories = categoryService.getAllCategories();

            for (Category cat : categories) {
//                TreeItem<Object> categoryTreeItem = new TreeItem<>(cat.getName());
                List<TreeItem<Object>> tmp_items = new LinkedList<>();
                BigDecimal sumBalance = BigDecimal.ZERO;
                BigDecimal sumPlanned = BigDecimal.ZERO;
                for (SubcategoryBudget subcat : subcategoryBudgetList) {
                    if (subcat.getSubcategory().getCategory().getId().equals(cat.getId())) {
                        BigDecimal balance = budgetService.calculateBudgetBalance(budget, subcat.getSubcategory());
                        Text text = new Text(balance + " / " + subcat.getPlannedBudget().toString());
                        sumBalance = sumBalance.add(balance);
                        sumPlanned = sumPlanned.add(subcat.getPlannedBudget());
                        text.setFill(subcat.getPlannedBudget().subtract(balance).doubleValue() >= 0 ? Color.GREEN : Color.RED);
                        GridPane gridPane = new GridPane();
                        gridPane.add(new Text(subcat.getSubcategory().getName()), 0, 0);
                        gridPane.add(text, 1, 0);
                        gridPane.setHgap(30);
                        tmp_items.add(new TreeItem<>(gridPane));

//                        categoryTreeItem.getChildren().add(new TreeItem<>(gridPane));
                    }
                }
                Text text = new Text(sumBalance + " / " + sumPlanned);
                text.setFill(sumPlanned.subtract(sumBalance).doubleValue() >= 0 ? Color.GREEN : Color.RED);
                GridPane gridPane = new GridPane();
                gridPane.add(new Text(cat.getName()), 0, 0);
                gridPane.add(text, 1, 0);
                gridPane.setHgap(30);
                TreeItem<Object> categoryTreeItem = new TreeItem<>(gridPane);

                for (TreeItem<Object> pane : tmp_items){
                    categoryTreeItem.getChildren().add(pane);
                }

                rootItem.getChildren().add(categoryTreeItem);
            }

            Platform.runLater(() -> {
                categoryTree.setRoot(rootItem);
                categoryTree.setShowRoot(false);
            });
        }).start();
    }


}
