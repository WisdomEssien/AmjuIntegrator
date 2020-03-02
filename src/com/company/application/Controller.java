package com.company.application;

import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.company.application.account.AccountService;
import com.company.application.airtimeRecharge.AirtimeRechargeService;
import com.company.application.balanceEnquiry.BalanceEnquiryService;
import com.company.application.billsPayment.BillsPaymentService;
import com.company.application.cardManagement.CardManagementService;
import com.company.application.device.DeviceService;
import com.company.application.fundTransfer.FundTransferService;
import com.neptunesoftware.reuseableClasses.CommonMethods;
import com.neptunesoftware.reuseableClasses.Quickteller.Quickteller;
import com.company.application.provider.SecureMe;


@Path("/rest")
@Produces(MediaType.APPLICATION_JSON)
public class Controller {
	
	Quickteller quickteller = new Quickteller();
	AccountService accountService = new AccountService();
	BalanceEnquiryService balanceEnquiryService = new BalanceEnquiryService();
	FundTransferService fundTransferService = new FundTransferService();
	AirtimeRechargeService airtimeRechargeService = new AirtimeRechargeService();
	BillsPaymentService billsPaymentService = new BillsPaymentService();
	DeviceService deviceService = new DeviceService();
	CardManagementService cardManagementService = new CardManagementService();
	
	public Controller(){}
	
	//Method to get BankCodes from Interswitch Api
	@GET
	@SecureMe
	@Path("/GetBankCodes")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchBankCodes() {
		return quickteller.getBankCodes();
	}
	
	//Method to get Billers from Interswitch Api
	@GET
	@SecureMe
	@Path("/GetBillers")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchBillers() {
		return quickteller.getBillers();
	}
	
	//Method to get Billers Categories from Interswitch Api
	@GET
	@SecureMe
	@Path("/GetBillerCategories")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchBillerCategories() {
		return quickteller.getBillerCategories();
	}
	
	//Method to get Biller by Category from Interswitch Api
	@GET
	@SecureMe
	@Path("/GetBillersByCategory/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchBillersByCategory(@PathParam("id") String id){
		return quickteller.getBillersByCategory(id);
	}
	
	//Method to get Billers payment items from Interswitch Api
	@GET
	@SecureMe
	@Path("/GetBillersPaymentItems/{billerId}")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchBillersPaymentItems(@PathParam("billerId") String billerId){
		return quickteller.getBillersPaymentItems(billerId);
	}
	
	//Method to get Customer name by account no from Interswitch Api
	@GET
	@SecureMe
	@Path("/VerifyAccount/{bankCode}/{accountNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchNameEnquiry(@PathParam("bankCode")String bankCode, @PathParam("accountNo") String accountNo){
		return quickteller.nameEnquiry(bankCode, accountNo);
	}
	
	//Method to get Billers payment items from Interswitch Api
	@GET
	@SecureMe
	@Path("/QueryTransaction/{requestreference}")
	@Produces(MediaType.APPLICATION_JSON)
	public String interswitchQueryTransaction(@PathParam("requestreference") String requestreference){		
		return quickteller.queryTransaction(requestreference);
	}
	
	//Method to get Customer name by account number in RUBIKON
	@GET
	@SecureMe
	@Path("/NameEnquiry/{accountNo}")
	public String rubikonNameEnquiry(@PathParam("accountNo") String accountNo) {
		return CommonMethods.ObjectToJsonString(accountService.nameEnquiryRubikon(accountNo));
	}
	
	//Method to get account details
	@GET
	@SecureMe
	@Path("/UserInfo/{accountNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public String nameEnquiryFromDatabase(@PathParam("accountNo") String accountNo){
		return CommonMethods.ObjectToJsonString(accountService.accountInfo(accountNo));
	}
	
	//Method to get balance enquiry
	@GET
	@SecureMe
	@Path("/BalanceEnquiry/{accountNo}")
	public String rubikonBalanceEnquiry(@PathParam("accountNo") String accountNo) {
		return CommonMethods.ObjectToJsonString(balanceEnquiryService.balanceEnquiryRubikon(accountNo));
	}
	
	//Method to transfer to another bank
	@POST
	@SecureMe
	@Path("/ExternalFundTransfer")
	public String externalFundTransfer(String body){
		return fundTransferService.ExternalFundTransfer(body);
	}
	
	//Method to transfer to same bank
	@POST
	@SecureMe
	@Path("/InternalFundTransfer")
	public String internalFundTransfer(String body){
		return fundTransferService.InternalFundTransfer(body);
	}
	
	//Method to buy airtime
	@POST
	@Path("/AirtimeRecharge")
	public String airtimeRecharge(String body){
		return airtimeRechargeService.AirtimeRecharge(body);
	}
	
	//Method to pay bills
	@POST
	@SecureMe
	@Path("/BillsPayment")
	public String billsPayment(String body) {
		return billsPaymentService.BillsPayment(body);
	}
	
	//Method for Login
	@POST
	@SecureMe
	@Path("/DoLogin")
	public String loginDevice(String body){
		return CommonMethods.ObjectToJsonString(deviceService.login(body));
	}
	
	//Method for Mobile Phone Registration
	@POST
	@SecureMe
	@Path("/RegisterPhone")
	public String registerDevice(String body){
		return CommonMethods.ObjectToJsonString(deviceService.register(body));
	}
		
	//Method for Mobile Phone Registration
	@POST
	@SecureMe
	@Path("/SaveOTP")
	public String saveOTP(String body){
		return CommonMethods.ObjectToJsonString(deviceService.saveOTP(body));
	}
	
	//Method for Forget Password
	@POST
	@SecureMe
	@Path("/ForgetDetail")
	public String forgetPassword(String body){
		return CommonMethods.ObjectToJsonString(deviceService.resetCredential(body));
	}
	
	
	//Method for fetching beneficiaries
	@GET
	@SecureMe
	@Path("/Beneficiary/{SenderAcctNo}/{ModuleId}")
	public String beneficiaryEnquiry(@PathParam("SenderAcctNo") String senderAcctNo, @PathParam("ModuleId") String moduleId){
		return CommonMethods.ObjectToJsonString(accountService.beneficiary(senderAcctNo, moduleId));
	}
	
	//Method for creating beneficiaries
	@POST
	@SecureMe
	@Path("/Beneficiary")
	public String beneficiarySave(String body) {
		return CommonMethods.ObjectToJsonString(accountService.saveBeneficiary(body));
	}
	
	//Method for deleting beneficiaries
	@DELETE
	@SecureMe
	@Path("/Beneficiary/{SenderAcctNo}/{ModuleId}/{BeneficiaryId}")
	public String beneficiaryDelete(@PathParam("SenderAcctNo") String senderAcctNo, @PathParam("ModuleId") String moduleId, @PathParam("BeneficiaryId") int beneficiaryId) {
		return CommonMethods.ObjectToJsonString(accountService.removeBeneficiary(senderAcctNo, moduleId, beneficiaryId));
	}
	
	//Method to select other accounts owned by user
	@GET
	@SecureMe
	@Path("/MultiAccountInfo/{accountNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public String selectMultiAccount(@PathParam("accountNo") String accountNo){
		return CommonMethods.ObjectToJsonString(accountService.multipleAccount(accountNo));
	}
		
	//Method to select account history
	@GET
	@SecureMe
	@Path("/AccountHistory/{accountNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public String accountHistoryWithFilter(@PathParam("accountNo") String accountNo, 
			@QueryParam("startDate") @DefaultValue("") String startDate,
			@QueryParam("endDate") @DefaultValue("") String endDate){
		return CommonMethods.ObjectToJsonString(accountService.accountHistory(accountNo, startDate, endDate));
	}
	
	//Method to get Notifications
	@GET
	@SecureMe
	@Path("/Notification")
	@Produces(MediaType.APPLICATION_JSON)
	public String notification(){
		return CommonMethods.ObjectToJsonString(deviceService.notification());
	}
	
	//Method to get All possible response codes and messages
	@GET
	@SecureMe
	@Path("/Responses")
	@Produces(MediaType.APPLICATION_JSON)
	public String possibleResponse(){
		return CommonMethods.ObjectToJsonString(deviceService.possibleResponse());
	}
	
	//Method to get all ATM cards owned by customer
	@GET
	@SecureMe
	@Path("/Card/{accountNo}")
	@Produces(MediaType.APPLICATION_JSON)
	public String cardsOwned(@PathParam("accountNo") String accountNumber){
		return CommonMethods.ObjectToJsonString(cardManagementService.cardOwned(accountNumber));
	}
	
	//Method for requesting new file
	@POST
	@SecureMe
	@Path("/CardRequest")
	public String cardRequest(String body){
		return CommonMethods.ObjectToJsonString(cardManagementService.newCard(body));
	}
	
	//Method for cardHotlist
	@POST
	@SecureMe
	@Path("/CardHotlist")
	public String cardHotlist(String body){
		return CommonMethods.ObjectToJsonString(cardManagementService.cardHotlist(body));
	}
	
	//Method for cardReplacement
	@POST
	@SecureMe
	@Path("/CardReplacement")
	public String cardReplacement(String body){
		return CommonMethods.ObjectToJsonString(cardManagementService.cardReplacement(body));
	}
	
	//Method for cardReplacement
	@POST
	@SecureMe
	@Path("/ChangePassword")
	public String changePassword(String body){
		return CommonMethods.ObjectToJsonString(deviceService.changePassword(body));
	}
	
	//Method for cardReplacement
	@POST
	@SecureMe
	@Path("/ChangePin")
	public String changePin(String body){
		return CommonMethods.ObjectToJsonString(deviceService.changePin(body));
	}
	
	
}
