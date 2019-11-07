package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 * Created by Chun on 26 August 2019 for 10023.
 */

public class BaseRobot extends OpMode {
    public DcMotor leftBackDriveMotor, rightBackDriveMotor, leftFrontDriveMotor, rightFrontDriveMotor, armMotor, foundationMotor;
    public CRServo armServo;
    public ElapsedTime timer = new ElapsedTime();
    public ColorSensor skystoneSensor;
    private int certainty = 0;

    @Override
    public void init() {
        initMotors();
        initServos();
        initSensors();
    }

    @Override
    public void start() {
        timer.reset();
        reset_drive_encoders();
        reset_arm_encoder();
        reset_foundation_encoder();
        skystoneSensor.enableLed(true);
        armServo.setPower(ConstantVariables.K_ARM_SERVO_OPEN);
    }

    @Override
    public void loop() {
        addTelemetry();
    }

    private DcMotor initMotor(String motor) {
        return hardwareMap.get(DcMotor.class, motor);
    }

    private void initMotors() {
        leftBackDriveMotor = initMotor("leftBackDriveMotor");
        rightBackDriveMotor = initMotor("rightBackDriveMotor");
        leftFrontDriveMotor = initMotor("leftFrontDriveMotor");
        rightFrontDriveMotor = initMotor("rightFrontDriveMotor");

        armMotor = initMotor("armMotor");
        foundationMotor = initMotor("foundationMotor");
    }

    private CRServo initServo(String servo) {
        return hardwareMap.get(CRServo.class, servo);
    }

    private void initServos() {
        armServo = initServo("armServo");
        armServo.setPower(ConstantVariables.K_ARM_SERVO_CLOSED);
    }

    private void initSensors() {
        skystoneSensor = hardwareMap.get(ColorSensor.class, "skystoneSensor");
    }

    private void addTelemetry() {
        telemetry.addData("D00 Left Front Drive Motor Enc: ", get_motor_enc(leftFrontDriveMotor));
        telemetry.addData("D01 Left Back Drive Motor Enc: ", get_motor_enc(leftBackDriveMotor));
        telemetry.addData("D02 Right Front Drive Motor Enc: ", get_motor_enc(rightFrontDriveMotor));
        telemetry.addData("D03 Right Back Drive Motor Enc: ", get_motor_enc(rightBackDriveMotor));

        telemetry.addData("D04 Arm Motor Enc: ", get_motor_enc(armMotor));
        telemetry.addData("D05 Foundation Motor Enc: ", get_motor_enc(foundationMotor));

        telemetry.addData("D06 Arm Servo Power: ", armServo.getPower());

        telemetry.addData("D07 Red: ", skystoneSensor.red());
        telemetry.addData("D08 Blue: ", skystoneSensor.blue());
        telemetry.addData("D09 Green: ", skystoneSensor.green());
        telemetry.addData("D10 Sees Black: ", checkBlack(skystoneSensor.red(),skystoneSensor.blue()));
    }

    public void setPower(DcMotor motor, double power) {
        double speed = Range.clip(power, -1, 1);
        motor.setPower(speed);
    }

    //left front power, left back power, right front power, right back power
    public void drive(double lf, double lb, double rf, double rb) {
        lf = Range.clip(lf, -1, 1);
        lb = Range.clip(lb, -1, 1);
        rf = Range.clip(rf, -1, 1);
        rb = Range.clip(rb, -1, 1);

        leftFrontDriveMotor.setPower(lf);
        leftBackDriveMotor.setPower(lb);
        rightFrontDriveMotor.setPower(rf);
        rightBackDriveMotor.setPower(rb);
    }

    public void arm(double power) {
        double speed = Range.clip(power, -1, 1);
        armMotor.setPower(speed);
    }

    public void foundation(double power) {
        double speed = Range.clip(power, -1, 1);
        foundationMotor.setPower(speed);
    }

    /**
     * @param power:   the speed to drive at. Negative for reverse.
     * @param inches:  the distance to drive.
     * @return Whether the target distance has been reached.
     */

    public boolean auto_drive(double power, double inches) {
        double TARGET_ENC = ConstantVariables.K_PPIN_DRIVE * inches;
        telemetry.addData("Target_enc: ", TARGET_ENC);
        double left_speed = power;
        double right_speed = power;
        double error = get_motor_enc(rightFrontDriveMotor) + get_motor_enc(leftFrontDriveMotor);
        //positive means right turned more, negative means left turned more

        error /= ConstantVariables.K_DRIVE_ERROR_P;
        left_speed += error;
        right_speed -= error;

        left_speed *= -1; //left motors turn opposite directions


        left_speed = Range.clip(left_speed, -1, 1);
        right_speed = Range.clip(right_speed, -1, 1);

        drive(left_speed, left_speed, right_speed, right_speed);

        if (Math.abs(get_motor_enc(leftFrontDriveMotor)) >= TARGET_ENC || Math.abs(get_motor_enc(rightFrontDriveMotor)) >= TARGET_ENC) {
            drive(0,0,0,0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param power:   the speed to turn at. Positive for right, negative for left.
     * @param degrees: the number of degrees to turn.
     * @return Whether the target angle has been reached.
     */
    public boolean auto_turn(double power, double degrees) {
        double TARGET_ENC = Math.abs(ConstantVariables.K_PPDEG_DRIVE * degrees);
        telemetry.addData("D99 TURNING TO ENC: ", TARGET_ENC);

        double speed = Range.clip(power, -1, 1);
        drive(-speed, -speed, -speed, -speed);

        if (Math.abs(get_motor_enc(leftFrontDriveMotor)) >= TARGET_ENC || Math.abs(get_motor_enc(rightFrontDriveMotor)) >= TARGET_ENC) {
            drive(0,0,0,0);
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param power:   the speed to drive at. Positive for right, negative for left.
     * @param inches:  the distance to drive.
     * @return Whether the target distance has been reached.
     */
    public boolean auto_horizontal(double power, double inches) {
        double TARGET_ENC = ConstantVariables.K_PPIN_DRIVE * inches * 1.414;
        telemetry.addData("Target_enc: ", TARGET_ENC);

        double front_speed = power;
        double back_speed = power;
        double error = get_motor_enc(leftFrontDriveMotor) + get_motor_enc(leftBackDriveMotor);
        //positive means front turned more, negative means back turned more

        error /= ConstantVariables.K_DRIVE_ERROR_P;
        front_speed -= error;
        back_speed += error;

        back_speed *= -1; //left motors turn opposite directions

        front_speed = Range.clip(front_speed, -1, 1);
        back_speed = Range.clip(back_speed, -1, 1);

        drive(front_speed, back_speed, front_speed, back_speed);

//        double frontPower = Range.clip(0 - power, -1.0, 1.0);
//        double backPower = Range.clip(0 + power, -1.0, 1.0);
//
//        drive(frontPower, backPower, frontPower, backPower);

        if (Math.abs(get_motor_enc(leftFrontDriveMotor)) >= TARGET_ENC || Math.abs(get_motor_enc(leftBackDriveMotor)) >= TARGET_ENC) {
            drive(0,0,0,0);
            return true;
        } else {
            return false;
        }
    }

    public void holonomic_drive(double xPower, double yPower, double turn) {
        double leftFrontPower = Range.clip(yPower + xPower + turn, -1.0, 1.0);
        double rightFrontPower = Range.clip(yPower - xPower - turn, -1.0, 1.0);
        double leftBackPower = Range.clip(yPower - xPower + turn, -1.0, 1.0);
        double rightBackPower = Range.clip(yPower + xPower - turn, -1.0, 1.0);

        drive(-leftFrontPower, -leftBackPower, rightFrontPower, rightBackPower);
    }

//    public void tankanum_drive(double leftPwr, double rightPwr, double lateralpwr) {
//        rightPwr *= -1;
//
//        double leftFrontPower = Range.clip(leftPwr - lateralpwr, -1.0, 1.0);
//        double leftBackPower = Range.clip(leftPwr + lateralpwr, -1.0, 1.0);
//        double rightFrontPower = Range.clip(rightPwr - lateralpwr, -1.0, 1.0);
//        double rightBackPower = Range.clip(rightPwr + lateralpwr, -1.0, 1.0);
//
//        drive(leftFrontPower, leftBackPower, rightFrontPower, rightBackPower);
//    }
//
//    public void tank_drive(double leftPwr, double rightPwr) {
//        double leftPower = Range.clip(leftPwr, -1.0, 1.0);
//        double rightPower = Range.clip(rightPwr, -1.0, 1.0);
//        rightPower *= -1;
//
//        drive(leftPower, leftPower, rightPower, rightPower);
//    }
//
//    public void set_arm_servo(double pos) {
//        double position = Range.clip(pos,0,1.0);
//        armServo.setPosition(position);
//    }

    public double pow(float num, int power) {
        if (num>=0 || power%2 == 1)
            return Math.pow(num,power);
        else
            return -Math.pow(num,power);
    }

    public boolean checkBlack(int red, int blue) {
        return blue > (3.0/4)*red;
//        if (blue > (3.0/4)*red)
//            certainty++;
//        else
//            certainty = 0;
//        return certainty>3;
    }

    public void reset_all_encoders() {
        reset_drive_encoders();
        reset_arm_encoder();
        reset_foundation_encoder();
    }

    public void reset_drive_encoders() {
        leftFrontDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightFrontDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        leftBackDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightBackDriveMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        leftFrontDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightFrontDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        leftBackDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rightBackDriveMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void reset_arm_encoder() {
        armMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        armMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void reset_foundation_encoder() {
        foundationMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        foundationMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int get_motor_enc(DcMotor motor) {
        if (motor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        return motor.getCurrentPosition();
    }
}