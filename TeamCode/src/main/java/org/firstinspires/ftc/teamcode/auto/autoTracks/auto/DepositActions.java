package org.firstinspires.ftc.teamcode.auto.autoTracks.auto;


import static org.firstinspires.ftc.teamcode.auto.autoTracks.auto.ActionHelper.activateSystem;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;

import org.firstinspires.ftc.teamcode.subsystems.Claw;
import org.firstinspires.ftc.teamcode.subsystems.Elevator;
import org.firstinspires.ftc.teamcode.subsystems.ReleaseSystem;
import org.firstinspires.ftc.teamcode.subsystems.TelescopicHand;
import org.firstinspires.ftc.teamcode.util.ClawSide;
import org.firstinspires.ftc.teamcode.util.Stopwatch;

public class DepositActions {



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

    private TelescopicHand telescopicHand;

    private ReleaseSystem releaseSystem;

    private boolean activated;

    ReleaseSide releaseSide;
    LockSide lockSide;
    TypeClaws typeClaws;

    public DepositActions(Elevator elevator, Claw claw , TelescopicHand telescopicHand , ReleaseSystem releaseSystem){
        this.elevator = elevator;
        this.claw = claw;
        this.telescopicHand = telescopicHand;
        this.releaseSystem = releaseSystem;
        typeClaws = TypeClaws.LOCKED;
        releaseSide = ReleaseSide.BOTH_OPEN;
        lockSide = LockSide.BOTH_LOCKS;

    }




    private void moveElevatorByTraj(int elevatorTarget) {
        elevator.setTarget(elevatorTarget);
        elevator.setPidControl();
    }


    private void moveTelescopicByTraj(int elevatorTarget) {
        elevator.setTarget(elevatorTarget);
        elevator.setPidControl();
    }
    //This function will prepare the intake and outtake  for deposit


    public void retractElevator() {
        elevator.setTarget(0);
        elevator.setPidControl();
    }

    public void retractTelescopic() {
        elevator.setTarget(0);
        elevator.setPidControl();
    }





    public class MoveOuttake implements Action {
        private ReleaseSystem.Angle _outtakeState;

        public MoveOuttake(ReleaseSystem.Angle _outtakeState  ) {

            this._outtakeState = _outtakeState;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            releaseSystem.setAngle(this._outtakeState);
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
                return !activateSystem(retractDepositTimer, () -> retractElevator(), 1000);


            }
        }


        public class MoveElevator implements Action {

            int target;

            public MoveElevator(int target) {
                this.target = target;
            }

            @Override
            public boolean run(@NonNull TelemetryPacket telemetryPacket) {

                moveElevatorByTraj(target);
                return false;

            }
        }

    public class MoveTelescopic implements Action {

        int target;

        public MoveTelescopic(int target) {
            this.target = target;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {

            moveTelescopic(target);
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
                claw.updateState(this._clawState ,ClawSide.BOTH);
                return false;
            }
        }

        public Action retractDeposit() {
            return new RetractDeposit();
        }



        public Action moveClaw(Claw.ClawState clawState, ClawSide clawSide) {
            return new MoveClaw(clawState, clawSide);
        }


        public Action moveElevator(int thisTarget) {
            return new MoveElevator(thisTarget);
        }

        public Action moveTelescopic(int thisTarget) {
        return new MoveElevator(thisTarget);
    }

        public Action moveOuttake(ReleaseSystem.Angle angle) { return new MoveOuttake(angle); }

}

