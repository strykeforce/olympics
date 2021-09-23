package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.simulation.ElevatorSimulation;

public class ElevatorSubsystem extends SubsystemBase {
    private TalonSRX elevatorTalon;



    public TalonSRX getTalon(){
        return elevatorTalon;
    }

    public void zeroElevator(){
        /*Put logic to zero elevator here
        The new zeroed position needs to be passed down to the simulation, 
        replace the newPos variable with the new calcualted encoder position
        */
        int newPos = 0; 
        ElevatorSimulation.getInstance().zeroSimulation(newPos);
    }
}

