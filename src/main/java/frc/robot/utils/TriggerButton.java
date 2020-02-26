package frc.robot.utils;

import java.util.function.BooleanSupplier;
import org.frcteam2910.common.robot.input.Axis;
import edu.wpi.first.wpilibj2.command.button.Button;

/**
 * A class for reading triggers as buttons
 */
public class TriggerButton extends Button {

    private static final double PRESSED_THRESHOLD = 0.2;

    /**
     * @param trigger The axis of the trigger
     */
    public TriggerButton(Axis trigger) {
        super(createSupplier(trigger));
    }

    /**
     * This creates and returns a boolean supplier for the trigger button
     * @param trigger the axis of the trigger
     * @return A boolean supplier that reads true when the trigger is past a certain threshold
     */
    private static BooleanSupplier createSupplier(Axis trigger) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return Math.abs(trigger.get()) > PRESSED_THRESHOLD;
            }
        };
    }
}
