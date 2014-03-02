package org.openmrs.module.dhisintegration;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;
import org.openmrs.BaseOpenmrsObject;
import org.openmrs.module.dhisintegration.api.impl.CodeGenerator;

public class OpenmrsDhisObject extends BaseOpenmrsObject {

	private Integer id;
	private String name;
	private String code;
	private String uid;
    private static final SimpleDateFormat LONG_DATE_FORMAT = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss" );

	public OpenmrsDhisObject() {
		super();
	}

	public OpenmrsDhisObject(String dhisName, String dhisCode, String dhisUid) {
		super();

		if (isNullOrEmpty(dhisUid)) {
			this.setUid(CodeGenerator.generateCode());
		} else {
			this.setUid(dhisUid);
		}

		if (isNullOrEmpty(dhisCode)) {
			this.setCode(this.getUid());
		} else {
			this.setCode(dhisCode);
		}

		if (isNullOrEmpty(dhisName)) {
			this.setName(this.getCode());
		} else {
			this.setName(dhisName);
		}		
	}

	private static Boolean isNullOrEmpty(String s) {
		if (s==null)
			return true;
		else if (s.length()==0)
			return true;
		return false;
	}

	public Date dateFromDhis(String s) throws ParseException {
		return LONG_DATE_FORMAT.parse(s);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name=name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code=code;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid=uid;
	}

	@Override
	public Integer getId() {
		return id;
	}

	@Override
	public void setId(Integer id) {
		this.id=id;
	}

}