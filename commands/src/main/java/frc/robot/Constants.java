// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {

    //SuperStructure Dimensions
    public static final double kStructureHeightM = 0.5;
    public static final double kStructureOpeningWidthM = 0.6;

    public static final class ElevatorConstants{
        public static final int kAbsZeroTicks = 1000;

        //Tuning Constants
        public static final double kP = 0.3;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
        public static final double kF = 0.15;
        public static final double kMotionAcceleration = 8_000;
        public static final double kMotionCruiseVelocity = 4_000;
        
        //Simulation Constants
        public static final double kCarriageMasslb = 1.0;
        public static final double kFirstStage = 5.0;
        public static final double kSecondStage = 5.0;
        public static final double kDrumRadiusIn = 3.0;
        public static final double kTicksPerIn = 1120;
        public static final double kInOffset = 4.0;
        public static final int kMaxTicks = 33_000;
        public static final int kMinTicks = 0;
    }

    public static final class ArmConstants{
        public static final int kAbsZeroTicks = 2000;
        
        //Tuning Constants
        public static final double kP = 1.0;
        public static final double kI = 0.0;
        public static final double kD = 0.0;
        public static final double kF = 0.65;
        public static final double kMotionAcceleration = 2_500;
        public static final double kMotionCruiseVelocity = 1_000;
        
        //Simulation Constants
        public static final double kArmMassLb = 0.5;
        public static final double kArmLengthM = 0.45;
        public static final double kTicksPerDeg = 34.0;
        public static final double kMaxAngleDeg = 360.0;
        public static final double kMinAngleDeg = -180.0;
        public static final int kFirstStage = 9;
        public static final int kSecondStage = 9;
        public static final double kDegreeOffset = 90.0;
    }
}
