package org.jpascalcoin.api.client;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jpascalcoin.api.constants.PascalCoinConstants;
import org.jpascalcoin.api.model.Account;
import org.jpascalcoin.api.model.Block;
import org.jpascalcoin.api.model.Connection;
import org.jpascalcoin.api.model.DecryptedPayload;
import org.jpascalcoin.api.model.KeyType;
import org.jpascalcoin.api.model.NodeStatus;
import org.jpascalcoin.api.model.OpResult;
import org.jpascalcoin.api.model.Operation;
import org.jpascalcoin.api.model.PayLoadEncryptionMethod;
import org.jpascalcoin.api.model.PublicKey;
import org.jpascalcoin.api.model.RawOperation;
import org.jpascalcoin.api.services.PascalCoinService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PascalCoinClientImpl implements PascalCoinClient {
	private static final Logger logger = Logger.getLogger(PascalCoinClientImpl.class.getName());
	
	private String baseUrl;
    private Retrofit retrofit;
    private PascalCoinService pascalCoinService;
    private int counter=0;
    
    public synchronized int getCounter()
    {
    		return counter++;
    }
    
    /**
     * Creates the basic json body for RPC 2.0
     * @return
     */
    private Map<String,Object> getRPCBody()
    {
    		Map<String,Object> result = new HashMap<>();
    		result.put("jsonrpc", "2.0");
    		result.put("id", getCounter());
    		return result;
    }

    /**
     * Gets or instantiates retrofit library with Gson converter
     * @return initialized Retrofit object
     */
    Retrofit getRetrofit() {
        if (this.retrofit == null) {
            this.retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
	
	public PascalCoinClientImpl()
	{
		this(PascalCoinConstants.DEFAULT_URL,PascalCoinConstants.DEFAULT_MAINNET_PORT );
	}
	
	public PascalCoinClientImpl(String server, Short port)
	{
		super();
		this.baseUrl="http://"+server+":"+port;
		pascalCoinService=getRetrofit().create(PascalCoinService.class);
		logger.setLevel(Level.ALL);
	}
	
	@Override
	public Integer addNode(String nodes) {
		Integer result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","addnode");
		params.put("nodes", nodes);
		body.put("params",params);
		Call<OpResult<Integer>> addNodeCall= pascalCoinService.addNode(body);
		try {
			Response<OpResult<Integer>> response = addNodeCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Account getAccount(Integer account) {
		Account result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getaccount");
		params.put("account", account);
		body.put("params",params);
		Call<OpResult<Account>> accountCall= pascalCoinService.getAccount(body);
		try {
			Response<OpResult<Account>> response = accountCall.execute();
			logger.log(Level.FINEST, response.raw().body().toString());
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Account> findAccounts(String name, Integer type, Integer status, Integer start, Integer max) {
		List<Account> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","findaccounts");
		if (name!=null && !"".equals(name))
			params.put("name", name);
		if (type!=null)
			params.put("type", type);		
		if (status!=null)
			params.put("status", status);
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<List<Account>>> findAccountsCall= pascalCoinService.findAccounts(body);
		try {
			Response<OpResult<List<Account>>> response = findAccountsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}
	
//	public Integer findAccountsCount(String name, Integer type , Integer status)
//	{
//		Integer result=null;
//		Map<String,Object> body = getRPCBody();
//		Map<String,Object> params = new HashMap<>();
//		body.put("method","findaccountscount");
//		if (name!=null && !"".equals(name))
//			params.put("name", name);
//		if (type!=null)
//			params.put("type", type);		
//		if (status!=null)
//			params.put("status", status);
//		body.put("params",params);
//		Call<OpResult<Integer>> findAccountsCountCall= pascalCoinService.findAccountsCount(body);
//		try {
//			Response<OpResult<Integer>> response = findAccountsCountCall.execute();
//			if (response.body().isError())
//			{
//				logger.log(Level.SEVERE, response.body().getErrorMessage());
//				throw new RuntimeException(response.body().getErrorMessage());
//			}
//			result = response.body().getResult();
//		} catch (IOException e) {
//			logger.log(Level.SEVERE, e.getMessage());
//		}
//		return result;		
//	}

	@Override
	public List<Account> getWalletAccounts(String encPubKey, String b58PubKey, Integer start, Integer max) {
		List<Account> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getwalletaccounts");
		if (encPubKey!=null)
			params.put("enc_pubkey", encPubKey);
		if (b58PubKey!=null)
			params.put("b58_pubkey", b58PubKey);
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<List<Account>>> walletAccountsCall= pascalCoinService.getWalletAccounts(body);
		try {
			Response<OpResult<List<Account>>> response = walletAccountsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Integer getWalletAccountsCount(String encPubKey, String b58PubKey, Integer start, Integer max) {
		Integer result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getwalletaccountscount");
		if (encPubKey!=null)
			params.put("enc_pubkey", encPubKey);
		if (b58PubKey!=null)
			params.put("b58_pubkey", b58PubKey);
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<Integer>> walletAccountsCountCall= pascalCoinService.getWalletAccountsCount(body);
		try {
			Response<OpResult<Integer>> response = walletAccountsCountCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public PublicKey getWalletPubKey(String encPubKey, String b58PubKey) {
		PublicKey result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getwalletpubkey");
		if (encPubKey!=null)
			params.put("enc_pubkey", encPubKey);
		if (b58PubKey!=null)
			params.put("b58_pubkey", b58PubKey);
		body.put("params",params);
		Call<OpResult<PublicKey>> walletPubCall= pascalCoinService.getWalletPubKey(body);
		try {
			Response<OpResult<PublicKey>> response = walletPubCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<PublicKey> getWalletPubKeys(Integer start, Integer max) {
		List<PublicKey> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getwalletpubkeys");
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<List<PublicKey>>> walletPubKeysCall= pascalCoinService.getWalletPubKeys(body);
		try {
			Response<OpResult<List<PublicKey>>> response = walletPubKeysCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Double getWalletCoins(String encPubKey, String b58PubKey) {
		Double result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getwalletcoins");
		if (encPubKey!=null)
			params.put("enc_pubkey", encPubKey);
		if (b58PubKey!=null)
			params.put("b58_pubkey", b58PubKey);
		body.put("params",params);
		Call<OpResult<Double>> walletCoinsCall= pascalCoinService.getWalletCoins(body);
		try {
			Response<OpResult<Double>> response = walletCoinsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Block getBlock(Integer block) {
		Block result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getblock");
		if (block!=null)
			params.put("block", block);
		body.put("params",params);
		Call<OpResult<Block>> getBlockCall= pascalCoinService.getBlock(body);
		try {
			Response<OpResult<Block>> response = getBlockCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Block> getBlocks(Integer last, Integer start, Integer end) {
		List<Block> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getblocks");
		if (last!=null && (start!=null || end!=null))
			throw new IllegalArgumentException("Cannot specify both last and start/end arguments");
		if (last!=null)
			params.put("last", last);
		if (start!=null)
			params.put("start", start);
		if (end!=null)
			params.put("end", end);
		body.put("params",params);
		Call<OpResult<List<Block>>> getBlocksCall= pascalCoinService.getBlocks(body);
		try {
			Response<OpResult<List<Block>>> response = getBlocksCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Integer getBlockCount() {
		Integer result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getblockcount");
		
		body.put("params",params);
		Call<OpResult<Integer>> blockCountCall= pascalCoinService.getBlockCount(body);
		try {
			Response<OpResult<Integer>> response = blockCountCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation getBlockOperation(Integer block, Integer opblock) {
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getblockoperation");
		if (block==null || opblock==null)
			throw new IllegalArgumentException("Block num and operation number are mandatory arguments");
		params.put("block", block);
		params.put("opblock", opblock);
		body.put("params",params);
		Call<OpResult<Operation>> blockOperationCall= pascalCoinService.getBlockOperation(body);
		try {
			Response<OpResult<Operation>> response = blockOperationCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> getBlockOperations(Integer block, Integer start, Integer max) {
		List<Operation> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getblockoperations");
		if (block==null )
			throw new IllegalArgumentException("Block param is mandatory");
		params.put("block", block);
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<List<Operation>>> blockOperationsCall= pascalCoinService.getBlockOperations(body);
		try {
			Response<OpResult<List<Operation>>> response = blockOperationsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> getAccountOperations(Integer account, Integer depth, Integer start, Integer max) {
		List<Operation> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getaccountoperations");
		if (account==null)
			throw new IllegalArgumentException("Account param is mandatory");
		params.put("account", account);
		if (depth!=null)
			params.put("depth", depth);
		if (start!=null)
			params.put("start", start);
		if (max!=null)
			params.put("max", max);
		body.put("params",params);
		Call<OpResult<List<Operation>>> accountOperationsCall= pascalCoinService.getAccountOperations(body);
		try {
			Response<OpResult<List<Operation>>> response = accountOperationsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> getPendings() {
		List<Operation> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getpendings");
		body.put("params",params);
		Call<OpResult<List<Operation>>> pendingOperationsCall= pascalCoinService.getPendings(body);
		try {
			Response<OpResult<List<Operation>>> response = pendingOperationsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation findOperation(String ophash) {
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","findoperation");
		if (ophash==null)
			throw new IllegalArgumentException("Operation hash param is mandatory");
		params.put("ophash", ophash);
		
		body.put("params",params);
		Call<OpResult<Operation>> findOperationCall= pascalCoinService.findOperation(body);
		try {
			Response<OpResult<Operation>> response = findOperationCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation changeAccountInfo(Integer account_target, Integer account_signer, String newEncPubKey,
			String newB58PubKey, String newName, Short new_type, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","changeaccountinfo");
		if (newEncPubKey!=null)
			params.put("enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("b58_pubkey", newB58PubKey);
		if (account_target!=null)
			params.put("account_target", account_target);
		if (account_signer!=null)
			params.put("account_signer", account_signer);
		if (newName!=null)
			params.put("new_name", newName);
		if (new_type!=null)
			params.put("new_type", new_type);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> changeAccountInfoCall= pascalCoinService.changeAccountInfo(body);
		try {
			Response<OpResult<Operation>> response = changeAccountInfoCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation sendTo(Integer sender, Integer target, Double amount, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd) {
		Operation result = null;
		if (sender==null||target==null||amount==null||fee==null)
			throw new IllegalArgumentException("Missing mandatory params. sender,target, amount and fee are mandatory");
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","sendto");
		params.put("sender", sender);
		params.put("target", target);
		params.put("amount", amount);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> sendToCall= pascalCoinService.sendTo(body);
		try {
			Response<OpResult<Operation>> response = sendToCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation changeKey(Integer account, Integer account_signer, String newEncPubKey, String newB58PubKey,
			Double fee, byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","changekey");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		if (account!=null)
			params.put("account", account);
		if (account_signer!=null)
			params.put("account_signer", account_signer);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> changeKeyCall= pascalCoinService.changeKey(body);
		try {
			Response<OpResult<Operation>> response = changeKeyCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> changeKeys(String accounts, String newEncPubKey, String newB58PubKey, Double fee,
			byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		List<Operation> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","changekeys");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		if (accounts!=null)
			params.put("accounts", accounts);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<List<Operation>>> changeKeysCall= pascalCoinService.changeKeys(body);
		try {
			Response<OpResult<List<Operation>>> response = changeKeysCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation listAccountForSale(Integer accountTarget, Integer accountSigner, Double price,
			Integer sellerAccount, String newB58PubKey, String newEncPubKey, Integer lockedUntilBlock, Double fee,
			byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		if (accountTarget==null||accountSigner==null||price==null|| sellerAccount ==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner, lockedUntilBlock, price and sellerAccount must be specified");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","listaccountforsale");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		params.put("locked_until_block",lockedUntilBlock);
		params.put("account_target", accountTarget);
		params.put("account_signer", accountSigner);
		params.put("price", price);
		params.put("seller_account", sellerAccount);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> listAccountForSaleCall= pascalCoinService.listAccountForSale(body);
		try {
			Response<OpResult<Operation>> response = listAccountForSaleCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation delistAccountForSale(Integer accountTarget, Integer accountSigner, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (accountTarget==null||accountSigner==null||fee==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner and fee must be specified");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","delistaccountforsale");
		params.put("account_target", accountTarget);
		params.put("account_signer", accountSigner);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> delistAccountForSaleCall= pascalCoinService.delistAccountForSale(body);
		try {
			Response<OpResult<Operation>> response = delistAccountForSaleCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation buyAccount(Integer buyerAccount, Integer accountToPurchase, Double price, Integer sellerAccount,
			String newB58PubKey, String newEncPubKey, Double amount, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (buyerAccount==null||accountToPurchase==null||fee==null||price==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner and fee must be specified");
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","buyaccount");
		if (newEncPubKey!=null)
			params.put("enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("b58_pubkey", newB58PubKey);
		if (buyerAccount!=null)
			params.put("buyer_account", buyerAccount);
		if (sellerAccount!=null)
			params.put("seller_account", sellerAccount);		
		if (accountToPurchase!=null)
			params.put("account_to_purchase", accountToPurchase);
		params.put("price", price);	
		params.put("amount", amount);	
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> buyAccountCall= pascalCoinService.buyAccount(body);
		try {
			Response<OpResult<Operation>> response = buyAccountCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Operation signChangeAccountInfo(Integer accountTarget, Integer accountSigner, String newEncPubKey,
			String newB58PubKey, String newName, Short newType, Integer lastNOperation, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd, String signerB58PubKey, String signerEncPubKey,
			String rawOperations) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		Operation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","signchangeaccountinfo");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		if (signerB58PubKey!=null)
			params.put("signer_b58_pubkey", signerB58PubKey);
		if (signerEncPubKey!=null)
			params.put("signer_enc_pubkey", signerEncPubKey);		
		if (accountTarget!=null)
			params.put("account_target", accountTarget);
		if (accountSigner!=null)
			params.put("account_signer", accountSigner);
		if (newName!=null)
			params.put("new_name", newName);
		if (newType!=null)
			params.put("new_type", newType);
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<Operation>> signChangeAccountInfoCall= pascalCoinService.signChangeAccountInfo(body);
		try {
			Response<OpResult<Operation>> response = signChangeAccountInfoCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public RawOperation signSendTo(Integer senderAccount, Integer targetAccount, String senderEncPubKey,
			String senderB58PubKey, String targetEncPubKey, String targetB58PubKey, Integer lastNOperation,
			Double amount, Double fee, byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd,
			String rawoperations) {
		RawOperation result = null;
		if (senderAccount==null||targetAccount==null||amount==null||fee==null||lastNOperation==null)
			throw new IllegalArgumentException("Missing mandatory params. sender,target,lastNOperation, amount and fee are mandatory");
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","signsendto");
		params.put("sender", senderAccount);
		params.put("target", targetAccount);
		params.put("amount", amount);
		params.put("fee", fee);	
		if (senderEncPubKey!=null)
			params.put("sender_enc_pubkey", senderEncPubKey);
		if (senderB58PubKey!=null)
		params.put("sender_b58_pubkey", senderB58PubKey);
		if (targetEncPubKey!=null)
			params.put("target_enc_pubkey", targetEncPubKey);
		if (targetB58PubKey!=null)
			params.put("target_b58_pubkey", targetB58PubKey);	
		params.put("last_n_operation", lastNOperation);	
		
		if (rawoperations!=null && !"".equals(rawoperations))
			params.put("rawoperations", rawoperations);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<RawOperation>> signSendToCall= pascalCoinService.signSendTo(body);
		try {
			Response<OpResult<RawOperation>> response = signSendToCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public RawOperation signChangeKey(Integer account, Integer accountSigner, String oldEncPubKey, String oldB58PubKey,
			String newEncPubKey, String newB58PubKey, Integer lastNOperation, Double fee, byte[] payload,
			PayLoadEncryptionMethod payloadMethod, String pwd, String rawOperations) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		if (oldEncPubKey!=null && oldB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both oldEncPubKey and oldB58PubKey");
		RawOperation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","signchangekey");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		if (oldEncPubKey!=null)
			params.put("old_enc_pubkey", oldEncPubKey);
		if (oldB58PubKey!=null)
			params.put("old_b58_pubkey", oldB58PubKey);		
		if (account!=null)
			params.put("account", account);
		if (accountSigner!=null)
			params.put("account_signer", accountSigner);
		if (rawOperations!=null && !"".equals(rawOperations))
			params.put("rawoperations", rawOperations);	
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);			
		body.put("params",params);
		Call<OpResult<RawOperation>> signChangeKeyCall= pascalCoinService.signChangeKey(body);
		try {
			Response<OpResult<RawOperation>> response = signChangeKeyCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public RawOperation signListAccountForSale(Integer accountTarget, Integer accountSigner, Double price,
			Integer sellerAccount, String newB58PubKey, String newEncPubKey, Integer lockedUntilBlock,
			Integer lastNOperation, Double fee, byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd,
			String signerB58PubKey, String signerEncPubKey, String rawOperations) {
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		if (signerB58PubKey!=null && signerEncPubKey!=null)
			throw new IllegalArgumentException("Cannot specify both signerB58PubKey and signerEncPubKey");
		if (accountTarget==null||accountSigner==null||price==null|| sellerAccount ==null||lastNOperation==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner, lockedUntilBlock,lastNOperation, price and sellerAccount must be specified");
		RawOperation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","signlistaccountforsale");
		if (newEncPubKey!=null)
			params.put("new_enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("new_b58_pubkey", newB58PubKey);
		if (signerEncPubKey!=null)
			params.put("signer_enc_pubkey", signerEncPubKey);
		if (signerB58PubKey!=null)
			params.put("signer_b58_pubkey", signerB58PubKey);
		params.put("locked_until_block",lockedUntilBlock);
		params.put("account_target", accountTarget);
		params.put("account_signer", accountSigner);
		params.put("price", price);
		params.put("seller_account", sellerAccount);
		params.put("fee", fee);	
		params.put("last_n_operation", lastNOperation);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);	
		if (rawOperations!=null)
			params.put("rawoperations", rawOperations);
		body.put("params",params);
		Call<OpResult<RawOperation>> signListAccountForSaleCall= pascalCoinService.signListAccountForSale(body);
		try {
			Response<OpResult<RawOperation>> response = signListAccountForSaleCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public RawOperation signDelistAccountForSale(Integer accountTarget, Integer accountSigner,
			Integer lastNOperation, Double fee, byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd,
			String signerB58PubKey, String signerEncPubKey, String rawOperations) {
		if (accountTarget==null||accountSigner==null||fee==null||lastNOperation==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner,lastNOperation and fee must be specified");
		if (signerB58PubKey!=null && signerEncPubKey!=null)
			throw new IllegalArgumentException("Cannot specify both signerB58PubKey and signerEncPubKey");
		RawOperation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","signdelistaccountforsale");
		params.put("account_target", accountTarget);
		params.put("account_signer", accountSigner);
		params.put("last_n_operation", lastNOperation);	
		params.put("fee", fee);	
		if (signerEncPubKey!=null)
			params.put("signer_enc_pubkey", signerEncPubKey);
		if (signerB58PubKey!=null)
			params.put("signer_b58_pubkey", signerB58PubKey);		
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);	
		if (rawOperations!=null)
			params.put("rawoperations", rawOperations);
		body.put("params",params);
		Call<OpResult<RawOperation>> delistAccountForSaleCall= pascalCoinService.signDelistAccountForSale(body);
		try {
			Response<OpResult<RawOperation>> response = delistAccountForSaleCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}


	@Override
	public RawOperation signBuyAccount(Integer buyerAccount, Integer accountToPurchase, Double price,
			Integer sellerAccount, String newB58PubKey, String newEncPubKey, Double amount, Integer lastNOperation,
			Double fee, byte[] payload, PayLoadEncryptionMethod payloadMethod, String pwd, String signerB58PubKey,
			String signerEncPubKey, String rawOperations) {
		if (buyerAccount==null||accountToPurchase==null||fee==null||price==null||lastNOperation==null)
			throw new IllegalArgumentException("Missing mandatory parameters. At least parameters accountTarget,accountSigner, lastNOperation and fee must be specified");
		if (newEncPubKey!=null && newB58PubKey!=null)
			throw new IllegalArgumentException("Cannot specify both newEncPubKey and newB58PubKey");
		if (signerB58PubKey!=null && signerEncPubKey!=null)
			throw new IllegalArgumentException("Cannot specify both signerB58PubKey and signerEncPubKey");
		RawOperation result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","buyaccount");
		if (newEncPubKey!=null)
			params.put("enc_pubkey", newEncPubKey);
		if (newB58PubKey!=null)
			params.put("b58_pubkey", newB58PubKey);
		if (signerEncPubKey!=null)
			params.put("signer_enc_pubkey", signerEncPubKey);
		if (signerB58PubKey!=null)
			params.put("signer_b58_pubkey", signerB58PubKey);		
		if (buyerAccount!=null)
			params.put("buyer_account", buyerAccount);
		if (sellerAccount!=null)
			params.put("seller_account", sellerAccount);		
		if (accountToPurchase!=null)
			params.put("account_to_purchase", accountToPurchase);
		params.put("price", price);	
		params.put("amount", amount);
		params.put("last_n_operation", lastNOperation);	
		params.put("fee", fee);	
		if (payload!=null)
			params.put("payload", Base64.getEncoder().encodeToString(payload));		
		if (payloadMethod!=null)
			params.put("payloadMethod", payloadMethod);	
		if (pwd!=null)
			params.put("pwd", pwd);	
		if (rawOperations!=null)
			params.put("rawoperations", rawOperations);
		body.put("params",params);
		Call<OpResult<RawOperation>> signBuyAccountCall= pascalCoinService.signBuyAccount(body);
		try {
			Response<OpResult<RawOperation>> response = signBuyAccountCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> operationsInfo(String rawOperations) {
		List<Operation> result = null;
		if (rawOperations==null)
			throw new IllegalArgumentException("Missing rawOperations parameter");
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","operationsinfo");
		params.put("rawoperations", rawOperations);	
		body.put("params",params);
		Call<OpResult<List<Operation>>> operationsInfoCall= pascalCoinService.operationsInfo(body);
		try {
			Response<OpResult<List<Operation>>> response = operationsInfoCall.execute();
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Operation> executeOperations(String rawOperations) {
		List<Operation> result = null;
		if (rawOperations==null)
			throw new IllegalArgumentException("Missing rawOperations parameter");
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","executeoperations");
		params.put("rawoperations", rawOperations);	
		body.put("params",params);
		Call<OpResult<List<Operation>>> executeOperationsCall= pascalCoinService.executeOperations(body);
		try {
			Response<OpResult<List<Operation>>> response = executeOperationsCall.execute();
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public NodeStatus getNodeStatus() {
		NodeStatus result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","nodestatus");
		body.put("params",params);
		Call<OpResult<NodeStatus>> nodeStatusCall= pascalCoinService.getNodeStatus(body);
		try {
			Response<OpResult<NodeStatus>> response = nodeStatusCall.execute();
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public String encodePubKey(KeyType ecNid, String x, String y) {
		if (ecNid == null || x == null || y ==null) throw new IllegalArgumentException("Params ecNid,x and y are mandatory");
		String result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","encodepubkey");
		params.put("ec_nid", ecNid);
		params.put("x", x);
		params.put("y", y);
		body.put("params",params);
		Call<OpResult<String>> encodePubKeyCall= pascalCoinService.encodePubKey(body);
		try {
			Response<OpResult<String>> response = encodePubKeyCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public PublicKey decodePubKey(String encPubKey, String b58PubKey) {
		PublicKey result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","decodepubkey");
		if (encPubKey!=null)
			params.put("enc_pubkey", encPubKey);
		if (b58PubKey!=null)
			params.put("b58_pubkey", b58PubKey);
		body.put("params",params);
		Call<OpResult<PublicKey>> decodePubKeyCall= pascalCoinService.decodePubKey(body);
		try {
			Response<OpResult<PublicKey>> response = decodePubKeyCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public String payloadEncrypt(String payload, PayLoadEncryptionMethod payloadMethod, String pwd) {
		if (payload==null || payloadMethod==null) throw new IllegalArgumentException("Params are mandatory");
		String result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","payloadencrypt");
		params.put("payload", payload);
		params.put("payload_method", payloadMethod);
		if (pwd!=null)
			params.put("pwd", pwd);
		body.put("params",params);
		Call<OpResult<String>> payloadEncryptCall= pascalCoinService.payloadEncrypt(body);
		try {
			Response<OpResult<String>> response = payloadEncryptCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public DecryptedPayload payloadDecrypt(String payload, String[] pwds) {
		DecryptedPayload result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","payloaddecrypt");
		if (payload!=null)
			params.put("payload", payload);
		if (pwds!=null)
			params.put("pwds", pwds);
		body.put("params",params);
		Call<OpResult<DecryptedPayload>> payloadDecryptCall= pascalCoinService.payloadDecrypt(body);
		try {
			Response<OpResult<DecryptedPayload>> response = payloadDecryptCall.execute();
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public List<Connection> getConnections() {
		List<Connection> result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","getconnections");
		body.put("params",params);
		Call<OpResult<List<Connection>>> getConnectionsCall= pascalCoinService.getConnections(body);
		try {
			Response<OpResult<List<Connection>>> response = getConnectionsCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
			
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public PublicKey addNewKey(KeyType ecNid, String name) {
		PublicKey result = null;
		if (name==null || ecNid==null) throw new IllegalArgumentException("Params ecNid and name are mandatory");
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","addnewkey");
		params.put("ec_nid", ecNid);
		params.put("name", name);
		body.put("params",params);
		Call<OpResult<PublicKey>> addNewKeyCall= pascalCoinService.addNewKey(body);
		try {
			Response<OpResult<PublicKey>> response = addNewKeyCall.execute();
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Boolean lock() {
		Boolean result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","lock");
		body.put("params",params);
		Call<OpResult<Boolean>> lockCall= pascalCoinService.lock(body);
		try {
			Response<OpResult<Boolean>> response = lockCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Boolean unlock(String pwd) {
		Boolean result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","unlock");
		params.put("pwd", pwd);
		body.put("params",params);
		Call<OpResult<Boolean>> unLockCall= pascalCoinService.unLock(body);
		try {
			Response<OpResult<Boolean>> response = unLockCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Boolean setWalletPassword(String pwd) {
		Boolean result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","setwalletpassword");
		params.put("pwd", pwd);
		body.put("params",params);
		Call<OpResult<Boolean>> setWalletPasswordCall= pascalCoinService.setWalletPassword(body);
		try {
			Response<OpResult<Boolean>> response = setWalletPasswordCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Boolean stopNode() {
		Boolean result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","stopnode");
		body.put("params",params);
		Call<OpResult<Boolean>> nodeStopCall= pascalCoinService.stopNode(body);
		try {
			Response<OpResult<Boolean>> response = nodeStopCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}

	@Override
	public Boolean startNode() {
		Boolean result = null;
		Map<String,Object> body = getRPCBody();
		Map<String,Object> params = new HashMap<>();
		body.put("method","startnode");
		body.put("params",params);
		Call<OpResult<Boolean>> nodeStartCall= pascalCoinService.startNode(body);
		try {
			Response<OpResult<Boolean>> response = nodeStartCall.execute();
			if (response.body().isError())
			{
				logger.log(Level.SEVERE, response.body().getErrorMessage());
				throw new RuntimeException(response.body().getErrorMessage());
			}
			result = response.body().getResult();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
		return result;
	}


}
