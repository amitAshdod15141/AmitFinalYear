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
import org.firstinspires.ftc.teamcode.util.ClawSide;

@Config
@TeleOp(name = "DEBUG OpMode Teleop")
public class DebugOpMode extends LinearOpMode {

    // robot
    private final RobotHardware robot = RobotHardware.getInstance();

    // subsystems
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

    // delays
    public static double delayTransfer = 300, delayRelease = 1100, delayCloseTransfer = 350, XDelay = 500, goToIntakeDelay = 50, goToAlmostIntakeDelay = 250;
    public static double WAIT_DELAY_TILL_OUTTAKE = 150, WAIT_DELAY_TILL_CLOSE = 250, ELEVATOR_ZERO = 10, COOL_DOWN = 350;
    public static double DEFAULT_INTAKE_EXTEND_PRECENTAGE = 42.5, SHORT_INTAKE_EXTEND_PRECENTAGE = 25, delayReleaseFromIntake = 200;
    // variables
    double elevatorReset = 0, previousElevator = 0, transferTimer = 0, releaseTimer = 0, closeTransferTimer = 0, goToTransferTimer = 0, goToIntakeTimer = 0, goToAlmostIntakeTimer = 0;
    double elevatorTarget = 400, intakePrecentage = DEFAULT_INTAKE_EXTEND_PRECENTAGE, releaseFromIntake = 0, startXDelay = 0, cooldown = 0;
    int openedXTimes = 0, ACount = 0;
    boolean firstOuttakeAngle = true, retract = false,  goToMid = false, intakeMid = true, canIntake = true, startedDelayTransfer = false, heldExtension = false, firstReleaseThreeTimer = true;
    boolean override = false, had2Pixels = false, hang = false, resetRightTrigger = true, closeClaw = false, wasClosed = false, firstExtend = true, XPressed = false;
    boolean overrideIntakeExtension = false, movedStack = false, outtakeToOuttake = true, firstReleaseThree = true, firstOuttake = true, goToIntake = false, goToAlmostIntake = false;
    boolean rightClaw = true, leftClaw = true , isRetarcted = false , firstRetract = true , canRetract = false;


    public static double delayRetract = 0;



    public enum IntakeState {
        RETARCTED,
        INTAKE_SHORT,
        INTAKE_MID,

        INTAKE_LONG
    }


    public enum LiftState {
        RETRACT,
        EXTRACT_HIGH,
        EXTRACT_CONFIRM,
        EXTRACT_HIGH_BASKET,
        EXTRACT_LOW_BAKSET,
        STUCK_2,
        HANG
    }



    LiftState liftState = LiftState.RETRACT;
    IntakeState intakeState = IntakeState.INTAKE_SHORT;

    @Override
    public void runOpMode() {
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
        releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
        elevator.setAuto(false);
        telescopicHand.setAuto(false);

        claw.update();
        releaseSystem.update();

        customRumbleEffect = new Gamepad.RumbleEffect.Builder()
                .addStep(0.0, 1.0, 500)  //  Rumble right motor 100% for 500 mSec
                .addStep(0.0, 0.0, 300)  //  Pause for 300 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .addStep(0.0, 0.0, 250)  //  Pause for 250 mSec
                .addStep(1.0, 0.0, 250)  //  Rumble left motor 100% for 250 mSec
                .build();


        while (opModeInInit() && !isStopRequested())
        {
            telemetry.addLine("Initialized");
            telemetry.update();
            claw.update();
            releaseSystem.update();

        }

        waitForStart();

        codeTime.reset();


        while (opModeIsActive())
        {
            betterGamepad1.update();
            betterGamepad2.update();
            drivetrain.update();
            releaseSystem.update();
            elevator.update();
            telescopicHand.update();
            claw.update();


            if(gamepad2.left_trigger != 0 || gamepad1.left_trigger != 0)
            {
                drivetrain.superSlow();
            }




            if (gamepad2.right_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }

            if (gamepad2.right_stick_y != 0) {
                telescopicHand.setUsePID( false);
            } else {
                telescopicHand.setUsePID(true);
            }


            if(betterGamepad1.shareOnce())
            {
                drivetrain.maxPower = 0.9;
            }

            elevatorStateMachine();
            intakeStateMachine();


            telemetry.addData("left" , leftClaw);
            telemetry.addData("right" , rightClaw);
            telemetry.update();
        }
    }

    void intakeStateMachine ()
    {

        switch (intakeState)
        {
            case RETARCTED:

                claw.updateState(Claw.ClawState.OPEN);

                if(firstRetract)
                {
                    telescopicHand.setTarget(telescopicHand.INTAKE_SHORT);

                }  else
                {

                    telescopicHand.setTarget(telescopicHand.RETARCT_TELESCOPE);
                }

                elevator.setTarget(0);

                if(betterGamepad1.rightBumperOnce() && !isRetarcted) {

                    intakeState = IntakeState.INTAKE_SHORT;

                }

                break;

            case INTAKE_SHORT:

                if(betterGamepad1.rightBumperOnce() && !isRetarcted)
                {

                    intakeState = IntakeState.INTAKE_MID;

                }


                telescopicHand.setTarget(telescopicHand.INTAKE_SHORT);
                elevator.setTarget(elevator.INTAKE_SHORT);

                if(betterGamepad1.leftBumperOnce())
                {

                    claw.updateState(Claw.ClawState.INTAKE);
                    canRetract = true;

                    delayRetract = getTime();


                }

                if((getTime() - delayRetract >= 400))
                {

                    intakeState = IntakeState.RETARCTED;

                    firstRetract = false;

                    canRetract = false;

                }

                break;

            case INTAKE_LONG:


                if(betterGamepad1.rightBumperOnce() && !isRetarcted)
                {

                    intakeState = IntakeState.RETARCTED;

                }

                telescopicHand.setTarget(telescopicHand.INTAKE_SHORT);

                elevator.setTarget(elevator.INTAKE_SHORT);



                if(betterGamepad1.leftBumperOnce())
                {

                    claw.updateState(Claw.ClawState.INTAKE);
                    canRetract = true;

                    delayRetract = getTime();


                }
                if((getTime() - delayRetract >= 400))
                {

                    intakeState = IntakeState.RETARCTED;

                    firstRetract = false;

                    canRetract = false;

                }


        }
    }


    void elevatorStateMachine()
    {
        switch (liftState) {
            case RETRACT:



                firstRetract = true;
                isRetarcted = true;

                    if (betterGamepad1.BOnce()) {
                        releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                        liftState = LiftState.EXTRACT_HIGH_BASKET;
                    }


                    if (betterGamepad1.YOnce()) {

                        releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                        liftState = LiftState.EXTRACT_HIGH;
                    }



                firstOuttake = true;
                elevator.setTarget(0);



                if(betterGamepad2.dpadUpOnce())
                {
                    liftState = LiftState.HANG;
                }

                if(betterGamepad2.XOnce())
                {
                    liftState = LiftState.STUCK_2;
                }
                break;

            case EXTRACT_HIGH:


                isRetarcted = false;


                if(betterGamepad1.BOnce())
                {

                    liftState  = LiftState.EXTRACT_CONFIRM;
                }

                elevator.setTarget(elevator.HIGH_EXTRACT_LEVEL);

                if(gamepad2.right_stick_y != 0)
                {
                    elevatorTarget = elevator.getPos() - (openedXTimes * (Elevator.ELEVATOR_INCREMENT));
                }



                if (betterGamepad1.rightBumperOnce())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                }


                if ((getTime() - previousElevator) >= WAIT_DELAY_TILL_OUTTAKE) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                }



                 if ((getTime() - elevatorReset) >= WAIT_DELAY_TILL_CLOSE && retract)
                {
                    firstOuttakeAngle = true;
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                    liftState = LiftState.RETRACT;
                }
                break;

            case EXTRACT_CONFIRM:

                elevator.setTarget(elevator.CONFIRM_LEVEL);

                isRetarcted = false;


                if (betterGamepad1.rightBumperOnce())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                    elevatorReset = getTime();

                    retract = true;
                }

                if(gamepad2.right_stick_y != 0)
                {
                    elevatorTarget = elevator.getPos() - (openedXTimes * (Elevator.ELEVATOR_INCREMENT));
                }

                if ((getTime() - previousElevator) >= WAIT_DELAY_TILL_OUTTAKE) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if(betterGamepad1.dpadRightOnce() && cooldowned())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                }


                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 150;
                    coolDownReset();
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 150;
                    coolDownReset();
                }


                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                }


              if ((getTime() - elevatorReset) >= WAIT_DELAY_TILL_CLOSE && retract)
                {
                    retract = false;
                    firstOuttakeAngle = true;
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                    liftState = LiftState.RETRACT;
                }
                break;

            case EXTRACT_HIGH_BASKET:


                isRetarcted = false;

                if (betterGamepad1.YOnce())
                {
                    liftState = LiftState.EXTRACT_LOW_BAKSET;

                }
                elevator.setTarget(elevator.HIGH_BASKET_LEVEL);



                if (betterGamepad1.rightBumperOnce())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                    elevatorReset = getTime();
                }


                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                }

                if(gamepad2.right_stick_y != 0)
                {

                    elevatorTarget = elevator.getPos() - (openedXTimes * (Elevator.ELEVATOR_INCREMENT));
                }

                if ((getTime() - previousElevator) >= WAIT_DELAY_TILL_OUTTAKE) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }

                if (betterGamepad1.rightBumperOnce())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                    elevatorReset = getTime();

                    retract = true;
                }


                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 150;
                    coolDownReset();
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 150;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadLeftOnce())
                {
                    elevatorTarget -= 415;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadRightOnce())
                {
                    elevatorTarget += 415;
                    coolDownReset();
                }


                 if ((getTime() - elevatorReset) >= WAIT_DELAY_TILL_CLOSE && retract)
                {
                    retract = false;
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                    liftState = LiftState.RETRACT;
                }
                break;

            case EXTRACT_LOW_BAKSET:

                isRetarcted = false;


                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                }

                elevator.setTarget(elevator.LOW_BASKET_LEVEL);


                if(gamepad2.right_stick_y != 0)
                {
                    elevatorTarget = elevator.getPos() - (openedXTimes * (Elevator.ELEVATOR_INCREMENT));
                }

                if ((getTime() - previousElevator) >= WAIT_DELAY_TILL_OUTTAKE) {
                    releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);
                }
                if (betterGamepad1.rightBumperOnce())
                {
                    claw.setClaw(Claw.ClawState.OPEN);
                    elevatorReset = getTime();

                    retract = true;
                }


                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 150;
                    coolDownReset();
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 150;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadLeftOnce())
                {
                    elevatorTarget -= 415;
                    coolDownReset();
                }
                else if(betterGamepad2.dpadRightOnce())
                {
                    elevatorTarget += 415;
                    coolDownReset();
                }


                if ((getTime() - elevatorReset) >= WAIT_DELAY_TILL_CLOSE && retract)
                {
                    retract = false;
                    firstOuttakeAngle = true;
                    releaseSystem.setAngle(ReleaseSystem.Angle.INTAKE);
                    liftState = LiftState.RETRACT;
                }

            case HANG:

                isRetarcted = false;

                elevatorTarget = elevator.HANG;

                releaseSystem.setAngle(ReleaseSystem.Angle.HANG);

                if(betterGamepad2.rightBumperOnce())
                {
                    elevatorTarget += 100;
                }
                else if(betterGamepad2.leftBumperOnce())
                {
                    elevatorTarget -= 100;
                }

                if(betterGamepad2.dpadUpOnce())
                {
                    hang = !hang;
                }

                if(!hang)
                {
                    elevator.setTarget(Elevator.HANG_OPEN);
                }
                else
                {
                    elevator.setTarget(Elevator.HANG);
                }


                if (betterGamepad1.AOnce() || betterGamepad1.leftBumperOnce() || betterGamepad2.shareOnce())
                {
                    liftState = LiftState.RETRACT;
                }

                break;
            default:
                liftState = LiftState.RETRACT;
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
