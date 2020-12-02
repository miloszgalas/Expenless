package pl.edu.agh;

import com.google.inject.Guice;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import pl.edu.agh.controller.AccountController;
import pl.edu.agh.controller.AccountDialogController;
import pl.edu.agh.guice.AppModule;
import pl.edu.agh.model.Account;
import pl.edu.agh.service.AccountService;
import pl.edu.agh.util.Router;

import java.io.IOException;

public class Main extends Application {
    private AccountService accountService;
    private Pane mainPane;

    public void createMockAccounts(){
        accountService.createAccount(new Account("Moje konto 1", 21.37));
        accountService.createAccount(new Account("Moje konto 2", 21.37));
        accountService.createAccount(new Account("Moje konto 3", 21.37));
        accountService.createAccount(new Account("Moje konto 4", 21.37));
    }


    @Override
    public void start(Stage primaryStage) {
        Injector injector = Guice.createInjector(new AppModule());
        accountService = injector.getInstance(AccountService.class);
        createMockAccounts();

        try{
            initializeAccounts();
            initializeHello();
            initializeAccountsDialog();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scene mainScene = new Scene(mainPane);
        mainScene.getStylesheets().add(getClass().getResource("/style/style.css").toExternalForm());

        Router.setMainScene(mainScene);
        Router.setMainStage(primaryStage);
        primaryStage.setTitle("ExpensLESS");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    private void initializeAccounts() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/accountsView.fxml"));
        Pane accountsPane = fxmlLoader.load();

        AccountController controller = fxmlLoader.getController();
        controller.setAccountService(accountService);

        Router.addPane("Account", accountsPane);
        mainPane = accountsPane;
    }

    private void initializeAccountsDialog() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/accountsDialog.fxml"));
        Pane accountsDialogPane = fxmlLoader.load();

        AccountDialogController controller = fxmlLoader.getController();
        controller.setAccountService(accountService);

        Router.addPane("AccountDialog", accountsDialogPane);
    }

    private void initializeHello() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/helloView.fxml"));
        Pane accountPane = fxmlLoader.load();

        Router.addPane("Hello", accountPane);
    }
}
