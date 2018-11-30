package com.github.davidbolet.jpascalcoin.common.model;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;

import com.github.davidbolet.jpascalcoin.common.helper.Base58;
import com.github.davidbolet.jpascalcoin.common.helper.CryptoUtils;
import com.github.davidbolet.jpascalcoin.common.helper.HexConversionsHelper;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PascPublicKey implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String B58_PUBKEY_PREFIX="01";

	/**
	* Human readable name stored at the Wallet for this key
	*/
	@SerializedName("name")
    @Expose
	protected String name;

	/**
	* If false then Wallet doesn't have Private key for this public key, so, Wallet cannot execute operations with this key
	*/
	@SerializedName("can_use")
	@Expose
	protected Boolean canUse;

	/**
	* Encoded value of this public key.This HEXASTRING has no checksum, so, if using it always must be sure that value is correct
	*/
	@SerializedName("enc_pubkey")
	@Expose
	protected String encPubKey;

	/**
	* Encoded value of this public key in Base 58 format, also contains a checksum.This is the same value that Application Wallet exports as a public key
	*/
	@SerializedName("b58_pubkey")
	@Expose
	protected String base58PubKey;

	/**
	*Indicates which EC type is used (EC_NID)
	*/
	@SerializedName("ec_nid")
	@Expose
	protected KeyType keyType;

	/**
	* HEXASTRING with x value of public key
	*/
	@SerializedName("x")
	@Expose
	protected String x;

	/**
	* HEXASTRING with y value of public key
	*/
	@SerializedName("y")
	@Expose	
	protected String y;

	protected ECPublicKey ecPublicKey;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getCanUse() {
		return canUse;
	}

	public void setCanUse(Boolean canUse) {
		this.canUse = canUse;
	}

	public String getEncPubKey() {
		return encPubKey;
	}

	public void setEncPubKey(String encPubKey) {
		this.encPubKey = encPubKey;
	}

	public String getBase58PubKey() {
		return base58PubKey;
	}

	public void setBase58PubKey(String base58PubKey) {
		this.base58PubKey = base58PubKey;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public void setKeyType(KeyType keyType) {
		this.keyType = keyType;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base58PubKey == null) ? 0 : base58PubKey.hashCode());
		result = prime * result + ((encPubKey == null) ? 0 : encPubKey.hashCode());
		result = prime * result + ((keyType == null) ? 0 : keyType.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PascPublicKey)) {
			return false;
		}
		PascPublicKey other = (PascPublicKey) obj;
		if (base58PubKey == null) {
			if (other.base58PubKey != null) {
				return false;
			}
		} else if (!base58PubKey.equals(other.base58PubKey)) {
			return false;
		}
		if (encPubKey == null) {
			if (other.encPubKey != null) {
				return false;
			}
		} else if (!encPubKey.equals(other.encPubKey)) {
			return false;
		}
		if (keyType != other.keyType) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (x == null) {
			if (other.x != null) {
				return false;
			}
		} else if (!x.equals(other.x)) {
			return false;
		}
		if (y == null) {
			if (other.y != null) {
				return false;
			}
		} else if (!y.equals(other.y)) {
			return false;
		}
		return true;
	}

	public ECPublicKey getECPublicKey() {
		
		java.security.spec.ECParameterSpec ecParameters= CryptoUtils.getECParameterSpec("secp256k1");
		ECPoint ecPoint = new ECPoint(new BigInteger(x,16), new BigInteger(y,16));
		ECPublicKeySpec keySpec = new ECPublicKeySpec(ecPoint, ecParameters);
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("EC");
			return (ECPublicKey) keyFactory.generatePublic(keySpec);
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public boolean verify(byte[] signed,byte[] original) {
		try {
			Signature dsa = Signature.getInstance("SHA256withECDSA"); //"SHA256withECDSA"
			dsa.initVerify(this.getECPublicKey());
			dsa.update(original);
	        return dsa.verify(signed);
		} catch (InvalidKeyException | SignatureException | NoSuchAlgorithmException  e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Calculates Public Key object from bc string representation
	 * @param bcPub  string representation in B58 encoded form
	 * @return PublicKey
	 */
	public static PascPublicKey fromEncodedPubKey(String bcPub)  {
		com.github.davidbolet.jpascalcoin.common.model.PascPublicKey pk=new com.github.davidbolet.jpascalcoin.common.model.PascPublicKey();
		String sx,sy;
		if (bcPub==null || bcPub.length()!=140) throw new IllegalArgumentException("bcPub must be 140 charachters long");
		if (!bcPub.startsWith(HexConversionsHelper.int2BigEndianHex(KeyType.SECP256K1.getValue())))
			throw new IllegalArgumentException("Only SECP256K1 keys are supported");
		sx=bcPub.substring(8,72);
		sy=bcPub.substring(76);
		pk.setX(sx);
		pk.setY(sy);
		//pk.setName(name);
		pk.setEncPubKey(bcPub);
		pk.setCanUse(true); //However, the wallet doesn't have the private key, as we only have it 
		//Now we must calculate Base58PubKey
		try {
			MessageDigest sha = MessageDigest.getInstance("SHA-256");
			byte[] s1 = sha.digest(HexConversionsHelper.decodeStr2Hex(bcPub));
			String shaTxt=HexConversionsHelper.byteToHex(s1).toUpperCase();
			//System.out.println("  sha: " + shaTxt);
			
			//set AUX = SHA256( ENC_PUBKEY ) set NEW_RAW = '01' + AUX (as hexadecimal) + Copy(AUX, 1, 4) (as hexadecmial)
			String base58PubKeyPre= B58_PUBKEY_PREFIX+bcPub+shaTxt.substring(0, 8);
			//System.out.println("pre "+base58PubKeyPre);
			String base58PubKey = Base58.encode(HexConversionsHelper.decodeStr2Hex(base58PubKeyPre));
			pk.setBase58PubKey(base58PubKey);
		} catch(NoSuchAlgorithmException ne) {}
		return pk;
	}

	 /**
     * Generates a PascPublicKey object from a given java.security.ECPublicKey object
     * @param epub java.security.PublicKey to generate Pascalcoin public Key
     * @return Pascalcoin public key object
     * @throws NoSuchAlgorithmException if given key is different from SECP256K1
     */
    public static com.github.davidbolet.jpascalcoin.common.model.PascPublicKey fromECPublicKey(ECPublicKey epub) throws NoSuchAlgorithmException {
        
    	//ECPublicKey epub = (ECPublicKey)pub;
		ECPoint pt = epub.getW();
//		String sx1 = HexConversionsHelper.byteToHex(pt.getAffineX().toByteArray()).toUpperCase(); 
//		String sy1 = HexConversionsHelper.byteToHex(pt.getAffineY().toByteArray()).toUpperCase();
		String sx = adjustTo64(pt.getAffineX().toString(16)).toUpperCase();
		String sy = adjustTo64(pt.getAffineY().toString(16)).toUpperCase();
		//We must divide by 2 as charset is Unicode, while Pasc uses AnsiString 
		String bcPub = HexConversionsHelper.int2BigEndianHex(KeyType.SECP256K1.getValue())+HexConversionsHelper.int2BigEndianHex(sx.length()/2) + sx + HexConversionsHelper.int2BigEndianHex(sy.length()/2)+sy;
		com.github.davidbolet.jpascalcoin.common.model.PascPublicKey pk=fromEncodedPubKey( bcPub);
        return pk;
    }
    
    /**
     * Code to generate public key using bouncy castle
     * Derivates Public Key depending on the given KeyHelper object
     * @param privateKey PascPrivateKey to derivate
     * @param helper BouncyCastleKeyHelper for stabndard Java or SpongyCastleKeyHelper for android
     * @return PublicKey
     
    public static com.github.davidbolet.jpascalcoin.api.model.PublicKey fromPrivateKey(PascPrivateKey privateKey,KeyHelper helper) {
    	com.github.davidbolet.jpascalcoin.api.model.PublicKey result=null;
    	BigInteger s=new BigInteger(privateKey.getPrivateKey(), 16);
    	java.security.PublicKey pub = helper.fromPrivateKey(s);
    	try {
    		result= com.github.davidbolet.jpascalcoin.api.model.PublicKey.fromECPublicKey((ECPublicKey)pub);
    	} catch (Exception ex) {
    		
    	}
    	return result;
    }
*/    
  
    
    static private String adjustTo64(String s) {
        switch(s.length()) {
        case 62: return "00" + s;
        case 63: return "0" + s;
        case 64: return s;
        default:
            throw new IllegalArgumentException("not a valid key: " + s);
        }
    }
	
    
	
}
