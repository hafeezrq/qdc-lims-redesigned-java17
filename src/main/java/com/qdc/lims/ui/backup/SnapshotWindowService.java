package com.qdc.lims.ui.backup;

import com.qdc.lims.QdcLimsApplication;
import com.qdc.lims.service.BrandingService;
import com.qdc.lims.ui.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Launches a separate window backed by a snapshot database.
 */
@Service
public class SnapshotWindowService {

    public void openSnapshotWindow(String jdbcUrl, String username, String password, String label) {
        Map<String, Object> props = new HashMap<>();
        props.put("spring.datasource.url", jdbcUrl);
        if (username != null && !username.isBlank()) {
            props.put("spring.datasource.username", username);
        }
        if (password != null && !password.isBlank()) {
            props.put("spring.datasource.password", password);
        }
        props.put("qdc.backup.auto-enabled", "false");

        ConfigurableApplicationContext snapshotContext = new SpringApplicationBuilder(QdcLimsApplication.class)
                .headless(false)
                .properties(props)
                .run();

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/main_window.fxml"));
                loader.setControllerFactory(snapshotContext::getBean);
                Parent root = loader.load();

                Scene scene = new Scene(root, 1100, 750);
                Stage stage = new Stage();
                BrandingService brandingService = snapshotContext.getBean(BrandingService.class);
                String title = "LIMS Snapshot - " + label;
                brandingService.tagStage(stage, title);
                stage.setScene(scene);
                stage.setMinWidth(900);
                stage.setMinHeight(600);
                stage.setResizable(true);
                stage.centerOnScreen();

                stage.setOnCloseRequest(event -> {
                    SessionManager.onWindowClosed(stage);
                    snapshotContext.close();
                });

                stage.show();
            } catch (Exception e) {
                snapshotContext.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Snapshot Failed");
                alert.setHeaderText("Unable to open snapshot window");
                alert.setContentText(e.getMessage() != null ? e.getMessage() : "Unknown error");
                alert.showAndWait();
            }
        });
    }
}
