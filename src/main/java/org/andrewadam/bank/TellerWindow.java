package org.andrewadam.bank;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JTextField;
import javax.swing.JInternalFrame;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.Panel;

public class TellerWindow {

	private JFrame frame;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private JTextField textField_6;
	private JTextField textField_7;
	private JTextField textField_8;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TellerWindow window = new TellerWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public TellerWindow() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 880, 492);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel EnterCheckTransaction = new JPanel();
		tabbedPane.addTab("Enter Check Transaction", null, EnterCheckTransaction, null);
		EnterCheckTransaction.setLayout(null);
		
		JLabel lblAccountIdNumber = new JLabel("account ID number");
		lblAccountIdNumber.setBounds(49, 55, 144, 20);
		EnterCheckTransaction.add(lblAccountIdNumber);
		
		textField = new JTextField();
		textField.setBounds(194, 52, 212, 26);
		EnterCheckTransaction.add(textField);
		textField.setColumns(10);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Deposit", "Top-Up", "Withdrawal", "Purchase", "Transfer", "Collect", "Pay-Friend", "Wire", "Write-Check", "Accrue-Interest"}));
		comboBox.setBounds(194, 108, 212, 20);
		EnterCheckTransaction.add(comboBox);
		
		JLabel lblAction = new JLabel("Action");
		lblAction.setBounds(126, 108, 67, 20);
		EnterCheckTransaction.add(lblAction);
		
		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setBounds(126, 168, 67, 20);
		EnterCheckTransaction.add(lblAmount);
		
		textField_1 = new JTextField();
		textField_1.setBounds(194, 165, 212, 26);
		EnterCheckTransaction.add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblTransferIdNumber = new JLabel("Transfer ID number");
		lblTransferIdNumber.setBounds(425, 55, 149, 20);
		EnterCheckTransaction.add(lblTransferIdNumber);
		
		textField_2 = new JTextField();
		textField_2.setBounds(589, 52, 237, 26);
		EnterCheckTransaction.add(textField_2);
		textField_2.setColumns(10);
		
		JButton btnEnter = new JButton("Enter");
		btnEnter.setBounds(291, 247, 115, 29);
		EnterCheckTransaction.add(btnEnter);
		
		JPanel GenerateMonthlyStatement = new JPanel();
		tabbedPane.addTab("Generate Monthly Statement", null, GenerateMonthlyStatement, null);
		GenerateMonthlyStatement.setLayout(null);
		
		JTextArea textArea = new JTextArea();
		textArea.setBounds(15, 90, 823, 296);
		GenerateMonthlyStatement.add(textArea);
		
		JLabel lblAccountIdNumber_1 = new JLabel("Account ID Number");
		lblAccountIdNumber_1.setBounds(39, 35, 148, 20);
		GenerateMonthlyStatement.add(lblAccountIdNumber_1);
		
		textField_3 = new JTextField();
		textField_3.setBounds(202, 32, 146, 26);
		GenerateMonthlyStatement.add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnEnter_1 = new JButton("Enter");
		btnEnter_1.setBounds(373, 31, 115, 29);
		GenerateMonthlyStatement.add(btnEnter_1);
		
		JPanel ListClosedAccounts = new JPanel();
		tabbedPane.addTab("List Closed Accounts", null, ListClosedAccounts, null);
		ListClosedAccounts.setLayout(null);
		
		JTextArea textArea_1 = new JTextArea();
		textArea_1.setBounds(15, 90, 823, 296);
		ListClosedAccounts.add(textArea_1);
		
		JButton btnEnter_2 = new JButton("Enter");
		btnEnter_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnEnter_2.setBounds(373, 31, 115, 29);
		ListClosedAccounts.add(btnEnter_2);
		
		JPanel DTER = new JPanel();
		tabbedPane.addTab("DTER", null, DTER, null);
		DTER.setLayout(null);
		
		JTextArea textArea_2 = new JTextArea();
		textArea_2.setBounds(15, 90, 823, 296);
		DTER.add(textArea_2);
		
		JButton button = new JButton("Enter");
		button.setBounds(373, 31, 115, 29);
		DTER.add(button);
		
		JPanel CustomerReport = new JPanel();
		tabbedPane.addTab("Customer Report", null, CustomerReport, null);
		CustomerReport.setLayout(null);
		
		JTextArea textArea_3 = new JTextArea();
		textArea_3.setBounds(15, 90, 823, 296);
		CustomerReport.add(textArea_3);
		
		JLabel label = new JLabel("Account ID Number");
		label.setBounds(39, 35, 148, 20);
		CustomerReport.add(label);
		
		textField_4 = new JTextField();
		textField_4.setColumns(10);
		textField_4.setBounds(202, 32, 146, 26);
		CustomerReport.add(textField_4);
		
		JButton button_1 = new JButton("Enter");
		button_1.setBounds(373, 31, 115, 29);
		CustomerReport.add(button_1);
		
		JPanel AddInterest = new JPanel();
		tabbedPane.addTab("Add Interest", null, AddInterest, null);
		AddInterest.setLayout(null);
		
		JButton button_2 = new JButton("Enter");
		button_2.setBounds(363, 59, 115, 29);
		AddInterest.add(button_2);
		
		JPanel CreateAccount = new JPanel();
		tabbedPane.addTab("Create Account", null, CreateAccount, "");
		CreateAccount.setLayout(null);
		
		textField_5 = new JTextField();
		textField_5.setBounds(692, 76, 146, 26);
		CreateAccount.add(textField_5);
		textField_5.setColumns(10);
		
		JLabel lblName = new JLabel("Name");
		lblName.setBounds(621, 79, 69, 20);
		CreateAccount.add(lblName);
		
		textField_6 = new JTextField();
		textField_6.setBounds(692, 118, 146, 26);
		CreateAccount.add(textField_6);
		textField_6.setColumns(10);
		
		JLabel lblTaxIdentificationNumber = new JLabel("tax identification number");
		lblTaxIdentificationNumber.setBounds(492, 121, 185, 20);
		CreateAccount.add(lblTaxIdentificationNumber);
		
		textField_7 = new JTextField();
		textField_7.setBounds(692, 163, 146, 26);
		CreateAccount.add(textField_7);
		textField_7.setColumns(10);
		
		JLabel lblAddress = new JLabel("address");
		lblAddress.setBounds(608, 166, 69, 20);
		CreateAccount.add(lblAddress);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(702, 205, 115, 29);
		CreateAccount.add(btnAdd);
		
		JLabel lblAddCustomerTax = new JLabel("Add customer, tax id only needed for existing customers");
		lblAddCustomerTax.setBounds(453, 36, 400, 20);
		CreateAccount.add(lblAddCustomerTax);
		
		JLabel lblAccountType = new JLabel(" account type");
		lblAccountType.setBounds(15, 79, 108, 20);
		CreateAccount.add(lblAccountType);
		
		JComboBox comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"student checking", "interest checking", "savings", "pocket"}));
		comboBox_1.setBounds(121, 82, 212, 20);
		CreateAccount.add(comboBox_1);
		
		JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.setBounds(128, 205, 164, 29);
		CreateAccount.add(btnCreateAccount);
		
		textField_8 = new JTextField();
		textField_8.setBounds(131, 133, 202, 26);
		CreateAccount.add(textField_8);
		textField_8.setColumns(10);
		
		JLabel lblDeposit = new JLabel("Deposit");
		lblDeposit.setBounds(31, 136, 69, 20);
		CreateAccount.add(lblDeposit);
		
		JPanel DeleteClosedAccountsandCustomers = new JPanel();
		tabbedPane.addTab("Delete Closed Accounts and Customers", null, DeleteClosedAccountsandCustomers, null);
		DeleteClosedAccountsandCustomers.setLayout(null);
		
		JButton btnEnter_3 = new JButton("Enter");
		btnEnter_3.setBounds(356, 36, 115, 29);
		DeleteClosedAccountsandCustomers.add(btnEnter_3);
		
		Panel DeleteTransactions = new Panel();
		tabbedPane.addTab("Delete Transactions", null, DeleteTransactions, null);
		DeleteTransactions.setLayout(null);
		
		JButton btnEnter_4 = new JButton("Enter");
		btnEnter_4.setBounds(356, 43, 115, 29);
		DeleteTransactions.add(btnEnter_4);
	}
}
