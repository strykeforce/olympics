package frc.robot.subsystems;

import edu.wpi.first.networktables.NetworkTableInstance;
import org.strykeforce.deadeye.Deadeye;
import org.strykeforce.deadeye.Point;
import org.strykeforce.deadeye.TargetDataListener;
import org.strykeforce.deadeye.UprightRectTargetData;

public class DeadeyeF0 implements TargetDataListener<UprightRectTargetData> {

  private final Deadeye<UprightRectTargetData> deadeye;

  public DeadeyeF0() {
    deadeye = new Deadeye<>("F0", UprightRectTargetData.class);
    deadeye.setTargetDataListener(this);
  }

  public DeadeyeF0(NetworkTableInstance nti) {
    deadeye = new Deadeye<>("F0", UprightRectTargetData.class, nti);
    deadeye.setTargetDataListener(this);
  }

  public void setEnabled(boolean enabled) {
    deadeye.setEnabled(enabled);
  }

  @Override
  public void onTargetData(UprightRectTargetData data) {
    Point center = data.center;
    System.out.printf("x=%d, y=%d %n", center.x, center.y);
  }
}
