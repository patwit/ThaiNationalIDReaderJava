
public class Address{
	private String address;
	private String thumbon;
	private String ampher;
	private String province;
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getThumbon() {
		return thumbon;
	}
	public void setThumbon(String thumbon) {
		this.thumbon = thumbon;
	}
	public String getAmpher() {
		return ampher;
	}
	public void setAmpher(String ampher) {
		this.ampher = ampher;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	
	public String toString() {
		return address + " " + thumbon + " " + ampher +
				" " + province; 
	}
}
