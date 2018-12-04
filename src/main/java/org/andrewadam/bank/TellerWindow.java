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
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import java.awt.Panel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

public class TellerWindow {

	// Database object:
	private Data db;

	private JFrame frame;
	private JTextField AccountIdNumberInput;
	private JTextField AmountInput;
	private JTextField TransferIdNumberInput;
	private JTextField taxIdNumberInput;
	private JTextField taxIdNumberCustomerReportInput;
	private JTextField NameInput;
	private JTextField TaxIdInput;
	private JTextField AddressInput;
	private JTextField DepositInput;

	private JTextField LinkedAccountInput;
	private ArrayList<String> tax_id_list = new ArrayList<String>();
	private JTextField BankBranchInput;

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

	// Checks the type of account
	public Boolean isAccountType(String accountType, String accountId) throws Exception {
		String qry = "select 1 from type where name='" + accountType + "' and account_id='" + accountId + "'";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		Boolean result = rs.next();
		rs.close();
		return result;
	}

	// Checks if the given tax id own the given account id
	public Boolean doesTaxidOwnAccount(String taxId, String accountId) throws Exception {
		String qry = "select 1 from owns where tax_id='" + taxId + "' and account_id='" + accountId + "'";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		Boolean result = rs.next();
		rs.close();
		return result;
	}

	// Checks if accounts are linked
	public Boolean areAccountsLinked(String accountId1, String accountId2) throws Exception {
		String qry = "select 1 from account where (account_id='" + accountId1 + "' and linked_account='" + accountId2
				+ "') or (account_id='" + accountId2 + "' and linked_account='" + accountId1 + "')";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		Boolean result = rs.next();
		rs.close();
		return result;
	}

	// Checks if there is a owner in common
	public Boolean ownerInAccounts(String accountId1, String accountId2) throws Exception {
		// select 1 from owns o1, owns o2 where o1.account_id=999999985 and
		// o2.account_id=999999989 and o1.tax_id=o2.tax_id;
		String qry = "select 1 from owns o1, owns o2 where o1.account_id='" + accountId1 + "' and o2.account_id='"
				+ accountId2 + "' and o1.tax_id=o2.tax_id";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		Boolean result = rs.next();
		rs.close();
		return result;
	}

	// Checks if account will go negative
	public Boolean accountGoesNegative(String accountId, String amount) throws Exception {
		String qry = "select balance from account where account_id='" + accountId + "'";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		rs.next();
		Double curBalance = Double.valueOf(rs.getString(1).trim());
		rs.close();
		System.out.println("curbalance = " + curBalance);
		System.out.println("Double.valueOf(amount)= " + Double.valueOf(amount));
		if ((curBalance - Double.valueOf(amount)) > 0.01)
			return false;
		else
			return true;
	}

	// Iterate, and return valid transaction ID
	public Integer nextTransactionId() throws Exception {
		// Getting next transaction ID to use
		String qry = "select max(id) from transactions";
		ResultSet rs = db.requestData(qry);
		rs.next();
		Integer transactionId = Integer.valueOf(rs.getString(1).trim());
		transactionId++;
		rs.close();
		return transactionId;

	}

	// Creates a transaction
	public void createTransaction(String accountId1, String accountId2, String amount, Integer checkNumber,
			String transactionTypeName) throws Exception {
		// id, transaction_date, check_number, amount
		// "insert into transactions values(1,'"+curDate+"',1,1.11)"
		java.util.Date date = new java.util.Date();
		String curDate = date.getMonth() + 1 + "/" + date.getDate() + "/" + (date.getYear() + 1900);
		String qry = "";
		Integer transId = nextTransactionId();

		// There is a check number with this transaction
		if (checkNumber > 0) {
			qry = "insert into transactions(id, transaction_date, check_number, amount) " + "values(" + transId + ",'"
					+ curDate + "'," + checkNumber + "," + amount + ")";

			// There is NOT a check number with this transaction
		} else {
			qry = "insert into transactions(id, transaction_date,amount) " + "values(" + transId + ",'" + curDate + "',"
					+ amount + ")";
		}
		System.out.println("prepre");
		// Insert into transactions
		System.out.println(qry);
		db.requestData(qry);

		// Insert into makes
		// 9999999999 is teller tax_id
		// Getting primary owner
		System.out.println("pre");
		qry = "select distinct c.tax_id from customers c, owns o, account a where c.tax_id=o.tax_id and c.tax_id=a.primary_owner and o.account_id='"
				+ accountId1 + "'";
		// AccountIdNumberInput.getText()
		System.out.println("post");
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		rs.next();
		String temp_tax_id = rs.getString("tax_id");
		System.out.println(temp_tax_id);

		qry = "insert into makes (account_id,id,tax_id) values('" + accountId1 + "'," + transId + ",'" + temp_tax_id
				+ "')";
		System.out.println(qry);
		db.requestData(qry);

		if (accountId2.length() > 0) {
			System.out.println("accountID2= " + accountId2 + "=");
			// 9999999999 is teller tax_id
			qry = "insert into makes (account_id,id,tax_id) values('" + accountId2 + "'," + transId + ",'" + temp_tax_id
					+ "')";
			System.out.println(qry);
			db.requestData(qry);
		}

		// Insert into has transaction type
		qry = "insert into has_t_type(id,type_name) values(" + transId + ",'" + transactionTypeName + "')";
		System.out.println(qry);
		db.requestData(qry);

	}

	// Generate transactions for a given account
	public String accountTransactionsFor(String accountId) throws Exception {
		String transactions = "";
		System.out.println("in: accountID= " + accountId);
		ArrayList<String> idList = new ArrayList<String>();
		// Getting to and from transactions
		String qry = "select distinct t.transaction_date, t.id, c.name, hs.type_name, t.amount, m1.account_id, m2.account_id as account_id_2"
				+ " from transactions t, makes m1, makes m2, has_t_type hs, type ty, customers c"
				+ " where t.id=m1.id and t.id=m2.id and (ty.account_id=m1.account_id"
				+ " or ty.account_id=m2.account_id) and hs.id=t.id and not(m1.account_id=m2.account_id)"
				+ " and m1.id=m2.id and c.tax_id=m1.tax_id and m1.account_id='" + accountId + "'";
		System.out.println(qry);
		ResultSet rs = db.requestData(qry);
		while (rs.next()) {
			idList.add(rs.getString("id"));
			transactions = transactions + rs.getString("id") + " " + rs.getString("transaction_date") + " "
					+ rs.getString("account_id") + " " + rs.getString("name").trim() + " "
					+ rs.getString("type_name").trim() + " " + rs.getString("amount") + " "
					+ rs.getString("account_id_2") + "\n";
		}

		// Getting single account transactions
		qry = "select distinct t.id, t.transaction_date, c.name, hs.type_name, t.amount, m1.account_id"
				+ " from transactions t, makes m1, makes m2, has_t_type hs, type ty, customers c"
				+ " where t.id=m1.id and t.id=hs.id and ty.account_id=m1.account_id" + " and m1.account_id='"
				+ accountId + "' and c.tax_id=m1.tax_id";
		System.out.println(qry);
		rs = db.requestData(qry);
		while (rs.next()) {
			// Checking if i've already added this transaction
			if (!idList.contains(rs.getString("id")))
				transactions = transactions + rs.getString("id") + " " + rs.getString("transaction_date") + " "
						+ rs.getString("account_id") + " " + rs.getString("name").trim() + " "
						+ rs.getString("type_name").trim() + " " + rs.getString("amount") + "\n";
		}
		rs.close();
		db.closeConn();
		System.out.println(transactions);
		return transactions;

	}

	// select c.name,c.address from customers c, owns o where c.tax_id=o.tax_id
	// and o.account_id='999999989';
	// Gets customers names and addresses from account
	public String customersFromAccount(String accountId) throws Exception {
		try {
			String customers = "";
			System.out.println("in: accountID= " + accountId);
			ArrayList<String> idList = new ArrayList<String>();
			// Getting to and from transactions
			String qry = "select c.name,c.address from customers c, owns o where c.tax_id=o.tax_id and o.account_id='"
					+ accountId + "'";
			System.out.println(qry);
			ResultSet rs = db.requestData(qry);
			// adding customer data
			while (rs.next())
				customers = customers + rs.getString("name") + rs.getString("address") + "\n";
			return customers;

		} catch (Exception e1) {
			System.out.println("Get customers data failed");
		}
		return "";
	}

	// Gets all acounts with transactions over 10k
	public String DTER() throws Exception {
		try {
			String customers = "";
			// Getting list of names, amounts and type of transfer that are over
			// $10000
			String qry = "select distinct c.name, t.amount, hs.type_name "
					+ " from has_t_type hs, customers c, makes m, transactions t, owns o"
					+ " where (hs.type_name='deposit' or hs.type_name='transfer' or hs.type_name='wire') and hs.id=m.id and m.tax_id=m.tax_id"
					+ " and o.account_id=m.account_id and o.tax_id=c.tax_id" + " and t.id=m.id and t.amount > 10000";

			System.out.println(qry);
			ResultSet rs = db.requestData(qry);

			// Compiling list of people to be presented
			while (rs.next())
				customers = customers + rs.getString("name") + " " + rs.getString("amount") + " "
						+ rs.getString("type_name") + "\n";
			return customers;

		} catch (Exception e1) {
			System.out.println("Get customers data failed");
		}
		return "";
	}

	// Check if account is open
	public Boolean accountOpen(String account) throws Exception {
		try {
			String status = "";
			// Getting list of names, amounts and type of transfer that are over
			// $10000
			String qry = "select * from account where account_id='" + account + "'";
			// System.out.println(qry);
			ResultSet rs = db.requestData(qry);

			rs.next();
			status = rs.getString("status");
			if (status.equals("1")) {
				// System.out.println("status = "+status +" and returning
				// true");
				return true;
			} else {
				// System.out.println("status = "+status +" and returning
				// false");
				return false;
			}

		} catch (Exception e1) {
			System.out.println("check if account is closed failed");
			return false;
		}
	}

	// DELETING ACCOUNT AND CUSTOMERS
	public void deleteClosedAccounts() throws Exception {
		try {
			String qry = "select account_id from account where status='0'";
			System.out.println(qry);
			ResultSet rs = db.requestData(qry);
			ResultSet rs2;

			while (rs.next()) {
				qry = "delete from makes where account_id='" + rs.getString("account_id") + "'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "delete from type where account_id='" + rs.getString("account_id") + "'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "delete from owns where account_id='" + rs.getString("account_id") + "'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "update account set linked_account=null where linked_account='" + rs.getString("account_id") + "'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "delete from account where account_id='" + rs.getString("account_id") + "'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "select tax_id from customers minus select tax_id from owns";
				System.out.println(qry);
				rs2 = db.requestData(qry);
				while (rs2.next()){
					qry = "delete from customers where tax_id='"+ rs2.getString("tax_id") + "'";
					System.out.println(qry);
					db.requestData(qry);
				}
				rs2.close();
					
			}
			rs.close();
			db.closeConn();

		} catch (Exception e1) {
			System.out.println("delete accounts and customers failed");

		}
	}

	// DELETING transactions
		public void deleteTransactions() throws Exception {
			try {
				String qry = "delete from makes where not id='1'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "delete from has_t_type where not id='1'";
				System.out.println(qry);
				db.requestData(qry);
				qry = "delete from transactions where not id='1'";
				System.out.println(qry);
				db.requestData(qry);
				db.closeConn();

			} catch (Exception e1) {
				System.out.println("delete transactions failed");

			}
		}
		
	/**
	 * Create the application.
	 */
	public TellerWindow() {
		// Establish data object.
		db = new Data();

		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 880, 492);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel EnterCheckTransaction = new JPanel();
		tabbedPane.addTab("Enter Check Transaction", null, EnterCheckTransaction, null);
		EnterCheckTransaction.setLayout(null);

		JLabel lblAccountIdNumber = new JLabel("account ID number");
		lblAccountIdNumber.setBounds(49, 55, 144, 20);
		EnterCheckTransaction.add(lblAccountIdNumber);

		AccountIdNumberInput = new JTextField();
		AccountIdNumberInput.setBounds(194, 52, 212, 26);
		EnterCheckTransaction.add(AccountIdNumberInput);
		AccountIdNumberInput.setColumns(10);

		JComboBox ActionInput = new JComboBox();
		ActionInput.setModel(new DefaultComboBoxModel(new String[] { "Deposit", "Top-Up", "Withdrawal", "Purchase",
				"Transfer", "Collect", "Pay-Friend", "Wire", "Write-Check", "Accrue-Interest" }));
		ActionInput.setBounds(194, 108, 212, 20);
		EnterCheckTransaction.add(ActionInput);

		JLabel lblAction = new JLabel("Action");
		lblAction.setBounds(126, 108, 67, 20);
		EnterCheckTransaction.add(lblAction);

		JLabel lblAmount = new JLabel("Amount");
		lblAmount.setBounds(126, 168, 67, 20);
		EnterCheckTransaction.add(lblAmount);

		AmountInput = new JTextField();
		AmountInput.setBounds(194, 165, 212, 26);
		EnterCheckTransaction.add(AmountInput);
		AmountInput.setColumns(10);

		JLabel lblTransferIdNumber = new JLabel("Transfer ID number");
		lblTransferIdNumber.setBounds(425, 55, 149, 20);
		EnterCheckTransaction.add(lblTransferIdNumber);

		TransferIdNumberInput = new JTextField();
		TransferIdNumberInput.setBounds(589, 52, 237, 26);
		EnterCheckTransaction.add(TransferIdNumberInput);
		TransferIdNumberInput.setColumns(10);

		JButton btnEnter = new JButton("Enter");
		btnEnter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(ActionInput.getSelectedItem().toString() + " Selected");
				switch (ActionInput.getSelectedItem().toString()) {
				case "Deposit":

					try {
						// Check if accounts are closed
						if (!accountOpen(AccountIdNumberInput.getText())) {
							System.out.println("Make sure account is open:");
							System.out.println("true=open false=closed");
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checking if account is student checking
						Boolean sChecking = isAccountType("student checking", AccountIdNumberInput.getText());

						// Checking if account is interest checking
						Boolean iChecking = isAccountType("interest checking", AccountIdNumberInput.getText());

						// Checking if account is savings
						Boolean savings = isAccountType("savings", AccountIdNumberInput.getText());

						// Check if the account is a checking account
						if (sChecking || iChecking || savings) {
							System.out.println("exists");
							String qry = "update account set balance = balance + " + AmountInput.getText()
									+ " where account_id = " + AccountIdNumberInput.getText();
							System.out.println(qry);
							db.requestData(qry);

							createTransaction(AccountIdNumberInput.getText(), "0", AmountInput.getText(), 0, "deposit");
						} else
							System.out.println("Account is NOT a checking or savings account");

						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to deposit");
					}
					break;
				case "Top-Up":
					// That below gets a list of tax id that are savings
					// accounts
					// select o.tax_id, o.account from owns o, type t where
					// t.account_id=o.account_id and t.name='savings';
					try {
						// Check if accounts are closed
						if (!(accountOpen(TransferIdNumberInput.getText())
								&& accountOpen(AccountIdNumberInput.getText()))) {
							System.out.println("Make sure both accounts are open:");
							System.out.println("true=open false=closed");
							System.out.println(TransferIdNumberInput.getText() + "="
									+ accountOpen(TransferIdNumberInput.getText()));
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(TransferIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ TransferIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ TransferIdNumberInput.getText() + " is now closed");
							return;
						} else {
							// Checking if account is student checking
							Boolean sChecking = isAccountType("student checking", TransferIdNumberInput.getText());

							// Checking if account is interest checking
							Boolean iChecking = isAccountType("interest checking", TransferIdNumberInput.getText());

							// Checking if account is savings
							Boolean savings = isAccountType("savings", TransferIdNumberInput.getText());

							// Checking if account is pocket
							Boolean pocket = isAccountType("pocket", AccountIdNumberInput.getText());

							// Checking if accounts are linked
							Boolean linked = areAccountsLinked(AccountIdNumberInput.getText(),
									TransferIdNumberInput.getText());
							System.out.println("pocket=" + pocket);
							if ((sChecking || iChecking || savings) && pocket && linked) {
								String qry = "update account set balance = balance + " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + TransferIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);

								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), TransferIdNumberInput.getText(),
										AmountInput.getText(), 0, "top-up");
							} else
								// TODO actually check these as you go
								System.out.println("Fail: the account wasnt checking/saving, pocket, or linked");
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Top-Up");
					}
					break;
				case "Withdrawal":
					try {
						// Check if accounts are closed
						if (!accountOpen(AccountIdNumberInput.getText())) {
							System.out.println("Make sure account is open:");
							System.out.println("true=open false=closed");
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(AccountIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ AccountIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ AccountIdNumberInput.getText() + " is now closed");
							return;
						} else {
							System.out.println("account will NOT go negative");

							// Checking if account is student checking
							Boolean sChecking = isAccountType("student checking", AccountIdNumberInput.getText());

							// Checking if account is interest checking
							Boolean iChecking = isAccountType("interest checking", AccountIdNumberInput.getText());

							// Checking if account is savings
							Boolean savings = isAccountType("savings", AccountIdNumberInput.getText());

							if (sChecking || iChecking || savings) {
								String qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);

								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), "0", AmountInput.getText(), 0,
										"withdrawal");

							} else
								System.out.println("Account is NOT a checking or savings account");
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Withdrawal");
					}
					break;
				case "Purchase":
					try {
						// Check if accounts are closed
						if (!accountOpen(AccountIdNumberInput.getText())) {
							System.out.println("Make sure account is open:");
							System.out.println("true=open false=closed");
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checking if account is pocket
						Boolean pocket = isAccountType("pocket", AccountIdNumberInput.getText());

						if (pocket) {
							// Checks if account will go negative
							if (accountGoesNegative(AccountIdNumberInput.getText(), AmountInput.getText())) {
								String qry = "update account set status='0' where account_id='"
										+ AccountIdNumberInput.getText() + "'";
								db.requestData(qry);
								db.closeConn();
								System.out.println("Account WILL go negative, and now account "
										+ AccountIdNumberInput.getText() + " is now closed");
								return;
							} else {
								String qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), "0", AmountInput.getText(), 0,
										"purchase");
							}
						} else
							System.out.println("Account id is not a pocket account");
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Purchase");
					}
					break;
				case "Transfer":
					try {
						if(Double.parseDouble(AmountInput.getText()) > 2000){
							System.out.println("Please enter a amount less than 2000");
							return;
						}
						// Check if accounts are closed
						if (!(accountOpen(TransferIdNumberInput.getText())
								&& accountOpen(AccountIdNumberInput.getText()))) {
							System.out.println("Make sure both accounts are open:");
							System.out.println("true=open false=closed");
							System.out.println(TransferIdNumberInput.getText() + "="
									+ accountOpen(TransferIdNumberInput.getText()));
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(TransferIdNumberInput.getText(), AmountInput.getText())) {
							System.out.println("account WILL go negative");
							return;
						} else {
							// Checking if there is a owner in both accounts
							Boolean owner = ownerInAccounts(AccountIdNumberInput.getText(),
									TransferIdNumberInput.getText());

							if (owner) {
								String qry = "update account set balance = balance + " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + TransferIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), "0", AmountInput.getText(), 0,
										"transfer");
							} else {
								System.out.println("NOT owner in both accounts");
							}
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Transfer");
					}
					break;
				case "Collect":
					// TODO 3%fee
					try {
						// Check if accounts are closed
						if (!(accountOpen(TransferIdNumberInput.getText())
								&& accountOpen(AccountIdNumberInput.getText()))) {
							System.out.println("Make sure both accounts are open:");
							System.out.println("true=open false=closed");
							System.out.println(TransferIdNumberInput.getText() + "="
									+ accountOpen(TransferIdNumberInput.getText()));
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(TransferIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ TransferIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ TransferIdNumberInput.getText() + " is now closed");
							return;
						} else {
							// Checking if account is student checking
							Boolean sChecking = isAccountType("student checking", AccountIdNumberInput.getText());

							// Checking if account is interest checking
							Boolean iChecking = isAccountType("interest checking", AccountIdNumberInput.getText());

							// Checking if account is savings
							Boolean savings = isAccountType("savings", AccountIdNumberInput.getText());

							// Checking if account is pocket
							Boolean pocket = isAccountType("pocket", TransferIdNumberInput.getText());

							// Checking if accounts are linked
							Boolean linked = areAccountsLinked(AccountIdNumberInput.getText(),
									TransferIdNumberInput.getText());

							if ((sChecking || iChecking || savings) && pocket && linked) {
								String qry = "update account set balance = balance + " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + TransferIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), TransferIdNumberInput.getText(),
										AmountInput.getText(), 0, "collect");
							} else
								// TODO actually check these as you go
								System.out.println("Fail: the account wasnt checking/saving, pocket, or linked");
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Collect");
					}
					break;
				case "Pay-Friend":
					try {
						// Check if accounts are closed
						if (!(accountOpen(TransferIdNumberInput.getText())
								&& accountOpen(AccountIdNumberInput.getText()))) {
							System.out.println("Make sure both accounts are open:");
							System.out.println("true=open false=closed");
							System.out.println(TransferIdNumberInput.getText() + "="
									+ accountOpen(TransferIdNumberInput.getText()));
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(TransferIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ TransferIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ TransferIdNumberInput.getText() + " is now closed");
							return;
						} else {
							// Checking if account is pocket
							Boolean pocket1 = isAccountType("pocket", AccountIdNumberInput.getText());

							// Checking if account is pocket
							Boolean pocket2 = isAccountType("pocket", TransferIdNumberInput.getText());

							if (pocket1 && pocket2) {
								String qry = "update account set balance = balance + " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + TransferIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), TransferIdNumberInput.getText(),
										AmountInput.getText(), 0, "pay-friend");
							} else {
								System.out.println("to=" + pocket1);
								System.out.println("from=" + pocket2);
								System.out.println("Make sure both accounts are pocket accounts");
							}
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Pay-Friend");
					}
					break;
				case "Wire":
					// TODO a 2% fee for this action
					try {
						// Check if accounts are closed
						if (!(accountOpen(TransferIdNumberInput.getText())
								&& accountOpen(AccountIdNumberInput.getText()))) {
							System.out.println("Make sure both accounts are open:");
							System.out.println("true=open false=closed");
							System.out.println(TransferIdNumberInput.getText() + "="
									+ accountOpen(TransferIdNumberInput.getText()));
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(TransferIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ TransferIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ TransferIdNumberInput.getText() + " is now closed");
							return;
						} else {
							// Checking if accounts are student checking
							Boolean sChecking1 = isAccountType("student checking", AccountIdNumberInput.getText());
							Boolean sChecking2 = isAccountType("student checking", TransferIdNumberInput.getText());

							// Checking if accounts are interest checking
							Boolean iChecking1 = isAccountType("interest checking", AccountIdNumberInput.getText());
							Boolean iChecking2 = isAccountType("interest checking", TransferIdNumberInput.getText());

							// Checking if accounts are savings
							Boolean savings1 = isAccountType("savings", AccountIdNumberInput.getText());
							Boolean savings2 = isAccountType("savings", TransferIdNumberInput.getText());

							if ((sChecking1 || iChecking1 || savings1) && (sChecking2 || iChecking2 || savings2)) {
								String qry = "update account set balance = balance + " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + TransferIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								createTransaction(AccountIdNumberInput.getText(), TransferIdNumberInput.getText(),
										AmountInput.getText(), 0, "wire");
							} else {
								System.out.println("make sure both accounts are checking/savings");
							}
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Wire");
					}
					break;
				case "Write-Check":
					// TODO check number
					try {
						// Check if accounts are closed
						if (!accountOpen(AccountIdNumberInput.getText())) {
							System.out.println("Make sure account is open:");
							System.out.println("true=open false=closed");
							System.out.println(
									AccountIdNumberInput.getText() + "=" + accountOpen(AccountIdNumberInput.getText()));
							return;
						}
						// Checks if account will go negative
						if (accountGoesNegative(AccountIdNumberInput.getText(), AmountInput.getText())) {
							String qry = "update account set status='0' where account_id='"
									+ AccountIdNumberInput.getText() + "'";
							db.requestData(qry);
							db.closeConn();
							System.out.println("Account WILL go negative, and now account "
									+ AccountIdNumberInput.getText() + " is now closed");
							return;
						} else {
							// Checking if accounts are student checking
							Boolean sChecking = isAccountType("student checking", AccountIdNumberInput.getText());

							if (sChecking) {
								String qry = "update account set balance = balance - " + AmountInput.getText()
										+ " where account_id = " + AccountIdNumberInput.getText();
								System.out.println(qry);
								db.requestData(qry);
								// Create transaction
								// TODO get check number
								createTransaction(AccountIdNumberInput.getText(), "0", AmountInput.getText(), 9,
										"write-check");
							}
						}
						db.closeConn();

					} catch (Exception e2) {
						System.out.println("failed to Withdrawal");
					}
					break;
				case "Accrue-Interest":
					// TODO this one might take a while
					System.out.println("");
					break;

				}
			}
		});
		btnEnter.setBounds(291, 247, 115, 29);
		EnterCheckTransaction.add(btnEnter);

		JLabel lblTo = new JLabel("To");
		lblTo.setBounds(251, 28, 69, 20);
		EnterCheckTransaction.add(lblTo);

		JLabel lblFrom = new JLabel("From");
		lblFrom.setBounds(676, 28, 69, 20);
		EnterCheckTransaction.add(lblFrom);

		JPanel GenerateMonthlyStatement = new JPanel();
		tabbedPane.addTab("Generate Monthly Statement", null, GenerateMonthlyStatement, null);
		GenerateMonthlyStatement.setLayout(null);

		JTextArea monthlyStatement = new JTextArea();
		monthlyStatement.setBounds(15, 90, 823, 296);
		GenerateMonthlyStatement.add(monthlyStatement);

		JLabel TaxIDNumber = new JLabel("Tax ID Number");
		TaxIDNumber.setBounds(39, 35, 148, 20);
		GenerateMonthlyStatement.add(TaxIDNumber);

		taxIdNumberInput = new JTextField();
		taxIdNumberInput.setBounds(202, 32, 146, 26);
		GenerateMonthlyStatement.add(taxIdNumberInput);
		taxIdNumberInput.setColumns(10);

		JButton btnEnter_1 = new JButton("Enter");
		btnEnter_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO add scroll to text field
				// TODO needs account initial and final balances
				// TODO warn if accounts are over $100,000
				try {
					ArrayList<String> accountList = new ArrayList<String>();
					String qry = "select account_id from owns where tax_id='" + taxIdNumberInput.getText() + "'";
					ResultSet r = db.requestData(qry);

					// Getting all accounts owned by the tax id
					while (r.next())
						accountList.add(r.getString("account_id"));

					// Creating text field for transactions output
					String statement = "Accounts\n________\n";
					String transactions = "";
					for (String acnt : accountList) {
						statement = statement + acnt + "\n";
						statement = statement + customersFromAccount(acnt);
					}
					statement = statement + "\nTransactions\n____________\n";
					for (String acnt : accountList) {
						// ID #
						// transactions...
						// ...
						statement = statement + "ID " + acnt + "\n";
						transactions = accountTransactionsFor(acnt);
						statement = statement + transactions + "\n";
					}
					monthlyStatement.setText(statement);
				} catch (Exception e1) {
					System.out.println("Make sure you entered a valid account number");
					System.out.println(e1);
				}

			}
		});
		btnEnter_1.setBounds(373, 31, 115, 29);
		GenerateMonthlyStatement.add(btnEnter_1);

		JPanel ListClosedAccounts = new JPanel();
		tabbedPane.addTab("List Closed Accounts", null, ListClosedAccounts, null);
		ListClosedAccounts.setLayout(null);

		JTextArea closedAccountTextField = new JTextArea();
		closedAccountTextField.setBounds(15, 90, 823, 296);
		ListClosedAccounts.add(closedAccountTextField);

		JButton btnEnter_2 = new JButton("Enter");
		btnEnter_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					ArrayList<String> accountList = new ArrayList<String>();
					String qry = "select * from account where status='0'";
					ResultSet r = db.requestData(qry);

					// Getting all accounts owned by the tax id
					while (r.next()) {
						accountList.add(r.getString("account_id"));
					}

					// Creating text field for transactions output
					String statement = "Accounts\n________\n";
					for (String acnt : accountList) {
						statement = statement + acnt + "\n";
					}

					closedAccountTextField.setText(statement);
				} catch (Exception e1) {
					System.out.println(e1);
				}

			}
		});
		btnEnter_2.setBounds(373, 31, 115, 29);
		ListClosedAccounts.add(btnEnter_2);

		JPanel DTER = new JPanel();
		tabbedPane.addTab("DTER", null, DTER, null);
		DTER.setLayout(null);

		JTextArea dterTextFieldOutput = new JTextArea();
		dterTextFieldOutput.setBounds(15, 90, 823, 296);
		DTER.add(dterTextFieldOutput);

		JButton button = new JButton("Enter");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String dterReport = DTER();
					dterTextFieldOutput.setText(dterReport);

				} catch (Exception e1) {
					e1.printStackTrace();
				}

			}
		});
		button.setBounds(373, 31, 115, 29);
		DTER.add(button);

		JPanel CustomerReport = new JPanel();
		tabbedPane.addTab("Customer Report", null, CustomerReport, null);
		CustomerReport.setLayout(null);

		JTextArea customerReportTextField = new JTextArea();
		customerReportTextField.setBounds(15, 90, 823, 296);
		CustomerReport.add(customerReportTextField);

		JLabel lblTaxIdNumber = new JLabel("Tax Id Number");
		lblTaxIdNumber.setBounds(39, 35, 148, 20);
		CustomerReport.add(lblTaxIdNumber);

		taxIdNumberCustomerReportInput = new JTextField();
		taxIdNumberCustomerReportInput.setColumns(10);
		taxIdNumberCustomerReportInput.setBounds(202, 32, 146, 26);
		CustomerReport.add(taxIdNumberCustomerReportInput);

		JButton button_1 = new JButton("Enter");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String qry = "select o.account_id, t.name, a.status from owns o, account a, type t where t.account_id=a.account_id and t.account_id=o.account_id and a.account_id=o.account_id and o.tax_id='"
							+ taxIdNumberCustomerReportInput.getText() + "'";
					ResultSet r = db.requestData(qry);

					String reportOutput = "1 = open\n0 = close\n\n   Account              type                    Status\n";// r.getString("account_id")
					// Getting all accounts owned by the tax id
					while (r.next())
						reportOutput = reportOutput + r.getString("account_id") + "      " + r.getString("name")
								+ "      " + r.getString("status") + "\n";
					customerReportTextField.setText(reportOutput);
				} catch (Exception e1) {
					System.out.println("Make sure you entered a valid tax id");
					System.out.println(e1);
				}
			}
		});
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

		NameInput = new JTextField();
		NameInput.setBounds(692, 76, 146, 26);
		CreateAccount.add(NameInput);
		NameInput.setColumns(10);

		JLabel lblName = new JLabel("Name");
		lblName.setBounds(621, 79, 69, 20);
		CreateAccount.add(lblName);

		TaxIdInput = new JTextField();
		TaxIdInput.setBounds(692, 118, 146, 26);
		CreateAccount.add(TaxIdInput);
		TaxIdInput.setColumns(10);

		JLabel lblTaxIdentificationNumber = new JLabel("tax identification number");
		lblTaxIdentificationNumber.setBounds(492, 121, 185, 20);
		CreateAccount.add(lblTaxIdentificationNumber);

		AddressInput = new JTextField();
		AddressInput.setBounds(692, 163, 146, 26);
		CreateAccount.add(AddressInput);
		AddressInput.setColumns(10);

		JLabel lblAddress = new JLabel("address");
		lblAddress.setBounds(608, 166, 69, 20);
		CreateAccount.add(lblAddress);

		JButton btnAdd = new JButton("Add");
		// ArrayList<String> tax_id_list = new ArrayList<String>();

		btnAdd.addActionListener(new ActionListener() {
			// Add person to account
			public void actionPerformed(ActionEvent e) {
				Boolean addCustomer = false;
				Boolean createCustomer = false;
				try {
					String qry = "select * from customers";
					ResultSet r = db.requestData(qry);

					// Check if tax id already exists
					while (r.next()) {
						String db_tax_id = r.getString("tax_id").trim();
						String db_name = r.getString("name").trim();

						// Tax id is in db, and user inputed invalid name or
						// address
						if (db_tax_id.equals(TaxIdInput.getText())
								&& (!"".equals(NameInput.getText()) || !"".equals(AddressInput.getText()))) {
							System.out.println("Tax ID already exists with name " + db_name + ", Please try again");
							addCustomer = false;
							break;
						}
						// Tax id is in db, and rest of fields are empty. Add
						// existing account to new account
						else if (db_tax_id.equals(TaxIdInput.getText()) && "".equals(NameInput.getText())
								&& "".equals(AddressInput.getText())) {
							// System.out.println("Add existing account to new
							// account");
							createCustomer = false;
							addCustomer = true;
							break;
						}
						// Tax id is valid, and user has entered a name and
						// address
						else if (TaxIdInput.getText().length() == 10 && !"".equals(NameInput.getText())
								&& !"".equals(AddressInput.getText())) {
							// System.out.println("Create new customers, and add
							// to this current account");
							createCustomer = true;
							addCustomer = true;
						} // else {
							// System.out.println("Invalid input, please try
							// again");
							// if(addCustomer==false)
							// addCustomer = false;
							// break;
							// }//TODO this else breaks logic, but also covers
							// all other cases of invalid input
					}
					;

					if (addCustomer == false)
						return;

					// Creating customer, and adding
					else if (createCustomer == true) {
						qry = "insert into customers (name,tax_id,address,hash) " + "values('" + NameInput.getText()
								+ "','" + TaxIdInput.getText() + "','" + AddressInput.getText() + "','" + "1717" + "')";
						// TODO add hash for 1717
						System.out.println(qry);
						db.requestData(qry);
						tax_id_list.add(TaxIdInput.getText());
					}
					// Adding existing customer
					else
						tax_id_list.add(TaxIdInput.getText());

					r.close();
					db.closeConn();
					System.out.println(tax_id_list);
				}

				catch (Exception e1) {
					System.out.println("fail");
				}
			}
		});
		btnAdd.setBounds(702, 205, 115, 29);
		CreateAccount.add(btnAdd);

		JLabel lblAddCustomerTax = new JLabel("Add customer, tax id only needed for existing customers");
		lblAddCustomerTax.setBounds(453, 36, 400, 20);
		CreateAccount.add(lblAddCustomerTax);

		JLabel lblAccountType = new JLabel(" account type");
		lblAccountType.setBounds(15, 79, 108, 20);
		CreateAccount.add(lblAccountType);

		JComboBox AccountTypeInput = new JComboBox();
		AccountTypeInput.setModel(new DefaultComboBoxModel(
				new String[] { "student checking", "interest checking", "savings", "pocket" }));
		AccountTypeInput.setBounds(121, 82, 212, 20);
		CreateAccount.add(AccountTypeInput);

		JButton btnCreateAccount = new JButton("Create Account");
		btnCreateAccount.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				java.util.Date date = new java.util.Date();
				String curDate = date.getMonth() + 1 + "/" + date.getDate() + "/" + date.getYear() + 1900;
				try {
					// if we have values to add to account, the amount to
					// deposit is atleast 0.1 and there is a bank branch
					if (tax_id_list.size() != 0 && Double.parseDouble(DepositInput.getText()) >= 0.1
							&& BankBranchInput.getText() != "") {
						// Getting the lowest account ID, and subtracting it for
						// the next value
						String qry = "select min(account_id) from account";
						ResultSet r = db.requestData(qry);
						r.next();
						Integer cur_account_id = Integer.valueOf(r.getString(1).trim());
						cur_account_id--;

						// Checks that the linked account matches with the owner
						// and that pocket account is selected
						if (LinkedAccountInput.getText().length() == 9
								&& doesTaxidOwnAccount(tax_id_list.get(0), LinkedAccountInput.getText())
								&& AccountTypeInput.getSelectedItem().toString() == "pocket") {
							qry = "insert into account(account_id,balance,primary_owner,bank_branch,status,linked_account) "
									+ "values('" + cur_account_id + "'," + DepositInput.getText() + ",'"
									+ tax_id_list.get(0) + "','" + BankBranchInput.getText() + "','" + "1" + "','"
									+ LinkedAccountInput.getText() + "')";

							// No linked account
						} else if (LinkedAccountInput.getText().length() == 0) {
							qry = "insert into account(account_id,balance,primary_owner,bank_branch,status) "
									+ "values('" + cur_account_id + "'," + DepositInput.getText() + ",'"
									+ tax_id_list.get(0) + "','" + BankBranchInput.getText() + "','" + "1" + "')";
						} else {
							System.out.println("Linked account " + LinkedAccountInput.getText() + " does not belong to "
									+ tax_id_list.get(0) + ", and pocket account must be used for a linked account");
							r.close();
							db.closeConn();
							return;
						}
						System.out.println(qry);
						db.requestData(qry);

						// Inserting into owns table
						for (String cur_id : tax_id_list) {
							qry = "insert into owns(tax_id,account_id) values('" + cur_id + "','" + cur_account_id
									+ "')";
							System.out.println(qry);
							db.requestData(qry);
						}

						// Inserting into type table
						qry = "insert into type(name,account_id) values('"
								+ AccountTypeInput.getSelectedItem().toString() + "','" + cur_account_id + "')";
						System.out.println(qry);
						db.requestData(qry);

						// Creating transaction
						if (AccountTypeInput.getSelectedItem().toString() == "pocket") {
							if (LinkedAccountInput.getText().length()==10){
							createTransaction(cur_account_id.toString(), LinkedAccountInput.getText(),
									DepositInput.getText(), 0, "top-up");
							qry = "update account set balance = balance - " + DepositInput.getText()
									+ " where account_id = " + LinkedAccountInput.getText();
							System.out.println(qry);
							db.requestData(qry);
							}
							else{
								System.out.println("Enter a valid linked Account");
							}

						} else
							createTransaction(cur_account_id.toString(), LinkedAccountInput.getText(),
									DepositInput.getText(), 0, "deposit");

						r.close();
						db.closeConn();

						tax_id_list.clear();
					}
				}

				catch (Exception e1) {
					System.out.println("create account failed");
				}
			}
		});
		btnCreateAccount.setBounds(147, 234, 164, 29);
		CreateAccount.add(btnCreateAccount);

		DepositInput = new JTextField();
		DepositInput.setBounds(131, 133, 202, 26);
		CreateAccount.add(DepositInput);
		DepositInput.setColumns(10);

		JLabel lblDeposit = new JLabel("Deposit");
		lblDeposit.setBounds(31, 136, 69, 20);
		CreateAccount.add(lblDeposit);

		LinkedAccountInput = new JTextField();
		LinkedAccountInput.setBounds(128, 176, 205, 26);
		CreateAccount.add(LinkedAccountInput);
		LinkedAccountInput.setColumns(10);

		JLabel lblLinkedAccount = new JLabel("Linked account");
		lblLinkedAccount.setBounds(15, 179, 108, 20);
		CreateAccount.add(lblLinkedAccount);

		BankBranchInput = new JTextField();
		BankBranchInput.setBounds(121, 33, 212, 26);
		CreateAccount.add(BankBranchInput);
		BankBranchInput.setColumns(10);

		JLabel lblBankBranch = new JLabel("Bank Branch");
		lblBankBranch.setBounds(15, 36, 91, 20);
		CreateAccount.add(lblBankBranch);

		JPanel DeleteClosedAccountsandCustomers = new JPanel();
		tabbedPane.addTab("Delete Closed Accounts and Customers", null, DeleteClosedAccountsandCustomers, null);
		DeleteClosedAccountsandCustomers.setLayout(null);

		JButton btnEnter_3 = new JButton("Enter");
		btnEnter_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deleteClosedAccounts();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnEnter_3.setBounds(356, 36, 115, 29);
		DeleteClosedAccountsandCustomers.add(btnEnter_3);

		Panel DeleteTransactions = new Panel();
		tabbedPane.addTab("Delete Transactions", null, DeleteTransactions, null);
		DeleteTransactions.setLayout(null);

		JButton btnEnter_4 = new JButton("Enter");
		btnEnter_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					deleteTransactions();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		btnEnter_4.setBounds(356, 43, 115, 29);
		DeleteTransactions.add(btnEnter_4);
	}
}
