package frc.robot.utils;

import org.frcteam2910.common.math.Vector2;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.commands.C_AutoDrive;
import frc.robot.subsystems.SS_Drivebase;

public class AutonomousBuilder {

    private SendableChooser startingPositionSelector;
    private SendableChooser movementSelector;
    private NetworkTableEntry shootDelayEntry;
    private NetworkTableEntry movementDelayEntry;
    private SS_Drivebase drivebase;
    
    public AutonomousBuilder(SS_Drivebase drivebase) {
        this.drivebase = drivebase;
        initStartingPositionSelector();
        initMovementSelector();

        ShuffleboardTab autoTab = Shuffleboard.getTab("Auto Selector");
        //Add widgets to shuffleboard
        autoTab.add("Starting Position", startingPositionSelector)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(0, 0)
            .withSize(2, 0);
        shootDelayEntry = autoTab.add("Shoot Delay", 0)
            .withWidget(BuiltInWidgets.kTextView)
            .withPosition(0, 1)
            .withSize(2, 1)
            .getEntry();
        movementDelayEntry = autoTab.add("Movement Delay", 0)
            .withWidget(BuiltInWidgets.kTextView)
            .withPosition(0, 2)
            .withSize(2, 1)
            .getEntry();
        autoTab.add("Movement Strategy", movementSelector)
            .withWidget(BuiltInWidgets.kComboBoxChooser)
            .withPosition(0, 3)
            .withSize(1, 2);
    }

    private void initStartingPositionSelector() {
        startingPositionSelector = new SendableChooser<StartingPosition>();
        //Add options to the starting position selector
        startingPositionSelector.setDefaultOption("Middle", StartingPosition.MIDDLE);
        startingPositionSelector.addOption("Left", StartingPosition.LEFT);
        startingPositionSelector.addOption("Right", StartingPosition.RIGHT);
    }

    private void initMovementSelector() {
        movementSelector = new SendableChooser<MovementStrategy>();
         //Add options to the movement strategy selector
        movementSelector.setDefaultOption("Backward", MovementStrategy.BACKWARD);
        movementSelector.addOption("Forward", MovementStrategy.FORWARD);
        movementSelector.addOption("Trench", MovementStrategy.TRENCH);
        movementSelector.addOption("None", MovementStrategy.NONE);
    }

    /**
     * Create the routine for autonomous based on the selected options from the Shuffleboard
     * @return A command group for autonomous
     */
    public SequentialCommandGroup buildAutoRoutine() {
        //Create commands for autonomous
        WaitCommand shootDelay = new WaitCommand(shootDelayEntry.getDouble(0.0));
        WaitCommand movementDelay = new WaitCommand(movementDelayEntry.getDouble(0.0));

        SequentialCommandGroup autoRoutine = new SequentialCommandGroup(shootDelay);

        //add shoot command

        autoRoutine.addCommands(movementDelay);

        if(movementSelector.getSelected().equals(MovementStrategy.BACKWARD)){
            autoRoutine.addCommands(new C_AutoDrive(drivebase, new Vector2(-24.0, 0.0), .5, 0.0, 0.0));
        } else if (movementSelector.getSelected().equals(MovementStrategy.FORWARD)) {
            autoRoutine.addCommands(new C_AutoDrive(drivebase, new Vector2(24.0, 0.0), .5, 0.0, 0.0));
        } else if(movementSelector.getSelected().equals(MovementStrategy.TRENCH)) {
            if(startingPositionSelector.getSelected().equals(StartingPosition.RIGHT)){
                autoRoutine.addCommands(new ParallelCommandGroup(
                        new C_AutoDrive(drivebase, new Vector2(-190, 0.0), .5, 0.0, 0.0)
                        //intake command
                    )
                );
            } else if (startingPositionSelector.getSelected().equals(StartingPosition.MIDDLE)) {
                autoRoutine.addCommands(new C_AutoDrive(drivebase, new Vector2(-80.0, 66.0), .7, 0.0, 0.0),
                        new ParallelCommandGroup(
                            new C_AutoDrive(drivebase, new Vector2(-100.0, 0.0), .5, 0.0, 0.0)
                            // intake command
                            )
                );
            } else if (startingPositionSelector.getSelected().equals(StartingPosition.LEFT)) {
                //TODO
            }
        }

        return autoRoutine;
    }

    public StartingPosition getStartingPosition() {
        return (StartingPosition)startingPositionSelector.getSelected();
    }

    public enum StartingPosition {
        LEFT,
        MIDDLE,
        RIGHT
    }

    public enum MovementStrategy {
        FORWARD,
        BACKWARD,
        TRENCH,
        NONE
    }
}