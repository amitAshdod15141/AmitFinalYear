package org.firstinspires.ftc.teamcode.subsystems;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.util.BetterGamepad;
import org.firstinspires.ftc.teamcode.util.PIDFController;

@Config
public class TelescopicHand implements Subsystem
{
    private final RobotHardware robot = RobotHardware.getInstance();
    public static double ticksPerRevolution = 1993.6 * 3;

    public DcMotorEx telescopicMotorRight;
    public DcMotorEx telescopicMotorLeft;
    double currentTarget = 0;
    boolean usePID = true;
    public static double maxPower = 0.75;
    public static double kPR = 0.0075, kIR = 0, kDR = 0.01;
    public static double kPL = 0.005, kIL = 0, kDL = 0.01;

    public static double OUTTAKE_TELESCOPE = 97 , RETARCT_TELESCOPE = 0 , INTAKE_LONG = -3 , INTAKE_SHORT = -6;



    double currentTargetRight = 0, currentTargetLeft = 0;

    Gamepad gamepad;
    BetterGamepad cGamepad;
    PIDFController controllerR;
    PIDFController controllerL;
    PIDFController.PIDCoefficients pidCoefficientsR = new PIDFController.PIDCoefficients();
    PIDFController.PIDCoefficients pidCoefficientsL = new PIDFController.PIDCoefficients();

    boolean isAuto, firstPID = false;
    public TelescopicHand (Gamepad gamepad, boolean isAuto, boolean firstPID) {


            this.isAuto = isAuto;
            this.telescopicMotorRight = robot.hardwareMap.get(DcMotorEx.class, "mTR");
            this.telescopicMotorLeft = robot.hardwareMap.get(DcMotorEx.class, "mTL");
            telescopicMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);

            if(firstPID && !isAuto)
            {
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

            }
            else if(firstPID && isAuto)
            {
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);
            }
            else if (isAuto)
            {
                telescopicMotorRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            }
            else
            {
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            }

            this.telescopicMotorRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
            this.telescopicMotorLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

            this.gamepad = gamepad;
            this.cGamepad = new BetterGamepad(gamepad);

            pidCoefficientsR.kP = kPR;
            pidCoefficientsR.kI = kIR;
            pidCoefficientsR.kD = kDR;

            pidCoefficientsL.kP = kPL;
            pidCoefficientsL.kI = kIL;
            pidCoefficientsL.kD = kDL;

            controllerR = new PIDFController(pidCoefficientsR);
            controllerL = new PIDFController(pidCoefficientsL);

        }





    public double angleToTicks(double angle) {
        return Math.round((angle / 360.0) * ticksPerRevolution);
    }

    public void setPidControl()
    {
        controllerR.updateError(currentTargetRight - telescopicMotorRight.getCurrentPosition());
        controllerL.updateError(currentTargetLeft - telescopicMotorLeft.getCurrentPosition());

        telescopicMotorRight.setPower(controllerR.update());
        telescopicMotorLeft.setPower(controllerL.update());
    }


    public TelescopicHand(boolean isAuto)
    {
        this.isAuto = isAuto;

        this.telescopicMotorRight = robot.hardwareMap.get(DcMotorEx.class, "mER");
        this.telescopicMotorLeft = robot.hardwareMap.get(DcMotorEx.class, "mEL");
        telescopicMotorRight.setDirection(DcMotorSimple.Direction.REVERSE);
        if (isAuto)
        {
            telescopicMotorRight.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            telescopicMotorLeft.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
            telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        }
        else
        {
            telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
            telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
        }
        this.telescopicMotorRight.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);
        this.telescopicMotorLeft.setZeroPowerBehavior(DcMotorEx.ZeroPowerBehavior.BRAKE);

        pidCoefficientsR.kP = kPR;
        pidCoefficientsR.kI = kIR;
        pidCoefficientsR.kD = kDR;

        pidCoefficientsL.kP = kPL;
        pidCoefficientsL.kI = kIL;
        pidCoefficientsL.kD = kDL;

        controllerR = new PIDFController(pidCoefficientsR);
        controllerL = new PIDFController(pidCoefficientsL);
    }

    public void setFirstPID(boolean firstPID) {
        this.firstPID = firstPID;
    }

    public void update() {

        if (!firstPID) {
            cGamepad.update();

            if (usePID) {
                setPidControl();
            } else {
                telescopicMotorRight.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);
                telescopicMotorLeft.setMode(DcMotorEx.RunMode.RUN_WITHOUT_ENCODER);

                if (gamepad.right_stick_y != 0 && !gamepad.right_stick_button) {
                    if ((-gamepad.right_stick_y) < 0) {
                        telescopicMotorLeft.setPower(Range.clip(-gamepad.right_stick_y, -maxPower / 2, maxPower / 2));
                        telescopicMotorRight.setPower(Range.clip(-gamepad.right_stick_y, -maxPower / 2, maxPower / 2));
                    } else {
                        telescopicMotorLeft.setPower(Range.clip(-gamepad.right_stick_y, -maxPower, maxPower));
                        telescopicMotorRight.setPower(Range.clip(-gamepad.right_stick_y, -maxPower, maxPower));
                    }
                } else if (gamepad.right_stick_y != 0 && gamepad.right_stick_button) {
                    telescopicMotorRight.setPower(Range.clip(-gamepad.right_stick_y, -maxPower / 2, maxPower / 2));
                    telescopicMotorLeft.setPower(Range.clip(-gamepad.right_stick_y, -maxPower / 2, maxPower / 2));
                } else {
                    telescopicMotorRight.setPower(0);
                    telescopicMotorLeft.setPower(0);
                }

            }
        }
    }


    public void setTarget(double angle)
    {
        if(angle > OUTTAKE_TELESCOPE)
        {
            this.currentTargetRight = OUTTAKE_TELESCOPE;
            this.currentTargetLeft = OUTTAKE_TELESCOPE  ;
        }
        else
        {
            this.currentTargetRight = angleToTicks(angle);
            this.currentTargetLeft = angleToTicks(angle);
        }
    }

    public void move(double target)
    {
        this.setTarget(target);
        this.setPidControl();
    }

    @Override
    public void play() {

    }

    @Override
    public void loop(boolean allowMotors) {

        isAuto = true;
        update();

    }

    @Override
    public void stop() {

    }


    public void setUsePID(boolean usePID) {
        this.usePID = usePID;
    }

    public void setAuto(boolean isAuto)
    {
        this.isAuto = isAuto;
    }
}



