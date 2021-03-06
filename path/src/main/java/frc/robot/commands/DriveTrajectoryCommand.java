package frc.robot.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

public class DriveTrajectoryCommand extends CommandBase{
    private DriveSubsystem driveSubsystem;
    private Trajectory trajectory;
    private Rotation2d desiredAngle;
    private Timer timer = new Timer();
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Trajectory.State desiredState = new Trajectory.State();

    public DriveTrajectoryCommand(DriveSubsystem driveSubsystem, String trajectoryName, Double targetAngle){
        addRequirements(driveSubsystem);
        this.driveSubsystem = driveSubsystem;
        desiredAngle = Rotation2d.fromDegrees(targetAngle); //Desired swerve heading through full trajectory
        trajectory = driveSubsystem.generateTrajectory(trajectoryName);
        timer.start();
    }

    @Override
    public void initialize() {
        Pose2d initialPose = trajectory.getInitialPose();
        driveSubsystem.resetOdometry(new Pose2d(initialPose.getTranslation(),desiredAngle));
        driveSubsystem.grapherTrajectoryActive(true);
        timer.reset();
        logger.info("Begin Trajectory");
    }
    
    @Override
    public void execute() {
        desiredState = trajectory.sample(timer.get());
        driveSubsystem.calculateController(desiredState, desiredAngle);
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(trajectory.getTotalTimeSeconds());
    }

    @Override
    public void end(boolean interrupted) {
        driveSubsystem.drive(0, 0, 0);
        driveSubsystem.grapherTrajectoryActive(false);
        logger.info("End Trajectory: {}", timer.get());
    }
}
