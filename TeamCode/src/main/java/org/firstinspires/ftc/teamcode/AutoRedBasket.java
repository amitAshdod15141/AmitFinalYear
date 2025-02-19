package org.firstinspires.ftc.teamcode;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.roadrunner.MecanumDrive;

@Autonomous(name = "RoadRunnerAutoMeepMeep", group = "Autonomous")
public class AutoRedBasket extends LinearOpMode {


    public AutoConstants autoConstants;
    RobotHardware robot = RobotHardware.getInstance();
    @Override
    public void runOpMode() {
        robot.init(hardwareMap , telemetry, autoConstants.startPoseRedLeft);

        waitForStart();

        if (isStopRequested()) return;

        // Define the trajectory sequence
        Action redTrajIntake = robot.drive.actionBuilder(robot.drive.pose)
                .strafeToLinearHeading(new Vector2d(-48, -42), Math.toRadians(90)) // Strafe left 13 inches
                // TODO: Implement action for taking the sample

                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                // TODO: Implement action for putting the sample

                .strafeToLinearHeading(new Vector2d(-59, -42), Math.toRadians(90))
                // TODO: Implement action for taking the sample

                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                // TODO: Implement action for putting the sample

                // Parking sequence:
                .strafeToLinearHeading(new Vector2d(-48, -62), Math.toRadians(90))
                .build();

        // Follow the trajectory sequence
        runBlocking(redTrajIntake);


        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }
}
