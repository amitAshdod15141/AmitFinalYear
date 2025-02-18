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
@TeleOp(name = "Full OpMode Teleop")
public class ReadyOpMode extends LinearOpMode {


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

    public static boolean firstIntake = true , canRetract = false , stayClosed = false;
    public static double cooldown = 0, COOL_DOWN = 350 , transferDelay = 0 , retractTime = 0 ,target = 0;


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

            if (gamepad2.right_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }

            if (gamepad2.left_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }


            if(betterGamepad1.shareOnce())
            {
                drivetrain.maxPower = 0.9;
            }

            systemStateMachine();
        }

    }



    void systemStateMachine ()
    {

        switch (liftState)
        {

            case RETRACT:


                telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                elevator.setTarget(0);


                if (stayClosed == true)
                {
                    claw.updateState(Claw.ClawState.INTAKE);
                } else
                {
                    claw.updateState(Claw.ClawState.OPEN);
                }

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

                target = Elevator.HIGH_BASKET_LEVEL;

                elevator.setTarget(target);

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad2.dpadUpOnce()) { target += 200; }

                if(betterGamepad2.dpadDownOnce()) { target -= 200; }

                if(betterGamepad1.AOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    stayClosed = false;

                }
                break;

            case EXTRACT_LOW_BAKSET:


                elevator.setTarget(Elevator.LOW_BASKET_LEVEL);

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.AOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if (betterGamepad2.dpadUpOnce())
                {


                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    stayClosed = false;
                }
                break;

            case EXTRACT_HIGH:

                if(betterGamepad1.BOnce())
                {
                    liftState = LiftState.EXTRACT_CONFIRM;
                }

                target = Elevator.HIGH_EXTRACT_LEVEL;
                elevator.setTarget(target);

                if(betterGamepad2.dpadUpOnce()) { target += 200; }

                if(betterGamepad2.dpadDownOnce()) { target -= 200; }

                if(getTime() - transferDelay >= 300)
                {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.AOnce())
                {
                    claw.updateState(Claw.ClawState.OPEN);
                    retractTime = getTime();
                }

                if(getTime() - retractTime >= 300 && canRetract)
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    stayClosed = false;

                }
                break;


            case EXTRACT_CONFIRM:

                target = Elevator.HIGH_EXTRACT_LEVEL;
                elevator.setTarget(target);

                if(betterGamepad2.dpadUpOnce()) { target += 200; }

                if(betterGamepad2.dpadDownOnce()) { target -= 200; }

                if (betterGamepad1.AOnce())
                {
                    liftState = LiftState.RETRACT;
                    canRetract = false;
                    telescopicHand.setTarget(TelescopicHand.RETARCT_TELESCOPE);
                    stayClosed = false;
                }

                break;
            case INTAKE_SHORT:


                if (betterGamepad1.rightBumperOnce()) {
                    liftState = liftState.INTAKE_LONG;
                }

                target = Elevator.INTAKE_SHORT;
                elevator.setTarget(target);

                if(betterGamepad2.dpadRightOnce()) { target += 200; }

                if(betterGamepad2.dpadLeftOnce()) { target -= 200; }

                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                    stayClosed = true;
                }

                break;

            case INTAKE_LONG:

                if (betterGamepad1.rightBumperOnce()) {
                    liftState = liftState.RETRACT;
                }

                telescopicHand.setTarget(TelescopicHand.INTAKE_LONG);
                elevator.setTarget(target);

                target = Elevator.INTAKE_LONG;

                if(betterGamepad2.dpadRightOnce()) { target += 200; }

                if(betterGamepad2.dpadLeftOnce()) { target -= 200; }

                if (betterGamepad1.leftBumperOnce()) {
                    firstIntake = false;
                    claw.updateState(Claw.ClawState.INTAKE);
                    liftState = LiftState.RETRACT;
                    stayClosed = true;
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
