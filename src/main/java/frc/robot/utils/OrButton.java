package frc.robot.utils;

import java.util.function.BooleanSupplier;
import edu.wpi.first.wpilibj2.command.button.Button;

public class OrButton extends Button {
    public OrButton(Button one, Button two) {
        super(createSupplier(one, two));
    }
    private static BooleanSupplier createSupplier(Button one, Button two) {
        return new BooleanSupplier() {
            @Override
            public boolean getAsBoolean() {
                return one.get() || two.get();
            }
        };
    }
}
