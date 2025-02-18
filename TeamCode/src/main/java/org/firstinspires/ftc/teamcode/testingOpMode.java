package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.command.CommandScheduler;
import com.arcrobotics.ftclib.gamepad.GamepadEx;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Drivetrain;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;

@Config
@TeleOp(name = "Testing OpMode Teleop")
public class testingOpMode extends LinearOpMode {


    Drivetrain drivetrain;
    Elevator elevator;

    ReleaseSystem releaseSystem;
    Claw claw;

    TelescopicHand telescopicHand;

    ElapsedTime codeTime;
    Gamepad.RumbleEffect customRumbleEffect;    // Use to build a custom rumble sequence.

    // gamepads
    GamepadEx gamepadEx, gamepadEx2;
    BetterGamepad betterGamepad1, betterGamepad2;

    public static boolean firstIntake = true , canRetract = false;
    public static double cooldown = 0, COOL_DOWN = 350 , transferDelay = 0 , retractTime = 0;


    public enum LiftState {
        RETRACT,
        EXTRACT_HIGH,
        EXTRACT_CONFIRM,
        EXTRACT_HIGH_BASKET,
        EXTRACT_LOW_BAKSET,
        STUCK_2,
        HANG,


        RETARCTED,
        INTAKE_SHORT,

        INTAKE_LONG
    }


    LiftState liftState = LiftState.RETRACT;


    RobotHardware robot = RobotHardware.getInstance();

    @Override
    public void runOpMode() throws InterruptedException {

        CommandScheduler.getInstance().reset();

        telemetry = new MultipleTelemetry(FtcDashboard.getInstance().getTelemetry());

        gamepadEx = new GamepadEx(gamepad1);
        gamepadEx2 = new GamepadEx(gamepad2);

        betterGamepad1 = new BetterGamepad(gamepad1);
        betterGamepad2 = new BetterGamepad(gamepad2);

        robot.init(hardwareMap, telemetry);

        drivetrain = new Drivetrain(gamepad1, true, true);
        elevator = new Elevator(gamepad2, true, false);
        releaseSystem = new ReleaseSystem();
        telescopicHand = new TelescopicHand(gamepad2, true, false);


        claw = new Claw();

        codeTime = new ElapsedTime();

        claw.setClaw(Claw.ClawState.OPEN);
        releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);

        elevator.setAuto(false);
        telescopicHand.setAuto(false);

        drivetrain.update();
        releaseSystem.update();
        elevator.update();
        telescopicHand.update();
        claw.update();

        customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 250)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .build();

        while (opModeInInit() && !isStopRequested()) {
            telemetry.addLine("Initialized");
            telemetry.update();
            drivetrain.update();
            releaseSystem.update();

        }

        waitForStart();

        codeTime.reset();


        while (opModeIsActive()) {

            betterGamepad1.update();
            betterGamepad2.update();
            drivetrain.update();
            releaseSystem.update();
            elevator.update();
            telescopicHand.update();
            claw.update();

            if(elevator.getPos() == 0 && betterGamepad1.rightBumperOnce()) {



            }
            elevatorStateMachine();
        }

    }

    /*
    void intakeStateMachine () {

        switch (intakeState) {
            case RETARCTED:

                elevator.setTarget(0);
                releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                claw.updateState(Claw.ClawState.OPEN);

                if (firstIntake) {
                    telescopicHand.setTarget(TelescopicHand.INTAKE_SHORT);
                } else {
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    liftState = LiftState.RETRACT;
                }



                if (betterGamepad1.rightBumperOnce()) {
                    intakeState = IntakeState.INTAKE_SHORT;
                }

                break;

            case INTAKE_SHORT:


                if (betterGamepad1.rightBumperOnce()) {
                    intakeState = IntakeState.INTAKE_LONG;
                }

                elevator.setTarget(Elevator.INTAKE_SHORT);

                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                }

                break;

            case INTAKE_LONG:

                if (betterGamepad1.rightBumperOnce()) {
                    intakeState = IntakeState.RETARCTED;
                }

                telescopicHand.setTarget(TelescopicHand.INTAKE_LONG);
                elevator.setTarget(Elevator.INTAKE_LONG);


                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                }

                break;
        }
    }

     */

    void elevatorStateMachine ()
    {

        switch (liftState)
        {

            case RETRACT:


                telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                claw.updateState(Claw.ClawState.OPEN);
                elevator.setTarget(0);


                if (firstIntake) {
                    telescopicHand.setTarget(TelescopicHand.INTAKE_SHORT);
                } else {
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                }

                if (betterGamepad1.rightBumperOnce()) {
                    liftState = liftState.INTAKE_SHORT;
                }

                if(betterGamepad1.YOnce())
                {
                    telescopicHand.setTarget(TelescopicHand.OUTTAKE_TELESCOPE);

                    transferDelay = getTime();
                    liftState = LiftState.EXTRACT_HIGH_BASKET;


                }

                if(betterGamepad1.BOnce())
                {
                    transferDelay = getTime();
                    liftState = LiftState.EXTRACT_HIGH;
                }

            break;

            case EXTRACT_HIGH_BASKET:

                if(betterGamepad1.YOnce())
                {
                    liftState = LiftState.EXTRACT_LOW_BAKSET;
                }

                elevator.setTarget(Elevator.HIGH_BASKET_LEVEL);

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.leftBumperOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);

                }
                break;

            case EXTRACT_LOW_BAKSET:


                elevator.setTarget(Elevator.LOW_BASKET_LEVEL);

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.leftBumperOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                }
                break;

            case EXTRACT_HIGH:

                if(betterGamepad1.BOnce())
                {
                    liftState = LiftState.EXTRACT_CONFIRM;
                }

                elevator.setTarget(Elevator.HIGH_EXTRACT_LEVEL);

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.leftBumperOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);

                }
                break;


            case EXTRACT_CONFIRM:

                elevator.setTarget(Elevator.HIGH_EXTRACT_LEVEL);

                if (betterGamepad1.AOnce())
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                }

                break;
            case INTAKE_SHORT:


                if (betterGamepad1.rightBumperOnce()) {
                    liftState = liftState.INTAKE_LONG;
                }

                elevator.setTarget(Elevator.INTAKE_SHORT);

                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                }

                break;

            case INTAKE_LONG:

                if (betterGamepad1.rightBumperOnce()) {
                    liftState = liftState.RETRACT;
                }

                telescopicHand.setTarget(TelescopicHand.INTAKE_LONG);
                elevator.setTarget(Elevator.INTAKE_LONG);


                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                }

                break;
        }



    }




    double getTime()
    {
        return codeTime.nanoseconds() / 1000000;
    }

    boolean cooldowned()
    {
        return (getTime() - cooldown) >= COOL_DOWN;
    }

    void coolDownReset()
    {
        cooldown = getTime();
    }

}
