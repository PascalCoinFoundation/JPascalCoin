package com.github.davidbolet.jpascalcoin.common.model;

import com.google.gson.annotations.SerializedName;

public enum KeyType {
	
	@SerializedName("714")
	SECP256K1(714),
	
	@SerializedName("715")
	SECP384R1(715),
	
	@SerializedName("729")
	SECT283K1(729), 
	
	@SerializedName("716")
	SECP521R1(716);
	
	private final int value;
    public int getValue() {
        return value;
    }
    
    public static KeyType fromValue(int value) {
    	for(KeyType result: KeyType.values()) {
    		if (result.getValue()==value) return result;
    	}
    	return null;
    }

    private KeyType(int value) {
        this.value = value;
    }
}
