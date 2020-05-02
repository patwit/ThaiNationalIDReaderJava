import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ThaiNationalCard {
	private String thTitle,  thFirstName, thMiddleName, thLastName;
	private String enTitle, enFirstName,  enMiddleName, enLastName;
	private String cid, cardIssuer; 
	private Date dob, cardIssuedDate,  cardExpiredDate;
	private Address address = new Address();
	private int gender; // 1 is for male, 2 is for female
	private byte[] picture;
	
	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public String getThName(){
		if(thMiddleName!=null)
			return thTitle+thFirstName+" "+thMiddleName+" "+thLastName;
		else
			return thTitle+thFirstName+" "+thLastName;
	}
	
	public String getEnName(){
		if(enMiddleName!=null)
			return enTitle+enFirstName+" "+enMiddleName+" "+enLastName;
		else
			return enTitle+enFirstName+" "+enLastName;
	}
	
	public String getThTitle() {
		return thTitle;
	}
	public void setThTitle(String thTitle) {
		this.thTitle = thTitle;
	}
	public String getThFirstName() {
		return thFirstName;
	}
	public void setThFirstName(String thFirstName) {
		this.thFirstName = thFirstName;
	}
	public String getThMiddleName() {
		return thMiddleName;
	}
	public void setThMiddleName(String thMiddleName) {
		this.thMiddleName = thMiddleName;
	}
	public String getThLastName() {
		return thLastName;
	}
	public void setThLastName(String thLastName) {
		this.thLastName = thLastName;
	}
	public String getEnTitle() {
		return enTitle;
	}
	public void setEnTitle(String enTitle) {
		this.enTitle = enTitle;
	}
	public String getEnFirstName() {
		return enFirstName;
	}
	public void setEnFirstName(String enFirstName) {
		this.enFirstName = enFirstName;
	}
	public String getEnMiddleName() {
		return enMiddleName;
	}
	public void setEnMiddleName(String enMiddleName) {
		this.enMiddleName = enMiddleName;
	}
	public String getEnLastName() {
		return enLastName;
	}
	public void setEnLastName(String enLastName) {
		this.enLastName = enLastName;
	}
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid.trim();
	}
	public String getGender() {
		if(gender==1)
			return "ชาย";
		else 
			return "หญิง";
	}
	public void setGender(String gender) {
		this.gender = Integer.parseInt(gender);
	}
	public void setDob(Date dob) {
		this.dob = dob;
	}
	public String getCardIssuer() {
		return cardIssuer;
	}
	public void setCardIssuer(String cardIssuer) {
		this.cardIssuer = cardIssuer.trim();
	}
	public String getCardIssuedDate() {
		return dateFormat(cardIssuedDate);
	}
	public void setCardIssuedDate(String cardIssuedDate) {
		this.cardIssuedDate = setupDate(cardIssuedDate);
	}
	public String getCardExpiredDate() {
		return dateFormat(cardExpiredDate);
	}
	public void setCardExpiredDate(String cardExpiredDate) {
		this.cardExpiredDate = setupDate(cardExpiredDate);
	}
	public String getDob() {
		return dateFormat(dob);
	}
	public void setDob(String dob) {
		this.dob = setupDate(dob);
	}
	public Address getAddress() {
		return address;
	}
	public void setAddress(String address) {
		int tm = address.indexOf("ตำบล");
		int am = address.indexOf("อำเภอ");
		int pr = address.indexOf("จังหวัด");
		this.address.setAddress(address.substring(0, tm).replace('#', ' ').trim());
		this.address.setThumbon(address.substring(tm, am).replace('#', ' ').trim());
		this.address.setAmpher(address.substring(am, pr).replace('#', ' ').trim());
		this.address.setProvince((address.substring(pr)).trim());
	}
	
	public void setThName(String name){
		//trim and get rid of #
		name = name.replace('#', ' ').trim();
		//split the name using space(s) as a spliter
		String[] splited = name.split("\\s+");
		
		setThTitle(splited[0].trim());
		setThFirstName(splited[1].trim());
		if(splited.length <=3){ // no middle name
			setThLastName(splited[2].trim());
		} else { // in case there is middle name
			setThMiddleName(splited[2].trim());
			setThLastName(splited[3].trim());
		}
	}
	
	public void setEnName(String name){
		//trim and get rid of #
		name = name.replace('#', ' ').trim();
		//split the name using space(s) as a spliter
		String[] splited = name.split("\\s+");
		
		setEnTitle(splited[0].trim());
		setEnFirstName(splited[1].trim());
		if(splited.length <=3){ // no middle name
			setEnLastName(splited[2].trim());
		} else { // in case there is middle name
			setEnMiddleName(splited[2].trim());
			setEnLastName(splited[3].trim());
		}
	}
	
	@SuppressWarnings("deprecation")
	private Date setupDate(String strDate){
		DateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		try {
			date = formatter.parse(strDate);
			//adjust to Christian year minus 543
			date.setYear(date.getYear()-543);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	private String dateFormat(Date date) {
		return date.getDate() +"/"+ (date.getMonth()+1)+"/" + (date.getYear()+1900);
	}
}

