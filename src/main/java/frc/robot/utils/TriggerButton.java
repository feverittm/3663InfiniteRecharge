package frc.robot.utils;

import java.util.function.BooleanSupplier;
import org.frcteam2910.common.robot.input.Axis;
import edu.wpi.first.wpilibj2.command.button.Button;

/**
 * A class for reading triggers as buttons
 */
public class TriggerButton {

    private final double THRESHOLD = 0.2;
    private Button triggerButton;
    private Axis trigger;

    public TriggerButton(Axis trigger) {
        this.trigger = trigger;

        BooleanSupplier isPressed = new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return Math.abs(trigger.get()) > THRESHOLD;
            }
        };

        triggerButton = new Button(isPressed);
    }

    /**
     * return a button that reads true when the trigger is pressed beyond a certain threshold
     */
    public Button getTriggerButton() {
        return triggerButton;
    }
}
