package ru.dkolmogortsev;

import griffon.core.artifact.GriffonView;
import griffon.metadata.ArtifactProviderFor;
import java.util.Collections;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import javax.annotation.Nonnull;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView.class)
public class ControlAndTaskView extends AbstractJavaFXGriffonView
{

    private GridPane pane;

    public GridPane getPane()
    {
        return pane;
    }

    @Override
    public void mvcGroupInit(
            @Nonnull
                    Map<String, Object> args)
    {
        createMVCGroup("controlPanel");
        createMVCGroup("taskPanel");
    }

    @Override
    public void initUI()
    {
        Stage stage = (Stage)getApplication().createApplicationContainer(Collections.emptyMap());
        pane = new GridPane();
        pane.setVgap(10);
        ColumnConstraints constr = new ColumnConstraints();
        constr.setPercentWidth(100.0);
        pane.getColumnConstraints().add(constr);
        RowConstraints rc = new RowConstraints();
        rc.setPercentHeight(10.0);
        RowConstraints rc2 = new RowConstraints();
        rc2.setPercentHeight(90.0);
        pane.getRowConstraints().addAll(rc, rc2);
        Scene scene = new Scene(pane);
        scene.getStylesheets().add("bootstrapfx.css");
        scene.getStylesheets().add("/ru/dkolmogortsev/controlpanel.css");
        scene.getStylesheets().add("/ru/dkolmogortsev/daygrid");
        stage.setScene(scene);
        stage.setTitle(getApplication().getConfiguration().getAsString("application.title"));
        stage.setHeight(400.0);
        getApplication().getWindowManager().attach("main", stage);
    }

    public GridPane getContainerPane()
    {
        return pane;
    }
}
