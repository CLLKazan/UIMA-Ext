package ru.kfu.cll.uima.tokenizer;

public class TokenInfo {
	public String typeName;
	public int begin;
	public int end;
	
	public  TokenInfo (String typeName, int begin, int end) {		   
	    this.begin = begin;
	    this.end = end;
	    this.typeName = typeName;
	  }
}

