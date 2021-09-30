package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.simulation.ArmSimulation;

public class ArmSubsystem extends SubsystemBase{
    private TalonSRX armTalon;


    public TalonSRX getTalon(){
        return armTalon;
    }

    public void zeroArm(){
        /*Put logic to zero arm here
        The new zeroed position needs to be passed down to the simulation, 
        replace the newPos variable with the new calcualted encoder position
        */
        int newPos = 0; 
        ArmSimulation.getInstance().zeroSimulation(newPos);
    }    
}