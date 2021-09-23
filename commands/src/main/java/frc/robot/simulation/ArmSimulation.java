package frc.robot.simulation;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.simulation.SingleJointedArmSim;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.VecBuilder;
import edu.wpi.first.wpiutil.math.numbers.N1;
import edu.wpi.first.wpiutil.math.numbers.N2;
import frc.robot.Constants.ArmConstants;

public class ArmSimulation implements Sendable {
    private static final ArmSimulation sim = new ArmSimulation();
    private TalonSRX armTalon;
    private SingleJointedArmSim armSim;
    
    private DCMotor armGearbox = DCMotor.getVex775Pro(1);
    private double gearRatio = ArmConstants.kFirstStage * ArmConstants.kSecondStage;
    private double armLengthMeters = ArmConstants.kArmLengthM;
    private double armMassKg = Units.lbsToKilograms(ArmConstants.kArmMassLb);
    private double minAngleRads = Units.degreesToRadians(ArmConstants.kMinAngleDeg);
    private double maxAngleRads = Units.degreesToRadians(ArmConstants.kMaxAngleDeg);

    //State Variables
    private int lastPosTicks;
    private int lastVelTicks = 0;
    private double lastCurrDraw = 0;
    private double lastOutPerc = 0;
    private static boolean hasZeroed = false;
    private int newAbs = 0;
    private double lastHeightM = 0;
    private double lastDistOffCenterM = 0;

    public static ArmSimulation getInstance(){
        return sim;
    }

    private ArmSimulation(){
        armSim = new SingleJointedArmSim(
            armGearbox, 
            gearRatio, 
            SingleJointedArmSim.estimateMOI(armLengthMeters, armMassKg), 
            armLengthMeters, 
            minAngleRads, 
            maxAngleRads, 
            armMassKg, 
            false, 
            VecBuilder.fill(Units.degreesToRadians(0.05)) //Std Dev of 0.05 deg
        );
        hasZeroed = false;
    }

    public void setupSimulation(TalonSRX talon){
        if(talon != null){
            armTalon = talon;
            lastPosTicks = (int) talon.getSelectedSensorPosition();
            armTalon.getSimCollection().setPulseWidthPosition(2048);
            System.out.println("setting pulse width position");
            setLimitSwitchState();
        } else{
            System.out.println("No Arm Talon Supplied");
        }
    }

    public void zeroSimulation(int newPos){
        double angleDegs = -1 * (newPos / ArmConstants.kTicksPerDeg) + ArmConstants.kDegreeOffset;
        double angleRads = Units.degreesToRadians(angleDegs);
        System.out.println("Zeroing Arm - posTicks: " + newPos + ", angleDegs: " + angleDegs + ", angleRads: " + angleRads);
        Matrix<N2,N1> currState = new Matrix<>(N2.instance, N1.instance);
        currState.set(0,0,angleRads); //New sensor position
        currState.set(1,0,0); //Not including sensor velocity for zeroing
        armSim.setState(currState);
        lastPosTicks = newPos;
        hasZeroed = true;
    }

    private void setLimitSwitchState(){
        double curPosDeg = -1* armTalon.getSelectedSensorPosition() / ArmConstants.kTicksPerDeg + ArmConstants.kDegreeOffset;
        double normalizedPos = Math.IEEEremainder(curPosDeg, 360);
        double pulsePos = armTalon.getSensorCollection().getPulseWidthPosition() & 0XFFF;
        double offsetTicks = 60 * ArmConstants.kTicksPerDeg;
        if((normalizedPos > 30 && normalizedPos < 150) || 
        (pulsePos > ArmConstants.kAbsZeroTicks - offsetTicks && 
        pulsePos < ArmConstants.kAbsZeroTicks + offsetTicks && 
        !hasZeroed)){
            armTalon.getSimCollection().setLimitFwd(true);
        }else{
            armTalon.getSimCollection().setLimitFwd(false);
        }
    }

    public void update(){
        //Update Simulation State based on Talon Setpoint
        double outPerc = armTalon.getMotorOutputPercent();
        armSim.setInputVoltage(-outPerc * RobotController.getBatteryVoltage());
        armSim.update(0.020);

        //Get New Arm State
        double angleDegs = Units.radiansToDegrees(armSim.getAngleRads());
        double speedRpm = Units.radiansPerSecondToRotationsPerMinute(armSim.getVelocityRadPerSec());
        double currDraw = armSim.getCurrentDrawAmps();

        //Set Talon State
        armTalon.getSimCollection().setStatorCurrent(currDraw);
        armTalon.getSimCollection().setSupplyCurrent(currDraw * outPerc);
        setLimitSwitchState();
        armTalon.getSimCollection().setBusVoltage(RobotController.getBatteryVoltage());
        
        int posTicks = (int) ((-angleDegs + ArmConstants.kDegreeOffset) * ArmConstants.kTicksPerDeg);
        int offset = posTicks - lastPosTicks;
        armTalon.getSimCollection().addQuadraturePosition(offset);
        int absPos = armTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
        newAbs = Math.floorMod(absPos + offset, 4096);
        armTalon.getSimCollection().addPulseWidthPosition(newAbs - absPos);

        int velTicks = (int) (speedRpm * ArmConstants.kTicksPerDeg * 360 / 60.0 / 10.0); //Ticks per 100ms
        armTalon.getSimCollection().setQuadratureVelocity(velTicks);

        //Calculate Arm Height Above Ground
        double elevatorHeightM = ElevatorSimulation.getInstance().getInnerCarriageHeightM();
        double armHeightM = elevatorHeightM + armLengthMeters * Math.sin(Units.degreesToRadians(angleDegs));
        double distOffCenterM = armLengthMeters * Math.cos(Units.degreesToRadians(angleDegs));

        lastPosTicks = posTicks;
        lastVelTicks = velTicks;
        lastCurrDraw = currDraw;
        lastOutPerc = outPerc;
        lastDistOffCenterM = distOffCenterM;
        lastHeightM = armHeightM;
    }

    public double getCurrentDraw(){
        return lastCurrDraw;
    }

    //Sendable Interface
    DoubleSupplier getAngleDeg = () -> Units.radiansToDegrees(armSim.getAngleRads()) - 90;
    DoubleSupplier getVelRpm = () -> Units.radiansPerSecondToRotationsPerMinute(armSim.getVelocityRadPerSec());
    DoubleSupplier getCurrent = () -> armSim.getCurrentDrawAmps();
    DoubleSupplier getPosTicks = () -> lastPosTicks;
    DoubleSupplier getVelTicks = () -> lastVelTicks;
    DoubleSupplier getOutPerc = () -> lastOutPerc;
    DoubleSupplier getAbsPos = () -> newAbs;
    DoubleSupplier getHeightAboveGndM = () -> lastHeightM;
    DoubleSupplier getDistOffCenterM = () -> lastDistOffCenterM;
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("ArmSimSendable");
        builder.addDoubleProperty("Position (deg)", getAngleDeg, null);
        builder.addDoubleProperty("Velocity (rpm)", getVelRpm, null);
        builder.addDoubleProperty("Stator Current (A)", getCurrent, null);
        builder.addDoubleProperty("Position (ticks)", getPosTicks, null);
        builder.addDoubleProperty("Percent Output", getOutPerc, null);
        builder.addDoubleProperty("Absolute Position", getAbsPos, null);
        builder.addDoubleProperty("Height above gnd (m)", getHeightAboveGndM, null);
        builder.addDoubleProperty("Distsance off center (m)", getDistOffCenterM, null);
    }


    
}
