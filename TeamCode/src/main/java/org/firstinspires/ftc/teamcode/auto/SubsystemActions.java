package org.firstinspires.ftc.teamcode.auto;

import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.RobotHardware;
import org.firstinspires.ftc.teamcode.auto.DepositActions;
import org.firstinspires.ftc.teamcode.auto.UpdateActions;

public class SubsystemActions {


    private final RobotHardware robot = RobotHardware.getInstance();
    ElapsedTime time;


    private DepositActions depositActions;

    private UpdateActions updateActions;

    public SequentialAction intake5CloseAction, intake43OpenAction, transfer, depositAction, readyForDepositAction, deposit43Action;
    public ParallelAction placePreloadAndIntakeAction;
    int tempHeight = 1250;




    public SubsystemActions(DepositActions depositActions, UpdateActions updateActions) {


        this.depositActions = depositActions;

        this.updateActions = updateActions;






        depositAction = new SequentialAction(
                depositActions.placePixel(),
                new SleepAction(.5),
                depositActions.moveElevator(tempHeight + 400),
                depositActions.retractDeposit()
        );


        readyForDepositAction = new SequentialAction(
                new SleepAction(.75),
                depositActions.readyForDeposit(tempHeight));


        deposit43Action = new SequentialAction(
                depositActions.placeIntermediatePixel(DepositActions.Cycles.PRELOAD, 500),
                new SleepAction(0.6),
                depositActions.placePixel(),
                new SleepAction(0.25),
                depositActions.moveElevator(tempHeight + 300),
                depositActions.retractDeposit());

    }


}
