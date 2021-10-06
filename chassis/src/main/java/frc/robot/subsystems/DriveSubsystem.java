package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import static frc.robot.Constants.kTalonConfigTimeout;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class DriveSubsystem extends SubsystemBase{

    private TalonSpeedController leftDrive;
    private TalonSpeedController rightDrive;
    private DifferentialDrive diffDrive;
    

    public DriveSubsystem(){
        leftDrive = new TalonSpeedController(0);
        rightDrive = new TalonSpeedController(1);

        leftDrive.configFactoryDefault(kTalonConfigTimeout);
        leftDrive.configAllSettings(Constants.getDriveTalonConfig(),kTalonConfigTimeout);
        leftDrive.enableCurrentLimit(true);
        leftDrive.enableVoltageCompensation(true);
        leftDrive.setNeutralMode(NeutralMode.Coast);

        rightDrive.configFactoryDefault(kTalonConfigTimeout);
        rightDrive.configAllSettings(Constants.getDriveTalonConfig(),kTalonConfigTimeout);
        rightDrive.enableCurrentLimit(true);
        rightDrive.enableVoltageCompensation(true);
        rightDrive.setNeutralMode(NeutralMode.Coast);

        diffDrive = new DifferentialDrive(leftDrive, rightDrive);
        diffDrive.setDeadband(0.1);
        diffDrive.setRightSideInverted(false);
    }

    public void drive(double speed, double rotate){
        diffDrive.arcadeDrive(speed, rotate);
    }


    
}
