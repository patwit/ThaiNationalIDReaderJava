import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import javax.swing.*;
import javax.imageio.ImageIO;
import javax.smartcardio.*;

public class KKUThaiIDReaderGUI extends JFrame implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7102414898225983825L;

	TerminalFactory factory;
	CardChannel channel;
	ResponseAPDU response;
	Card card;
	LinkedList<ThaiNationalCard> cards = new LinkedList<ThaiNationalCard>();
	int numberOfCardRead = 0;

	// variable for GUI
	private javax.swing.JTextArea addressTA;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JTextField cardExpiredDateTF;
    private javax.swing.JTextField cardIssuedDateTF;
    private javax.swing.JTextField cardIssuerTF;
    private javax.swing.JPanel diplayPanel;
    private javax.swing.JTextField dobTF;
    private javax.swing.JTextField enNameTF;
    private javax.swing.JTextField fileAddressTF;
    private javax.swing.JTextField genderTF;
    private javax.swing.JTextField idNumberTF;
    private javax.swing.JButton fileAddressButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel numReadCardTF;
    private javax.swing.JLabel picture;
    private javax.swing.JButton readButton;
    private javax.swing.JComboBox<String> readTypeCB;
    private javax.swing.JTextField readerNameTF;
    private javax.swing.JTextField readerStatusTF;
    private javax.swing.JButton saveButton;
    private javax.swing.JCheckBox saveCB;
    private javax.swing.JPanel settingPanel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JTextField thNameTF;

	public KKUThaiIDReaderGUI(String title) {
		super(title);
	}

	protected void createAndShowGUI() {
		createComponents();
		addListeners();
		setFrameFeatures();
	}

	protected void runProgram() {
		if (isReaderPresent()) {
			if (isCardPresent()) {
				ThaiNationalCard currentCard = new ThaiNationalCard();
				readCard(currentCard);
				if (saveCB.isSelected() && !cardSavedBefore(currentCard)) {
					saveToFile(currentCard);
					cards.add(currentCard);
					numReadCardTF.setText(Integer.toBinaryString(++numberOfCardRead));
				} 
				
			} else {

			}
		}
		
	}

	private boolean cardSavedBefore(ThaiNationalCard card) {
		for(ThaiNationalCard c : cards) {
			if(c.getCid().equals(card.getCid())) {
				return true;
			}
		}
		return false;
	}


	private void displayData(ThaiNationalCard card) {
		// TODO Auto-generated method stub
		idNumberTF.setText(card.getCid());
		thNameTF.setText(card.getThName());
		enNameTF.setText(card.getEnName());
		dobTF.setText(card.getDob());
		genderTF.setText(card.getGender());
		cardIssuerTF.setText(card.getCardIssuer());
		cardIssuedDateTF.setText(card.getCardIssuedDate());
		cardExpiredDateTF.setText(card.getCardExpiredDate());
		addressTA.setText(card.getAddress().toString());
	}

	private void displayPhoto(ThaiNationalCard card) {
		try {
			picture.setIcon(new ImageIcon(ImageIO.read(new ByteArrayInputStream(card.getPicture()))));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readCard(ThaiNationalCard currentCard) {
		if (readTypeCB.getSelectedItem().equals("อ่านข้อมูลทั้งหมด")) {
			readData(currentCard);
			readPhoto(currentCard);
		} else if (readTypeCB.getSelectedItem().equals("อ่านเฉพาะข้อมูลบุคคล")) {
			readData(currentCard);
		} else { // "อ่านเฉพาะข้อมูลรูป"
			readPhoto(currentCard);
		}
	}

	private void readData(ThaiNationalCard card) {
		try {
			sendCommand(ThaiAPDU.SELECT);
			// Read all data
			card.setCid(sendCommand(ThaiAPDU.CID));
			card.setThName(sendCommand(ThaiAPDU.TH_NAME));
			card.setEnName(sendCommand(ThaiAPDU.EN_NAME));
			card.setDob(sendCommand(ThaiAPDU.DOB));
			card.setGender(sendCommand(ThaiAPDU.GENDER));
			card.setCardIssuer(sendCommand(ThaiAPDU.CARD_ISSUER));
			card.setCardIssuedDate(sendCommand(ThaiAPDU.ISSUED_DATE));
			card.setCardExpiredDate(sendCommand(ThaiAPDU.EXPIRED_DATE));
			card.setAddress(sendCommand(ThaiAPDU.ADDRESS));
			displayData(card);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // send SELECT command

	}

	private void readPhoto(ThaiNationalCard card) {
		try {
			sendCommand(ThaiAPDU.SELECT);
			card.setPicture(sendPhotoCommand(ThaiAPDU.PHOTO));
			displayPhoto(card);
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private boolean checkingReaderAndCard() {
		if(isReaderPresent() && isCardPresent()) {
			readerStatusTF.setForeground(Color.GREEN);
			readerStatusTF.setText("online");
			return true;
		}
		return false;
	}

	private boolean isReaderPresent() {
		try {
			TerminalFactory.getDefault().terminals().list();
			//System.out.println(TerminalFactory.getDefault().terminals().list().get(0));
		} catch (CardException e) {
			// e.printStackTrace();
			readerStatusTF.setForeground(Color.RED);
			readerStatusTF.setText("No reader");
			return false;
		}
		
		readerStatusTF.setForeground(Color.GREEN);
		readerStatusTF.setText("online");
		
		try {
			readerNameTF.setText(TerminalFactory.getDefault().terminals().list().get(0).getName().trim());
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}

	private boolean isCardPresent() {
		factory = TerminalFactory.getDefault();

		// Use the first card reader:
		// First card is the contact reader (smart card reader)
		// The second one is the contactless reader (rfid reader)
		CardTerminal terminal;

		try {
			terminal = factory.terminals().list().get(0);
			// Establish a connection with the card:
			card = terminal.connect("*");
			channel = card.getBasicChannel();
		} catch (CardException e) {
			// e.printStackTrace();
			readerStatusTF.setForeground(Color.RED);
			readerStatusTF.setText("No Card");
			return false;
		}

		return true;
	}

	private void addListeners() {
		readButton.addActionListener(this);
		saveButton.addActionListener(this);
		fileAddressButton.addActionListener(this);
	}

	private void createComponents() {
	       diplayPanel = new javax.swing.JPanel();
	        jPanel1 = new javax.swing.JPanel();
	        jLabel14 = new javax.swing.JLabel();
	        idNumberTF = new javax.swing.JTextField();
	        jLabel9 = new javax.swing.JLabel();
	        thNameTF = new javax.swing.JTextField();
	        jLabel10 = new javax.swing.JLabel();
	        enNameTF = new javax.swing.JTextField();
	        jLabel11 = new javax.swing.JLabel();
	        dobTF = new javax.swing.JTextField();
	        jLabel12 = new javax.swing.JLabel();
	        genderTF = new javax.swing.JTextField();
	        jLabel2 = new javax.swing.JLabel();
	        cardIssuerTF = new javax.swing.JTextField();
	        jLabel5 = new javax.swing.JLabel();
	        cardIssuedDateTF = new javax.swing.JTextField();
	        jLabel6 = new javax.swing.JLabel();
	        cardExpiredDateTF = new javax.swing.JTextField();
	        jPanel2 = new javax.swing.JPanel();
	        jScrollPane1 = new javax.swing.JScrollPane();
	        addressTA = new javax.swing.JTextArea();
	        jLabel13 = new javax.swing.JLabel();
	        picture = new javax.swing.JLabel();
	        buttonPanel = new javax.swing.JPanel();
	        saveButton = new javax.swing.JButton();
	        readButton = new javax.swing.JButton();
	        settingPanel = new javax.swing.JPanel();
	        saveCB = new javax.swing.JCheckBox();
	        jLabel1 = new javax.swing.JLabel();
	        readTypeCB = new javax.swing.JComboBox<>();
	        jLabel7 = new javax.swing.JLabel();
	        numReadCardTF = new javax.swing.JLabel();
	        jLabel8 = new javax.swing.JLabel();
	        fileAddressTF = new javax.swing.JTextField();
	        fileAddressButton = new javax.swing.JButton();
	        statusPanel = new javax.swing.JPanel();
	        jLabel3 = new javax.swing.JLabel();
	        jLabel4 = new javax.swing.JLabel();
	        readerStatusTF = new javax.swing.JTextField();
	        readerNameTF = new javax.swing.JTextField();

	        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	        setBackground(java.awt.Color.red);

	        diplayPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        jLabel14.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel14.setText("เลขประจำตัวประชาชน:");

	        idNumberTF.setEditable(false);

	        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel9.setText("ชื่อตัวและชื่อสกุล:");

	        thNameTF.setEditable(false);

	        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel10.setText("Name:");

	        enNameTF.setEditable(false);

	        jLabel11.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel11.setText("เกิดวันที่");

	        dobTF.setEditable(false);

	        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel12.setText("เพศ:");

	        genderTF.setEditable(false);

	        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel2.setText("บัตรออกที่:");

	        cardIssuerTF.setEditable(false);

	        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel5.setText("วันที่ออกบัตร:");

	        cardIssuedDateTF.setEditable(false);

	        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel6.setText("วันบัตรหมดอายุ:");

	        cardExpiredDateTF.setEditable(false);

	        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
	        jPanel1.setLayout(jPanel1Layout);
	        jPanel1Layout.setHorizontalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(jLabel12)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(genderTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(jLabel9)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(thNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addGap(4, 4, 4)
	                        .addComponent(jLabel10)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(enNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(jLabel11)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(dobTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(jPanel1Layout.createSequentialGroup()
	                        .addComponent(jLabel2)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(cardIssuerTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                        .addGroup(jPanel1Layout.createSequentialGroup()
	                            .addComponent(jLabel14)
	                            .addGap(29, 29, 29)
	                            .addComponent(idNumberTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
	                            .addGap(0, 0, Short.MAX_VALUE))
	                        .addGroup(jPanel1Layout.createSequentialGroup()
	                            .addComponent(jLabel6)
	                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(cardExpiredDateTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
	                        .addComponent(jLabel5)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(cardIssuedDateTF, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap())
	        );
	        jPanel1Layout.setVerticalGroup(
	            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel1Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel14)
	                    .addComponent(idNumberTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel9)
	                    .addComponent(thNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel10)
	                    .addComponent(enNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel11)
	                    .addComponent(dobTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel12)
	                    .addComponent(genderTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel2)
	                    .addComponent(cardIssuerTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel5)
	                    .addComponent(cardIssuedDateTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
	                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel6)
	                    .addComponent(cardExpiredDateTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap(14, Short.MAX_VALUE))
	        );

	        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        addressTA.setEditable(false);
	        addressTA.setColumns(20);
	        addressTA.setRows(5);
	        addressTA.setWrapStyleWord(true);
	        addressTA.setLineWrap(true);
	        jScrollPane1.setViewportView(addressTA);

	        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel13.setText("ที่อยู่:");

	        picture.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
	        picture.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

	        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	        jPanel2.setLayout(jPanel2Layout);
	        jPanel2Layout.setHorizontalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(jPanel2Layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(jPanel2Layout.createSequentialGroup()
	                        .addComponent(jLabel13)
	                        .addContainerGap(187, Short.MAX_VALUE))
	                    .addGroup(jPanel2Layout.createSequentialGroup()
	                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                            .addComponent(picture, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
	                        .addGap(0, 0, Short.MAX_VALUE))))
	        );
	        jPanel2Layout.setVerticalGroup(
	            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
	                .addComponent(picture, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addComponent(jLabel13)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	        );

	        javax.swing.GroupLayout diplayPanelLayout = new javax.swing.GroupLayout(diplayPanel);
	        diplayPanel.setLayout(diplayPanelLayout);
	        diplayPanelLayout.setHorizontalGroup(
	            diplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(diplayPanelLayout.createSequentialGroup()
	                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap())
	        );
	        diplayPanelLayout.setVerticalGroup(
	            diplayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	        );

	        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        saveButton.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
	        saveButton.setText("บันทึก");
	        saveButton.setPreferredSize(new java.awt.Dimension(87, 36));

	        readButton.setFont(new java.awt.Font("SansSerif", 1, 18)); // NOI18N
	        readButton.setText("อ่านบัตร");


	        javax.swing.GroupLayout buttonPanelLayout = new javax.swing.GroupLayout(buttonPanel);
	        buttonPanel.setLayout(buttonPanelLayout);
	        buttonPanelLayout.setHorizontalGroup(
	            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(buttonPanelLayout.createSequentialGroup()
	                .addGap(170, 170, 170)
	                .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addGap(71, 71, 71)
	                .addComponent(readButton, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        buttonPanelLayout.setVerticalGroup(
	            buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, buttonPanelLayout.createSequentialGroup()
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                .addGroup(buttonPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(saveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(readButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addContainerGap())
	        );

	        settingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        saveCB.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        saveCB.setSelected(true);
	        saveCB.setText("บันทึก");


	        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel1.setText("รูปแบบการอ่าน : ");

	        readTypeCB.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
	        readTypeCB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "อ่านข้อมูลทั้งหมด", "อ่านเฉพาะข้อมูลบุคคล", "อ่านเฉพาะข้อมูลรูป" }));

	        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel7.setText("จำนวนบัตรที่อ่าน:");

	        numReadCardTF.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
	        numReadCardTF.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
	        numReadCardTF.setText("0");

	        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel8.setText("ที่เก็บแฟ้มข้อมูล");

	        fileAddressTF.setEditable(false);
	        Path p = Paths.get("");
	        fileAddressTF.setText(p.toAbsolutePath().toString() + File.separator + "output.csv");
	        
	        
	        fileAddressButton.setText("...");

	        javax.swing.GroupLayout settingPanelLayout = new javax.swing.GroupLayout(settingPanel);
	        settingPanel.setLayout(settingPanelLayout);
	        settingPanelLayout.setHorizontalGroup(
	            settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(settingPanelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(settingPanelLayout.createSequentialGroup()
	                        .addComponent(saveCB)
	                        .addGap(100, 100, 100))
	                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingPanelLayout.createSequentialGroup()
	                        .addComponent(jLabel8)
	                        .addGap(18, 18, 18)))
	                .addGroup(settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	                    .addGroup(settingPanelLayout.createSequentialGroup()
	                        .addComponent(jLabel1)
	                        .addGap(18, 18, 18)
	                        .addComponent(readTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addGap(51, 51, 51)
	                        .addComponent(jLabel7)
	                        .addGap(18, 18, 18)
	                        .addComponent(numReadCardTF, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(settingPanelLayout.createSequentialGroup()
	                        .addGap(6, 6, 6)
	                        .addComponent(fileAddressTF, javax.swing.GroupLayout.PREFERRED_SIZE, 389, javax.swing.GroupLayout.PREFERRED_SIZE)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                        .addComponent(fileAddressButton, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        settingPanelLayout.setVerticalGroup(
	            settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, settingPanelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(saveCB)
	                    .addComponent(jLabel1)
	                    .addComponent(readTypeCB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jLabel7)
	                    .addComponent(numReadCardTF))
	                .addGap(12, 12, 12)
	                .addGroup(settingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
	                    .addComponent(fileAddressTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(fileAddressButton)))
	        );

	        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

	        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel3.setText("Reader Status:");

	        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
	        jLabel4.setText("Reader Name:");

	        readerStatusTF.setEditable(false);
	        readerStatusTF.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N


	        readerNameTF.setEditable(false);
	        readerNameTF.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
	        //readerNameTF.setHorizontalAlignment(SwingConstants.RIGHT);

	        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
	        statusPanel.setLayout(statusPanelLayout);
	        statusPanelLayout.setHorizontalGroup(
	            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(statusPanelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
	                    .addGroup(statusPanelLayout.createSequentialGroup()
	                        .addComponent(jLabel3)
	                        .addGap(18, 18, 18)
	                        .addComponent(readerStatusTF, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE))
	                    .addGroup(statusPanelLayout.createSequentialGroup()
	                        .addComponent(jLabel4)
	                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                        .addComponent(readerNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );
	        statusPanelLayout.setVerticalGroup(
	            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(statusPanelLayout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(jLabel3)
	                    .addComponent(readerStatusTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
	                    .addComponent(readerNameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(jLabel4))
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
	        getContentPane().setLayout(layout);
	        layout.setHorizontalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
	                    .addComponent(statusPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(diplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE)
	                    .addComponent(settingPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	                .addGap(0, 10, Short.MAX_VALUE))
	        );
	        layout.setVerticalGroup(
	            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	            .addGroup(layout.createSequentialGroup()
	                .addContainerGap()
	                .addComponent(settingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(diplayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(statusPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
	                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
	                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
	        );

	        pack();
	}
	
	private String sendCommand(byte[] command) throws CardException {
		response = channel.transmit(new CommandAPDU(command));
		String str = new String(response.getData(), Charset.forName("TIS-620"));

		return str;
	}

	private byte[] sendPhotoCommand(byte[][] command) throws CardException {
		byte[] recv = new byte[5120];
		byte[] cmd;
		int length = 0;
		for (int i = 0; i < 20; i++) {
			cmd = command[i];
			response = channel.transmit(new CommandAPDU(cmd));
			System.arraycopy(response.getData(), 0, recv, length, response.getData().length);
			length += response.getData().length;
		}
		return recv;
	}

	private void setFrameFeatures() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		int w = getSize().width;
		int h = getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		setLocation(x, y);
		setDefaultLookAndFeelDecorated(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
		pack();
	}

	protected void clearDisplay() {
		idNumberTF.setText("");
		thNameTF.setText("");
		enNameTF.setText("");
		dobTF.setText("");
		genderTF.setText("");
		cardIssuerTF.setText("");
		cardIssuedDateTF.setText("");
		cardExpiredDateTF.setText("");
		addressTA.setText("");
		picture.setIcon(null);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == readButton) {
			clearDisplay();
			if (checkingReaderAndCard()) {
				ThaiNationalCard currentCard = new ThaiNationalCard();
				readCard(currentCard);
				if (saveCB.isSelected() && !cardSavedBefore(currentCard)) {
					saveToFile(currentCard);
					cards.add(currentCard);
					numReadCardTF.setText(Integer.toString(++numberOfCardRead));
				}
			}
		} else if (e.getSource() == saveButton) {
			saveToFile(cards.getLast());
		} else if (e.getSource() == fileAddressButton) {
			JFileChooser fc = new JFileChooser();
			int returnVal = fc.showOpenDialog(this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				fileAddressTF.setText(file.toString());
			}
		}
	}

	protected void saveToFile(ThaiNationalCard card) {
		String fileName = fileAddressTF.getText();
		// save data
		if (card.getCid() != null) {
			try {
				OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(fileName),"UTF-8");

				
				//FileWriter writer = new FileWriter(fileName, true);
				writer.write(card.getCid() + ",");
				writer.write(card.getThTitle() + ",");
				writer.write(card.getThFirstName() + ",");
				writer.write(card.getThMiddleName() + ",");
				writer.write(card.getThLastName() + ",");
				writer.write(card.getEnTitle() + ",");
				writer.write(card.getEnFirstName() + ",");
				writer.write(card.getEnMiddleName() + ",");
				writer.write(card.getEnLastName() + ",");
				writer.write(card.getDob() + ",");
				writer.write(card.getGender() + ",");
				writer.write(card.getAddress().getAddress() + ",");
				writer.write(card.getAddress().getThumbon() + ",");
				writer.write(card.getAddress().getAmpher() + ",");
				writer.write(card.getAddress().getProvince() + ",");
				writer.write(card.getCardIssuer() + ",");
				writer.write(card.getCardIssuedDate() + ",");
				writer.write(card.getCardExpiredDate() + ",");
				writer.write("\n");
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// save photo
		if (card.getPicture() != null) {
			try {
				//create directory
				
				String path = fileName.substring(0, fileName.lastIndexOf(File.separator)) + 
						File.separator + "photos";
				new File(path).mkdir();
				readerNameTF.setText(path+ File.separator + card.getCid() + ".jpg");
				OutputStream out = new FileOutputStream(path+ File.separator + card.getCid() + ".jpg");
				out.write(card.getPicture());
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
