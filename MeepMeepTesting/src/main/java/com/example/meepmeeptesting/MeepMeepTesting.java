package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        RoadRunnerBotEntity robot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 12.75)
                .setDimensions(14.5, 15)
                .setStartPose(new Pose2d(-35, -62, Math.toRadians(90)))
                .build();

        Action redTrajIntake = robot.getDrive().actionBuilder(robot.getDrive().getPoseEstimate())
                .strafeToLinearHeading(new Vector2d(-54, -54), Math.toRadians(45))
                // TODO: Implement action for putting the sample

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

        robot.runAction(redTrajIntake);

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(robot)
                .start();
    }
}