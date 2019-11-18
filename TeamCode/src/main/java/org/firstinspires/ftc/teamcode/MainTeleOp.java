package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/*
 * Created by Chun on 26 August 2019 for 10023.
 */

@TeleOp
@Disabled

public class MainTeleOp extends BaseRobot {
    @Override
    public void init() {
        super.init();
        gamepad1.setJoystickDeadzone(0.1f);
    }

    @Override
    public void start() { super.start(); }

    @Override
    public void loop() {
        super.loop();
        //triggers no work
        telemetry.addData("D98 Left Trigger Pos: ", gamepad1.left_trigger);
        telemetry.addData("D99 Right Trigger Pos: ", gamepad1.right_trigger);

        //drive train
        holonomic_drive(pow(gamepad1.left_stick_x,3), -pow(gamepad1.left_stick_y,3), pow(gamepad1.right_stick_x,3));

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
        else if (gamepad1.y)
            foundation(1);
        else
            foundation(0);

        //arm servo
        if(gamepad1.b)
            armServo.setPower(armServo.getPower() + 0.05);
            //armServo.setPower(1);
            //armServo.setDirection(DcMotorSimple.Direction.FORWARD);
        else if (gamepad1.a)
            armServo.setPower(armServo.getPower() - 0.05);
            //armServo.setPower(-1);
            //armServo.setDirection(DcMotorSimple.Direction.REVERSE);
        //else
            //armServo.setPower(0);

//        if(gamepad1.dpad_up)
//            arm_servo_pos = Range.clip(armServo.getPosition() + 0.05,0,1);
//        else if (gamepad1.dpad_down)
//            arm_servo_pos = Range.clip(armServo.getPosition() - 0.05,0,1);
    }
}