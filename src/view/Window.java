package view;

import controller.Controller;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import model.FileTransferManager;

public final class Window extends JFrame implements Observer, ActionListener {

	private JButton _btn_Send;
	private JButton _btn_Receive;
	private JButton _btn_ChooseFile;
	private JButton _btn_ChooseDownloadFile;
	private JTextField _txt_PathSend;
	private JTextField _txt_PathReceive;
	private JTextField _txt_PathServerSend;
	private JTextField _txt_PathServerReceive;
	private JTextField _txt_IPSend;
	private JTextField _txt_IPReceive;
	private JLabel _lbl_LocationSend;
	private JLabel _lbl_IPSend;
	private JLabel _lbl_ServerSend;
	private JLabel _lbl_LocationReceive;
	private JLabel _lbl_IPReceive;
	private JLabel _lbl_ServerReceive;
	private TextArea _textArea;
	private Controller _controller;

	public Window() {
		_controller = new Controller(this);
		//Gestion send

		_btn_Send = new JButton("Send");
		_btn_Send.addActionListener(this);

		_btn_ChooseFile = new JButton("Choose");
		_btn_ChooseFile.addActionListener(this);

		_lbl_LocationSend = new JLabel("Location on your computer :");
		_lbl_IPSend = new JLabel("Server's IP :");
		_lbl_ServerSend = new JLabel("Location on the server :");

		_lbl_LocationReceive = new JLabel("Location on your computer :");
		_lbl_IPReceive = new JLabel("Server's IP :");
		_lbl_ServerReceive = new JLabel("Location on the server :");

		_txt_PathSend = new JTextField(10);
		_txt_PathServerSend = new JTextField(20);
		_txt_IPSend = new JTextField(20);

		//Gestion receive
		_btn_Receive = new JButton("Receive");
		_btn_Receive.addActionListener(this);

		_btn_ChooseDownloadFile = new JButton("Choose");
		_btn_ChooseDownloadFile.addActionListener(this);

		_txt_PathReceive = new JTextField(10);
		_txt_PathServerReceive = new JTextField(20);
		_txt_IPReceive = new JTextField(20);


		//Gestion fenetre & layout
		JPanel total = new JPanel();
		total.setLayout(new BorderLayout());
		this.setTitle("Connexion to TFTP server");
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


		JPanel formSend = new JPanel();
		formSend.setLayout(new GridLayout(4, 1));
		JPanel sendChooser = new JPanel();
		sendChooser.setLayout(new GridLayout(1, 2));
		sendChooser.add(_lbl_LocationSend);
		JPanel sendChooserText = new JPanel();
		sendChooserText.setLayout(new GridLayout(1, 2));
		sendChooserText.add(_txt_PathSend);
		sendChooserText.add(_btn_ChooseFile);
		sendChooser.add(sendChooserText);

		JPanel sendServer = new JPanel();
		sendServer.setLayout(new GridLayout(1, 2));
		//sendServer.add(_lbl_ServerSend);
		sendServer.add(_txt_PathServerSend);

		JPanel sendIP = new JPanel();
		sendIP.setLayout(new GridLayout(1, 2));
		sendIP.add(_lbl_IPSend);
		sendIP.add(_txt_IPSend);
		formSend.add(sendChooser);
		formSend.add(sendServer);
		formSend.add(sendIP);

		sendServer.setVisible(false);

		formSend.add(_btn_Send);


		JPanel formReceive = new JPanel();
		formReceive.setLayout(new GridLayout(4, 1));
		JPanel receiveChooser = new JPanel();
		receiveChooser.setLayout(new GridLayout(1, 2));
		receiveChooser.add(_lbl_LocationReceive);
		JPanel receiveChooserText = new JPanel();
		receiveChooserText.setLayout(new GridLayout(1, 2));
		receiveChooserText.add(_txt_PathReceive);
		receiveChooserText.add(_btn_ChooseDownloadFile);
		receiveChooser.add(receiveChooserText);

		JPanel receiveServer = new JPanel();
		receiveServer.setLayout(new GridLayout(1, 2));
		receiveServer.add(_lbl_ServerReceive);
		receiveServer.add(_txt_PathServerReceive);

		JPanel receiveIP = new JPanel();
		receiveIP.setLayout(new GridLayout(1, 2));
		receiveIP.add(_lbl_IPReceive);
		receiveIP.add(_txt_IPReceive);
		formReceive.add(receiveChooser);
		formReceive.add(receiveServer);
		formReceive.add(receiveIP);
		formReceive.add(_btn_Receive);



		JTabbedPane panel = new JTabbedPane();
		panel.addTab("Send", formSend);
		panel.addTab("Download", formReceive);

		total.add(panel, BorderLayout.NORTH);
		_textArea = new TextArea();
		_textArea.setFocusable(false);
		//JScrollPane scrollPane = new JScrollPane(_textArea);
		total.add(_textArea);

		JPanel main = new JPanel();
		main.setLayout(new BorderLayout());
		main.add(total, BorderLayout.CENTER);
		main.setBackground(Color.GRAY);

		this.setMinimumSize(new Dimension(394, 300));
		this.setContentPane(main);
		this.setFocusable(true);
		//this.setResizable(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == _btn_ChooseFile) {
			JFileChooser fc = new JFileChooser();
			if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
				String path = fc.getSelectedFile().getPath();
				_txt_PathSend.setText(path);
			}
		} else if (e.getSource() == _btn_ChooseDownloadFile) {
			boolean test = false;
			JFileChooser fc = new JFileChooser();
			do {
				if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
					String path = fc.getSelectedFile().getPath();
					File file = new File(path);
					if (!file.exists()) {
						_txt_PathReceive.setText(path);
						test = false;
					} else if (JOptionPane.showConfirmDialog(null, "Ce fichier existe déjà, l'écraser?", "Confirmer l'écrasement", JOptionPane.OK_CANCEL_OPTION) == 0) {
						_txt_PathReceive.setText(path);
						test = false;
					} else {
						test = true;
					}

				} else {
					test = false;
				}
			} while (test);
		} else if (e.getSource() == _btn_Receive) {
			if (_txt_PathReceive.getText().length() == 0) {
				this.write("Vous devez choisir un fichier à envoyer.");
				return;
			}
			if (_txt_PathServerReceive.getText().length() == 0) {
				this.write("Vous devez la destination du fichier.");
				return;
			}
			if (!Window.validIP(_txt_IPReceive.getText())) {
				this.write("L'adresse IP n'est pas valide.");
				return;
			}
			_controller.receive(_txt_PathReceive.getText(), _txt_PathServerReceive.getText(), _txt_IPReceive.getText());
		} else if (e.getSource() == _btn_Send) {
			if (_txt_PathSend.getText().length() == 0) {
				this.write("Vous devez choisir un fichier à envoyer.");
				return;
			}
			/*
			 if (_txt_PathServerSend.getText().length() == 0) {
			 this.write("Vous devez compléter la localisation.");
			 this.write(_txt_PathServerSend.getText());
			 return;
			 }
			 //*/
			if (!Window.validIP(_txt_IPSend.getText())) {
				this.write("L'adresse IP n'est pas valide.");
				return;
			}
			_controller.send(_txt_PathSend.getText(), _txt_IPSend.getText());
		}
	}

	public void write(String s) {
		_textArea.append(s + "\n");
	}

	public static boolean validIP(String ip) {
		if (ip == null || ip.isEmpty()) {
			return false;
		}
		ip = ip.trim();
		if ((ip.length() < 6) & (ip.length() > 15)) {
			return false;
		}

		try {
			Pattern pattern = Pattern.compile("^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
			Matcher matcher = pattern.matcher(ip);
			return matcher.matches();
		} catch (PatternSyntaxException ex) {
			return false;
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof FileTransferManager) {
			String message = (String) arg;

			this.write(message);

		}
	}
}
