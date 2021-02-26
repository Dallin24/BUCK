package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PWMVictorSPX;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

public class Robot extends TimedRobot {

  /* DRIVETRAIN */
  /***********************************************************************************************************************************************/
  // INSTANTIATE LEFT MOTORS AND LEFT DRIVE
  public SpeedController leftBackDrive = new PWMVictorSPX(4);
  public SpeedController leftFrontDrive = new PWMVictorSPX(5);
  public SpeedControllerGroup leftDrive = new SpeedControllerGroup(leftBackDrive, leftFrontDrive);

  // INSTANTIATE RIGHT MOTORS AND RIGHT DRIVE
  public SpeedController rightBackDrive = new PWMVictorSPX(3);
  public SpeedController rightFrontDrive = new PWMVictorSPX(2);
  public SpeedControllerGroup rightDrive = new SpeedControllerGroup(rightBackDrive, rightFrontDrive);
  /***********************************************************************************************************************************************/
  
  /* INTAKE, STORAGE, AND FLYWHEEL MOTORS */
  /***********************************************************************************************************************************************/
  // INSTANTIATE INTAKE MOTOR
  public PWMVictorSPX intakeMotor = new PWMVictorSPX(6);

  // INSTANTIATE STORAGE MOTOR
  public PWMVictorSPX storageMotor = new PWMVictorSPX(9);

  // INSTANTIATE FLYWHEEL MOTORS AND FLYWHEEL
  public SpeedController leftFlywheelMotor = new PWMVictorSPX(7);
  public SpeedController rightFlywheelMotor = new PWMVictorSPX(8);
  /***********************************************************************************************************************************************/ 

  /* PNEUMANTICS AND LIMELIGHT */
  /***********************************************************************************************************************************************/
  // INSTANTIATE COMPRESSOR
  public Compressor compressor = new Compressor();

  // INSTANTIATE INTAKE SOLENOID
  public DoubleSolenoid intakePiston = new DoubleSolenoid(0, 1);

  // INSTANTIATE STORAGE SOLENOID
  public DoubleSolenoid storagePiston = new DoubleSolenoid(3, 2);

  // INSTANTIATE LIMELIGHT TABLE
  public NetworkTable tableLimelight = NetworkTableInstance.getDefault().getTable("limelight");

  /***********************************************************************************************************************************************/

  /* CONTROLLER */
  /***********************************************************************************************************************************************/
  // INSTANTIATE JOYSTICKS
  public Joystick leftJoystick = new Joystick(0);
  public Joystick rightJoystick = new Joystick(1);

  // INSTANTIATE RIGHT JOYSTICK AXES VARIABLES
  public static int rightStickX = 0;
  public static int rightStickY = 1;

  // INSTANTIATE LEFT JOYSTICK AXES VARIABLES
   public static int leftStickX = 0;
   public static int leftStickY = 1;

  // INSTANTIATE JOYSTICK BUTTONS
  public JoystickButton l1Button = new JoystickButton(leftJoystick, 1);
  public JoystickButton l2Button = new JoystickButton(leftJoystick, 2);
  public JoystickButton l3Button = new JoystickButton(leftJoystick, 3);
  public JoystickButton l4Button = new JoystickButton(leftJoystick, 4);
  public JoystickButton l5Button = new JoystickButton(leftJoystick, 5);
  public JoystickButton l6Button = new JoystickButton(leftJoystick, 6);
   
  // INSTANTIATE JOYSTICK BUTTONS
  public JoystickButton r1Button = new JoystickButton(rightJoystick, 1);
  public JoystickButton r2Button = new JoystickButton(rightJoystick, 2);
  public JoystickButton r3Button = new JoystickButton(rightJoystick, 3);
  public JoystickButton r4Button = new JoystickButton(rightJoystick, 4);
  public JoystickButton r5Button = new JoystickButton(rightJoystick, 5);
  public JoystickButton r6Button = new JoystickButton(rightJoystick, 6);
  /***********************************************************************************************************************************************/
  
  @Override
  public void robotInit() {
     // RESET PNEUMATICS
    storagePiston.set(Value.kReverse);
    intakePiston.set(Value.kReverse);
    compressor.clearAllPCMStickyFaults();
    compressor.start();

    // TURN LIMELIGHT OFF
    NetworkTableInstance.getDefault().getTable("limelight").getEntry("ledMode").setNumber(1);
  }

  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {
    intakePiston.set(Value.kForward);
  }

  @Override
  public void teleopPeriodic() {
    double leftY = leftJoystick.getRawAxis(leftStickY);
    double rightY = rightJoystick.getRawAxis(rightStickY);
    boolean right1 = r1Button.get();
    boolean right2 = r2Button.get();
    boolean left1 = l1Button.get();
    boolean left2 = l2Button.get();
    
    /* DRIVETRAIN */
    /***********************************************************************************************************************************************/
    // SCALE DOWN JOYSTICK AXIS VALUES
    leftY = leftY*.7;
    rightY = rightY*.7;
    

    // READS BUTTON VALUES TO ADJUST SPEED OF DRIVETRAIN
    if (left2 == true) {
      leftY = leftY * .95;
      rightY = rightY * .95;
    } else {
      leftY = leftY * .55;
      rightY = rightY * .55;
    }

    // REMOVES CONTROLLER DRIFT
    if (leftY < .1) {
      if (leftY > -.1) {
        leftY = 0;
      }
    }
    if (rightY < .1) {
      if (rightY > -.1) {
        rightY = 0;
      }
    }

    // SETS DRIVE SIDES TO AXES
    leftDrive.set(-leftY);
    rightDrive.set(rightY);
    
    /***********************************************************************************************************************************************/
    
    /* INTAKE w/STORAGE */
    /***********************************************************************************************************************************************/
    // IF LEFTBUMPER PRESSED, ACTIVATE INTAKE MOTOR AND ACTIVATE STORAGE PISTON
    if(left1 == true) {
      intakeMotor.set(.4);
      storagePiston.set(Value.kForward);
    } else {
      intakeMotor.set(0);
      storagePiston.set(Value.kReverse);
    }
    
    /***********************************************************************************************************************************************/

    /* FLYWHEEL w/STORAGE */
    /***********************************************************************************************************************************************/
    // IF RIGHT TRIGGER PRESSED, ACTIVATE FLYWHEEL MOTORS
    if(right1 == true) {
      leftFlywheelMotor.set(-1);
      rightFlywheelMotor.set(-1);
    } else {
      leftFlywheelMotor.set(0);
      rightFlywheelMotor.set(0);
    }

    // IF BUTTON 2 PRESSED, ACTIVATE STORAGE MOTOR
    if(right2 == true) {
      storageMotor.set(.85);
    } else {
      storageMotor.set(0);
    }
    /***********************************************************************************************************************************************/

  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
