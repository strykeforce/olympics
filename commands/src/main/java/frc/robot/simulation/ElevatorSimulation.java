package frc.robot.simulation;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Constants.ElevatorConstants;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.Sendable;
import edu.wpi.first.wpilibj.simulation.ElevatorSim;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;
import edu.wpi.first.wpilibj.system.plant.DCMotor;
import edu.wpi.first.wpilibj.util.Units;
import edu.wpi.first.wpiutil.math.Matrix;
import edu.wpi.first.wpiutil.math.VecBuilder;
import edu.wpi.first.wpiutil.math.numbers.N1;
import edu.wpi.first.wpiutil.math.numbers.N2;

public class ElevatorSimulation implements Sendable{
    private static final ElevatorSimulation sim = new ElevatorSimulation();
    private TalonSRX elevatorTalon;
    private ElevatorSim elevatorSim;

    private DCMotor elevatorGearbox = DCMotor.getVex775Pro(1);
    private double gearRatio = ElevatorConstants.kFirstStage * ElevatorConstants.kSecondStage;
    private double carriageMassKg = Units.lbsToKilograms(ElevatorConstants.kCarriageMasslb);
    private double drumRadiusMeters = Units.inchesToMeters(ElevatorConstants.kDrumRadiusIn);
    private double minHeightMeters = Units.inchesToMeters(ElevatorConstants.kMinTicks * ElevatorConstants.kTicksPerIn + ElevatorConstants.kInOffset);
    private double maxHeightMeters = Units.inchesToMeters(ElevatorConstants.kMaxTicks * ElevatorConstants.kTicksPerIn + ElevatorConstants.kInOffset);

    //State Variables
    private static boolean hasZeroed = false;
    private int lastPosTicks;
    private int lastVelTicks = 0;
    private double lastCurrDraw = 0;
    private double lastOutPerc = 0;
    private int newAbs = 0;
    
    public static ElevatorSimulation getInstance(){
        return sim;
    }

    private ElevatorSimulation(){
        elevatorSim = new ElevatorSim(
            elevatorGearbox, 
            gearRatio, 
            carriageMassKg, 
            drumRadiusMeters, 
            minHeightMeters, 
            maxHeightMeters, 
            VecBuilder.fill(Units.inchesToMeters(0.01)) //Std dev in inches
        );
        hasZeroed = false;
    }

    public void setupSimulation(TalonSRX talon){
        if(talon != null){
            elevatorTalon = talon;
            lastPosTicks = (int) talon.getSelectedSensorPosition();
            elevatorTalon.getSimCollection().setPulseWidthPosition(2048);
            setLimitSwitchState();
        }else{
            System.out.println("No elevator talon supplied");
        }
    }

    private void setLimitSwitchState(){
        double absPos = elevatorTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
        double relPos = elevatorTalon.getSelectedSensorPosition();
        if(!hasZeroed && (absPos < 4096) ||
            hasZeroed && (relPos < 3000)){
                elevatorTalon.getSimCollection().setLimitFwd(true);
        } else{
            elevatorTalon.getSimCollection().setLimitFwd(false);
        } 
    }

    public void zeroSimulation(int newPos){
        double heightIn = newPos * ElevatorConstants.kTicksPerIn + ElevatorConstants.kInOffset;
        double heightM = Units.inchesToMeters(heightIn);
        Matrix<N2,N1> currState = new Matrix<>(N2.instance,N1.instance);
        currState.set(0,0,heightM); //new elevator height in m
        currState.set(1,0,0); //new elevator speed m/s
        elevatorSim.setState(currState);
        lastPosTicks = newPos;
        hasZeroed = true;
    }

    public void update(){
        double outPerc = elevatorTalon.getMotorOutputPercent();
        elevatorSim.setInputVoltage(outPerc * RobotController.getBatteryVoltage());
        elevatorSim.update(0.020);

        //Get new Elevator State
        double heightIn = Units.metersToInches(elevatorSim.getPositionMeters());
        double speedInPerS = Units.metersToInches(elevatorSim.getVelocityMetersPerSecond());
        double currDraw = elevatorSim.getCurrentDrawAmps();

        //Set Talon State
        elevatorTalon.getSimCollection().setStatorCurrent(currDraw);
        elevatorTalon.getSimCollection().setSupplyCurrent(currDraw * outPerc);
        setLimitSwitchState();
        elevatorTalon.getSimCollection().setBusVoltage(RobotController.getBatteryVoltage());

        int posTicks = (int) ((heightIn - ElevatorConstants.kInOffset) * ElevatorConstants.kTicksPerIn);
        int offset = posTicks - lastPosTicks;
        elevatorTalon.getSimCollection().addQuadraturePosition(offset);
        int absPos = elevatorTalon.getSensorCollection().getPulseWidthPosition() & 0xFFF;
        newAbs = Math.floorMod(absPos + offset, 4096);
        elevatorTalon.getSimCollection().addPulseWidthPosition(newAbs - absPos);

        int velTicks = (int) (speedInPerS * ElevatorConstants.kTicksPerIn / 10.0); //ticks per 100ms
        elevatorTalon.getSimCollection().setQuadratureVelocity(velTicks);

        lastPosTicks = posTicks;
        lastVelTicks = velTicks;
        lastCurrDraw = currDraw;
        lastOutPerc = outPerc;
    }

    public double getCurrentDraw(){
        return lastCurrDraw;
    }

    protected double getInnerCarriageHeightM(){
        return elevatorSim.getPositionMeters() * 2;
    }

    //Sendable Interface
    DoubleSupplier getPosM = () -> elevatorSim.getPositionMeters();
    DoubleSupplier getVelMperS = () -> elevatorSim.getVelocityMetersPerSecond();
    DoubleSupplier getCurrent = () -> elevatorSim.getCurrentDrawAmps();
    DoubleSupplier getPosTicks = () -> lastPosTicks;
    DoubleSupplier getVelTicks = () -> lastVelTicks;
    DoubleSupplier getOutPerc = () -> lastOutPerc;
    DoubleSupplier getAbsPos = () -> newAbs;
    DoubleSupplier getInnerPosM = () -> getInnerCarriageHeightM();
    @Override
    public void initSendable(SendableBuilder builder) {
        builder.setSmartDashboardType("ElevatorSimSendable");
        builder.addDoubleProperty("Position (m)", getPosM, null);
        builder.addDoubleProperty("Velocity (mps)", getVelMperS, null);
        builder.addDoubleProperty("Stator Current (A)", getCurrent, null);
        builder.addDoubleProperty("Position (ticks)", getPosTicks, null);
        builder.addDoubleProperty("Percent Output", getOutPerc, null);
        builder.addDoubleProperty("Absolute Pos (ticks)", getAbsPos, null);
        builder.addDoubleProperty("Inner carriage height (m)", getInnerPosM, null);
    }
    
}
