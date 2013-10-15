package openperipheral.api;

public interface ILaserRobot extends IRobot {
	public float getWeaponSpinSpeed();

	public void setWeaponSpinSpeed(float speed);

	public void modifyWeaponSpinSpeed(float speed);
}
