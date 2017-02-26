package ru.dkolmogortsev;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import org.codehaus.griffon.runtime.javafx.artifact.AbstractJavaFXGriffonView;
import org.reactfx.EventStream;
import org.reactfx.EventStreams;
import org.reactfx.Subscription;
import ru.dkolmogortsev.task.search.SearchFields;

@ArtifactProviderFor(GriffonView.class)
public class ControlPanelView extends AbstractJavaFXGriffonView
{
    @FXML
    Button startButton;
    @FXML
    Label timerLabel;
    @FXML
    TextField taskDescription;
    @FXML
    TextField taskName;
    @MVCMember
    private ControlPanelController controller;
    @MVCMember
    private ControlPanelModel model;
    @MVCMember
    private ControlAndTaskView parentView;
    private BooleanProperty started = new SimpleBooleanProperty(false);
    private Subscription descInputSubscription;
    private SimpleListProperty<String> tasksToShow = new SimpleListProperty<>();

    private ContextMenu menu = new ContextMenu();
    private GridPane customPopup = new GridPane();

    @Override
    public void initUI()
    {
        Node node = init();
        timerLabel.textProperty().bind(model.getTimerTextProp());
        started.bind(model.taskStartedProperty());
        tasksToShow.bind(model.tasksProperty());

        taskDescription.textProperty().bindBidirectional(model.taskDescriptionProperty());
        taskName.textProperty().bindBidirectional(model.taskNameProperty());

        toStartButton();
        parentView.getAnchorPane().addRow(0, node);
        started.addListener(((observable, oldValue, newValue) ->
        {
            if (newValue)
            {
                toStopButton();
                return;
            }
            toStartButton();
        }));

        EventStream<String> inputStream = EventStreams.valuesOf(taskDescription.textProperty());

        EventStreams.valuesOf(taskDescription.focusedProperty()).subscribe(aBoolean ->
        {
            if (aBoolean)
            {
                descInputSubscription = inputStream
                        .subscribe((searchStr) -> controller.search(searchStr, SearchFields.DESCRIPTION));
                return;
            }
            if (descInputSubscription != null)
            {
                descInputSubscription.unsubscribe();
            }
        });

    }

    private void buildPopupElement(ContextMenu menu, String s)
    {
        GridPane pane = new GridPane();

        ColumnConstraints c = new ColumnConstraints();
        c.setPercentWidth(100);
        pane.getColumnConstraints().add(c);
        Label innerButton = new Label(s);
        innerButton.setPadding(new Insets(5, 0, 0, 5));
        innerButton.setMaxWidth(Double.MAX_VALUE);
        innerButton.setMaxHeight(Double.MAX_VALUE);
        innerButton.setAlignment(Pos.CENTER_LEFT);

        pane.addRow(0, innerButton);
        pane.setPrefWidth(taskDescription.getWidth());
        pane.setPrefHeight(taskDescription.getHeight() * 0.75);
        CustomMenuItem customMenuItem = new CustomMenuItem(pane);
        menu.getItems().add(customMenuItem);
    }

    // build the UI
    private Node init()
    {
        Node node = loadFromFXML();
        connectActions(node, controller);
        connectMessageSource(node);
        return node;
    }

    private void toStartButton()
    {
        startButton.getStyleClass().setAll("btn", "btn-success");
    }

    private void toStopButton()
    {
        startButton.getStyleClass().setAll("btn", "btn-danger");
    }

    private void onFocus(TextField focusedField)
    {
        ContextMenu menu = new ContextMenu();
        menu.maxWidthProperty().bind(focusedField.widthProperty());
        menu.setAutoFix(true);

        EventStreams.changesOf(focusedField.focusedProperty()).subscribe(booleanChange ->
        {
            if (booleanChange.getNewValue())
            {
                menu.show(focusedField, Side.BOTTOM, 0, 0);
            }
            else
            {
                menu.hide();
            }
        });

        EventStreams.valuesOf(tasksToShow).subscribe(tasks ->
        {
            if (tasks == null)
            {
                return;
            }
            menu.getItems().clear();
            tasks.forEach(s -> buildPopupElement(menu, s));
            menu.show(focusedField, Side.BOTTOM, 0, 0);
        });
    }

}
