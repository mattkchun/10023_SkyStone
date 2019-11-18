package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * Created by Chun on 26 August 2019 for 10023.
 */

@TeleOp

public class LinearTeleOp extends BaseRobot {
    @Override
    public void init() {
        super.init();
        gamepad1.setJoystickDeadzone(0.1f);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {
        super.loop();
        //triggers no work
        telemetry.addData("D98 Left Trigger Pos: ", gamepad1.left_trigger);
        telemetry.addData("D99 Right Trigger Pos: ", gamepad1.right_trigger);

        //drive train
        if (gamepad1.left_bumper)
            holonomic_drive(gamepad1.left_stick_x/2, -gamepad1.left_stick_y/2, gamepad1.right_stick_x/2);
        else
            holonomic_drive(gamepad1.left_stick_x, -gamepad1.left_stick_y, gamepad1.right_stick_x);

        //arm
        if(gamepad1.right_bumper)
            arm(-0.7);
        else if (gamepad1.right_trigger>0.1)
            arm(pow(gamepad1.right_trigger,2));
        else
            arm(0);

        //foundation
        if(gamepad1.x)
            foundation(-1);
        else if (gamepad1.b)
            foundation(1);
        else
            foundation(0);

        //arm servo
        if(gamepad1.y)
            armServo.setPower(armServo.getPower() + 0.05);
        else if (gamepad1.a)
            armServo.setPower(armServo.getPower() - 0.05);
    }
}