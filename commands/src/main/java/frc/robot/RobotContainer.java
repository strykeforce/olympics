// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.simulation.BatterySim;
import edu.wpi.first.wpilibj.simulation.RoboRioSim;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.simulation.ArmSimulation;
import frc.robot.simulation.ElevatorSimulation;
import frc.robot.subsystems.ArmSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  private ArmSubsystem arm;
  private ElevatorSubsystem elevator;

  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    //Instantiate all subsystems here - this method runs in robotInit
    

    // Configure the button bindings
    configureButtonBindings();
    // Update data sent to smart dashboard
    updateSmartDashboard();
  }

  /**
   * Use this method to define your button->command mappings. Buttons can be created by
   * instantiating a {@link GenericHID} or one of its subclasses ({@link
   * edu.wpi.first.wpilibj.Joystick} or {@link XboxController}), and then passing it to a {@link
   * edu.wpi.first.wpilibj2.command.button.JoystickButton}.
   */
  private void configureButtonBindings() {}

  //Any data to be visible on the smart dashboard - put here
  private void updateSmartDashboard(){
    //Arm Data
    SmartDashboard.putData("ArmSim", ArmSimulation.getInstance());

    //Elevator Data
    SmartDashboard.putData("ElevatorSim", ElevatorSimulation.getInstance());
  }

  public void setupSimulation(){
    ArmSimulation.getInstance().setupSimulation(arm.getTalon());
    ElevatorSimulation.getInstance().setupSimulation(elevator.getTalon());
  }

  public void updateSimulation(){
    ArmSimulation.getInstance().update();
    ElevatorSimulation.getInstance().update();

    RoboRioSim.setVInVoltage(BatterySim.calculateDefaultBatteryLoadedVoltage(
      ArmSimulation.getInstance().getCurrentDraw(),
      ElevatorSimulation.getInstance().getCurrentDraw()
    ));
  }


}
