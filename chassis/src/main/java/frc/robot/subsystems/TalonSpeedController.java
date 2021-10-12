package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.TalonSRXConfiguration;

import edu.wpi.first.wpilibj.SpeedController;

public class TalonSpeedController implements SpeedController{
    private TalonSRX talon;

    public TalonSpeedController(int id){
        talon = new TalonSRX(id);
    }

    public void configFactoryDefault(int timeout){
        talon.configFactoryDefault(timeout);
    }

    public void configAllSettings(TalonSRXConfiguration config, int timeout){
        talon.configAllSettings(config, timeout);
    }

    public void enableCurrentLimit(Boolean enable){
        talon.enableCurrentLimit(enable);
    }

    public void enableVoltageCompensation(Boolean enable){
        talon.enableVoltageCompensation(enable);
    }

    public void setNeutralMode(NeutralMode mode){
        talon.setNeutralMode(mode);
    }

    @Override
    public void set(double speed) {
        talon.set(ControlMode.PercentOutput, speed);
    }

    @Override
    public double get() {
        return talon.getMotorOutputPercent();
    }

    @Override
    public void setInverted(boolean isInverted) {
        talon.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return talon.getInverted();
    }

    @Override
    public void disable() {
        talon.set(ControlMode.Disabled,0.0);
    }

    @Override
    public void stopMotor() {
        talon.set(ControlMode.PercentOutput, 0.0);
    }

    @Override
    public void pidWrite(double output) {
        set(output);
    }
    
}
