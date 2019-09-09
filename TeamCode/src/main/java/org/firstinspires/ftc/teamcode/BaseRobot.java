package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

/*
 * Created by Chun on 26 August 2019 for 10023.
 */

public class BaseRobot extends OpMode {
    public DcMotor leftBackDriveMotor, rightBackDriveMotor, leftFrontDriveMotor, rightFrontDriveMotor, climbMotor, flipMotor, liftMotor, bucketMotor;
    public Servo marker_servo, intake_servo;
    public ElapsedTime timer = new ElapsedTime();

    @Override
    public void init() {
        initMotors();
        initServos();
    }

    @Override
    public void start() {
        timer.reset();
        reset_drive_encoders();
        reset_climb_encoders();
        reset_intake_outtake_encoders();
    }

    @Override
    public void loop() {
        addTelemetry();
    }

    private DcMotor initMotor(string motor) {
        return hardwareMap.get(DcMotor.class, motor);
    }

    private void initMotors() {
        //Make these the same as the congifuration on the robot phone
        leftBackDriveMotor = initMotor("leftBackDriveMotor");
        rightBackDriveMotor = initMotor("rightBackDriveMotor");
        leftFrontDriveMotor = initMotor("leftFrontDriveMotor");
        rightFrontDriveMotor = initMotor("rightFrontDriveMotor");

        climbMotor = initMotor("climbMotor");
        flipMotor = initMotor("flipMotor");
        liftMotor = initMotor("liftMotor");
        bucketMotor = initMotor("bucketMotor");
    }

    private Servo initServo(string servo) {
        return hardwareMap.get(Servo.class, servo);
    }

    private void initServos() {
        //Make these the same as the congifuration on the robot phone
        marker_servo = initServo("marker_servo");
        intake_servo = initServo("intake_servo");

        set_marker_servo(ConstantVariables.K_MARKER_SERVO_UP);
        set_intake_servo(ConstantVariables.K_INTAKE_SERVO_IN);
    }

    private void addTelemetry() {
        telemetry.addData("D00 Left Front Drive Motor Enc: ", get_motor_enc(leftFrontDriveMotor));
        telemetry.addData("D01 Left Back Drive Motor Enc: ", get_motor_enc(leftBackDriveMotor));
        telemetry.addData("D02 Right Front Drive Motor Enc: ", get_motor_enc(rightFrontDriveMotor));
        telemetry.addData("D03 Right Back Drive Motor Enc: ", get_motor_enc(rightBackDriveMotor));

        telemetry.addData("D04 Climb Motor Enc: ", get_motor_enc(climbMotor));
        telemetry.addData("D05 Flip Motor Enc: ", get_motor_enc(flipMotor));
        telemetry.addData("D06 Lift Motor Enc: ", get_motor_enc(liftMotor));
        telemetry.addData("D07 Bucket Motor Enc: ", get_bucket_motor_enc(bucketMotor));

        telemetry.addData("D08 Marker Servo Pos: ", marker_servo.getPosition());
        telemetry.addData("D09 Intake Servo Pos: ", intake_servo.getPosition());
    }

    public void setPower(DcMotor motor, double power) {
        double speed = Range.clip(power, -1, 1);
        motor.setPower(speed)
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

    public void climb(double power) {
        double speed = Range.clip(power, -1, 1);
        climbMotor.setPower(speed);
    }

    public void flip(double power) {
        double speed = Range.clip(power, -1, 1);
        flipMotor.setPower(speed);
    }

    public void lift(double power) {
        double speed = Range.clip(power, -1, 1);
        liftMotor.setPower(speed);
    }

    public void bucket(double power) {
        double speed = Range.clip(power, -1, 1);
        bucketMotor.setPower(speed);
    }

    public boolean auto_drive(double power, double inches) {
        double TARGET_ENC = ConstantVariables.K_PPIN_DRIVE * inches;
        telemetry.addData("Target_enc: ", TARGET_ENC);
        double left_speed = -power;
        double right_speed = power;
        /*double error = -get_motor_enc(leftFrontDriveMotor) - get_motor_enc(rightFrontDriveMotor);

         */
        error /= ConstantVariables.K_DRIVE_ERROR_P;
        left_speed += error;
        right_speed -= error;*/

        left_speed = Range.clip(left_speed, -1, 1);
        right_speed = Range.clip(right_speed, -1, 1);

        drive(left_speed, left_speed, right_speed, right_speed);

        if (Math.abs(get_motor_enc(rightFrontDriveMotor) >= TARGET_ENC) {
            drive(0,0,0,0)
            return true;
        } else {
            return false;
        }
    }

    /**
     * @param power:   the speed to turn at. Negative for left. ***CHECK FOR 2019***
     * @param degrees: the number of degrees to turn.
     * @return Whether the target angle has been reached.
     */
    public boolean auto_turn(double power, double degrees) {
        double TARGET_ENC = Math.abs(ConstantVariables.K_PPDEG_DRIVE * degrees);
        telemetry.addData("D99 TURNING TO ENC: ", TARGET_ENC);

        double speed = Range.clip(power, -1, 1);
        drive(-speed, -speed, -speed, -speed);

        //if (Math.abs(get_right_front_drive_motor_enc()) >= TARGET_ENC) {
        if (Math.abs(get_motor_enc(rightFrontDriveMotor)) >= TARGET_ENC) {
            drive(0,0,0,0);
            return true;
        } else {
            return false;
        }
    }

    //positive for right, negative for left ***CHECK FOR 2019***
    public boolean auto_mecanum(double power, double inches) {
        double TARGET_ENC = ConstantVariables.K_PPIN_DRIVE * inches;
        telemetry.addData("Target_enc: ", TARGET_ENC);

        double leftFrontPower = Range.clip(0 - power, -1.0, 1.0);
        double leftBackPower = Range.clip(0 + power, -1.0, 1.0);
        double rightFrontPower = Range.clip(0 - power, -1.0, 1.0);
        double rightBackPower = Range.clip(0 + power, -1.0, 1.0);

        drive(leftFrontPower, leftBackPower, rightFrontPower, rightBackPower);

        //if (Math.abs(get_right_front_drive_motor_enc()) >= TARGET_ENC) {
        if (Math.abs(get_motor_enc(rightFrontDriveMotor)) >= TARGET_ENC) {
            drive(0,0,0,0);
            return true;
        } else {
            return false;
        }
    }

    public void tankanum_drive(double leftPwr, double rightPwr, double lateralpwr) {
        rightPwr *= -1;

        double leftFrontPower = Range.clip(leftPwr - lateralpwr, -1.0, 1.0);
        double leftBackPower = Range.clip(leftPwr + lateralpwr, -1.0, 1.0);
        double rightFrontPower = Range.clip(rightPwr - lateralpwr, -1.0, 1.0);
        double rightBackPower = Range.clip(rightPwr + lateralpwr, -1.0, 1.0);

        drive(leftFrontPower, leftBackPower, rightFrontPower, rightBackPower)
    }

    public void tank_drive(double leftPwr, double rightPwr) {
        double leftPower = Range.clip(leftPwr, -1.0, 1.0);
        double rightPower = Range.clip(rightPwr, -1.0, 1.0);
        rightPower *= -1;

        drive(leftPower, leftPower, rightPower, rightPower);
    }

    public void set_marker_servo(double pos) {
        double position = Range.clip(pos,0,1.0);
        marker_servo.setPosition(position);
    }

    public void set_intake_servo(double pos) {
        double position = Range.clip(pos,0,1.0);
        intake_servo.setPosition(position);
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

    public void reset_climb_encoders() {
        climbMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        climbMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void reset_intake_outtake_encoders() {
        flipMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bucketMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        flipMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bucketMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public int get_motor_enc(DcMotor motor) {
        if (motor.getMode() != DcMotor.RunMode.RUN_USING_ENCODER) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
        return motor.getCurrentPosition();
    }
}