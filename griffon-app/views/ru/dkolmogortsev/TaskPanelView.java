package ru.dkolmogortsev;

import static ru.dkolmogortsev.utils.TimeEntryUiHelper.getConstraints;
import static ru.dkolmogortsev.utils.TimeEntryUiHelper.getTimeFromMills;

import griffon.core.artifact.GriffonView;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javax.inject.Inject;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonView;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.reactfx.EventStreams;
import ru.dkolmogortsev.controls.TimeEntryButton;
import ru.dkolmogortsev.customui.DailyGridContainer;
import ru.dkolmogortsev.customui.DayGridPane;
import ru.dkolmogortsev.messages.StartTask;
import ru.dkolmogortsev.messages.TimeEntryDeleted;
import ru.dkolmogortsev.task.Task;
import ru.dkolmogortsev.task.TimeEntry;
import ru.dkolmogortsev.task.storage.TaskStorage;
import ru.dkolmogortsev.task.storage.TimeEntriesStorage;
import ru.dkolmogortsev.utils.ElapsedTimeFormatter;

/**
 * Created by dkolmogortsev on 2/7/17.
 */
@ArtifactProviderFor(GriffonView.class)
public class TaskPanelView extends AbstractGriffonView
{
    private static final String pattern = "dd/MM/yyyy"; //TODO make it configurable

    @MVCMember
    private ControlAndTaskView parentView;

    @MVCMember
    private TaskPanelModel model;

    @Inject
    private TaskStorage storage;

    @Inject
    private TimeEntriesStorage entriesStorage;

    private DailyGridContainer entriesPane;

    @Override
    public void initUI()
    {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        scrollPane.prefHeight(400);
        scrollPane.prefWidthProperty().bind(parentView.getContainerPane().widthProperty());
        entriesPane = new DailyGridContainer();
        entriesPane.prefWidthProperty().bind(scrollPane.widthProperty());
        scrollPane.setContent(entriesPane);
        parentView.getContainerPane().addRow(1, scrollPane);

        EventStreams.changesOf(model.getMap()).subscribe(change ->
        {
            long date = change.getKey();
            List<TimeEntry> list = change.getMap().get(date);
            int gridIndex = entriesPane.getGridIndex(date);
            if (gridIndex != -1)
            {
                entriesPane.getChildren().set(gridIndex, buildDayGrid(date, list));
            }
            else
            {
                entriesPane.getChildren().add(0, buildDayGrid(date, list));
            }
        });
    }

    private void buildTimeEntryLine(TimeEntry timeEntry, FlowPane holderPane)
    {
        GridPane header = (GridPane)parentView.getPane().getChildren().get(0);//Always header
        GridPane entry = new GridPane();
        entry.getColumnConstraints().addAll(getConstraints());
        Task t = storage.getTask(timeEntry.getTaskId());

        entry.prefWidthProperty().bind(parentView.getPane().widthProperty());
        TimeEntryButton timeEntryButton = new TimeEntryButton(t);
        bindHoverIcons(timeEntryButton, "time.entry.start.hover", "time.entry.start.normal");
        timeEntryButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        TimeEntryButton deleteButton = new TimeEntryButton(t);
        bindHoverIcons(deleteButton, "time.entry.delete.hover", "time.entry.delete.normal");
        deleteButton.prefHeightProperty().bind(header.heightProperty().multiply(0.75));

        EventStreams.eventsOf(timeEntryButton, MouseEvent.MOUSE_CLICKED)
                .subscribe(mouseEvent -> getApplication().getEventRouter().publishEvent(new StartTask(t.getUUID())));

        deleteButton.setOnAction(
                event -> getApplication().getEventRouter().publishEvent(new TimeEntryDeleted(timeEntry.getId())));

        String startStopLabel = new StringBuilder().append(getTimeFromMills(timeEntry.getStart())).append(" -> ")
                .append(getTimeFromMills(timeEntry.getEnd())).toString();
        entry.addRow(0, new Label(t.getDescription()), new Label(t.getTaskName()), new Label(
                        ElapsedTimeFormatter.formatElapsed(new Duration(timeEntry.getDuration()).getStandardSeconds())),
                new Label(startStopLabel), timeEntryButton, deleteButton);
        holderPane.getChildren().add(0, entry);
    }

    private void bindHoverIcons(TimeEntryButton timeEntryButton, String hoverIconPath, String normalIconPath)
    {
        ImageView hoverIcon = new ImageView(
                (String)getApplication().getResourceResolver().resolveResource(hoverIconPath));
        ImageView noHover = new ImageView(
                (String)getApplication().getResourceResolver().resolveResource(normalIconPath));
        timeEntryButton.graphicProperty()
                .bind(Bindings.when(timeEntryButton.hoverProperty()).then(hoverIcon).otherwise(noHover));
    }

    private DayGridPane buildDayGrid(long date, List<TimeEntry> entries)
    {
        DayGridPane pane = new DayGridPane(date);
        pane.prefWidthProperty().bind(parentView.getPane().widthProperty());
        GridPane dayHeader = new GridPane();
        dayHeader.prefWidthProperty().bind(parentView.getPane().widthProperty());
        ReadOnlyDoubleProperty headerHeight = ((GridPane)parentView.getPane().getChildren().get(0)).heightProperty();

        ColumnConstraints c1 = new ColumnConstraints();
        c1.setPercentWidth(50);

        ColumnConstraints c2 = new ColumnConstraints();
        c2.setPercentWidth(50);

        dayHeader.getColumnConstraints().addAll(c1, c2);

        LocalDate localDate = new LocalDate(date);
        boolean isToday = localDate.equals(LocalDate.now());
        Label currentDayLabel = new Label(isToday ? "Today" : localDate.toString(pattern));
        currentDayLabel.setAlignment(Pos.CENTER_LEFT);
        currentDayLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        currentDayLabel.getStyleClass().addAll("lbl-info", "daygrid-lbl");
        currentDayLabel.prefHeightProperty().bind(headerHeight.multiply(0.60));
        Label dayDuration = new Label(ElapsedTimeFormatter.formatElapsed(
                new Duration(entries.stream().mapToLong(TimeEntry::getDuration).sum()).getStandardSeconds()));
        dayDuration.setAlignment(Pos.CENTER_RIGHT);
        dayDuration.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        dayDuration.getStyleClass().addAll("lbl-info", "daygrid-lbl");
        dayHeader.addRow(0, currentDayLabel, dayDuration);
        pane.addRow(0, dayHeader);
        FlowPane e = new FlowPane();
        entries.forEach(timeEntry -> buildTimeEntryLine(timeEntry, e));
        pane.addRow(1, e);
        return pane;
    }

}
