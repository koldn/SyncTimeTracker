package ru.dkolmogortsev.controls;

import javafx.geometry.Pos;
import javafx.scene.control.Button;

/**
 * Created by dkolmogortsev on 2/26/17.
 */
public class TimeEntryButton extends Button
{

    public TimeEntryButton()
    {
        super();
        setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        getStyleClass().clear();
        setAlignment(Pos.CENTER);
    }
}
