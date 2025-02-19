package org.firstinspires.ftc.teamcode.auto;

import androidx.annotation.NonNull;

import static org.firstinspires.ftc.teamcode.auto.ActionHelper.activateSystem;


import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.SleepAction;

import org.checkerframework.checker.units.qual.A;
import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;

import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.Stopwatch;

public class DepositActions {


    public enum Cycles {
        PRELOAD,
        FIRST_CYCLE,
        SECOND_CYCLE
    }

    ;

    public enum TypeClaws {
        RELEASED,
        LOCKED
    }

    public enum LockSide {
        RIGHT_LOCK,
        LEFT_LOCK,
        BOTH_LOCKS,
    }

    public enum ReleaseSide {
        RIGHT_OPEN,
        LEFT_OPEN,
        BOTH_OPEN,
    }


    private Elevator elevator;

    private Claw claw;

    private ReleaseSystem releaseSystem;

    private TelescopicHand telescopicHand;
    private boolean activated;

    ReleaseSide releaseSide;
    LockSide lockSide;
    TypeClaws typeClaws;

    public DepositActions(Elevator elevator, Claw claw , TelescopicHand telescopicHand , ReleaseSystem releaseSystem) {
        this.elevator = elevator;
        this.releaseSystem = releaseSystem;
        this.claw = claw;
        this.telescopicHand = telescopicHand;
        typeClaws = TypeClaws.LOCKED;
        releaseSide = ReleaseSide.BOTH_OPEN;
        lockSide = LockSide.BOTH_LOCKS;

    }




    private void moveElevatorByTraj(int telescopicTarget) {
        elevator.setTarget(telescopicTarget);
        elevator.setPidControl();
    }

    private void moveTelescopicByTraj(int telescopicTarget) {
        telescopicHand.setTarget(telescopicTarget);
        telescopicHand.setPidControl();
    }



    public void retractElevator()
    {
        elevator.setTarget(0);
        elevator.setPidControl();
    }


    public class ReadyForDeposit implements Action {
        Stopwatch readyForDepositTimer;
        int elevator;

        public ReadyForDeposit(int elevator) {
            this.elevator = elevator;
            readyForDepositTimer = new Stopwatch();
            readyForDepositTimer.reset();

        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {




            releaseSystem.setAngle(ReleaseSystem.Angle.OUTTAKE);

            return false;
        }
    }


    public class MoveOuttake implements Action {
        Stopwatch readyForDepositTimer;
        ReleaseSystem.Angle angle;

        public MoveOuttake(ReleaseSystem.Angle angle) {
            this.angle = angle;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            releaseSystem.setAngle(angle);
            return false;
        }
    }




        public class RetractDeposit implements Action {
            Stopwatch retractDepositTimer;

            public RetractDeposit() {
                retractDepositTimer = new Stopwatch();
                retractDepositTimer.reset();
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                return !activateSystem(retractDepositTimer, () -> retractElevator(), 800);


            }
        }


        public class MoveElevator implements Action {
            Stopwatch retractDepositTimer;
            int target;

            public MoveElevator(int target) {
                this.target = target;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {

                elevator.setTarget(target);
                elevator.setPidControl();
                return false;

            }
        }

        public class MoveTelescopic implements Action {
        Stopwatch retractDepositTimer;
        int target;

        public MoveTelescopic(int target) {
            this.target = target;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            moveTelescopicByTraj(target);
            return false;

        }
    }


    public class MoveClaw implements Action {
            private Claw.ClawState _clawState;
            private ClawSide _clawSide;

            public MoveClaw(Claw.ClawState clawState, ClawSide clawSide) {
                this._clawState = clawState;
                this._clawSide = clawSide;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {
                claw.updateState(this._clawState, this._clawSide);
                return false;
            }
        }

        public Action retractDeposit() {
            return new RetractDeposit();
        }

        public Action readyForDeposit(int elevator) {
            return new ReadyForDeposit(elevator);
        }




        public Action moveClaw(Claw.ClawState clawState, ClawSide clawSide) {
            return new MoveClaw(clawState, clawSide);
        }


        public Action moveOuttake(ReleaseSystem.Angle thisAngle) {
            return new MoveOuttake(thisAngle);
        }

        public Action moveElevator(int thisTarget) {
            return new MoveElevator(thisTarget);
        }

        public Action moveTelescopic(int thisTarget)  {
            return new MoveTelescopic(thisTarget);
        }

}

