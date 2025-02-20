package org.firstinspires.ftc.teamcode;

import static com.acmerobotics.roadrunner.ftc.Actions.runBlocking;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.InstantAction;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.auto.AutoConstants;
import org.firstinspires.ftc.teamcode.auto.DepositActions;
import org.firstinspires.ftc.teamcode.auto.UpdateActions;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Autonomous(name = "RoadRunnerAutoMeepMeep", group = "Autonomous")
public class AutoRedBasket extends LinearOpMode {


    Claw claw;

    Elevator elevator;

    TelescopicHand telescopicHand;

    ReleaseSystem releaseSystem;

    DepositActions depositActions;
    UpdateActions updateActions;


    public AutoConstants autoConstants;
    RobotHardware robot = RobotHardware.getInstance();

    @Override
    public void runOpMode() {
        Pose2d startPoseRedLeft = new Pose2d(-35, -62, Math.toRadians(90));

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        robot.init(hardwareMap, telemetry, startPoseRedLeft);

        elevator = new Elevator(true);

        claw = new Claw();

        releaseSystem = new ReleaseSystem();

        telescopicHand = new TelescopicHand(true);


        elevator.setAuto(true);

        depositActions = new DepositActions(elevator, claw, telescopicHand, releaseSystem);
        updateActions = new UpdateActions(elevator, claw, telescopicHand, releaseSystem);


        SequentialAction retractElevatorPark = new SequentialAction(

                depositActions.moveOuttake(ReleaseSystem.Angle.INTAKE),
                new SleepAction(0.4),
                depositActions.moveElevator(0),
                new SleepAction(0.4),
                depositActions.moveTelescopic(0)

        );


        while (opModeInInit() && !isStopRequested()) {

            claw.updateState(Claw.ClawState.CLOSED, ClawSide.BOTH);
            releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
            releaseSystem.setAngle(ReleaseSystem.Angle.SPIN_MIDDLE);

        }

        waitForStart();


        if (isStopRequested()) return;

        // Define the trajectory sequence
        Action redTrajIntake = robot.drive.actionBuilder(startPoseRedLeft)
                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                .afterTime(0.1, releaseSampler())
                .afterTime(2.4, closeElevator())
                // TODO: Implement action for putting the sample


                .waitSeconds(2.3)
                .strafeToLinearHeading(new Vector2d(-46, -41.5), Math.toRadians(90)) // Strafe left 13 inches
                // TODO: Implement action for taking the sample


                .afterTime(0.7, intakeSample(-4))
                .afterTime(1.7, closeIntake())

                .waitSeconds(2)
                .afterTime(1.2, releaseSampler())
                .afterTime(3.4, closeElevator())
                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))

                // TODO: Implement action for putting the sample

                .waitSeconds(2)


                .strafeToLinearHeading(new Vector2d(-55, -42), Math.toRadians(90))

                .afterTime(1, intakeSample(-5))
                .afterTime(1.6, closeIntake())

                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                .afterTime(0.2, releaseSampler())

                .afterTime(3, closeElevator())

                .waitSeconds(2.5)


                .afterTime(1.5, intakeSample(-5))
                .afterTime(2.8, closeIntake())

                .strafeToLinearHeading(new Vector2d(-53.9, -38.5), Math.toRadians(120))

                .waitSeconds(2)

                .strafeToLinearHeading(new Vector2d(-51.5, -52.5), Math.toRadians(45))

                .afterTime(0, releaseSampler())
                .afterTime(2.3, closeElevator())

                .waitSeconds(2.5)

                .strafeToLinearHeading(new Vector2d(-48, -60), Math.toRadians(92))


                .build();

        // Follow the trajectory sequence
        runBlocking(new ParallelAction(
                updateActions.updateSystems(),
                redTrajIntake
        ));


        telemetry.addData("Status", "Autonomous Complete");
        telemetry.update();
    }

    SequentialAction releaseSampler() {

        return new SequentialAction(
                depositActions.moveTelescopic(97),
                new SleepAction(0.5),
                depositActions.moveElevator(3700),
                new SleepAction(0.3),
                depositActions.moveOuttake(ReleaseSystem.Angle.OUTTAKE),
                new SleepAction(1),
                depositActions.moveClaw(Claw.ClawState.OPEN, ClawSide.BOTH)

        );

    }

    SequentialAction closeElevator() {

        return new SequentialAction(
                depositActions.moveOuttake(ReleaseSystem.Angle.INTAKE),
                new SleepAction(0.4),
                depositActions.moveElevator(0),
                new SleepAction(0.8),
                depositActions.moveTelescopic(0)

        );

    }

    SequentialAction closeIntake() {

        return new SequentialAction(

                depositActions.moveClaw(Claw.ClawState.CLOSED, ClawSide.BOTH),
                new SleepAction(0.5),
                depositActions.moveTelescopic(0),
                new SleepAction(0.2),
                depositActions.moveElevator(0)

        );

    }

    SequentialAction intakeSample( int angle) {
          return new SequentialAction(

                depositActions.moveElevator(1000),
                new SleepAction(0.4),
                depositActions.moveTelescopic(angle)

        );

    }
}
