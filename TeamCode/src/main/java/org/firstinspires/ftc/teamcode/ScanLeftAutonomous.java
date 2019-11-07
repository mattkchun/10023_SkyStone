package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

/*
 * Created by Chun on 11/4/19 for 10023.
 */

@Autonomous
//@Disabled

public class ScanLeftAutonomous extends BaseRobot {
    private int stage = 1;

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void loop() {
        super.loop();
        switch (stage) {
            case 0:
                //drive forward to blocks
                if (auto_drive(1, 10)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 1:
                //horizontal left (scanning for the black skystone)
                if (auto_horizontal(-0.2, 100) || checkBlack(skystoneSensor.red(),skystoneSensor.blue())) {
                    reset_drive_encoders();
                    timer.reset();
                    stage++;
                }
                break;
            case 2:
                //wait 1 sec then drive backward (so momentum from horizontal doesn't turn the robot)
                if (timer.time()>1) {
                    if (auto_drive(-0.2, 6)) {
                        reset_drive_encoders();
                        timer.reset();
                        stage++;
                    }
                }
                break;
            case 3:
                //lower arm
                if (get_motor_enc(armMotor) < -150) {
                    arm(0);
                    reset_arm_encoder();
                    timer.reset();
                    stage++;
                } else
                    arm(-0.7);
                break;
            case 4:
                //drive forward to skystone
                if (timer.time() > 1) {
                    if (auto_drive(0.2, 3)) {
                        reset_drive_encoders();
                        timer.reset();
                        stage++;
                    }
                }
                break;
            case 5:
                //close servo on skystone
                armServo.setPower(ConstantVariables.K_ARM_SERVO_CLOSED);
                if (timer.time()>1)
                    stage++;
                break;
            case 6:
                //drive backward with skystone
                if (auto_drive(-0.5, 8)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 7:
                //turn left to construction side
                if (auto_turn(-0.5, 90)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            case 8:
                //drive to construction side
                if (auto_drive(0.7, 12)) {
                    reset_drive_encoders();
                    timer.reset();
                    stage++;
                }
                break;
            case 10:
                //release block
                armServo.setPower(ConstantVariables.K_ARM_SERVO_OPEN);
                if (timer.time()>1)
                    stage++;
                break;
            case 11:
                //drive backward
                if (auto_drive(-0.7, 6)) {
                    reset_drive_encoders();
                    stage++;
                }
                break;
            default:
                break;
        }
    }
}