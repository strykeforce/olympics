package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strykeforce.swerve.SwerveDrive;
import org.strykeforce.swerve.TalonSwerveModule;
import org.strykeforce.telemetry.TelemetryService;
import org.strykeforce.telemetry.measurable.MeasurableSubsystem;
import org.strykeforce.telemetry.measurable.Measure;

import edu.wpi.first.wpilibj.controller.HolonomicDriveController;
import edu.wpi.first.wpilibj.controller.PIDController;
import edu.wpi.first.wpilibj.controller.ProfiledPIDController;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.geometry.Translation2d;
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig;
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.trajectory.Trajectory.State;
import frc.robot.Constants.DriveConstants;
import net.consensys.cava.toml.Toml;
import net.consensys.cava.toml.TomlArray;
import net.consensys.cava.toml.TomlParseResult;
import net.consensys.cava.toml.TomlTable;

import static frc.robot.Constants.kTalonConfigTimeout;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Set;

public class DriveSubsystem extends MeasurableSubsystem{
    private static final Logger logger = LoggerFactory.getLogger(DriveSubsystem.class);
    private final SwerveDrive swerveDrive;
    private final HolonomicDriveController holonomicController;

    //Grapher Variables
    private ChassisSpeeds holoContOutput = new ChassisSpeeds();
    private State holoContInput = new State();
    private Rotation2d holoContAngle = new Rotation2d();
    private Double trajectoryActive = 0.0;

    public DriveSubsystem(TelemetryService telemetryService){
        //Configure Swerve Drive
        var moduleBuilder = new TalonSwerveModule.Builder()
            .driveGearRatio(DriveConstants.kDriveGearRatio)
            .wheelDiameterInches(DriveConstants.kWheelDiameterInches)
            .driveMaximumMetersPerSecond(DriveConstants.kMaxSpeedMetersPerSecond);

        TalonSwerveModule[] swerveModules = new TalonSwerveModule[4];
        Translation2d[] wheelLocations = DriveConstants.getWheelLocationMeters();

        //Initialize talon/falcon configs for each wheel
        for(int i = 0; i < 4; i++){
            var azimuthTalon = new TalonSRX(i);
            azimuthTalon.configFactoryDefault(kTalonConfigTimeout);
            azimuthTalon.configAllSettings(DriveConstants.getAzimuthTalonConfig(), kTalonConfigTimeout);
            azimuthTalon.enableCurrentLimit(true);
            azimuthTalon.enableVoltageCompensation(true);
            azimuthTalon.setNeutralMode(NeutralMode.Coast);

            var driveTalon = new TalonFX(i + 10);
            driveTalon.configFactoryDefault(kTalonConfigTimeout);
            driveTalon.configAllSettings(DriveConstants.getDriveTalonConfig(), kTalonConfigTimeout);
            driveTalon.enableVoltageCompensation(true);
            driveTalon.setNeutralMode(NeutralMode.Brake);
            driveTalon.setInverted(true);

            swerveModules[i] = moduleBuilder
                .azimuthTalon(azimuthTalon)
                .driveTalon(driveTalon)
                .wheelLocationMeters(wheelLocations[i])
                .build();

            swerveModules[i].loadAndSetAzimuthZeroReference();
            telemetryService.register(azimuthTalon);
            telemetryService.register(driveTalon);
        }

        swerveDrive = new SwerveDrive(swerveModules);
        swerveDrive.resetGyro();

        //Setup Holonomic Controller
        ProfiledPIDController omegaCont = new ProfiledPIDController(DriveConstants.kPOmega, DriveConstants.kIOmega, DriveConstants.kDOmega,
            new TrapezoidProfile.Constraints(DriveConstants.kMaxOmega, DriveConstants.kMaxAccelOmega));
        omegaCont.enableContinuousInput(Math.toRadians(-180), Math.toRadians(180));
        holonomicController = new HolonomicDriveController(
            new PIDController(DriveConstants.kPHolonomic, DriveConstants.kIHoonomic, DriveConstants.kDHolonomic), 
            new PIDController(DriveConstants.kPHolonomic, DriveConstants.kIHoonomic, DriveConstants.kDHolonomic), 
            omegaCont
        );
        //Disabling the holonomic controller makes the robot directly follow the trajectory output (no closing the loop on x,y,theta errors)
        holonomicController.setEnabled(true); 
    }

    @Override
    public void periodic() {
        //Update swerve module states every robot loop
        swerveDrive.periodic();
    }

    //Open-Loop Swerve Movements
    public void drive(double vXmps, double vYmps, double vOmegaRadps){
        swerveDrive.drive(vXmps, vYmps, vOmegaRadps, true);
    }

    //Closed-Loop (Velocity-Controlled) Swerve Movements
    public void move(double vXmps, double vYmps, double vOmegaRadps, Boolean isFieldOriented){
        swerveDrive.move(vXmps, vYmps, vOmegaRadps, isFieldOriented);
    }

    public void resetGyro(){
        swerveDrive.resetGyro();
    }

    public void setGyroOffset(Rotation2d rotation){
        swerveDrive.setGyroOffset(rotation);
    }

    public void resetOdometry(Pose2d pose){
        swerveDrive.resetOdometry(pose);
        logger.info("reset odometry with: {}", pose);
    }

    public Pose2d getPoseMeters(){
        return swerveDrive.getPoseMeters();
    }

    //Trajectory TOML Parsing
    public Trajectory generateTrajectory(String trajectoryName){
        Trajectory trajectoryGenerated = new Trajectory();
        try{
            TomlParseResult parseResult = Toml.parse(Paths.get("/home/lvuser/deploy/paths/" + trajectoryName + ".toml"));
            logger.info("Generating Trajectory: {}", trajectoryName);
            Pose2d startPose = new Pose2d(
                parseResult.getTable("start_pose").getDouble("x"),
                parseResult.getTable("start_pose").getDouble("y"),
                Rotation2d.fromDegrees(parseResult.getTable("start_pose").getDouble("angle"))
            );
            Pose2d endPose = new Pose2d(
                parseResult.getTable("end_pose").getDouble("x"),
                parseResult.getTable("end_pose").getDouble("y"),
                Rotation2d.fromDegrees(parseResult.getTable("end_pose").getDouble("angle"))
            );
            TomlArray internalPointsToml = parseResult.getArray("internal_points");
            ArrayList<Translation2d> path = new ArrayList<>();
            logger.info("Toml Internal Points Array Size: {}", internalPointsToml.size());

            for(int i = 0; i < internalPointsToml.size(); i++){
                TomlTable waypointToml = internalPointsToml.getTable(i);
                Translation2d waypoint = new Translation2d(
                    waypointToml.getDouble("x"),
                    waypointToml.getDouble("y")
                );
                path.add(waypoint);
            }
            
            //Trajectory Config parsed from toml - any additional constraints would be added here
            TrajectoryConfig trajectoryConfig = new TrajectoryConfig(
                parseResult.getDouble("max_velocity"),
                parseResult.getDouble("max_acceleration")
            );
            trajectoryConfig.setReversed(parseResult.getBoolean("is_reversed"));
            trajectoryConfig.setStartVelocity(parseResult.getDouble("start_velocity"));
            trajectoryConfig.setEndVelocity(parseResult.getDouble("end_velocity"));

            trajectoryGenerated = TrajectoryGenerator.generateTrajectory(startPose, path, endPose, trajectoryConfig);
        }catch (IOException error){
            logger.error(error.toString());
            logger.error("Path {} not found", trajectoryName);
        }
        return trajectoryGenerated;
    }


    //Holonomic Controller
    public void calculateController(State desiredState, Rotation2d desiredAngle){
        holoContInput = desiredState;
        holoContAngle = desiredAngle;
        holoContOutput = holonomicController.calculate(getPoseMeters(), desiredState, desiredAngle);
        move(holoContOutput.vxMetersPerSecond, holoContOutput.vyMetersPerSecond, holoContOutput.omegaRadiansPerSecond, true);

    }

    //Make whether a trajectory is currently active obvious on grapher
    public void grapherTrajectoryActive(Boolean active){
        if(active) trajectoryActive = 1.0;
        else trajectoryActive = 0.0;
    }



    //Measureable Implementation - Grapher Support
    @Override
    public Set<Measure> getMeasures() {
        return Set
            .of(
                new Measure("Gyro Rotation2D(deg)", () -> swerveDrive.getHeading().getDegrees()),
                new Measure("Odometry X", () -> swerveDrive.getPoseMeters().getX()),
                new Measure("Odometry Y", () -> swerveDrive.getPoseMeters().getY()),
                new Measure("Odometry Rotation2D(deg)", () -> swerveDrive.getPoseMeters().getRotation().getDegrees()),
                new Measure("Trajectory Vel", () -> holoContInput.velocityMetersPerSecond),
                new Measure("Trajectory Accel", () -> holoContInput.accelerationMetersPerSecondSq),
                new Measure("Trajectory X", () -> holoContInput.poseMeters.getX()),
                new Measure("Trajectory Y", () -> holoContInput.poseMeters.getY()),
                new Measure("Trajectory Rotation2D(deg)", () -> holoContInput.poseMeters.getRotation().getDegrees()),
                new Measure("Desired Gyro Heading(deg)", () -> holoContAngle.getDegrees()),
                new Measure("Holonomic Cont Vx", () -> holoContOutput.vxMetersPerSecond),
                new Measure("Holonomic Cont Vy", () -> holoContOutput.vyMetersPerSecond),
                new Measure("Holonomic Cont Vomega", () -> holoContOutput.omegaRadiansPerSecond),
                new Measure("Trajectory Active", () -> trajectoryActive)
            );
    }

    
}
